/* global window */
import modelExtend from 'dva-model-extend'
import { routerRedux } from 'dva/router'
import { config } from 'utils'
import { queryList } from 'services/storeServices'
import { queryMonitors } from 'services/monitorServices'

const { prefix } = config

export default {
  namespace: 'monitor',

  state: {
    storeList:[],
    monitorList:[],
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen((location) => {
        if (location.pathname === '/monitor') {
          dispatch({
            type: 'query',
            payload: location.query,
          })
          dispatch({
            type: 'queryMonitor',
            payload: location.query,
          })
        }
      })
    },
  },

  effects: {

    * query ({ payload = {} }, { call, put }) {
      const data = yield call(queryList, {...payload,...{userId:JSON.parse(localStorage.getItem("userInfo")).id,page: 1,pageSize: 10000000}});
      if (data && data.errCode==0) {
        yield put({
          type: 'querySuccess',
          payload: {
            storeList: data.data,
          },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * queryMonitor ({ payload = {} }, { call, put }) {
      console.log("请求布控---",payload)
      const { param } = payload;
      const data = yield call(queryMonitors, {...{"areaIds":"","queryText":"","page":1,"pageSize":1000000000},...param,})
      if (data && data.errCode==0) {
        yield put({
          type: 'querySuccess',
          payload: {
            ...payload,
            monitorList: data.data,
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

    showModal (state, { payload }) {
      return { ...state, ...payload, modalVisible: true }
    },
    querySuccess (state, { payload }) {
      // const {  storeList,monitorList, } = payload
      return {
        ...state,
        ...payload
      }
    },

  },
}
