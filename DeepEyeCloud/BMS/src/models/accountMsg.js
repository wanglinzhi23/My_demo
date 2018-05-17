/* global window */
import modelExtend from 'dva-model-extend'
import { routerRedux } from 'dva/router'
import { config } from 'utils'
import { userInfoServer } from 'services/accountMsg'
import { pageModel } from './common'

const { prefix } = config

export default modelExtend(pageModel, {
  namespace: 'account',

  state: {
    userData: {}
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen((location) => {
        if (location.pathname === '/accountMsg') {
          dispatch({
            type: 'query',
            payload: location.query,
          })
        }
      })
    },
  },

  effects: {

    * query ({ payload = {} }, { call, put }) {
      const data = yield call(userInfoServer, {id:JSON.parse(localStorage.getItem('userInfo')).id})
      if (data && data.errCode==0) {
        yield put({
          type: 'querySuccess',
          payload: {
            userData: data.data,
          },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

  },

  reducers: {

    querySuccess (state, { payload }) {
      return { ...state, ...payload, }
    },

  },
})
