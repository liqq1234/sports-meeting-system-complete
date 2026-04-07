import request from '@/utils/request'

export function getUserPage(params) {
  return request({ url: '/auth/user/page', method: 'get', params })
}

export function getUserById(id) {
  return request({ url: `/auth/user/${id}`, method: 'get' })
}

export function addUser(data) {
  return request({ url: '/auth/user', method: 'post', data })
}

export function updateUser(data) {
  return request({ url: '/auth/user', method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/auth/user/${id}`, method: 'delete' })
}

export function resetPassword(id) {
  return request({ url: `/auth/user/resetPassword/${id}`, method: 'put' })
}

export function updateStatus(id, status) {
  return request({ url: `/auth/user/status/${id}`, method: 'put', params: { status } })
}

export function updateProfile(data) {
  return request({ url: '/auth/user/profile', method: 'put', data })
}

export function changePassword(data) {
  return request({ url: '/auth/user/password', method: 'put', data })
}
