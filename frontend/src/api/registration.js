import request from '@/utils/request'

export function getRegistrationPage(params) {
  return request({ url: '/sports/registration/page', method: 'get', params })
}

export function getMyRegistrations(params) {
  return request({ url: '/sports/registration/my', method: 'get', params })
}

export function enroll(eventId, remark) {
  return request({ url: `/sports/registration/enroll/${eventId}`, method: 'post', params: { remark } })
}

export function cancelRegistration(id) {
  return request({ url: `/sports/registration/cancel/${id}`, method: 'delete' })
}

export function reviewRegistration(data) {
  return request({ url: `/sports/registration/review/${data.id}`, method: 'put', params: { status: data.status, rejectReason: data.rejectReason } })
}

export function batchReview(data) {
  return request({ url: '/sports/registration/batchReview', method: 'put', data })
}

export function getRegistrationStats(meetingId) {
  return request({ url: `/sports/registration/stats/${meetingId}`, method: 'get' })
}

export function getCollegeRegistrationStats(meetingId) {
  return request({ url: `/sports/registration/collegeStats/${meetingId}`, method: 'get' })
}

export function withdrawForMedical(params) {
  return request({ url: '/sports/registration/withdraw', method: 'post', params })
}