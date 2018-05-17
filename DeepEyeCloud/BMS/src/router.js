import React from 'react'
import PropTypes from 'prop-types'
import { Router } from 'dva/router'
import App from 'routes/app'

const registerModel = (app, model) => {
  if (!(app._models.filter(m => m.namespace === model.namespace).length === 1)) {
    app.model(model)
  }
}

const Routers = function ({ history, app }) {
  const routes = [
    {
      path: '/',
      component: App,
      getIndexRoute (nextState, cb) {
        require.ensure([], (require) => {
          cb(null, require('routes/product/'))
        }, 'product')
      },
      childRoutes: [
        {
          path: 'product',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              cb(null, require('routes/product/'))
            }, 'product')
          },
        },{
          path: 'monitor',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              registerModel(app, require('models/monitor'))
              cb(null, require('routes/monitor/'))
            }, 'monitor')
          },
        }, {
          path: 'faceid',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              registerModel(app, require('models/faceid'))
              cb(null, require('routes/faceid/'))
            }, 'faceid')
          },
        },{
          path: 'storeManage',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              registerModel(app, require('models/storeManage'))
              cb(null, require('routes/storeManage/'))
            }, 'storeManage')
          },
        }, {
          path: 'store/:id',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              registerModel(app, require('models/storeManage/detail'))
              cb(null, require('routes/storeManage/detail/'))
            }, 'storeDetail')
          },
        }, {
          path: 'device',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              registerModel(app, require('models/device'))
              cb(null, require('routes/device/'))
            }, 'device')
          },
        }, {
          path: 'accountMsg',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              registerModel(app, require('models/accountMsg'))
              cb(null, require('routes/accountMsg/'))
            }, 'accountMsg')
          },
        }, {
          path: 'login',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              registerModel(app, require('models/login'))
              cb(null, require('routes/login/'))
            }, 'login')
          },
        }, {
          path: 'request',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              cb(null, require('routes/request/'))
            }, 'request')
          },
        }, {
          path: 'post',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              registerModel(app, require('models/post'))
              cb(null, require('routes/post/'))
            }, 'post')
          },
        }, {
          path: '*',
          getComponent (nextState, cb) {
            require.ensure([], (require) => {
              cb(null, require('routes/error/'))
            }, 'error')
          },
        },
      ],
    },
  ]

  return <Router history={history} routes={routes} />
}

Routers.propTypes = {
  history: PropTypes.object,
  app: PropTypes.object,
}

export default Routers
