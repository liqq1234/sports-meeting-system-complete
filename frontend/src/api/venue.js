import request from '@/utils/request'

export function getVenuePage(params) {
  return request({ url: '/sports/venue/page', method: 'get', params })
}

export function getVenueById(id) {
  return request({ url: `/sports/venue/${id}`, method: 'get' })
}

export function getVenueList() {
  return request({ url: '/sports/venue/list', method: 'get' })
}

export function addVenue(data) {
  return request({ url: '/sports/venue', method: 'post', data })
}

export function updateVenue(data) {
  return request({ url: '/sports/venue', method: 'put', data })
}

export function deleteVenue(id) {
  return request({ url: `/sports/venue/${id}`, method: 'delete' })
}