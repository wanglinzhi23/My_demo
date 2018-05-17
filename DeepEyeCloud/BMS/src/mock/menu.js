const { config } = require('./common')

const { apiPrefix } = config
let database = [
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
    id: '12',
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
]

module.exports = {

  [`GET ${apiPrefix}/menus`] (req, res) {
    res.status(200).json(database)
  },
}
