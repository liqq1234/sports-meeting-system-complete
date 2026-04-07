package com.sports.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sports.logistics.entity.Material;
import com.sports.logistics.mapper.MaterialMapper;
import com.sports.logistics.service.MaterialService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {

    @Override
    @Cacheable(value = "logistics", key = "'material_list_' + #name + '_' + #page.current + '_' + #page.size")
    public IPage<Material> getMaterialList(Page<Material> page, String name) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Material::getName, name);
        }
        wrapper.orderByDesc(Material::getUpdateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @CacheEvict(value = "logistics", allEntries = true)
    public boolean save(Material entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "logistics", allEntries = true)
    public boolean updateById(Material entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "logistics", allEntries = true)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}
