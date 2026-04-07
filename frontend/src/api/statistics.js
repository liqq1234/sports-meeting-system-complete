import request from '@/utils/request'

export function getDashboard(meetingId) {
  return request({ url: '/sports/stats/dashboard', method: 'get', params: { meetingId } })
}

export function getOverview() {
  return request({ url: '/sports/stats/overview', method: 'get' })
}
