import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Row, Col, Button, Popconfirm } from 'antd'
import List from './List'
import Filter from './Filter'
import Modal from './Modal'

const Store = ({ location, dispatch, store, loading }) => {
  const { list, storeListParam, pagination, currentItem, modalVisible, modalType, isMotion, selectedRowKeys } = store
  const { pageSize } = pagination

  const modalProps = {
    item: modalType === 'create' ? {} : currentItem,
    visible: modalVisible,
    maskClosable: false,
    confirmLoading: loading.effects['store/update'],
    title: `${modalType === 'create' ? '添加店铺' : '修改店铺'}`,
    wrapClassName: 'vertical-center-modal',
    onOk (data) {
      console.log("编辑的店铺-",currentItem);
      if(modalType === 'create'){
        dispatch({
          type: `store/create`,
          payload: data,
        })
      }else{
        const curDate = new Date();
        dispatch({
          type: `store/update`,
          payload: {...currentItem,...data,"updated": curDate},
        })
      }
    },
    onCancel () {
      dispatch({
        type: 'store/hideModal',
      })
    },
  }
  const listProps = {
    dataSource: list,
    loading: loading.effects['store/query'],
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
        type: 'store/delete',
        payload: id,
      })
    },
    onEditItem (item) {
      dispatch({
        type: 'store/showModal',
        payload: {
          modalType: 'update',
          currentItem: item,
        },
      })
    },
    onReadItem (id) {
      dispatch(routerRedux.push(`/store/${id}`));
    },
    /*rowSelection: {
      selectedRowKeys,
      onChange: (keys) => {
        dispatch({
          type: 'store/updateState',
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
      ...storeListParam,
    },
    onFilterChange (value) {
      dispatch(routerRedux.push({
        pathname: location.pathname,
        query: {
          ...value,
          page: 1,
          pageSize
        },
      }))
    },
    onSearch (fieldsValue) {
      fieldsValue.keyword.length ? dispatch(routerRedux.push({
        pathname: '/storeManagestore',
        query: {
          field: fieldsValue.field,
          keyword: fieldsValue.keyword,
        },
      })) : dispatch(routerRedux.push({
        pathname: '/storeManagestore',
      }))
    },
    onAdd () {
      dispatch({
        type: 'store/showModal',
        payload: {
          modalType: 'create',
        },
      })
    },
    switchIsMotion () {
      dispatch({ type: 'store/switchIsMotion' })
    },
  }

  const handleDeleteItems = () => {
    dispatch({
      type: 'store/multiDelete',
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
            {`选择了 ${selectedRowKeys.length} 个店铺 `}
            <Popconfirm title={`您确定要删除选中的${selectedRowKeys.length}个店铺吗?`} placement="left" onConfirm={handleDeleteItems}>
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

Store.propTypes = {
  store: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ store, loading }) => ({ store, loading }))(Store)
