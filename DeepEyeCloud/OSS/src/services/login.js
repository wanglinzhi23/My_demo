import { request, config } from 'utils'

const { api } = config
const { userLogin, userRight } = api

export async function login (data) {
  return request({
    url: userLogin,
    method: 'form',
    data,
  })
}

export async function userRightRequest (params) {
  return request({
    url: userRight,
    method: 'get',
    data: params,
  })
}