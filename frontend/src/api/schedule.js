import request from '@/utils/request'

export function getSchedulePage(params) {
  return request({ url: '/sports/schedule/page', method: 'get', params })
}

export function getScheduleById(id) {
  return request({ url: `/sports/schedule/${id}`, method: 'get' })
}

export function addSchedule(data) {
  return request({ url: '/sports/schedule', method: 'post', data })
}

export function updateSchedule(data) {
  return request({ url: '/sports/schedule', method: 'put', data })
}

export function deleteSchedule(id) {
  return request({ url: `/sports/schedule/${id}`, method: 'delete' })
}

export function updateScheduleStatus(id, status) {
  return request({ url: `/sports/schedule/status/${id}`, method: 'put', params: { status } })
}

export function autoGenerate(meetingId) {
  return request({ url: `/sports/schedule/autoGenerate/${meetingId}`, method: 'post' })
}

export function getScheduleAthletes(scheduleId) {
  return request({ url: `/sports/schedule/athletes/${scheduleId}`, method: 'get' })
}

export function assignAthletes(scheduleId, userIds) {
  return request({ url: `/sports/schedule/assignAthletes/${scheduleId}`, method: 'post', data: { userIds } })
}