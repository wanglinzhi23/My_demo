import React from 'react'
import PropTypes from 'prop-types'
import moment from 'moment'
import { FilterItem } from 'components'
import { Form, Button, Row, Col, DatePicker, Input, Cascader, Switch, Select } from 'antd'
import city from '../../utils/city'

const Search = Input.Search;
const Option = Select.Option;
const { RangePicker } = DatePicker

const ColProps = {
  xs: 24,
  sm: 12,
  style: {
    marginBottom: 16,
  },
}

const TwoColProps = {
  ...ColProps,
  xl: 96,
}

const Filter = ({
  onAdd,
  isMotion,
  switchIsMotion,
  onFilterChange,
  filter,
  storeList,
  form: {
    getFieldDecorator,
    getFieldsValue,
    setFieldsValue,
  },
}) => {
  const handleFields = (fields) => {
    const { createTime } = fields
    if (createTime.length) {
      fields.createTime = [createTime[0].format('YYYY-MM-DD'), createTime[1].format('YYYY-MM-DD')]
    }
    return fields
  }

  const handleSubmit = () => {
    let fields = getFieldsValue()
    // fields = handleFields(fields)
    onFilterChange(fields)
  }

  const handleReset = () => {
    const fields = getFieldsValue()
    console.log("fields---",fields);
    for (let item in fields) {
      if ({}.hasOwnProperty.call(fields, item)) {
        if (fields[item] instanceof Array) {
          fields[item] = []
        } else {
          fields[item] = undefined
        }
      }
    }
    setFieldsValue(fields)
    handleSubmit()
  }

  const handleChange = (key, values) => {
    let fields = getFieldsValue()
    fields[key] = values
    console.log("过滤----",fields);
    // fields = handleFields(fields)
    onFilterChange(fields)
  }
  const { searchName, areaIds } = filter
  console.info("filter--",filter);

  //设置过滤项的值
  // setFieldsValue({ 'searchName':searchName, 'areaIds':areaIds })

  let initialCreateTime = []
  if (filter.createTime && filter.createTime[0]) {
    initialCreateTime[0] = moment(filter.createTime[0])
  }
  if (filter.createTime && filter.createTime[1]) {
    initialCreateTime[1] = moment(filter.createTime[1])
  }
  console.log("店铺列表--",storeList);
  return (
    <Row gutter={24}>
      <Col {...ColProps} xl={{ span: 4 }} md={{ span: 8 }}>
        {getFieldDecorator('searchName', { initialValue: searchName })(<Search placeholder="搜索设备ID号" size="large" onSearch={handleSubmit} />)}
      </Col>
      <Col {...ColProps} xl={{ span: 4 }} md={{ span: 8 }}>
        {getFieldDecorator('areaIds',{ initialValue: areaIds })(
            <Select size="large" style={{ width: 200 }} placeholder="请选择店铺" onChange={handleChange.bind(null, 'areaIds')}>
              <Option value="">全部店铺</Option>
              {
                storeList.map(function(store){
                  return <Option key={store.id} value={store.id}>{store.name}</Option>;
                })
              }
            </Select>
          )}
      </Col>
      {/*<Col {...ColProps} xl={{ span: 4 }} md={{ span: 8 }}>
        <div >
          <Button type="primary" size="large" className="margin-right" onClick={handleSubmit}>Search</Button>
          <Button size="large" onClick={handleReset}>重置</Button>
        </div>
      </Col>*/}
      {/*<Col {...ColProps} xl={{ span: 4 }} md={{ span: 8 }}>
        {getFieldDecorator('address', { initialValue: address })(
          <Cascader
            size="large"
            style={{ width: '100%' }}
            options={city}
            placeholder="Please pick an address"
            onChange={handleChange.bind(null, 'address')}
          />)}
      </Col>
      <Col {...ColProps} xl={{ span: 6 }} md={{ span: 8 }} sm={{ span: 12 }}>
        <FilterItem label="Createtime">
          {getFieldDecorator('createTime', { initialValue: initialCreateTime })(
            <RangePicker style={{ width: '100%' }} size="large" onChange={handleChange.bind(null, 'createTime')} />
          )}
        </FilterItem>
      </Col>*/}
      <Col {...TwoColProps} xl={{ span: 4 }} md={{ span: 8 }} sm={{ span: 8 }}>
        <Button size="large" type="ghost" onClick={onAdd}>添加设备</Button>
        {/*<div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap' }}>
          <div >
            <Button type="primary" size="large" className="margin-right" onClick={handleSubmit}>Search</Button>
            <Button size="large" onClick={handleReset}>Reset</Button>
          </div>
          <div>
            <Switch style={{ marginRight: 16 }} size="large" defaultChecked={isMotion} onChange={switchIsMotion} checkedChildren={'Motion'} unCheckedChildren={'Motion'} />
            <Button size="large" type="ghost" onClick={onAdd}>Create</Button>
          </div>
        </div>*/}
      </Col>
    </Row>
  )
}

Filter.propTypes = {
  onAdd: PropTypes.func,
  isMotion: PropTypes.bool,
  switchIsMotion: PropTypes.func,
  form: PropTypes.object,
  filter: PropTypes.object,
  onFilterChange: PropTypes.func,
}

export default Form.create()(Filter)
