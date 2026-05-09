package com.sports.logistics.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sports.logistics.entity.Material;
import com.sports.logistics.entity.MaterialAllocation;
import com.sports.logistics.mapper.MaterialAllocationMapper;
import com.sports.logistics.service.MaterialAllocationService;
import com.sports.logistics.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MaterialAllocationServiceImpl extends ServiceImpl<MaterialAllocationMapper, MaterialAllocation> implements MaterialAllocationService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private com.sports.sports.client.UserClient userClient;

    @Autowired
    private com.sports.sports.client.SportsServiceClient sportsServiceClient;

    @Override
    public IPage<MaterialAllocation> getAllocationList(Page<MaterialAllocation> page, Long meetingId, Integer status, Long applicantId) {
        LambdaQueryWrapper<MaterialAllocation> wrapper = new LambdaQueryWrapper<>();
        if (meetingId != null) {
            wrapper.eq(MaterialAllocation::getMeetingId, meetingId);
        }
        if (status != null) {
            wrapper.eq(MaterialAllocation::getStatus, status);
        }
        if (applicantId != null) {
            wrapper.eq(MaterialAllocation::getApplicantId, applicantId);
        }
        wrapper.orderByDesc(MaterialAllocation::getCreateTime);
        IPage<MaterialAllocation> allocationPage = baseMapper.selectPage(page, wrapper);
        
        // 填充申请人姓名
        if (allocationPage.getRecords() != null && !allocationPage.getRecords().isEmpty()) {
            java.util.List<Long> userIds = allocationPage.getRecords().stream()
                .map(MaterialAllocation::getApplicantId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
            
            if (!userIds.isEmpty()) {
                try {
                    com.sports.logistics.common.Result<java.util.List<com.sports.sports.client.vo.UserVO>> userResult = userClient.listByIds(userIds);
                    if (userResult.getCode() == 200 && userResult.getData() != null) {
                        java.util.Map<Long, String> userMap = userResult.getData().stream()
                            .collect(java.util.stream.Collectors.toMap(com.sports.sports.client.vo.UserVO::getId, u -> u.getRealName() != null ? u.getRealName() : "未知用户"));
                        
                        for (MaterialAllocation allocation : allocationPage.getRecords()) {
                            allocation.setApplicant(userMap.get(allocation.getApplicantId()));
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to fetch user names for allocations", e);
                }
            }
        }
        
        return allocationPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Long id, Integer status) {
        MaterialAllocation allocation = getById(id);
        if (allocation == null) {
            return false;
        }

        Integer oldStatus = allocation.getStatus();

        // 仅在状态变更为“已发放(3)”时执行库存扣减
        // 逻辑：如果新状态是3，且旧状态不是3（避免重复扣减），则扣减库存
        if (status == 3 && (oldStatus == null || oldStatus != 3)) {
            deductStock(allocation);
        }

        allocation.setStatus(status);
        boolean updated = updateById(allocation);

        if (updated) {
            sendNotification(allocation, status);
            log.info("Material allocation {} status updated to {}", id, status);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnMaterial(Long id) {
        MaterialAllocation allocation = getById(id);
        // 只有已发放(3)的物资才能归还并加回库存
        if (allocation == null || allocation.getStatus() != 3) {
            return false;
        }

        // 返还库存
        JSONArray items = JSONUtil.parseArray(allocation.getItems());
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            Long materialId = item.getLong("materialId");
            Integer quantity = item.getInt("quantity");

            materialService.update(new LambdaUpdateWrapper<Material>()
                    .eq(Material::getId, materialId)
                    .setSql("stock = stock + " + quantity));
        }

        allocation.setStatus(4); // 已归还
        return updateById(allocation);
    }

    private void deductStock(MaterialAllocation allocation) {
        JSONArray items = JSONUtil.parseArray(allocation.getItems());
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            Long materialId = item.getLong("materialId");
            Integer quantity = item.getInt("quantity");

            boolean success = materialService.update(new LambdaUpdateWrapper<Material>()
                    .eq(Material::getId, materialId)
                    .ge(Material::getStock, quantity)
                    .setSql("stock = stock - " + quantity));
            
            if (!success) {
                throw new RuntimeException("物资库存不足，无法批准申请");
            }
        }
    }

    private void sendNotification(MaterialAllocation allocation, Integer status) {
        try {
            com.sports.sports.client.SportsServiceClient.MessageDTO msg = new com.sports.sports.client.SportsServiceClient.MessageDTO();
            msg.setUserId(allocation.getApplicantId());
            msg.setRelatedId(allocation.getId());
            msg.setType(1);
            
            if (status == 1) {
                msg.setTitle("物资领用申请已批准");
                msg.setContent("您的物资申请已获批准，请前往后勤处领取。");
            } else if (status == 3) {
                msg.setTitle("物资已发放通知");
                msg.setContent("您申请的物资已正式发放，请妥善保管并在使用后及时归还。");
            } else if (status == 2) {
                msg.setTitle("物资申请被驳回");
                msg.setContent("您的物资领用申请未能通过审核。");
            } else {
                return;
            }
            sportsServiceClient.sendMessage(msg);
        } catch (Exception e) {
            log.error("Failed to send material notification", e);
        }
    }

    @Override
    public boolean save(MaterialAllocation entity) {
        // 申请时校验库存
        JSONArray items = JSONUtil.parseArray(entity.getItems());
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            Long materialId = item.getLong("materialId");
            Integer quantity = item.getInt("quantity");
            
            Material material = materialService.getById(materialId);
            if (material == null) {
                throw new RuntimeException("物资不存在: " + materialId);
            }
            if (material.getStock() < quantity) {
                throw new RuntimeException("物资 [" + material.getName() + "] 库存不足，当前库存: " + material.getStock());
            }
        }
        
        boolean saved = super.save(entity);
        if (saved) {
             // TODO: 微服务架构下可使用消息队列或专用 WebSocket 服务进行通知
             log.info("New material allocation applied by user {}", entity.getApplicantId());
        }
        return saved;
    }
}
