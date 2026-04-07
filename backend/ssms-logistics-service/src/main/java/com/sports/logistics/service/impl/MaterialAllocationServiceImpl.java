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

    @Override
    public IPage<MaterialAllocation> getAllocationList(Page<MaterialAllocation> page, Long meetingId, Integer status) {
        LambdaQueryWrapper<MaterialAllocation> wrapper = new LambdaQueryWrapper<>();
        if (meetingId != null) {
            wrapper.eq(MaterialAllocation::getMeetingId, meetingId);
        }
        if (status != null) {
            wrapper.eq(MaterialAllocation::getStatus, status);
        }
        wrapper.orderByDesc(MaterialAllocation::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Long id, Integer status) {
        MaterialAllocation allocation = getById(id);
        if (allocation == null) {
            return false;
        }

        // 仅在状态由“待审”变为“批准”时执行库存预扣减
        if (status == 1 && (allocation.getStatus() == null || allocation.getStatus() == 0)) {
            JSONArray items = JSONUtil.parseArray(allocation.getItems());
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                Long materialId = item.getLong("materialId");
                Integer quantity = item.getInt("quantity");

                // 原子性扣减：UPDATE t_material SET stock = stock - quantity WHERE id = ? AND stock >= quantity
                boolean success = materialService.update(new LambdaUpdateWrapper<Material>()
                        .eq(Material::getId, materialId)
                        .ge(Material::getStock, quantity)
                        .setSql("stock = stock - " + quantity));
                
                if (!success) {
                    throw new RuntimeException("物资库存不足，无法批准申请");
                }
            }
        }

        allocation.setStatus(status);
        boolean updated = updateById(allocation);

        if (updated) {
            // TODO: 微服务架构下可使用消息队列或专用 WebSocket 服务进行通知
            log.info("Material allocation {} status updated to {}", id, status);
        }
        return updated;
    }

    @Override
    public boolean save(MaterialAllocation entity) {
        boolean saved = super.save(entity);
        if (saved) {
             // TODO: 微服务架构下可使用消息队列或专用 WebSocket 服务进行通知
             log.info("New material allocation applied by user {}", entity.getApplicantId());
        }
        return saved;
    }
}
