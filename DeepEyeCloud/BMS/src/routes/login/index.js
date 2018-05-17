import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import { Button, Icon, Row, Form, Input, Checkbox } from 'antd'
import md5 from "react-native-md5";
import { config } from 'utils'
import styles from './index.less'

const FormItem = Form.Item

const Login = ({
  login,
  loading,
  dispatch,
  form: {
    getFieldDecorator,
    validateFieldsAndScroll,
  },
}) => {

  const { isError, errorMsg } = login;

  if(isError){
    let timer = setTimeout(function(){
      dispatch({ type: 'login/hideError', payload: { errorMsg: '' } })
      clearTimeout(timer);
    },3000);
  }

  function handleOk () {
    validateFieldsAndScroll((errors, values) => {
      if (errors) {
        return
      }
      const password = md5.hex_md5(values.password);
      console.log("password---",values.password,"password--md-",password);
      values = {...values,...{"grant_type":"password","scope":"read write","client_secret":"123456","client_id":"clientapp","password":password}};
      dispatch({ type: 'login/login', payload: values })
    })
  }

  return (
    <div className={styles['oos-wrap']}>
      <div className={styles['oos-loginWrap']}>
        <div className={styles['oos-title']}>慧眼云BMS管理系统</div>
        <div className={styles['oos-bg']}></div>
        <div id="loginModule" className={styles['oos-login']}>
          <h2 className={styles['oos-login-title']}>系统登录</h2>
          <Form className={styles["oos-login-form"]}>
            <FormItem>
              {getFieldDecorator('username', {
                rules: [{ required: true, message: '请输入用户名!' }],
              })(
                <Input style={{ height: 42 }} prefix={<Icon type="user" style={{ fontSize: 16 }} />} placeholder="用户名"  onPressEnter={handleOk} />
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('password', {
                rules: [{ required: true, message: '请输入登录密码!' }],
              })(
                <Input style={{ height: 42 }} prefix={<Icon type="lock" style={{ fontSize: 16 }} />} type="password" placeholder="密码"  onPressEnter={handleOk} />
              )}
            </FormItem>
            <FormItem>
              {/*getFieldDecorator('remember', {
                valuePropName: 'checked',
                initialValue: false,
              })(
                <Checkbox>记住密码</Checkbox>
              )*/}
                <Checkbox>记住密码</Checkbox>
              <Button style={{ height: 42 }} type="primary" className={styles['oos-login-form-button']} onClick={handleOk} loading={loading.effects.login}>
                登 录
              </Button>
            </FormItem>
            <div className={styles['errorTips']}>{isError && errorMsg}</div>
          </Form>
        </div>
      </div>
      <p className={styles['oos-copyright']}>&copy; 2017 intellif.com 版权所有</p>
      <div className={styles['oos-qrCode']}>
        <img src="icon/qr_code.png" alt="" />
        <p>扫码下载Android APP</p>
      </div>
    </div>
  )
}

Login.propTypes = {
  form: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
  login: PropTypes.object,
}

export default connect(({ login, loading }) => ({ login, loading }))(Form.create()(Login))