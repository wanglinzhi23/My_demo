import { request, config } from 'utils'

const { api } = config;
const { device, devices, store, stores } = api;

export async function query (params) {
  return request({
    url: store,
    method: 'get',
    data: params,
  })
}

export async function create (params) {
  return request({
    url: store.replace('/:id', ''),
    method: 'post',
    data: params,
  })
}

export async function remove (params) {
  return request({
    url: store,
    method: 'delete',
    data: params,
  })
}

export async function update (params) {
  return request({
    url: store,
    method: 'put',
    data: params,
  })
}


export async function queryList (params) {
  return request({
    url: stores,
    method: 'post',
    data: params,
  })
}

export async function removeList (params) {
  return request({
    url: stores,
    method: 'delete',
    data: params,
  })
}

export async function queryDevices (params) {
  return request({
    url: devices,
    method: 'post',
    data: params,
  })
}

export async function removeDevice (params) {
  return request({
    url: device,
    method: 'delete',
    data: params,
  })
}

export async function queryStores (params) {
  return request({
    url: stores,
    method: 'post',
    data: params,
  })
}

export async function createDevice (params) {
  return request({
    url: device.replace('/:id', ''),
    method: 'post',
    data: params,
  })
}

export async function updateDevice (params) {
  return request({
    url: device,
    method: 'put',
    data: params,
  })
}
