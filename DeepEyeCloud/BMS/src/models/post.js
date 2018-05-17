import modelExtend from 'dva-model-extend'
import { routerRedux } from 'dva/router'
import { query } from 'services/posts'
import { pageModel } from 'models/common'

export default modelExtend(pageModel, {

  namespace: 'post',

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen((location) => {
        if (location.pathname === '/post') {
          dispatch({ type: 'query',
            payload: {
              status: 2,
              ...location.query,
            } })
        }
      })
    },
  },

  effects: {
    * query ({
      payload,
    }, { call, put }) {
      const data = yield call(query, payload)
      if (data.success && data.errCode==0) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data,
            pagination: {
              current: Number(payload.page) || 1,
              pageSize: Number(payload.pageSize) || 10,
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
  },
})
