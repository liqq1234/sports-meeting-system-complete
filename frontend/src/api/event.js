import request from '@/utils/request'

export function getEventPage(params) {
  return request({ url: '/sports/event/page', method: 'get', params })
}

export function getEventById(id) {
  return request({ url: `/sports/event/${id}`, method: 'get' })
}

export function getEventsByMeeting(meetingId) {
  return request({ url: `/sports/event/list/${meetingId}`, method: 'get' })
}

export function addEvent(data) {
  return request({ url: '/sports/event', method: 'post', data })
}

export function updateEvent(data) {
  return request({ url: '/sports/event', method: 'put', data })
}

export function deleteEvent(id) {
  return request({ url: `/sports/event/${id}`, method: 'delete' })
}

export function updateEventStatus(id, status) {
  return request({ url: `/sports/event/status/${id}`, method: 'put', params: { status } })
}

export function getEventStats(meetingId) {
  return request({ url: `/sports/event/stats/${meetingId}`, method: 'get' })
}