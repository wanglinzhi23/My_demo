
var baseURI='http://127.0.0.1:8080/demo';
// var baseURI='/demo';
var Const ={
	insertURI: baseURI+'/insert'
	,updateURI: baseURI+'/update'
	,deleteUserURI: baseURI+'/delete'
	,listAllURI: baseURI+'/listAll'
	,findByIdURI: baseURI+'/findById'
};

$(function(){

	$("#insertUserBtn").on('click',function () {
		if(!$("#nickname").val()){
			alert('请输入昵称');
			$("#nickname").focus();
			return;
		}
		if(!$("#username").val()){
			alert('请输入用户名');
			$("#username").focus();
			return;
		}
		if(!$("#password").val()){
			alert('请输入初始密码');
			$("#password").focus();
			return;
		}
		var param={
			username:$("#username").val()
			,password:$("#password").val()
			,nickname:$("#nickname").val()
			,modifyTime:new Date()
			,modifierId:0
			,modifierName:'ADMIN'
		};
		$.ajax({
			url: Const.insertURI
			,type:'POST'
			,data: param
			,success:function(data){
				if(data>0){
					$("#username").val('');
					$("#password").val('');
					$("#nickname").val('');
					alert('Insert Operation Success !');
					$("#listAllUserBtn").click();
				}
			}
			,error:function(){
				alert('Insert Operation Failed Check Server Status Please.');
			}
		});
	});

	window.deleteUser = function(id){
		if(window.confirm("确定要删除吗？")){
			var param={
				id:id
			};
			$.post(Const.deleteUserURI,param,function(data){
				if(data>0){
					alert('Insert Operation Success !');
				}
				$("#listAllUserBtn").click();
			});
		}
	};

	$("#listAllUserBtn").on('click',function () {
		var param={};
		var str1 =
			'<table class="table table-sm table-hover">' +
			'    <thead class="thead-light">' +
			'      <tr>' +
			'        <th>ID</th>' +
			'        <th>Nickname</th>' +
			'        <th>Username</th>' +
			'        <th>Password</th>' +
			'        <th>Operate</th>' +
			'      </tr>' +
			'    </thead>' +
			'    <tbody>';
		var str2Tmp =
			'      <tr>' +
			'        <td>${Id}</td>' +
			'        <td>${Nickname}</td>' +
			'        <td>${Username}</td>' +
			'        <td>${Password}</td>' +
			'        <td><button type="button" class="btn btn-primary btn-sm" onclick="deleteUser(${Id})">Delete</button></td>' +
			'      </tr>';
		var str3 =
			'    </tbody>' +
			'</table>';
		var str2 = '';
		$.ajax({
			url: Const.listAllURI
			,type:'GET'
			,data: param
			,success:function(data){
				if($.isArray(data)){
					for(var i=0;i<data.length;i++){
						var tmpStr = str2Tmp.replace(/\${Id}/g,data[i]['id'])
						.replace(/\${Username}/g,data[i]['username'])
						.replace(/\${Nickname}/g,data[i]['nickname'])
						.replace(/\${Password}/g,data[i]['password'])+'\n';
						str2+=tmpStr;
					}
					$("#listAllUserDiv").html(str1+str2+str3);
				}
			}
			,error:function(){
				$("#listAllUserDiv").html(str1+str2+str3);
			}
		});
	});

	$("#listAllUserBtn").click();

});
