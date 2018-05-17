import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Card, Button ,Select } from 'antd'
import styles from './index.less'

const Option = Select.Option;


let faceData=[];
for(let i=0; i<100;i++){
  faceData.push("icon/a1.jpg");
}
const Monitor = ({ location, dispatch, monitor, loading }) => {
  const { storeList ,monitorList } = monitor;

  const handleChange = (value) => {
    dispatch({
      type: 'monitor/queryMonitor',
      payload: {
        storeList:storeList,
        param:{"areaIds":value,"queryText":"","page":1,"pageSize":1000000000},
      },
    })
  }

  return(
  <div className='content-inner'>
    <div className={styles.header}>已录入信息</div>
    <div className={styles.body}>
      <div className={styles.choose}>
        <span className={styles.title}>选择店铺</span>
        <Select size="large" style={{ width: 200 }} defaultValue="全部门店" placeholder="请选择店铺" onChange={handleChange}>
          <Option value="">全部门店</Option>
          {
            storeList.map(function(store){
              return <Option key={store.id} value={store.id}>{store.areaName}</Option>;
            })
          }
        </Select>
      </div>
      <div className={styles.personnel}>
        <ul className={styles.list}>
          {
            monitorList.map(function(monitor){
              return <li className={styles.item} key={monitor.id}><img src={monitor.photoData} /></li>;
            })
          }
        </ul>
      </div>
      <div></div>
    </div>
  </div>)
}

export default connect(({ monitor, loading }) => ({ monitor, loading }))(Monitor) 