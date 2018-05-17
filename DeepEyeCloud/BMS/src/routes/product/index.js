import React from 'react'
import { Card, Button ,message } from 'antd'
import styles from './index.less'


const openService = () => {
  message.info('暂不支持开通此服务');
}

const product = () => (
  <div className="content-inner">
    <Card className={styles.card} bodyStyle={{padding:0,display:'flex'}}>
      <img className={styles.bigIcon} src="icon/bukong.png" />
      <div className={styles.info}>
        <h2>人员布控</h2>
        <p>针对入库人员进行布控,抓拍比对成功则发送告警信息.</p>
      </div>
      <div className={styles.state}>
        <div><img src='icon/member.png' /><span>已开通</span></div>
        <p>有效期：2018-08-15</p>
      </div>
    </Card>
    <Card className={styles.card} bodyStyle={{padding:0,display:'flex'}}>
      <img className={styles.bigIcon} src="icon/statistics.png" />
      <div className={styles.info}>
        <h2>人流统计</h2>
        <p>针对单店模式，可提供基于有效采集及去重后的人流量统计,并支持多种图表汇总呈现.</p>
      </div>
      <div className={styles.state}>
        <Button style={{backgroundColor:'#f1771d',color:'#fff'}} onClick={ openService }>开通服务</Button>
      </div>
    </Card>
  </div>)

export default product