
var baseURI='http://127.0.0.1:8080/';
// var baseURI='/demo';
var Const ={
	insertURI: baseURI+'/faceInfo/insert'
	,updateURI: baseURI+'/faceInfo/update'
	,deleteUserURI: baseURI+'/faceInfo/delete/'
	,listAllURI: baseURI+'/faceInfo/all'
	,findByIdURI: baseURI+'/faceInfo/{id:\\d+}'
};

/*
* new Date().format("yyyy-MM-dd hh:mm:ss");
*
* */
Date.prototype.format = function(fmt) {
	var o = {
		"M+" : this.getMonth()+1,                 //月份
		"d+" : this.getDate(),                    //日
		"h+" : this.getHours(),                   //小时
		"m+" : this.getMinutes(),                 //分
		"s+" : this.getSeconds(),                 //秒
		"q+" : Math.floor((this.getMonth()+3)/3), //季度
		"S"  : this.getMilliseconds()             //毫秒
	};
	if(/(y+)/.test(fmt)) {
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	}
	for(var k in o) {
		if(new RegExp("("+ k +")").test(fmt)){
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
		}
	}
	return fmt;
}


$(function(){


	/* 因为跨域问题无法在其他域上加载成功
	$.get('http://www.woyaogexing.com/touxiang/z/ktkeai/index_1.html',{},function(data){
		var imgArr = $(data).find('div.pMain div.txList img');
		if(!!imgArr && imgArr.length>0){
			var path = imgArr.eq(0).attr('src');
			var fileName = path.substr(path.lastIndexOf('/')+1, 9)+path.substr(path.lastIndexOf('.'));
			$('#path').val(path);
			$('#fileName').val(fileName);
		}
	});*/

	$("#insertUserBtn").on('click',function () {
		if(!$("#path").val()){
			alert('请输入头像路径');
			$("#path").focus();
			return;
		}
		if(!$("#fileName").val()){
			alert('请输入头像文件名');
			$("#fileName").focus();
			return;
		}
		if(!$("#age").val()){
			alert('请输入年龄');
			$("#age").focus();
			return;
		}
		var param={
			path:$("#path").val()
			,fileName:$("#fileName").val()
			,age:$("#age").val()
			,gender:$("#gender").val()
			,createTime:new Date()
		};
		$.ajax({
			url: Const.insertURI
			,type:'POST'
			,data: param
			,success:function(data){
				if(data>0){
					$("#path").val('');
					$("#fileName").val('');
					$("#age").val('');
					$("#gender").val('');
					alert('Insert Operation Success !');
					$("#listAllFaceInfoBtn").click();
				}
			}
			,error:function(){
				alert('Insert Operation Failed Check Server Status Please.');
			}
		});
	});

	window.deleteFaceInfo = function(id){
		if(window.confirm("确定要删除吗？")){
			var param={

			};
			$.post(Const.deleteUserURI+id,param,function(data){
				if(data>0){
					alert('Insert Operation Success !');
				}
				$("#listAllFaceInfoBtn").click();
			});
		}
	};

	$("#listAllFaceInfoBtn").on('click',function () {
		var param={};
		var strTmp =
			'<div class="card img-fluid col-2  m-1" style="padding: 0;">' +
			'  <img class="card-img-top" src="${path}" alt="Card image" style="width:100%">' +
			'  <div class="<!--card-img-overlay-->">' +
			'    <b class="card-title">${fileName}</b>' +
			'    <p class="card-text"><small>${createTime} </small></p>' +
			'    <a href="javascript:void(0);" class="btn btn-primary float-lg-right btn-sm" onclick="deleteFaceInfo(${id})">Delete</a>' +
			'  </div>\n' +
			'</div>';
		var str = '';
		$.ajax({
			url: Const.listAllURI
			,type:'GET'
			,data: param
			,success:function(data){
				if($.isArray(data)){
					for(var i=0;i<data.length;i++){
						var tmpStr = strTmp.replace(/\${id}/g,data[i]['id'])
						.replace(/\${path}/g,data[i]['path'])
						.replace(/\${fileName}/g,data[i]['fileName'])
						.replace(/\${createTime}/g,new Date(data[i]['createTime']).format("yyyy-MM-dd hh:mm:ss"))+'\n';
						str+=tmpStr;
					}
					$("#listAllFaceInfoDiv").html(str);
				}
			}
			,error:function(){
				$("#listAllFaceInfoDiv").html(str);
			}
		});
	});

	$("#listAllFaceInfoBtn").click();

});
