import React, { Component } from 'react';
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Card, Button ,Select,Tabs,DatePicker ,Icon} from 'antd'
import styles from './index.less'
import { Radio } from 'antd';
import echarts from 'echarts/lib/echarts'; 
import EchartsTest from './echarts/echart';
import moment from 'moment';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');
import { request, config } from '../../utils'
import { ENGINE_METHOD_DIGESTS } from 'constants';
import src from '../../public/nodata.png';

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const TabPane = Tabs.TabPane;
const RangePicker = DatePicker.RangePicker;
const dateFormat = 'YYYY-MM-DD';

class Faceid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            mode: ['month', 'month'],
            value: [],
            storeList: [],//店铺列表
            endTime:this.thisDate(), //请求参数
            startTime:this.GetDateStr(10),
            typeVaule: '总榜',
            typeVaule1: '总榜',
            areaIdString:5, 
            show: true, //图片为隐藏状态  
            show1:true,
            personType:10,  //年龄、性别、还是总榜
            timeType:10, //按日、月、年查询
            loading: false,
            TotalNumber: 0,
            HTotalNumber:0,
            timeShOW:true,
            echartDate: {
                color:[],
                tooltip: {
                    trigger: 'axis',
                    padding: [10,20,10,25], 
                    textStyle : {
                    },         
                },
                formatter:"",
                grid: {
                    left: '1%',
                    right: '1%',
                    bottom: '3%',
                    containLabel: true
                },
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                        restore : {show: true},
                        saveAsImage : {show: true},
                    },
                    
                
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: ['00点','01点','02点','03点','04点','05点','06点','07点','08点','09点','10点','11点','12点','13点','14点','15点','16点','17点','18点','19点','20点','21点','22点','23点'],
                    axisLabel: {
                        rotate: 45,
                    },

                },
                yAxis: {
                    type: 'value',
                    
                },
                series: []
            },
            HechartDate: {
                color: ['#e78dc2', '#fcb7ae', '#fbe18c', '#95e5e7'],
                tooltip: {
                    trigger: 'axis', 
                    padding: [10,20,10,25], 
                    axisPointer: { // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                grid: {
                    left: '1%',
                    right: '4%',
                    bottom: '0%',
                    containLabel: true
                },

                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: [1,2,3,4,5]
                },
                yAxis: {
                    type: 'value'
                },
                series: []
            }
        }
    }  
    onChange = (dates, dateStrings) => {//选定日期触发事件。
        this.setState(
            Object.assign({}, {
                startTime:dateStrings[0], 
                endTime:dateStrings[1],
                timeType:10
            }),
            () => console.log(this.state)
        );
    }
    handleChange = (e) => {
        this.setState({
            areaIdString: e
        });
        setTimeout(()=>{
            this.renderValue(this.state.typeVaule);
            this.renderValue1(this.state.typeVaule1);
        },300);
    }
    onMonthRangeChange = (value, mode) => {
        this.setState({
          value,
          mode: [
            mode[0] === 'date' ? 'month' : mode[0],
            mode[1] === 'date' ? 'month' : mode[1],
          ],
          startTime: `${value[0].format('YYYY-MM')}-01`   ,
          endTime:   `${value[1].format('YYYY-MM')}-01`
        });
      };
    componentWillMount(){     
        this.setState({
            startTime: this.GetDateStr(10),
            endTime:this.thisDate()
        });
    }
    componentDidMount() {
        this.renderValue('总榜');
        this.renderValue1('总榜');
        this.getStores();
    }
    allTime = (arr)=> {
        arr = arr || [];
        var arrTarget = new Array(24).fill(0)
        for (let i = 0; i < arr.length; i++) {
            arrTarget[parseInt(arr[i].time)] = arr[i].count
        }
        return arrTarget
    }
    getStores = () => {
        request({
            url: config.api.stores,
            method: 'post',
            data: {
                userId: 1,
                page: 1,
                pageSize: 100000
            },
        }).then(res=>{
            this.setState({
                storeList: res.data
            });
        });
    }

    //点击切换数据。
    onCheck = (e) => { 
        let value = e.target.value;
        this.renderValue(value);
    }
    //获取N天前
    GetDateStr = (AddDayCount) => {   
        var dd = new Date();  
        dd.setDate(dd.getDate()-AddDayCount);//获取AddDayCount天前的日期  
        var y = dd.getFullYear();   
        var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0  
        var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate();//获取当前几号，不足10补0  
        return y+"-"+m+"-"+d;    
     }  
    //获取当天

    //导出表格
    export = () =>{
        request({
            url: config.api.export,
            method: 'get',
            data: {
                startTime:this.state.startTime,
                endTime:this.state.endTime,
                type:'2',
                areaIdString:this.state.areaIdString,
                timeType:this.state.timeType
            },
        }).then(res =>{
             window.open(config.baseURL + config.api.exportTable + `?sn=${res.sn}`)
        })
     }
    thisDate=()=> {  
        var now = new Date;  
        var str = "";  
        var year = "";  
        var month = "";  
        var day = "";  
        year = now.getFullYear();  
        if (now.getMonth() == 0) {  
            month = "01";  
        } else if (now.getMonth() + 1 < 10) {  
            month = "0" + (now.getMonth() + 1);  
        } else {  
            month = now.getMonth() + 1;  
        }  
        if (now.getDate() < 10) {  
            day = "0" + now.getDate();  
        } else {  
            day = now.getDate();  
        }  
        str = year + "-" + month + "-" + day;  
        return str;  
    } 
    //触发历史总事件。
    onCheck1 = (e) =>{
        this.renderValue1(e.target.value);
    }
    renderValue1 = (value) => {
        this.setState({
            loading: true,
            typeVaule1: value
        });
        switch (value){
            case '总榜':
            //请求数据。
            this.getData({
                type:2,
                personType: this.state.personType,
                areaIdString: this.state.areaIdString,
                timeType:this.state.timeType,
                startTime:this.state.startTime,
                endTime:this.state.endTime
            }, (res)=>{
                var all = res.PersonFlowCollectDto.all;
                if(all.length>=2){
                    this.setState({
                        show1:false
                    });
                }else{
                    this.setState({
                        show1:true
                    });
                }
                let HTotalNumber = 0;//总人数
                let HechartsxAxis = []; //时间横轴
                let HechartSeries = []; //数据
                let HechartsSeries1 = []; //数据        //获取历史

                for(let i = 0; i<all.length;i++){
                    HTotalNumber = parseInt(HTotalNumber)+parseInt(all[i].count);//获取历史总人流量。
                    HechartsxAxis.push(all[i].time);//获取历史时间段。
                    HechartSeries.push(all[i].count);//获取历史时间段人数。
                }
                var HechartDate = this.state.HechartDate;
                HechartDate.xAxis.data = HechartsxAxis;
                HechartDate.color = ['#3896F3'],
                HechartDate.series=[
                    {   
                        name:'总人数',
                        type:'line',
                        data:HechartSeries,
                        lineStyle:{
                            color:'#3896F3'    
                        }
                    }
                ];
                this.setState({
                    loading: false,
                    HechartDate:HechartDate,
                    HTotalNumber:HTotalNumber
                });
            });    
            break;
            case '性别':
                //请求数据。
                this.getData({
                    type:2,
                    personType: 20,
                    areaIdString: this.state.areaIdString,
                    timeType:this.state.timeType,
                    startTime:this.state.startTime,
                    endTime:this.state.endTime
                }, (res)=>{
                    let HTotalNumber = res.PersonFlowCollectDto.count;//总人数
                    let echartsxAxis = []; //时间横轴
                    let echarstSeries = []; //数据
                    let echarstSeries1 = []; //数据
                    var Male = res.PersonFlowCollectDto.male;
                    var Famale = res.PersonFlowCollectDto.female;
                    if(Male.length>=2||Famale.length>=2){
                        this.setState({
                            show1:false
                        });
                    }else{
                        this.setState({
                            show1:true
                        });
                    }
                    for(let i = 0; i<Male.length;i++){//循环男性。
                        echarstSeries.push(Male[i].count); //男性数据
                        echartsxAxis.push(Male[i].time);//获取时间段。
                    }
                    for(let j = 0; j<Famale.length;j++){//循环女性。
                        echarstSeries1.push(Famale[j].count);//女性数据
                    }
                    var HechartDate = this.state.HechartDate;
                    HechartDate.xAxis.data = echartsxAxis;
                    HechartDate.color = ['#3896F3','#16BE88'],
                    HechartDate.series=[
                        {   
                            name:'男性',
                            type:'line',
                            data:echarstSeries,
                            lineStyle:{
                                color:'#3896F3'    
                            }
                        },
                        {   
                            name:'女性',
                            type:'line',
                            data:echarstSeries1,
                            lineStyle:{
                                color:'#16BE88'    
                            }
                        }
                    ];
                    this.setState({
                        loading: false,
                        HechartDate:HechartDate,
                        HTotalNumber:HTotalNumber
                    });
                });    
                break;
            case '年龄':
                //请求数据。
                this.getData({
                    type:2,
                    personType: 30,
                    areaIdString: this.state.areaIdString,
                    timeType:this.state.timeType,
                    startTime:this.state.startTime,
                    endTime:this.state.endTime
                }, (res)=>{
                    let HTotalNumber = res.PersonFlowCollectDto.count;//总人数
                    let echartsxAxis = []; //时间横轴
                    let middleAgeDate = []; //中年 
                    let oldAgeDate = [];//老年
                    let teensDate = []; //少年
                    let youngDate = []; //青年

                    var middleAge = res.PersonFlowCollectDto.middleAge; //中年 
                    var oldAge = res.PersonFlowCollectDto.oldAge; //老年
                    var teens = res.PersonFlowCollectDto.teens; //少年
                    var young = res.PersonFlowCollectDto.young;//青年

                    if(teens.length>=2||oldAge.length>=2||middleAge.length>=2){
                        this.setState({
                            show1:false
                        });
                    }else{
                        this.setState({
                            show1:true
                        });
                    }
                    for(let i=0;i<middleAge.length;i++){
                        middleAgeDate.push(middleAge[i].count);
                       //获取时间段。
                    }
                    for(let j=0;j<oldAge.length;j++){
                        oldAgeDate.push(oldAge[j].count);
                    }
                    for(let k=0;k<teens.length;k++){
                        teensDate.push(teens[k].count);
                    }
                    for(let l=0;l<young.length;l++){
                        youngDate.push(young[l].count);
                        echartsxAxis.push(young[l].time);
                    }
                    var HechartDate = this.state.HechartDate;
                    HechartDate.xAxis.data = echartsxAxis;
                    HechartDate.color = ['#e78dc2','#fcb7ae','#fbe18c','#95e5e7'];
                    HechartDate.series=[
                        {   
                            name:'青年',
                            type:'line',
                            data:youngDate,
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        color:'#e78dc2', 
                                    },
                                    areaStyle: {
                                        color : '#e78dc2'
                                    }
                                }
                            },
                        },
                        {   
                            name:'中年',
                            type:'line',
                            data:middleAgeDate,
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        color:'#fcb7ae'    
                                    },
                                    areaStyle: {
                                        color : '#fcb7ae'
                                    }
                                }
                            }
                        },
                        {   
                            name:'老年',
                            type:'line',
                            data:oldAgeDate,
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        color:'#fbe18c'    
                                    },
                                    areaStyle: {
                                        color : '#fbe18c'
                                    }
                                }
                            },
                        },
                        {   
                            name:'少年',
                            type:'line',
                            data:teensDate,
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        color:'#95e5e7'    
                                    },
                                    areaStyle: {
                                        color : '#95e5e7'
                                    }
                                }
                            }
                        }
                    ];
                    this.setState({
                        loading: false,
                        HechartDate:HechartDate,
                        HTotalNumber:HTotalNumber
                    });
                });    
                break;
        }
    }
    renderValue = (value) => { 
        this.setState({
            loading: true,
            typeVaule: value
        });
        this.state.echartDate.series = null;
        switch (value) {   
            case '总榜':
                this.getData({type: 1, personType: 10, areaIdString: this.state.areaIdString}, (res)=>{
                    let TotalNumber = res.PersonFlowCollectDto.count;//总人数
                    let echarstSeries = []; //数据
                    var all = res.PersonFlowCollectDto.all;
                    var _public = res.PersonFlowCollectDto;

                    let allDate = this.allTime(_public.all);
                    if(all.length >= 2){
                        this.setState({
                            show:false
                        });
                    }else{
                        this.setState({
                            show:true
                        });
                    }          
                    var echartDate = this.state.echartDate;
                    echartDate.color = ['#3896F3'];
                    echartDate.series=[
                        {   
                            name:'总人数',
                            type:'line',
                            data:allDate,
                            lineStyle:{
                                color:'#3896F3'    
                            }
                        }
                    ];
                    this.setState({
                        loading: false,
                        echartDate:echartDate,
                        TotalNumber:TotalNumber
                    });
                });
            break;
            case '性别':
                this.getData({type: 1, personType: 20, areaIdString: this.state.areaIdString}, (res)=>{
                    var _public = res.PersonFlowCollectDto
                    let TotalNumber = res.PersonFlowCollectDto.count;//总人数
                    var male = res.PersonFlowCollectDto.male;
                    var female = res.PersonFlowCollectDto.female;

                    if(male.length>=2){
                        this.setState({
                            show:false
                        });
                    }else{
                        this.setState({
                            show:true
                        });
                    }
                    let ManDate = this.allTime(_public.male); //数据
                    let WomenDate =this.allTime(_public.female); //数据
                    var echartDate = this.state.echartDate;

                    echartDate.color = ['#3896F3','#16BE88'];
                    echartDate.series=[
                        {   
                            name:'男性',
                            type:'line',
                            data:ManDate,
                            lineStyle:{
                                color:'#3896F3'    
                            }
                        },
                        {   
                            name:'女性',
                            type:'line',
                            data:WomenDate,
                            lineStyle:{
                                color:'#16BE88'    
                            }
                        }
                    ];
                    this.setState({
                        loading: false,
                        TotalNumber:TotalNumber,
                        echartDate:echartDate
                    }); 
                });    
            break;
            case '年龄':
                this.getData({type: 1, personType: 30, areaIdString: this.state.areaIdString}, (res)=>{
                    let TotalNumber = res.PersonFlowCollectDto.count;//总人数
                    var _public = res.PersonFlowCollectDto
                    var teens = res.PersonFlowCollectDto.teens; //少年
                    var oldAge = res.PersonFlowCollectDto.oldAge; //老年
                    var middleAge = res.PersonFlowCollectDto.middleAge; //中年 
                    var young = res.PersonFlowCollectDto.young; //青年
    
                     let teensDate =this.allTime(_public.teens); //少年数据
                     let oldAgeDate =this.allTime(_public.oldAge); //老年数据
                     let middleAgeDate = this.allTime(_public.middleAge); //中年数据
                     let youngDate =  this.allTime(_public.young); //青年数据

            
                    if(middleAge.length>=2 ||oldAge.length>=2 ||teens.length>=2){
                        this.setState({
                            show:false
                        });
                    }else{
                        this.setState({
                            show:true
                        });
                    }
                    var echartDate = this.state.echartDate;
                    // echartDate.formatter = '{b} 点 00 分 <br/>青年：{c}<br/>中年：{c1}<br/>老年：{c2}<br/>少年：{c3}';
                    echartDate.color = ['#e78dc2','#fcb7ae','#fbe18c','#95e5e7'];
                    echartDate.series=[
                        {   
                            name:'青年',
                            type:'line',
                            data:youngDate,
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        color:'#e78dc2'    
                                    },
                                    areaStyle: {
                                        color : '#e78dc2'
                                    }
                                }
                            },
                        },
                        {   
                            name:'中年',
                            type:'line',
                            data:middleAgeDate,
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        color:'#fcb7ae'    
                                    },
                                    areaStyle: {
                                        color : '#fcb7ae'
                                    }
                                }
                            },
                        },
                        {   
                            name:'老年',
                            type:'line',
                            data:oldAgeDate,
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        color:'#fbe18c'    
                                    },
                                    areaStyle: {
                                        color : '#fbe18c'
                                    }
                                }
                            },
                        },
                        {   
                            name:'少年',
                            type:'line',
                            data:teensDate,
                            itemStyle: {
                                normal: {
                                    lineStyle:{
                                        color:'#95e5e7'    
                                    },
                                    areaStyle: {
                                        color : '#95e5e7'
                                    }
                                }
                            }
                        }
                    ];
                    this.setState({
                        loading: false,
                        TotalNumber:TotalNumber,
                        echartDate:echartDate
                    }); 
                });
            break;
        }

    }
    //查询
    search = () => {
        if(this.state.timeShOW){
            this.setState({
                timeType:10
            });
        }else{
            this.setState({
                timeType:20
            });
        }
        setTimeout(()=>{
            this.renderValue1(this.state.typeVaule1);
        },100);
    }

    NewLook = () =>{//
        this.setState({
            timeType: 30,
            startTime:moment().format('YYYY')-10+'-01-01',
            endTime:moment().format('YYYY')+'-01-01'
        });  
        setTimeout(()=>{
            this.renderValue1(this.state.typeVaule1);
        },100);
    }
    getDatemout = (e) =>{
        if(e == 0){ 
            setTimeout(
                ()=>{
                    this.setState({
                        timeType:10,
                        timeShOW:true
                    })
                },300
            )
        }
        if(e == 1){
            setTimeout(()=>{
                this.setState({
                    timeType:20,
                    timeShOW:false
                })
            },300
            )
        }
    }
    getData = (obj, callback) => {
        request({ 
            url: config.api.getTodayPersonCount,
            method: 'get',
            data: {
                type: obj.type,
                personType: obj.personType,
                areaIdString: obj.areaIdString,
                startTime:obj.startTime,
                endTime:obj.endTime,
                timeType: obj.timeType
            },
        }).then((res)=>{
            if(res && res.errCode){
                this.setState({
                    TotalNumber: 0,
                    HTotalNumber: 0,
                    show: true
                });
                return false;
            }
            if(typeof callback == 'function')callback(res);
        });    
    }

    render(){
        const { value, mode } = this.state;
        return(
            <div className='content-inner'>
                <div className={styles.body}>
                    <div className={styles.choose}>
                        <span className={styles.title}>选择店铺</span>
                        <Select size="large" defaultValue='5' style={{ width: 200 }} placeholder="请选择店铺" onChange={this.handleChange}>
                        <Select.Option value="5">岁宝石厦店</Select.Option>
                        {/* {
                            this.state.storeList.map(function(store){ 拿取所有id。默认是岁宝石厦
                                return <Select.Option key={store.id} value={store.id}>{store.areaName}</Select.Option>;
                            })
                        } */}
                        </Select>
                    </div>
                </div>
                <Tabs defaultActiveKey="1" >
                    <TabPane tab="当日实时统计" key="1">
                        <div className={styles.chart}>
                            <span className={styles.size}>榜单类型</span>
                            <RadioGroup defaultValue="总榜"  onChange={this.onCheck}>
                                <RadioButton value="总榜">总榜</RadioButton>
                                <RadioButton value="性别">性别</RadioButton>
                                <RadioButton value="年龄">年龄</RadioButton>
                            </RadioGroup>
                            <div className={styles.Right_p}><p>当日人流量<span>{this.state.TotalNumber}</span></p></div>
                        </div>
                        <div style={{display:this.state.show?'none':'block'}}>
                            {!this.state.loading ? <EchartsTest  id = 'main1' option = {this.state.echartDate} /> : ''}
                        </div>    
                        <div className={styles.DataImg} style={{display:this.state.show?'block':'none'}}>
                            <p><img src={src} /> </p>
                            <div>暂无数据</div>
                        </div> 
                    </TabPane>
    
                    <TabPane tab="历史统计" key="2">
                    <div className={styles.chart1}>
                    <span className={styles.size}>榜单类型</span>
                            <RadioGroup defaultValue="总榜" onChange={this.onCheck1}>
                                <RadioButton value="总榜">总榜</RadioButton>
                                <RadioButton value="性别">性别</RadioButton>
                                <RadioButton value="年龄">年龄</RadioButton>
                            </RadioGroup>
                        <Select size="small" defaultValue='0' style={{ width: 100,marginRight:15}} placeholder="按日查询" onChange = {this.getDatemout}>
                            <Select.Option value="0">按日查询</Select.Option>
                            <Select.Option value="1">按月查询</Select.Option>
                        </Select>
                        <RangePicker
                        format={dateFormat}
                        onChange={this.onChange}
                        value={[moment(this.state.startTime), moment(this.state.endTime)]}
                        style={{display:this.state.timeShOW?'inline-block':'none'}}
                        />
                         <RangePicker
                            placeholder={['开始月份', '结束月份']}
                            format="YYYY-MM"
                            value={[moment(this.state.startTime), moment(this.state.endTime)]}
                            mode={this.state.mode}
                            onPanelChange={this.onMonthRangeChange}
                            style={{display:this.state.timeShOW?'none':'inline-block'}}
                        />
                        <Button onClick={this.search}>查询</Button>
                        <Button onClick={this.NewLook}>按年查看</Button>
                        <div className={styles.Right_p}>
                            <p>总人数<span>{this.state.HTotalNumber}</span></p>
                            <Button onClick={this.export} size='small'>导出数据</Button>
                        </div>
                    </div>
                        <div style={{display:this.state.show1?'none':'block'}}>
                            {!this.state.loading ? <EchartsTest id = 'main2'  option = {this.state.HechartDate} /> : ''}
                        </div>     
                        <div className={styles.DataImg} style={{display:this.state.show1?'block':'none'}}>
                            <p><img src={src} /> </p>
                            <div>暂无数据</div>
                        </div> 
                    </TabPane>
                </Tabs>
            </div>    
            
        )
    }
}
export default connect(({ Faceid, loading }) => ({ Faceid, loading }))(Faceid) 