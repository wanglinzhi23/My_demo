import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Row, Col, Button, Popconfirm } from 'antd'
import List from './List'
import Filter from './Filter'
import Modal from './Modal'

const User = ({ location, dispatch, user, loading }) => {
  const { list, userListParam, pagination, currentItem, modalVisible, modalType, isMotion, selectedRowKeys } = user
  const { pageSize } = pagination

  const formatItem = {...currentItem,password:"Intellif069aaa"}
  const modalProps = {
    item: modalType === 'create' ? {} : formatItem,
    visible: modalVisible,
    maskClosable: false,
    confirmLoading: loading.effects['user/update'],
    title: `${modalType === 'create' ? '新建用户' : '修改用户'}`,
    wrapClassName: 'vertical-center-modal',
    onOk (data) {
      if(modalType === 'create'){
        dispatch({
          type: `user/create`,
          payload: data,
        })
      }else{
        dispatch({
          type: `user/update`,
          payload: {...currentItem,...data},
        })
      }
    },
    onCancel () {
      dispatch({
        type: 'user/hideModal',
      })
    },
  }

  const listProps = {
    dataSource: list,
    loading: loading.effects['user/query'],
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
        type: 'user/delete',
        payload: id,
      })
    },
    onEditItem (item) {
      dispatch({
        type: 'user/showModal',
        payload: {
          modalType: 'update',
          currentItem: item,
        },
      })
    },
    onReadItem (id) {
      dispatch(routerRedux.push(`/user/${id}`));
    },
    /*rowSelection: {
      selectedRowKeys,
      onChange: (keys) => {
        dispatch({
          type: 'user/updateState',
          payload: {
            selectedRowKeys: keys,
          },
        })
      },
    },*/
  }

  const filterProps = {
    isMotion,
    filter: {
      ...userListParam,
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
        pathname: '/user',
        query: {
          field: fieldsValue.field,
          keyword: fieldsValue.keyword,
        },
      })) : dispatch(routerRedux.push({
        pathname: '/user',
      }))
    },
    onAdd () {
      dispatch({
        type: 'user/showModal',
        payload: {
          modalType: 'create',
        },
      })
    },
    switchIsMotion () {
      dispatch({ type: 'user/switchIsMotion' })
    },
  }

  const handleDeleteItems = () => {
    dispatch({
      type: 'user/multiDelete',
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
            {`选择了 ${selectedRowKeys.length} 个用户 `}
            <Popconfirm title={`您确定要删除选中的${selectedRowKeys.length}个用户吗?`} placement="left" onConfirm={handleDeleteItems}>
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

User.propTypes = {
  user: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ user, loading }) => ({ user, loading }))(User)
