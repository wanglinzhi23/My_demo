import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Row, Col, Button, Popconfirm } from 'antd'
import List from './List'
import Filter from './Filter'
import Modal from './Modal'

const Device = ({ location, dispatch, device, loading }) => {
  const { list, deviceListParam, storeList, pagination, currentItem, modalVisible, modalType, isMotion, selectedRowKeys } = device
  console.log("device",device);
  const { pageSize } = pagination
  const modalProps = {
    item: modalType === 'create' ? {} : currentItem,
    visible: modalVisible,
    maskClosable: false,
    confirmLoading: loading.effects['device/update'],
    title: `${modalType === 'create' ? '添加设备' : '修改设备'}`,
    wrapClassName: 'vertical-center-modal',
    storeList,
    onOk (data) {
      if(modalType === 'create'){
        dispatch({
          type: `device/create`,
          payload: data,
        })
      }else{
        dispatch({
          type: `device/update`,
          payload: {...currentItem,...data,"created": "2017-09-22T08:55:37.714Z","updated": "2017-09-22T08:55:37.714Z"},
        })
      }
    },
    onCancel () {
      dispatch({
        type: 'device/hideModal',
      })
    },
  }

  const listProps = {
    dataSource: list,
    loading: loading.effects['device/query'],
    pagination,
    location,
    isMotion,
    onChange (page) {
      const { query, pathname } = location
      dispatch(routerRedux.push({
        pathname,
        query: {
          ...query,
          page: page.current,
          pageSize: page.pageSize,
        },
      }))
    },
    onDeleteItem (id) {
      dispatch({
        type: 'device/delete',
        payload: id,
      })
    },
    onEditItem (item) {
      dispatch({
        type: 'device/showModal',
        payload: {
          modalType: 'update',
          currentItem: item,
        },
      })
    },
    onReadItem (id) {
      dispatch(routerRedux.push(`/device/${id}`));
    },
    /*rowSelection: {
      selectedRowKeys,
      onChange: (keys) => {
        dispatch({
          type: 'device/updateState',
          payload: {
            selectedRowKeys: keys,
          },
        })
      },
    },*/
  }

  const filterProps = {
    isMotion,
    storeList,
    filter: {
      ...deviceListParam,
    },
    onFilterChange (value) {
      dispatch(routerRedux.push({
        pathname: location.pathname,
        query: {
          ...value,
          page: 1,
          pageSize,
        },
      }))
    },
    onSearch (fieldsValue) {
      fieldsValue.keyword.length ? dispatch(routerRedux.push({
        pathname: '/device',
        query: {
          field: fieldsValue.field,
          keyword: fieldsValue.keyword,
        },
      })) : dispatch(routerRedux.push({
        pathname: '/device',
      }))
    },
    onAdd () {
      dispatch({
        type: 'device/showModal',
        payload: {
          modalType: 'create',
        },
      })
    },
    switchIsMotion () {
      dispatch({ type: 'device/switchIsMotion' })
    },
  }

  const handleDeleteItems = () => {
    dispatch({
      type: 'device/multiDelete',
      payload: {
        ids: selectedRowKeys,
      },
    })
  }

  return (
    <div className="content-inner">
      <Filter {...filterProps} />
      {
        /*selectedRowKeys.length > 0 &&
        <Row style={{ marginBottom: 24, textAlign: 'right', fontSize: 13 }}>
          <Col>
            {`选择了 ${selectedRowKeys.length} 个设备 `}
            <Popconfirm title={`您确定要删除选中的${selectedRowKeys.length}个设备吗?`} placement="left" onConfirm={handleDeleteItems}>
              <Button type="primary" size="large" style={{ marginLeft: 8 }}>批量删除</Button>
            </Popconfirm>
          </Col>
        </Row>*/
      }
      <List {...listProps} />
      {modalVisible && <Modal {...modalProps} />}
    </div>
  )
}

Device.propTypes = {
  device: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ device, loading }) => ({ device, loading }))(Device)
