import request from '@/utils/request'

// 物资管理
export function getMaterialPage(params) {
  return request({ url: '/logistics/material/page', method: 'get', params })
}

export function getMaterialList() {
  return request({ url: '/logistics/material/list', method: 'get' })
}

export function addMaterial(data) {
  return request({ url: '/logistics/material', method: 'post', data })
}

export function updateMaterial(data) {
  return request({ url: '/logistics/material', method: 'put', data })
}

export function deleteMaterial(id) {
  return request({ url: `/logistics/material/${id}`, method: 'delete' })
}

// 物资发放与领用
export function getAllocationList(params) {
  return request({ url: '/logistics/allocation/page', method: 'get', params })
}

export function applyAllocation(data) {
  return request({ url: '/logistics/allocation', method: 'post', data })
}

export function approveAllocation(id, status) {
  return request({ url: `/logistics/allocation/approve/${id}`, method: 'put', params: { status } })
}

export function returnAllocation(id) {
  return request({ url: `/logistics/allocation/return/${id}`, method: 'put' })
}

export function getMyEvents(params) {
  return request({ url: '/logistics/allocation/myEvents', method: 'get', params })
}
