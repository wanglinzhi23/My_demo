import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, InputNumber, Radio, Modal, Cascader, Select } from 'antd'

const FormItem = Form.Item
const Option = Select.Option;

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
}

const modals = ({
  item = {},
  onOk,
  storeList,
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
        <FormItem label="设备ID" hasFeedback {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: item.name,
            rules: [
              {
                required: true,
                message: '请输入8字以内的设备ID!',
                max: 8,
              },
            ],
          })(<Input placeholder="请输入设备ID" />)}
        </FormItem>
        <FormItem label="位置" hasFeedback {...formItemLayout}>
          {getFieldDecorator('addr', {
            initialValue: item.addr,
            rules: [
              {
                required: true,
                message: '请输入8字以内的位置!',
                max: 8,
              },
            ],
          })(<Input placeholder="请输入位置" />)}
        </FormItem>
        <FormItem label="店铺" hasFeedback {...formItemLayout}>
          {getFieldDecorator('stationId', {
            initialValue: item.areaName,
            rules: [
              { required: true, message: '请选择一个店铺!' },
            ],
          })(
            <Select placeholder="请选择店铺">
              {
                storeList.map(function(store){
                  return <Option value={store.id}>{store.areaName}</Option>;
                })
              }
            </Select>
          )}
        </FormItem>
      </Form>
    </Modal>
  )
}

modals.propTypes = {
  form: PropTypes.object.isRequired,
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
  storeList: PropTypes.array,
}

export default Form.create()(modals)
