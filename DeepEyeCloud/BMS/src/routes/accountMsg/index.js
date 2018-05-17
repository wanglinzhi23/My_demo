import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Card, Button } from 'antd'
import styles from './index.less'

const AccountMsg = ({ location, dispatch, account, loading }) => {
	// 对Date的扩展，将 Date 转化为指定格式的String
	// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
	// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
	// 例子： 
	// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
	// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
	Date.prototype.Format = function (fmt) { //author: meizz 
	    var o = {
	        "M+": this.getMonth() + 1, //月份 
	        "d+": this.getDate(), //日 
	        "h+": this.getHours(), //小时 
	        "m+": this.getMinutes(), //分 
	        "s+": this.getSeconds(), //秒 
	        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
	        "S": this.getMilliseconds() //毫秒 
	    };
	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	    return fmt;
	}
	const { userData } = account;
	console.info("userData--",userData);
	return(
	  <div className="content-inner">
	    <Card className={styles.card} bodyStyle={{padding:0}}>
	      <div><span>登录账号：</span><span>{userData.login}</span></div>
	      <div><span>注册时间：</span><span>{new Date(userData.created).format('yyyy-MM-dd')}</span></div>
	      <div><span>注册邮箱：</span><span>{userData.email}</span></div>
	      <div><span>联系方式：</span><span>{userData.mobile}</span></div>
	    </Card>
	  </div>)
}


AccountMsg.propTypes = {
  userInfo: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ account, loading }) => ({ account, loading }))(AccountMsg)