package com.sports.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_material_allocation")
public class MaterialAllocation implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long meetingId;
    
    /**
     * 关联项目ID
     */
    private Long eventId;
    
    private Long applicantId;

    @TableField(exist = false)
    private String applicant;

    private String purpose;
    
    /**
     * 状态：0-待审 1-批准 2-驳回 3-已分发 4-已归还
     */
    private Integer status;

    /**
     * 申请明细内容（JSON格式，例如 [{"materialId": 1, "quantity": 10}]）
     */
    private String items;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
