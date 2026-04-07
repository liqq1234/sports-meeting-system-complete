package com.sports.sports.service;

import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Venue;
import java.util.List;

public interface VenueService {
    PageResult<Venue> getPage(Integer pageNum, Integer pageSize, String name, String type, Integer status);
    Venue getById(Long id);
    List<Venue> listAll();
    void add(Venue venue);
    void update(Venue venue);
    void delete(Long id);
}
