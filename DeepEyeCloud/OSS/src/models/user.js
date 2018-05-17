/* global window */
import modelExtend from 'dva-model-extend'
import { config } from 'utils'
import { routerRedux } from 'dva/router'
import { create, remove, update } from 'services/user'
import * as usersService from 'services/users'
import { pageModel } from './common'

const { query } = usersService
const { prefix } = config

export default modelExtend(pageModel, {
  namespace: 'user',

  state: {
    userListParam:{
      name: "",
      stationId: JSON.parse(localStorage.getItem("userInfo")).policeStationId,
      page: 1,
      pageSize: 10,
    },
    currentItem: {},
    modalVisible: false,
    modalType: 'create',
    selectedRowKeys: [],
    isMotion: window.localStorage.getItem(`${prefix}userIsMotion`) === 'true',
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen((location) => {
        if (location.pathname === '/user') {
          dispatch({
            type: 'query',
            payload: location.query,
          })
        }
      })
    },
  },

  effects: {

    * query ({ payload = {} }, { select, call, put }) {
      console.log("用户---",payload)
      const { userListParam } = yield select(_ => _.user)
      const param = {...userListParam,...payload,};
      const data = yield call(query, param)
      if (data && data.errCode==0) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data,
            userListParam: param,
            pagination: {
              current: Number(param.page) || 1,
              pageSize: Number(param.pageSize) || 10,
              total: data.total,
            },
          },
        })
      } else if(data.statusCode==401){
        yield put(routerRedux.push('/login'));
      }else{
        throw data.data||"系统错误！"
      }
    },

    * delete ({ payload }, { call, put, select }) {
      const data = yield call(remove, { id: payload })
      const { selectedRowKeys } = yield select(_ => _.user)
      if (data && data.errCode==0) {
        yield put({ type: 'updateState', payload: { selectedRowKeys: selectedRowKeys.filter(_ => _ !== payload) } })
        yield put({ type: 'query' })
      } else if(data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * multiDelete ({ payload }, { call, put }) {
      const data = yield call(usersService.remove, payload)
      if (data.success && data.errCode==0) {
        yield put({ type: 'updateState', payload: { selectedRowKeys: [] } })
        yield put({ type: 'query' })
      } else if(data.statusCode==401){
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
      } else if (data.errCode==1001){
        throw "登录账号已存在请重新输入！"
      } else if(data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * update ({ payload }, { select, call, put }) {
      const id = yield select(({ user }) => user.currentItem.id)
      const newUser = { ...payload, id }
      const data = yield call(update, newUser)
      if (data.success && data.errCode==0) {
        yield put({ type: 'hideModal' })
        yield put({ type: 'query' })
      } else if(data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

  },

  reducers: {

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
