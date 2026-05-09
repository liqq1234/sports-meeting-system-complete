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
        <el-table-column label="物资明细" align="center" min-width="150">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top">
              <el-table :data="JSON.parse(scope.row.items || '[]')" size="mini">
                <el-table-column label="物资名称" prop="materialId">
                  <template slot-scope="item">
                    {{ getMaterialName(item.row.materialId) }}
                  </template>
                </el-table-column>
                <el-table-column label="数量" prop="quantity" />
              </el-table>
              <div slot="reference" class="name-wrapper">
                <span style="color: #409EFF; cursor: pointer">{{ formatItems(scope.row.items) }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center">
          <template slot-scope="scope">
            <el-tag :type="statusMap[scope.row.status]?.type">
              {{ statusMap[scope.row.status]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="220">
          <template slot-scope="scope">
            <!-- 管理员操作 -->
            <span v-if="role === 0" style="margin-right: 10px">
              <el-button v-if="scope.row.status === 0" size="mini" type="text" style="color: #67c23a" @click="handleApprove(scope.row, 1)">批准</el-button>
              <el-button v-if="scope.row.status === 0" size="mini" type="text" style="color: #f56c6c" @click="handleApprove(scope.row, 2)">驳回</el-button>
              <el-button v-if="scope.row.status === 1" size="mini" type="text" @click="handleApprove(scope.row, 3)">确认发放</el-button>
            </span>
            <!-- 管理员或申请人均可确认归还 -->
            <el-button v-if="scope.row.status === 3 && (role === 0 || scope.row.applicantId === userId)" 
                       size="mini" type="text" style="color: #e6a23c" @click="handleReturn(scope.row)">确认归还</el-button>
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
        <el-form-item label="所属运动会" prop="meetingId">
          <el-select v-model="form.meetingId" placeholder="请选择运动会" style="width: 100%" @change="handleMeetingChange">
            <el-option
              v-for="item in meetingOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关联项目" prop="eventId">
          <el-select v-model="form.eventId" :placeholder="form.meetingId ? '请选择项目' : '请先选择运动会'" style="width: 100%" :disabled="!form.meetingId">
            <el-option
              v-for="item in eventOptions"
              :key="item.eventId"
              :label="item.eventName"
              :value="item.eventId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请人" prop="applicant">
          <el-input v-model="form.applicant" placeholder="系统自动识别" readonly />
        </el-form-item>
        <el-form-item label="领用用途" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" placeholder="物资用途说明" />
        </el-form-item>
        <el-form-item label="物资明细">
          <div v-for="(item, index) in applyItems" :key="index" style="margin-bottom: 10px; display: flex; align-items: center;">
            <el-select v-model="item.materialId" size="small" placeholder="请选择物资" style="flex: 1; margin-right: 10px;">
              <el-option v-for="m in materialOptions" :key="m.id" :label="m.name" :value="m.id">
                <span style="float: left">{{ m.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">库: {{ m.stock }}{{ m.unit }}</span>
              </el-option>
            </el-select>
            <el-input-number v-model="item.quantity" size="small" :min="1" placeholder="数量" style="width: 120px; margin-right: 10px;" />
            <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="applyItems.splice(index, 1)" />
          </div>
          <el-button type="success" icon="el-icon-plus" size="mini" circle @click="applyItems.push({materialId: undefined, quantity: 1})" />
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
import { getAllocationList, applyAllocation, approveAllocation, getMaterialList, returnAllocation, getMyEvents } from '@/api/logistics'
import { getMeetingList } from '@/api/meeting'

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
        status: undefined,
        applicantId: undefined
      },
      open: false,
      form: {},
      materialOptions: [],
      meetingOptions: [],
      eventOptions: [], // 新增：项目选项
      applyItems: [{ materialId: undefined, quantity: 1 }],
      statusMap: {
        0: { label: '待审批', type: 'info' },
        1: { label: '已批准', type: 'success' },
        2: { label: '已驳回', type: 'danger' },
        3: { label: '已发放', type: 'primary' },
        4: { label: '已归还', type: 'info' }
      }
    }
  },
  computed: {
    role() {
      return this.$store.getters.role
    },
    userId() {
      return this.$store.getters.userInfo.id
    },
    realName() {
      return this.$store.getters.userInfo.realName || this.$store.getters.userInfo.username
    }
  },
  created() {
    this.loadMaterials().then(() => {
      this.getList()
    })
    this.loadMeetings()
  },
  methods: {
    loadMeetings() {
      getMeetingList().then(res => {
        this.meetingOptions = res.data
      })
    },
    handleMeetingChange(meetingId) {
      this.$set(this.form, 'eventId', undefined)
      this.eventOptions = []
      if (meetingId) {
        getMyEvents({ meetingId }).then(res => {
          this.eventOptions = res.data
        })
      }
    },
    getList() {
      this.loading = true
      // 清理空参数避免后端解析 400 错误
      const params = { ...this.queryParams }
      Object.keys(params).forEach(key => {
        if (params[key] === '' || params[key] === undefined || params[key] === null) {
          delete params[key]
        }
      })
      
      // 非管理员只看自己的申请
      if (this.role !== 0) {
        params.applicantId = this.userId
      }
      getAllocationList(params).then(res => {
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
      this.form = { 
        meetingId: undefined,
        eventId: undefined,
        purpose: undefined,
        applicant: this.realName 
      }
      this.applyItems = [{ materialId: undefined, quantity: 1 }]
      this.open = true
    },
    submitApply() {
      // 验证库存，避免超出库存的申请
      for (const item of this.applyItems) {
        if (!item.materialId) {
          this.$message.warning('请选择物资')
          return
        }
        const material = this.materialOptions.find(m => m.id === item.materialId)
        if (material && item.quantity > material.stock) {
          this.$message.warning(`物资 "${material.name}" 库存不足 (当前库存: ${material.stock})`)
          return
        }
      }

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
        approveAllocation(row.id, status).then(() => {
          this.$message.success('操作成功')
          this.getList()
        }).catch(err => {
          this.$message.error(err.message || '操作失败')
        })
      }).catch(() => {})
    },
    handleReturn(row) {
      this.$confirm('确认物资已归还？', '提示', { type: 'info' }).then(() => {
        returnAllocation(row.id).then(() => {
          this.$message.success('操作成功')
          this.getList()
        })
      }).catch(() => {})
    },
    loadMaterials() {
      return getMaterialList().then(res => {
        this.materialOptions = res.data
      })
    },
    getMaterialName(id) {
      const m = this.materialOptions.find(item => item.id === id)
      return m ? m.name : `未知物资(ID:${id})`
    },
    formatItems(itemsJson) {
      if (!itemsJson) return '无'
      try {
        const items = JSON.parse(itemsJson)
        return items.map(item => `${this.getMaterialName(item.materialId)} x${item.quantity}`).join(', ')
      } catch (e) {
        return '数据格式错误'
      }
    }
  }
}
</script>
