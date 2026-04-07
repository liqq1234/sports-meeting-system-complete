package com.sports.medical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sports.medical.entity.MedicalRecord;

public interface MedicalRecordService extends IService<MedicalRecord> {
    IPage<MedicalRecord> getRecordList(Page<MedicalRecord> page, Long meetingId, Long patientId);
}
