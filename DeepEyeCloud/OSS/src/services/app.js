import { request, config } from 'utils'

const { api } = config
const { users, userLogout, userLogin } = api

export async function login (params) {
  return request({
    url: userLogin,
    method: 'post',
    data: params,
  })
}

export async function logout (params) {
  return request({
    url: userLogout,
    method: 'get',
    data: params,
  })
}


export async function query (params) {
  return request({
    url: users,
    method: 'post',
    data: params,
  })
}