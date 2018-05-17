import { request, config } from 'utils'

const { api } = config;
const { monitors } = api;

export async function queryMonitors (params) {
  return request({
    url: monitors,
    method: 'post',
    data: params,
  })
}

