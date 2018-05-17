<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Demo Application</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="/js/lib/bootstrap_v4/css/bootstrap.min.css">
	<script src="/js/lib/jquery-3.2.1.min.js"></script>
	<script src="/js/lib/popper-1.12.5.min.js"></script>
	<script src="/js/lib/bootstrap_v4/js/bootstrap.min.js"></script>
	<script src="/js/demo-app.js" type="text/javascript"></script>
	<meta name="referrer" content="no-referrer"/>
</head>

<body>
    <nav class="navbar navbar-expand-sm bg-info navbar-dark">
        <!-- Brand -->
        <a class="navbar-brand" href="#">方案1: 赋能方案 - 业务项目功能</a>
        <!-- Links -->
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" data-parent="#accordion" data-toggle="collapse" href="#listAllUser">List all Face</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" data-parent="#accordion" data-toggle="collapse" href="#insertUser">Add Face</a>
            </li>
            <li class="nav-item">
                <a class="btn btn-warning ml-4" href="/demo-ui.html" target="_blank" >基础服务</a>
            </li>
        </ul>
    </nav>
    <br>

    <div class="container">
        <div id="accordion">

            <div class="m-2">
                <div id="listAllUser" class="collapse show">
                    <h3>查看所有头像</h3>
                    <p>项目功能: 查看所有头像。</p>
                    <div id="listAllFaceInfoDiv" class="m-4 row">
                    </div>
                    <br>
                    <button type="button" class="btn btn-primary" id="listAllFaceInfoBtn">Refresh</button>
                </div>
            </div>

            <div class="m-2">
                <div id="insertUser" class="collapse">
                    <h3>新增头像信息</h3>
                    <p>项目功能: 添加头像信息。</p>
                    <form>
                        <div class="form-group">
                            <label for="path">Path:</label>
                            <input type="text" class="form-control" id="path" value="http://img2.woyaogexing.com/2018/04/07/45fafda1611de46f!400x400_big.jpg"/>
                        </div>
                        <div class="form-group">
                            <label for="fileName">Username:</label>
                            <input type="text" class="form-control" id="fileName" value="45a1611de46f.jpg"/>
                        </div>
	                    <div class="form-group">
		                    <label for="age">Age:</label>
		                    <input type="number" class="form-control" id="age" value="21"/>
	                    </div>
                        <div class="form-group">
                            <label for="gender">Gender:</label>
                            <div class="form-group">
	                            <label class="form-check-label">
		                            <input class="form-check-input" type="radio" name="gender" checked> Female
	                            </label>
	                            <label class="form-check-label">
		                            <input class="form-check-input" type="radio" name="gender"> Male
                                </label>
                            </div>
                        </div>
                        <button type="button" class="btn btn-primary" id="insertUserBtn">Submit</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>