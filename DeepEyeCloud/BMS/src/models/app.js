/* global window */
/* global document */
/* global location */
import { routerRedux } from 'dva/router'
import { parse } from 'qs'
import config from 'config'
import { EnumRoleType } from 'enums'
import { query, logout } from 'services/app'
import * as menusService from 'services/menus'

const { prefix } = config

export default {
  namespace: 'app',
  state: {
    user: {},
    permissions: {
      visit: [],
    },
    menu: [
            {
              id: '1',
              icon: 'home',
              name: '产品与服务',
            },
            {
              id: '11',
              bpid: '1',
              mpid: '1',
              name: '产品与服务列表',
              route: '/product',
            },
            {
              id: '12',
              bpid: '1',
              mpid: '1',
              name: '人员布控',
              route: '/monitor',
            },
            {
              id: '13',
              bpid: '1',
              mpid: '1',
              name: '人流统计',
              route: '/faceid',
            },
            {
              id: '2',
              name: '店铺管理',
              icon: 'shop',
              route: '/storeManage',
            },
            {
              id: '21',
              mpid: '-1',
              bpid: '2',
              name: '详细信息',
              route: '/store/:id',
            },
            {
              id: '3',
              name: '设备管理',
              icon: 'setting',
              route: '/device',
            },
            {
              id: '4',
              name: '账号信息',
              icon: 'message',
              route: '/accountMsg',
            },
          ],
    menuPopoverVisible: false,
    siderFold: window.localStorage.getItem(`${prefix}siderFold`) === 'true',
    darkTheme: window.localStorage.getItem(`${prefix}darkTheme`) === 'true',
    isNavbar: document.body.clientWidth < 769,
    navOpenKeys: JSON.parse(window.localStorage.getItem(`${prefix}navOpenKeys`)) || [],
    locationPathname: '',
    locationQuery: {},
  },
  subscriptions: {

    setupHistory ({ dispatch, history }) {
      history.listen((location) => {
        dispatch({
          type: 'updateState',
          payload: {
            locationPathname: location.pathname,
            locationQuery: location.query,
          },
        })
      })
    },

    setup ({ dispatch }) {
      dispatch({ type: 'query' })
      let tid
      window.onresize = () => {
        clearTimeout(tid)
        tid = setTimeout(() => {
          dispatch({ type: 'changeNavbar' })
        }, 300)
      }
    },

  },
  effects: {

    * query ({
      payload,
    }, { call, put, select }) {
      if(localStorage.getItem('token')){
        yield put({
          type: 'updateState',
          payload: {
            
          },
        })
      }else{
        yield put(routerRedux.push({
          pathname: '/login',
        }))
      }
    },

    * logout ({
      payload,
    }, { call, put }) {
      const data = yield call(logout, parse(payload))
      if (data.success && data.errCode==0) {
        localStorage.removeItem('token');
        yield put(routerRedux.push({
            pathname: '/login',
          }))
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        localStorage.removeItem('token');
        yield put(routerRedux.push({
            pathname: '/login',
          }))
      }
    },

    * changeNavbar (action, { put, select }) {
      const { app } = yield (select(_ => _))
      const isNavbar = document.body.clientWidth < 769
      if (isNavbar !== app.isNavbar) {
        yield put({ type: 'handleNavbar', payload: isNavbar })
      }
    },

  },
  reducers: {
    updateState (state, { payload }) {
      return {
        ...state,
        ...payload,
      }
    },

    switchSider (state) {
      window.localStorage.setItem(`${prefix}siderFold`, !state.siderFold)
      return {
        ...state,
        siderFold: !state.siderFold,
      }
    },

    switchTheme (state) {
      window.localStorage.setItem(`${prefix}darkTheme`, !state.darkTheme)
      return {
        ...state,
        darkTheme: !state.darkTheme,
      }
    },

    switchMenuPopver (state) {
      return {
        ...state,
        menuPopoverVisible: !state.menuPopoverVisible,
      }
    },

    handleNavbar (state, { payload }) {
      return {
        ...state,
        isNavbar: payload,
      }
    },

    handleNavOpenKeys (state, { payload: navOpenKeys }) {
      return {
        ...state,
        ...navOpenKeys,
      }
    },
  },
}
