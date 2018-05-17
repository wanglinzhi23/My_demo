import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, InputNumber, Radio, Modal, Cascader } from 'antd'
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

const modal = ({
  item = {},
  onOk,
  form: {
    getFieldDecorator,
    validateFields,
    getFieldsValue,
  },
  ...modalProps
}) => {
  const handleOk = () => {
    validateFields((errors) => {
      if (errors) {
        return
      }
      const data = {
        ...getFieldsValue(),
        // key: item.key,
      }
      console.log("data==",data);
      onOk(data)
    })
  }

  const modalOpts = {
    ...modalProps,
    onOk: handleOk,
  }

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <FormItem label="店铺名称" hasFeedback {...formItemLayout}>
          {getFieldDecorator('areaName', {
            initialValue: item.areaName,
            rules: [
              {
                required: true,
                message: '请输入8字以内的店铺名称',
                max: 8,
              },
            ],
          })(<Input placeholder="请输入店铺名称" />)}
        </FormItem>
        <FormItem label="店铺地址" hasFeedback {...formItemLayout}>
          {getFieldDecorator('geoString', {
            initialValue: item.geoString,
            rules: [
              {
                required: true,
                message: '请输入64字以内的店铺地址!',
                max: 64,
              },
            ],
          })(<Input placeholder="请输入店铺地址" />)}
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
