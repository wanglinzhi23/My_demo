import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, InputNumber, Radio, Modal, Cascader } from 'antd'
import md5 from "react-native-md5";
import city from '../../utils/city'

const FormItem = Form.Item

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
}

const modal = ({item = {},onOk,form: {getFieldDecorator,validateFields,getFieldsValue,getFieldValue,},...modalProps}) => {
  const handleOk = () => {
    validateFields((errors) => {
      if (errors) {
        return
      }
      const data = {...getFieldsValue()}

      //密码有改动则修改，否则不修改，使用默认密码“Intellif069aaa”是为了规避密文符合校验问题
      if(data.password === "Intellif069aaa"){
        const { password,passwordAgain, ...userInfos } = data;
        onOk(userInfos)
      }else{
        data.password = md5.hex_md5(data.password);
        const { passwordAgain, ...userInfos } = data;
        onOk(userInfos)
      }
    })
  }
  const checkPassword = (rule, value, callback) => {
    if (value && value !== getFieldValue('password')) {
      callback('两次密码输入不一致！');
    } else {
      callback();
    }
  }

  const checkConfirm = (rule, value, callback) => {
    if (value) {
      validateFields(['passwordAgain'], { force: true });
    }
    callback();
  }

  const modalOpts = {
    ...modalProps,
    onOk: handleOk,
  }

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <FormItem label="公司名称" hasFeedback {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: item.name,
            rules: [
              {
                required: true,
                message: '请输入16个字以内的公司名称!',
                max: 16,
              },
            ],
          })(<Input placeholder="请输入公司名称" />)}
        </FormItem>
        <FormItem label="登录账号" hasFeedback {...formItemLayout}>
          {getFieldDecorator('login', {
            initialValue: item.login,
            rules: [
              {
                required: true,
                message: '请输入64字以内的账号!',
                max: 64,
              },
            ],
          })(<Input placeholder="请输入账号" />)}
        </FormItem>
        <FormItem label="密码" hasFeedback {...formItemLayout}>
          {getFieldDecorator('password', {
            initialValue: item.password,
            rules: [
              {
                required: true,
                pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{4,}$/,
                message: '请输入包含至少1个大写字母，1个小写字母，1个数字且不包含特殊字符的4位数以上密码!',
              },
              {
                validator: checkConfirm,
              }
            ],
          })(
            <Input type="password" placeholder="密码" />
          )}
        </FormItem>
        <FormItem label="确认密码" hasFeedback {...formItemLayout}>
          {getFieldDecorator('passwordAgain', {
            initialValue: item.password,
            rules: [
              {
                required: true,
                message: '请再次输入密码!',
              },
              {
                validator: checkPassword,
              }
            ],
          })(<Input type="password" placeholder="密码" />)}
        </FormItem>
        <FormItem label="联系方式" hasFeedback {...formItemLayout}>
          {getFieldDecorator('mobile', {
            initialValue: item.mobile,
            rules: [
              {
                required: false,
                // pattern: /^((0\d{2,3}-\d{7,8})|(1[34578]\d{9}))$/,
                message: '请输入联系方式!',
              },
            ],
          })(<Input placeholder="请输入联系方式" />)}
        </FormItem>
        <FormItem label="E-mail" hasFeedback {...formItemLayout}>
          {getFieldDecorator('email', {
            initialValue: item.email,
            rules: [
              {
                required: false,
                pattern: /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/,
                max:64,
                message: '请输入正确的邮箱!',
              },
            ],
          })(<Input placeholder="请输入邮箱" />)}
        </FormItem>
        <FormItem label="备注" hasFeedback {...formItemLayout}>
          {getFieldDecorator('remark', {
            initialValue: item.remark,
            rules: [
              {
                required: false,
                message: '请输入64字以内的备注!',
                max: 64,
              },
            ],
          })(<Input type="textarea" placeholder="请输入备注" />)}
        </FormItem>
      </Form>
    </Modal>
  )
}

modal.propTypes = {
  form: PropTypes.object.isRequired,
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default Form.create()(modal)
