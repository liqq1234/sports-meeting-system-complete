import request from '@/utils/request'

export function getMaterialPage(params) {
  return request({
    url: '/logistics/material/page',
    method: 'get',
    params
  })
}

export function addMaterial(data) {
  return request({
    url: '/logistics/material',
    method: 'post',
    data
  })
}

export function updateMaterial(data) {
  return request({
    url: '/logistics/material',
    method: 'put',
    data
  })
}

export function deleteMaterial(id) {
  return request({
    url: `/logistics/material/${id}`,
    method: 'delete'
  })
}

export function getAllocationList(params) {
  return request({
    url: '/logistics/allocation/page',
    method: 'get',
    params
  })
}

export function applyAllocation(data) {
  return request({
    url: '/logistics/allocation',
    method: 'post',
    data
  })
}

export function approveAllocation(data) {
  const { id, status } = data
  return request({
    url: `/logistics/allocation/approve/${id}`,
    method: 'put',
    params: { status }
  })
}
