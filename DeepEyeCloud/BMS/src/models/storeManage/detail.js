import pathToRegexp from 'path-to-regexp'
import { routerRedux } from 'dva/router'
import { query,queryDevices,removeDevice,queryStores,createDevice,updateDevice } from '../../services/storeServices'

export default {

  namespace: 'storeDetail',

  state: {
    storeInfo: {},
    currentItem: {},
    devicesData:{},
    modalVisible: false,
    modalType: 'create',
    storeList: [],
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen(({ pathname }) => {
        console.log("跳转了");
        const match = pathToRegexp('/store/:id').exec(pathname)
        if (match) {
          dispatch({ type: 'queryInfo', payload: { id: match[1] } })
          dispatch({ type: 'queryDevices', payload: {searchName:"",areaIds: match[1],page: 1,pageSize: 10000000}})
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
        console.log("详细信息--",other);
        yield put({
          type: 'querySuccess',
          payload: {
            storeInfo: other,
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
      const storeList = yield call(queryStores,{userId:JSON.parse(localStorage.getItem("userInfo")).id,page: 1,pageSize: 10000000});
      console.log("执行了设备列表",payload);
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
      console.log("执行了删除设备",payload);
      const { storeInfo } = yield select(_=>_.storeDetail);
      const data = yield call(removeDevice, payload)
      const { success, message, status, ...other } = data
      if (success && data.errCode==0) {
        yield put({
          type: 'queryDevices',
          payload: { searchName:"",areaIds: storeInfo.data.id,page: 1,pageSize: 10000000 },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * create ({ payload }, { select, call, put  }) {
      console.log("执行了创建设备",payload);
      const { storeInfo } = yield select(_=>_.storeDetail);
      const data = yield call(createDevice, payload)
      if (data.success && data.errCode==0) {
        yield put({ type: 'hideModal' })
        yield put({ 
          type: 'queryDevices',
          payload: { searchName:"",areaIds: storeInfo.data.id,page: 1,pageSize: 10000000 },
        })
      } else if (data.statusCode==401){
        yield put(routerRedux.push('/login'));
      } else {
        throw data.data||"系统错误！"
      }
    },

    * update ({ payload }, { select, call, put }) {
      const { storeInfo } = yield select(_=>_.storeDetail);
      const data = yield call(updateDevice, payload)
      if (data.success && data.errCode==0) {
        yield put({ type: 'hideModal'})
        yield put({ 
          type: 'queryDevices',
          payload: { searchName:"",areaIds: storeInfo.data.id,page: 1,pageSize: 10000000 },
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
      console.log("状态--",state,payload);
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
