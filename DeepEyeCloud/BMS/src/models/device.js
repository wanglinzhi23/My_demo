/* global window */
import modelExtend from 'dva-model-extend'
import { routerRedux } from 'dva/router'
import { config } from 'utils'
import { create, remove, update , queryList, removeList, queryStore } from 'services/deviceServices'
import { pageModel } from './common'

const { prefix } = config

export default modelExtend(pageModel, {
  namespace: 'device',

  state: {
    deviceListParam:{
      searchName: "",
      areaIds: "",
      page: "1",
      pageSize: "10",
    },
    currentItem: {},
    modalVisible: false,
    modalType: 'create',
    selectedRowKeys: [],
    isMotion: window.localStorage.getItem(`${prefix}userIsMotion`) === 'true',
    storeList: [],
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen((location) => {
        if (location.pathname === '/device') {
          if(JSON.stringify(location.query) == "{}"){//判断是否为空对象
            dispatch({
              type: 'query',
              payload: {searchName: "",areaIds: "",page: "1",pageSize: "10",},
            })
          }else{
            dispatch({
              type: 'query',
              payload: location.query,
            })
          }
          /*dispatch({
            type: 'query',
            payload: location.query,
          })*/
        }
      })
    },
  },

  effects: {

    * query ({ payload = {} }, { select, call, put }) {
      //店铺列表接口
      const storeList = yield call(queryStore,{userId:JSON.parse(localStorage.getItem("userInfo")).id,page: 1,pageSize: 10000000});
      console.log("storeList--",storeList);
      if(storeList && storeList.errCode==0){
        yield put({
          type: 'queryStoreSuccess',
          payload: {
            storeList: storeList.data
          },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }

      //设备列表接口
      const { deviceListParam } = yield select(_ => _.device)
      const param = {...deviceListParam,...payload,};
      console.log("param--",deviceListParam,param);
      const data = yield call(queryList, param)
      if (data && data.errCode==0) {
        yield put({
          type: 'querySuccess',
          payload: {
            storeList: storeList.data,
            list: data.data,
            deviceListParam: param,
            pagination: {
              current: Number(param.page) || 1,
              pageSize: Number(param.pageSize) || 10,
              total: data.total,
            },
          },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * delete ({ payload }, { call, put, select }) {
      const data = yield call(remove, { id: payload })
      const { selectedRowKeys } = yield select(_ => _.device)
      if (data.success && data.errCode==0) {
        yield put({ type: 'updateState', payload: { selectedRowKeys: selectedRowKeys.filter(_ => _ !== payload) } })
        yield put({ type: 'query' })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * multiDelete ({ payload }, { call, put }) {
      const data = yield call(removeList, payload)
      if (data.success && data.errCode==0) {
        yield put({ type: 'updateState', payload: { selectedRowKeys: [] } })
        yield put({ type: 'query' })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * create ({ payload }, { call, put }) {
      const data = yield call(create, payload)
      if (data.success && data.errCode==0) {
        yield put({ type: 'hideModal' })
        yield put({ type: 'query' })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * update ({ payload }, { select, call, put }) {
      const id = yield select(({ device }) => device.currentItem.id)
      const newDevice = { ...payload, id }
      const data = yield call(update, newDevice)
      if (data.success && data.errCode==0) {
        yield put({ type: 'hideModal' })
        yield put({ type: 'query' })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

  },

  reducers: {

    queryStoreSuccess (state, { payload }) {
      return { ...state, ...payload }
    },

    showModal (state, { payload }) {
      return { ...state, ...payload, modalVisible: true }
    },

    hideModal (state) {
      return { ...state, modalVisible: false }
    },

    switchIsMotion (state) {
      window.localStorage.setItem(`${prefix}userIsMotion`, !state.isMotion)
      return { ...state, isMotion: !state.isMotion }
    },

  },
})
