package com.sports.medical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_medical_record")
public class MedicalRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long meetingId;
    private Long patientId;
    
    private LocalDateTime visitTime;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String doctor;

    /**
     * 处置：0-返回比赛 1-留院观察 2-转院
     */
    private Integer disposition;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
