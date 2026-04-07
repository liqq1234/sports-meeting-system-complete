package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Venue;
import com.sports.sports.mapper.VenueMapper;
import com.sports.sports.service.VenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VenueServiceImpl implements VenueService {

    @Autowired
    private VenueMapper venueMapper;

    @Override
    public PageResult<Venue> getPage(Integer pageNum, Integer pageSize, String name, String type, Integer status) {
        Page<Venue> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Venue> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Venue::getName, name);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Venue::getType, type);
        }
        if (status != null) {
            wrapper.eq(Venue::getStatus, status);
        }
        wrapper.orderByDesc(Venue::getCreateTime);
        IPage<Venue> result = venueMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    public Venue getById(Long id) {
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            throw new RuntimeException("场地不存在");
        }
        return venue;
    }

    @Override
    public List<Venue> listAll() {
        return venueMapper.selectList(
                new LambdaQueryWrapper<Venue>().eq(Venue::getStatus, 1).orderByAsc(Venue::getName));
    }

    @Override
    public void add(Venue venue) {
        venueMapper.insert(venue);
    }

    @Override
    public void update(Venue venue) {
        venueMapper.updateById(venue);
    }

    @Override
    public void delete(Long id) {
        venueMapper.deleteById(id);
    }
}
