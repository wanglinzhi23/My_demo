import pathToRegexp from 'path-to-regexp'
import { query,queryDevices,removeDevice,queryStores,createDevice,updateDevice } from '../../services/user'

export default {

  namespace: 'userDetail',

  state: {
    userInfo: {},
    devicesData:{},
    modalVisible: false,
    modalType: 'create',
    storeList: [],
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen(({ pathname }) => {
        const match = pathToRegexp('/user/:id').exec(pathname)
        if (match) {
          dispatch({ type: 'queryInfo', payload: { id: match[1] } })
          dispatch({ type: 'queryDevices', payload: {userId: match[1],searchName:"",areaIds:"",page: 1,pageSize: 10000000}})
        }
      })
    },
  },

  effects: {
    * queryInfo ({
      payload,
    }, { call, put }) {
      const data = yield call(query, payload)
      const { success, message, status, ...other } = data
      if (success && data.errCode==0) {
        yield put({
          type: 'querySuccess',
          payload: {
            userInfo: other,
          },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },
    * queryDevices ({
      payload,
    }, { call, put }) {
      const { userId } = payload;
      const storeList = yield call(queryStores,{userId:userId,page: 1,pageSize: 10000000});
      const data = yield call(queryDevices, payload)
      const { success, message, status, ...other } = data
      if (success && data.errCode==0) {
        yield put({
          type: 'querySuccess',
          payload: {
            devicesData: other,
            storeList: storeList.data
          },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * removeDevice ({
      payload,
    }, { select, call, put }) {
      const userInfo = yield select(({ userDetail }) => userDetail.userInfo);
      const data = yield call(removeDevice, payload)
      const { success, message, status, ...other } = data
      if (success && data.errCode==0) {
        yield put({ 
          type: 'queryInfo',
          payload: { id: userInfo.data.id },
        })
        yield put({
          type: 'queryDevices',
          payload: { userId:userInfo.data.id,searchName:"",areaIds:"",page: 1,pageSize: 10000000 },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * create ({ payload }, { select, call, put }) {
      const userInfo = yield select(({ userDetail }) => userDetail.userInfo);
      const data = yield call(createDevice, payload)
      if (data.success && data.errCode==0) {
        yield put({ type: 'hideModal' })
        yield put({ 
          type: 'queryInfo',
          payload: { id: userInfo.data.id },
        })
        yield put({ 
          type: 'queryDevices',
          payload: { userId:userInfo.data.id,searchName:"",areaIds:"",page: 1,pageSize: 10000000 },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    /** update ({ payload }, { select, call, put }) {
      const userInfo = yield select(({ userDetail }) => userDetail.userInfo);
      if (data.success && data.errCode==0) {
        yield put({ type: 'hideModal'})
        yield put({ 
          type: 'queryDevices',
          payload: { userId:userInfo.data.id,searchName:"",areaIds:"",page: 1,pageSize: 10000000 },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },*/
  },

  reducers: {
    querySuccess (state, { payload }) {
      return {
        ...state,
        ...payload,
      }
    },
    showModal (state, { payload }) {
      return { ...state, ...payload, modalVisible: true }
    },

    hideModal (state) {
      return { ...state, modalVisible: false }
    },
  },
}
