import { routerRedux } from 'dva/router'
import { login, userRightRequest } from 'services/login'

export default {
  namespace: 'login',

  state: {
    isError: false,
    errorMsg: "",
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen((location) => {
        if (location.pathname === '/login') {
          /*dispatch({
            type: 'query',
            payload: location.query,
          })*/
          console.log("进入登录页");
        }
      })
    },
  },

  effects: {
    * login ({
      payload,
    }, { put, call, select }) {
      const data = yield call(login, payload)
      const { statusCode } = data;
      const { locationQuery } = yield select(_ => _.app)
      if (data.success && statusCode==200) {
        //保存token
        window.localStorage.setItem('token',data.access_token);
        window.localStorage.setItem('userInfo',JSON.stringify(data.oauth_AIK_user_info));

        const { username } = payload;
        //查看当前用户权限信息
        const userRightData = yield call(userRightRequest, { id: username })
        //不是超级管理员不允许进入OSS系统
        if(userRightData.data.userinfo.roleTypeName != 'SUPER_ADMIN'){
          yield put({ type: 'showError', payload: { errorMsg: '账号或密码输入错误!' } })
          return;
        }

        const { from } = locationQuery
        yield put({ type: 'app/query' })
        if (from && from !== '/login') {
          yield put(routerRedux.push(from))
        } else {
          yield put(routerRedux.push('/user'))
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
