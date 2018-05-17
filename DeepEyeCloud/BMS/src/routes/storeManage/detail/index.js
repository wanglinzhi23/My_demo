import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import { Card, Table, Modal, Row, Col, Button } from 'antd'
import { DropOption } from 'components'
import styles from './index.less'
import Error from '../../error'
import Modals from './Modal'

const confirm = Modal.confirm

const Detail = ({ location, dispatch, storeDetail, loading }) => {
  console.log("storeDetail==",storeDetail);
  const { storeInfo, devicesData , modalVisible, modalType, storeList } = storeDetail
  if(JSON.stringify(storeInfo) == "{}"){
    return(<Error />)
  }

  const modalProps = {
    item: modalType === 'create' ? {} : currentItem,
    visible: modalVisible,
    maskClosable: false,
    confirmLoading: loading.effects['storeDetail/update'],
    title: `${modalType === 'create' ? '添加设备' : '修改设备'}`,
    wrapClassName: 'vertical-center-modal',
    storeList,
    onOk (data) {
      if(modalType === 'create'){
        dispatch({
          type: `storeDetail/create`,
          payload: data,
        })
      }else{
        dispatch({
          type: `storeDetail/update`,
          payload: {...data,"startTime":"1970-01-01 00:00:00","endTime":"2050-01-01 00:00:00"},
        })
      }
    },
    onCancel () {
      dispatch({
        type: 'storeDetail/hideModal',
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
            type: 'storeDetail/removeDevice',
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
      type: 'storeDetail/showModal',
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

/*  const storeData = [];
  for (let i = 0; i < 46; i++) {
    storeData.push({
      numId: i+1,
      store: `中山店 ${i}`,
      equipmentId: `${i}ads32${i}`,
      address: `London, Park Lane no. ${i}`,
      state: '在线',
    });
  }*/

  return (<div className="content-inner">
    <div className={styles.personalInfo}>
      <Card className={styles.card}>
        <div className={styles.item}>
          <p><span>店铺名称：</span><span>{storeInfo.data.areaName}</span></p>
        </div>
        <div className={styles.item}>
          <p><span>店铺地址：</span><span>{storeInfo.data.geoString}</span></p>
        </div>
        <div className={styles.item}>
          <p><span>备注：</span><span>{storeInfo.data.remark}</span></p>
        </div>
      </Card>
      <Card className={styles.card}>
        <div className={styles.item}>
          <p><span>已开通服务：</span><span>人员布控</span></p>
        </div>
        <div className={styles.item}>
          <p><span>设备数目：</span><span>{storeInfo.data.countLeaf}</span></p>
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
  storeDetail: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ storeDetail, loading }) => ({ storeDetail, loading: loading }))(Detail)
