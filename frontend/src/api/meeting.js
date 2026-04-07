import request from '@/utils/request'

export function getMeetingPage(params) {
  return request({ url: '/sports/meeting/page', method: 'get', params })
}

export function getMeetingList() {
  return request({ url: '/sports/meeting/list', method: 'get' })
}

export function getMeetingById(id) {
  return request({ url: `/sports/meeting/${id}`, method: 'get' })
}

export function addMeeting(data) {
  return request({ url: '/sports/meeting', method: 'post', data })
}

export function updateMeeting(data) {
  return request({ url: '/sports/meeting', method: 'put', data })
}

export function deleteMeeting(id) {
  return request({ url: `/sports/meeting/${id}`, method: 'delete' })
}

export function updateMeetingStatus(id, status) {
  return request({ url: `/sports/meeting/status/${id}`, method: 'put', params: { status } })
}