import request from '@/utils/request'

export function getMedicalRecords(params) {
  return request({
    url: '/medical/record/page',
    method: 'get',
    params
  })
}

export function addMedicalRecord(data) {
  return request({
    url: '/medical/record',
    method: 'post',
    data
  })
}

export function updateMedicalRecord(data) {
  return request({
    url: '/medical/record',
    method: 'put',
    data
  })
}

export function deleteMedicalRecord(id) {
  return request({
    url: `/medical/record/${id}`,
    method: 'delete'
  })
}

export function getMedicalAnalytics(params) {
  return request({
    url: '/sports/stats/medical',
    method: 'get',
    params
  })
}
