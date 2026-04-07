package com.sports.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sports.medical.entity.MedicalRecord;
import com.sports.medical.mapper.MedicalRecordMapper;
import com.sports.medical.service.MedicalRecordService;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord> implements MedicalRecordService {

    @Override
    public IPage<MedicalRecord> getRecordList(Page<MedicalRecord> page, Long meetingId, Long patientId) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        if (meetingId != null) {
            wrapper.eq(MedicalRecord::getMeetingId, meetingId);
        }
        if (patientId != null) {
            wrapper.eq(MedicalRecord::getPatientId, patientId);
        }
        wrapper.orderByDesc(MedicalRecord::getVisitTime);
        return baseMapper.selectPage(page, wrapper);
    }
}
