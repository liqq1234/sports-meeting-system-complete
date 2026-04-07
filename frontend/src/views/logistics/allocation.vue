<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>物资申请记录</span>
        <el-button style="float: right; padding: 3px 0" type="primary" size="small" @click="handleApply">新增领用申请</el-button>
      </div>

      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="运动会编号">
          <el-input v-model="queryParams.meetingId" placeholder="请输入运动会ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待审批" :value="0" />
            <el-option label="已批准" :value="1" />
            <el-option label="已驳回" :value="2" />
            <el-option label="已分发" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="allocationList" border stripe>
        <el-table-column label="申请人" align="center" prop="applicant" />
        <el-table-column label="用途" align="center" prop="purpose" show-overflow-tooltip />
        <el-table-column label="物资明细" align="center">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top">
              <el-table :data="JSON.parse(scope.row.items || '[]')" size="mini">
                <el-table-column label="物资ID" prop="materialId" />
                <el-table-column label="数量" prop="quantity" />
              </el-table>
              <div slot="reference" class="name-wrapper">
                <el-tag size="medium">查看明细</el-tag>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center">
          <template slot-scope="scope">
            <el-tag :type="statusMap[scope.row.status].type">
              {{ statusMap[scope.row.status].label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="180">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 0">
              <el-button size="mini" type="text" style="color: #67c23a" @click="handleApprove(scope.row, 1)">批准</el-button>
              <el-button size="mini" type="text" style="color: #f56c6c" @click="handleApprove(scope.row, 2)">驳回</el-button>
            </span>
            <el-button v-if="scope.row.status === 1" size="mini" type="text" @click="handleApprove(scope.row, 3)">确认发放</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :current-page="queryParams.current"
        :page-size="queryParams.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="getList"
      />
    </el-card>

    <!-- 申请弹窗 -->
    <el-dialog title="新增领用申请" :visible.sync="open" width="600px">
      <el-form ref="form" :model="form" label-width="100px">
        <el-form-item label="所属运动会ID" prop="meetingId">
          <el-input v-model="form.meetingId" placeholder="请输入运动会ID" />
        </el-form-item>
        <el-form-item label="申请人" prop="applicant">
          <el-input v-model="form.applicant" placeholder="请输入姓名或单位" />
        </el-form-item>
        <el-form-item label="领用用途" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" placeholder="物资用途说明" />
        </el-form-item>
        <el-form-item label="物资明细">
          <div v-for="(item, index) in applyItems" :key="index" style="margin-bottom: 10px;">
            <el-input-number v-model="item.materialId" size="mini" placeholder="物资ID" />
            <el-input-number v-model="item.quantity" size="mini" :min="1" placeholder="数量" />
            <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="applyItems.splice(index, 1)" />
          </div>
          <el-button type="success" icon="el-icon-plus" size="mini" circle @click="applyItems.push({materialId: 1, quantity: 1})" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交申请</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAllocationList, applyAllocation, approveAllocation } from '@/api/logistics'

export default {
  name: 'AllocationList',
  data() {
    return {
      loading: true,
      allocationList: [],
      total: 0,
      queryParams: {
        current: 1,
        size: 10,
        meetingId: undefined,
        status: undefined
      },
      open: false,
      form: {},
      applyItems: [{ materialId: undefined, quantity: 1 }],
      statusMap: {
        0: { label: '待审批', type: 'info' },
        1: { label: '已批准', type: 'success' },
        2: { label: '已驳回', type: 'danger' },
        3: { label: '已发放', type: 'primary' }
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getAllocationList(this.queryParams).then(res => {
        this.allocationList = res.data.records
        this.total = res.data.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    handleApply() {
      this.form = { meetingId: 1 }
      this.applyItems = [{ materialId: 1, quantity: 10 }]
      this.open = true
    },
    submitApply() {
      this.form.items = JSON.stringify(this.applyItems)
      applyAllocation(this.form).then(() => {
        this.$message.success('申请提交成功')
        this.open = false
        this.getList()
      })
    },
    handleApprove(row, status) {
      const msg = status === 1 ? '确认批准该申请？系统将预扣库库存' : '确认操作？'
      this.$confirm(msg, '提示', { type: 'info' }).then(() => {
        approveAllocation({ id: row.id, status }).then(() => {
          this.$message.success('操作成功')
          this.getList()
        }).catch(err => {
          this.$message.error(err.message || '操作失败')
        })
      }).catch(() => {})
    }
  }
}
</script>
