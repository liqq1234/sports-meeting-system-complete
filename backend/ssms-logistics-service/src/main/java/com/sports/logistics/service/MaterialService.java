package com.sports.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sports.logistics.entity.Material;

public interface MaterialService extends IService<Material> {
    IPage<Material> getMaterialList(Page<Material> page, String name);
}
