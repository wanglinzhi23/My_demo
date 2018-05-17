import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import { Card, Table, Modal, Row, Col, Button } from 'antd'
import { DropOption } from 'components'
import styles from './index.less'
import Error from '../../error'
import Modals from './Modal'
// import List from './List'
// import DeviceTpl from './Modal'

const confirm = Modal.confirm

const Detail = ({ location, dispatch, userDetail, loading }) => {
  const { userInfo, devicesData , modalVisible, modalType, storeList } = userDetail
  if(JSON.stringify(userInfo) == "{}"){
    return(<Error />)
  }
  const userData = userInfo.data;
  console.log("userData--",userData);

  const modalProps = {
    item: modalType === 'create' ? {} : currentItem,
    visible: modalVisible,
    maskClosable: false,
    confirmLoading: loading.effects['userDetail/update'],
    title: `${modalType === 'create' ? '添加设备' : '修改设备'}`,
    wrapClassName: 'vertical-center-modal',
    storeList,
    onOk (data) {
      if(modalType === 'create'){
        dispatch({
          type: `userDetail/create`,
          payload: data,
        })
      }else{
        dispatch({
          type: `userDetail/update`,
          payload: {...data,"startTime":"1970-01-01 00:00:00","endTime":"2050-01-01 00:00:00"},
        })
      }
    },
    onCancel () {
      dispatch({
        type: 'userDetail/hideModal',
      })
    },
  }

  const handleMenuClick = (record, e) => {
    if (e.key === '1') {
      // onReadItem(record.id)
    }else if (e.key === '2') {
      // onEditItem(record)
    } else if (e.key === '3') {
      confirm({
        title: '您是否要删除该设备?',
        onOk () {
          console.log("删除Id",record);
          dispatch({
            type: 'userDetail/removeDevice',
            payload: {
              id: record.id
            },
          })
        },
      })
    }
  }

  const addDevice = () => {
    dispatch({
      type: 'userDetail/showModal',
      payload: {
        modalType: 'create',
      },
    })
  }


  const columns = [
    /*{
      title: '序号',
      dataIndex: 'id',
      key: 'id',
    },*/ {
      title: '店铺',
      dataIndex: 'areaName',
      key: 'areaName',
    }, {
      title: '设备ID',
      dataIndex: 'name',
      key: 'name',
    }, {
      title: '位置',
      dataIndex: 'addr',
      key: 'addr',
    }, {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (text) => {
        const txt = text==1?"正常":"异常";
        return(`${txt}`)
      }
    }, {
      title: '操作',
      key: 'operation',
      width: 100,
      render: (text, record) => {
        return <DropOption onMenuClick={e => handleMenuClick(record, e)} menuOptions={[{ key: '3', name: '删除' }]} />
      },
    },
  ]

  /*const storeData = [];
  for (let i = 0; i < 46; i++) {
    storeData.push({
      numId: i+1,
      store: `中山店 ${i}`,
      equipmentId: `${i}ads32${i}`,
      address: `London, Park Lane no. ${i}`,
      state: '在线',
    });
  }*/

  console.log("数据--",devicesData);
  return (<div className="content-inner">
    <div className={styles.personalInfo}>
      <Card className={styles.card}>
        <div className={styles.item}>
          <p><span>名称：</span><span>{userData.name}</span></p>
        </div>
        <div className={styles.item}>
          <p><span>账号：</span><span>{userData.login}</span></p>
          <p><span>密码：</span><span>{userData.password}</span></p>
        </div>
        <div className={styles.item}>
          <p><span>邮箱：</span><span>{userData.email}</span></p>
          <p><span>联系方式：</span><span>{userData.mobile}</span></p>
        </div>
        <div className={styles.item}>
          <p><span>备注：</span><span>{userData.remark}</span></p>
        </div>
      </Card>
      <Card className={styles.card}>
        <div className={styles.item}>
          <p><span>已开通服务：</span><span>人员布控</span></p>
        </div>
        <div className={styles.item}>
          <p><span>店铺数目：</span><span>{userData.areaCount} 个</span></p>
        </div>
        <div className={styles.item}>
          <p><span>摄像机数目：</span><span>{userData.cameraCount} 个</span></p>
        </div>
      </Card>
    </div>
    <div className={styles.storeTable}>
      <Row className={styles.header}>
        <Col  xl={{ span: 4 }} md={{ span: 8 }}>
          <p>设备列表</p>
        </Col>
        <Col  xl={{ span: 4 }} md={{ span: 8 }} sm={{ span: 8 }}>
          <Button size="large" type="ghost" onClick={addDevice}>添加设备</Button>
        </Col>
      </Row>
      <Table
        bordered
        scroll={{ x: 1250 }}
        columns={columns}
        simple
        rowKey={record => record.id}
        dataSource={devicesData.data}
      />
      {modalVisible && <Modals {...modalProps} />}
    </div>
  </div>)
}

Detail.propTypes = {
  userDetail: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ userDetail, loading }) => ({ userDetail, loading: loading }))(Detail)
