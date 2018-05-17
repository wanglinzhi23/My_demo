import React, { Component } from 'react';

// 引入 ECharts 主模块
import echarts from 'echarts/lib/echarts';

// 引入K线图
import  'echarts/lib/chart/line';
// 引入提示框和标题组件
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';

class Echartsline extends Component {

    constructor(props) {
        super(props);
        this.state = {
            option: {}
        }
    }    
    componentDidMount() {
        setTimeout(()=>{
            this.renderEchart(this.props.option, this.props.id);
        },100);
    }
    componentWillReceiveProps(nextProps) {
        setTimeout(()=>{
            this.renderEchart(nextProps.option, nextProps.id);
        },100);
    }
    renderEchart = (option, id) => {
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById(id));
        // 绘制图表
        myChart.setOption(option);
    }
    render() {
        return (
            <div id={this.props.id} style={{ width: '100%', height:450}}></div>
        );
    }
}

export default Echartsline;