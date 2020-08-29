<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>绑定账户列表</title>
    <!-- 新 Bootstrap4 核心 CSS 文件 -->
    <link rel="stylesheet" href="/static/lib/bootstrap/bootstrap-4.4.1-dist/css/bootstrap.css">

    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/static/lib/easyui/jquery.min.js"></script>

    <script src="/static/lib/bootstrap/bootstrap-4.4.1-dist/js/bootstrap.js"></script>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/static/lib/bootstrap/bootstrap-table.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="/static/lib/bootstrap/bootstrap-table.min.js"></script>
    <!-- Latest compiled and minified Locales -->
    <script src="/static/lib/bootstrap/bootstrap-table-zh-CN.min.js"></script>

    <script type="text/javascript" src="/static/lib/jquery/jquery.serializeObject.min.js"></script>

    <style>
        html { font-size: 1rem; }

        @media (min-width: 576px) {
            html { font-size: 1.25rem; }
        }
        @media (min-width: 768px) {
            html { font-size: 1.5rem; }
        }
        @media (min-width: 992px) {
            html { font-size: 1.75rem; }
        }
        @media (min-width: 1200px) {
            html { font-size: 2rem; }
        }
    </style>

</head>
<body>
<#if user??>
    <ul class="list-group">
        <li class="list-group-item active">已绑定账户</li>
        <li class="list-group-item">${user.nickName}</li>
        <li class="list-group-item">${user.mail}</li>
    </ul>
    <button id="unBind" class="btn btn-primary btn-lg">
        取消绑定
    </button>

<#else>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item active" aria-current="page">您尚未绑定账号</li>
        </ol>
    </nav>
    <form role="form" id="form1">
        <div class="form-group">
            <label for="appId">登录号</label>
            <input type="text" class="form-control" id="mail" name="mail" placeholder="邮箱">
        </div>
        <div class="form-group">
            <label for="loginPwd">密码</label>
            <input type="password" class="form-control" id="loginPwd" name="loginPwd" placeholder="">
        </div>
    </form>
    <button id="submitBind" class="btn btn-primary btn-lg" >
        绑定验证
    </button>

</#if>



<div id="successAlert" class="alert alert-success" style="display: none">
    <a href="#" class="close" data-dismiss="successs">&times;</a>
    <strong>成功：</strong><a id="successText"></a>
</div>

<div id="warnAlert" class="alert alert-warning" style="display: none">
    <a href="#" class="close" data-dismiss="alert">&times;</a>
    <strong>警告！</strong><a id="warnText"></a>
</div>

</body>
</html>

<script>
    function processResult(res){
        if(res.result){
            $("#successText").html(res.msg);
            $("#successAlert").show();
            $("#warnAlert").hide();
            setTimeout(function(){ window.location.reload(); }, 2000);
        }else{
            $("#warnText").html(res.msg);
            $("#warnAlert").show();
            $("#successAlert").hide();
        }
    }
    $(function(){
        $("#submitBind").click(
            function () {
                $.ajax({
                    type: "POST",
                    url: '/user/register/wx/bind/bind',
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify($('#form1').serializeObject()),
                    dataType: "json",
                    success: processResult
                })
            }
        );

        $("#unBind").click(function () {
            $.ajax({
                type: "GET",
                url: '/user/register/wx/bind/unbind',
                dataType: "json",
                success: processResult
            });
        });
    });

</script>