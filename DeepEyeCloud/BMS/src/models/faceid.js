/* global window */
import modelExtend from 'dva-model-extend'
import { routerRedux } from 'dva/router'
import { config } from 'utils'

const { prefix } = config

export default {
  namespace: 'faceid',

  state: {
    storeList:[],
    monitorList:[],
  },


}
