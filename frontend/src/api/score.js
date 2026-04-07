import request from '@/utils/request'

export function getScorePage(params) {
  return request({ url: '/sports/score/page', method: 'get', params })
}

export function getMyScores(params) {
  return request({ url: '/sports/score/my', method: 'get', params })
}

export function recordScore(data) {
  return request({ url: '/sports/score/record', method: 'post', data })
}

export function batchRecord(data) {
  return request({ url: '/sports/score/batchRecord', method: 'post', data })
}

export function confirmScore(id) {
  return request({ url: `/sports/score/confirm/${id}`, method: 'put' })
}

export function publishScore(eventId) {
  return request({ url: `/sports/score/publish/${eventId}`, method: 'put' })
}

export function getEventScores(eventId) {
  return request({ url: `/sports/score/list/${eventId}`, method: 'get' })
}

export function calculateRanking(eventId) {
  return request({ url: `/sports/score/publish/${eventId}`, method: 'put' })
}

export function getScoreDistribution(meetingId) {
  return request({ url: `/sports/score/stats/distribution/${meetingId}`, method: 'get' })
}

export function getCollegeRanking(meetingId) {
  return request({ url: `/sports/score/stats/collegeRanking/${meetingId}`, method: 'get' })
}

export function getTopAthletes(params) {
  return request({ url: `/sports/score/stats/topAthletes/${params.meetingId}`, method: 'get', params })
}