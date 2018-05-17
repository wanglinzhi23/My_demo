import React from 'react'
import PropTypes from 'prop-types'
import { Table, Modal } from 'antd'
import classnames from 'classnames'
import { DropOption } from 'components'
import { Link } from 'dva/router'
import AnimTableBody from '../../components/DataTable/AnimTableBody'
import styles from './List.less'

const confirm = Modal.confirm

const List = ({ onDeleteItem, onEditItem, isMotion, location, onReadItem, ...tableProps }) => {
  const handleMenuClick = (record, e) => {
    if (e.key === '1') {
      onReadItem(record.id)
    }else if (e.key === '2') {
      onEditItem(record)
    } else if (e.key === '3') {
      confirm({
        title: '您是否要删除该店铺?(该店铺及店铺下的设备将同时删除)',
        onOk () {
          onDeleteItem(record.id)
        },
      })
    }
  }

  const columns = [
    /*{
      title: '序号',
      dataIndex: 'id',
      key: 'id',
    },*/
    {
      title: '名称',
      dataIndex: 'areaName',
      key: 'areaName',
    }, {
      title: '创建时间',
      dataIndex: 'created',
      key: 'created',
      render: (text, record) => {
        return <span>{new Date(text).format('yyyy-MM-dd')}</span>
      },
    }, {
      title: '绑定设备数量',
      dataIndex: 'countLeaf',
      key: 'countLeaf',
    }, {
      title: '操作',
      key: 'operation',
      width: 100,
      render: (text, record) => {
        return <DropOption onMenuClick={e => handleMenuClick(record, e)} menuOptions={[{ key: '1', name: '查看' },{ key: '2', name: '修改' }, { key: '3', name: '删除' }]} />
      },
    },
  ]

  const getBodyWrapperProps = {
    page: location.query.page,
    current: tableProps.pagination.current,
  }

  const getBodyWrapper = (body) => { return isMotion ? <AnimTableBody {...getBodyWrapperProps} body={body} /> : body }

  return (
    <div>
      <Table
        {...tableProps}
        className={classnames({ [styles.table]: true, [styles.motion]: isMotion })}
        bordered
        scroll={{ x: 1250 }}
        columns={columns}
        simple
        rowKey={record => record.id}
        getBodyWrapper={getBodyWrapper}
      />
    </div>
  )
}

List.propTypes = {
  onDeleteItem: PropTypes.func,
  onEditItem: PropTypes.func,
  isMotion: PropTypes.bool,
  location: PropTypes.object,
}

export default List
