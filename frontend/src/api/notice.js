import request from '@/utils/request'

export function getNoticePage(params) {
  return request({ url: '/sports/notice/page', method: 'get', params })
}

export function getNoticeById(id) {
  return request({ url: `/sports/notice/${id}`, method: 'get' })
}

export function addNotice(data) {
  return request({ url: '/sports/notice', method: 'post', data })
}

export function updateNotice(data) {
  return request({ url: '/sports/notice', method: 'put', data })
}

export function deleteNotice(id) {
  return request({ url: `/sports/notice/${id}`, method: 'delete' })
}

export function publishNotice(id) {
  return request({ url: `/sports/notice/publish/${id}`, method: 'put' })
}