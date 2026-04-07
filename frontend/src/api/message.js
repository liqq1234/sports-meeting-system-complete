import request from '@/utils/request'

export function getMessagePage(params) {
  return request({ url: '/sports/message/page', method: 'get', params })
}

export function countUnread() {
  return request({ url: '/sports/message/unreadCount', method: 'get' })
}

export function markAsRead(id) {
  return request({ url: `/sports/message/markAsRead/${id}`, method: 'put' })
}

export function markAllAsRead() {
  return request({ url: '/sports/message/markAllAsRead', method: 'put' })
}

export function deleteMessage(id) {
  return request({ url: `/sports/message/${id}`, method: 'delete' })
}