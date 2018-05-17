import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import { Card, Table, Modal } from 'antd'
import { DropOption } from 'components'
import styles from './index.less'

const confirm = Modal.confirm

const Detail = ({ userDetail }) => {
  const { data } = userDetail
  console.log("userDetail--",userDetail);

  const handleMenuClick = (record, e) => {
    if (e.key === '1') {
      // onReadItem(record.id)
    }else if (e.key === '2') {
      // onEditItem(record)
    } else if (e.key === '3') {
      confirm({
        title: '您是否要删除该用户?',
        onOk () {
          onDeleteItem(record.id)
        },
      })
    }
  }

  const columns = [
    {
      title: '序号',
      dataIndex: 'numId',
      key: 'numId',
    }, {
      title: '店铺',
      dataIndex: 'store',
      key: 'store',
    }, {
      title: '设备ID',
      dataIndex: 'equipmentId',
      key: 'equipmentId',
    }, {
      title: '位置',
      dataIndex: 'address',
      key: 'address',
    }, {
      title: '状态',
      dataIndex: 'state',
      key: 'state',
    }, {
      title: '操作',
      key: 'operation',
      width: 100,
      render: (text, record) => {
        return <DropOption onMenuClick={e => handleMenuClick(record, e)} menuOptions={[{ key: '3', name: '删除' }]} />
      },
    },
  ]

  const storeData = [];
  for (let i = 0; i < 46; i++) {
    storeData.push({
      numId: i+1,
      store: `中山店 ${i}`,
      equipmentId: `${i}ads32${i}`,
      address: `London, Park Lane no. ${i}`,
      state: '在线',
    });
  }

  return (<div className="content-inner">
    <div className={styles.personalInfo}>
      <Card className={styles.card}>
        <div className={styles.item}>
          <p><span>名称：</span><span>{data.name}</span></p>
        </div>
        <div className={styles.item}>
          <p><span>账号：</span><span>{data.account}</span></p>
          <p><span>密码：</span><span>{data.password}</span></p>
        </div>
        <div className={styles.item}>
          <p><span>邮箱：</span><span>{data.email}</span></p>
          <p><span>联系方式：</span><span>{data.phone}</span></p>
        </div>
        <div className={styles.item}>
          <p><span>备注：</span><span>{data.remarks}</span></p>
        </div>
      </Card>
      <Card className={styles.card}>
        <div className={styles.item}>
          <p><span>已开通服务：</span><span>人员布控</span></p>
        </div>
        <div className={styles.item}>
          <p><span>店铺数目：</span><span>2 个</span></p>
        </div>
        <div className={styles.item}>
          <p><span>摄像机数目：</span><span>3 个</span></p>
        </div>
      </Card>
    </div>
    <div className={styles.storeTable}>
      <Table
        bordered
        scroll={{ x: 1250 }}
        columns={columns}
        simple
        rowKey={record => record.id}
        dataSource={storeData}
      />
    </div>
  </div>)
}

Detail.propTypes = {
  userDetail: PropTypes.object,
}

export default connect(({ userDetail, loading }) => ({ userDetail, loading: loading.models.userDetail }))(Detail)
