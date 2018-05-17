import { routerRedux } from 'dva/router'
import { login } from 'services/login'

export default {
  namespace: 'login',

  state: {
    isError: false,
    errorMsg: "",
  },

  effects: {
    * login ({
      payload,
    }, { put, call, select }) {
      const data = yield call(login, payload)
      const { statusCode } = data;
      const { locationQuery } = yield select(_ => _.app)
      if (data.success && statusCode==200) {
        window.localStorage.setItem('token',data.access_token);
        window.localStorage.setItem('userInfo',JSON.stringify(data.oauth_AIK_user_info));
        const { from } = locationQuery
        yield put({ type: 'app/query' })
        if (from && from !== '/login') {
          yield put(routerRedux.push(from))
        } else {
          yield put(routerRedux.push('/product'))
        }
      } else if(statusCode == 0) {
          yield put({ type: 'showError', payload: { errorMsg: '系统维护中，请稍后再试!' } })
      }else{
          yield put({ type: 'showError', payload: { errorMsg: '账号或密码输入错误!' } })
      }
    },
  },

  reducers: {

    showError (state, { payload }) {
      return { ...state, ...payload, isError: true }
    },

    hideError (state) {
      return { ...state, isError: false }
    },

  },

}
