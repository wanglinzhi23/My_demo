const APIIntellif = '/api/intellif'
const APIV2 = '/api'

module.exports = {
  name: '慧眼云OSS管理系统',
  prefix: '慧眼云OSS管理系统',
  footerText: '慧眼云OSS管理系统  © 2017 intellif',
  logo: '/icon_logo.png',
  iconFontCSS: '/iconfont.css',
  iconFontJS: '/iconfont.js',
  CORS: [],
  openPages: ['/login'],
  baseURL: 'http://39.108.169.236:8083',
  apiPrefix: '/api/intellif',
  APIIntellif,
  APIV2,
  api: {
    userLogin: `${APIV2}/oauth/token`,
    userLogout: `${APIIntellif}/server/logoff`,
    userRight: `${APIIntellif}/user/right/:id`,
    userInfo: `${APIIntellif}/userInfo`,
    user: `${APIIntellif}/user/:id`,
    users: `${APIIntellif}/user/query`,
    store: `${APIIntellif}/area/:id`,
    stores: `${APIIntellif}/area/query`,
    device: `${APIIntellif}/camera/:id`,
    devices: `${APIIntellif}/camera/query`,
    posts: `${APIIntellif}/posts`,
    dashboard: `${APIIntellif}/dashboard`,
    menus: `${APIIntellif}/menus`,
  },
}
