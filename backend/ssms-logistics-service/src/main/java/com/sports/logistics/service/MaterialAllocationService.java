package com.sports.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sports.logistics.entity.MaterialAllocation;

public interface MaterialAllocationService extends IService<MaterialAllocation> {
    IPage<MaterialAllocation> getAllocationList(Page<MaterialAllocation> page, Long meetingId, Integer status);
    boolean approve(Long id, Integer status);
}
