<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>${title}</title>
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body style="padding-left: 10px;padding-right: 10px;">
<div class="page-header">
    <h1>${title}
        <small>${version}</small>
    </h1>
</div>
<p>${description}</p>
<h1>目录</h1>
<ul class="list-group">
    <#list modules?keys as key>
    <#assign module=modules[key] />
        <li class="list-group-item"><a href="#div_${module.name}">${module.description}</a></li>
    </#list>
</ul>

<h1>接口</h1>
<div class="panel-group" id="accordion">
<#list modules?keys as key>
    <#assign module=modules[key] />
    <div class="panel panel-default" id="div_${module.name}">
        <div class="panel-heading">
            &nbsp;
            <h4 class="panel-title" style="float:left">
                <a data-toggle="collapse" data-parent="#accordion"
                   href="#${module.name}">
                    ${module.description} &nbsp;&nbsp;
                </a>
            </h4>
            <div style="float:right"><a href="#">首页</a></div>
        </div>
        <div id="${module.name}" class="panel-collapse">
            <div class="panel-body">
                <div class="panel-group" id="detail_${module.name}">
                   <#list module.functions as function>
                       <div class="panel panel-info">
                           <div class="panel-heading">
                               <h4 class="panel-title">
                                   <a data-toggle="collapse" data-parent="#detail_${module.name}"
                                      href="#${function.id}">
                                       ${function.summary} &nbsp;&nbsp; ${function.method}&nbsp;${function.path}
                                   </a>
                               </h4>
                           </div>
                           <div id="${function.id}" class="panel-collapse">
                               <div class="panel-body">
                                   <#if function.notes??>
                                   <div class="panel panel-default">
                                       <div class="panel-body">
                                           ${function.notes}
                                       </div>
                                   </div>
                                   </#if>
                                   <#assign itemGroups=[function.queryItems,function.bodyItems,function.result.itemList] />
                                   <#list itemGroups as itemGroup>
                                        <#if itemGroup?size &gt; 0>
                                            <#if itemGroup_index ==0><h4>Query参数</h4></#if>
                                            <#if itemGroup_index ==1><h4>Body参数</h4></#if>
                                            <#if itemGroup_index ==2><h4>Response</h4>${function.result.typeName}</#if>
                                           <table class="table table-striped table-hover">
                                               <thead>
                                               <tr><th width="100">Name</th><th width="80">DataType</th><th width="80">NotNull</th><th width="120">Example</th><th>Description</th>
                                               </tr>
                                               </thead>
                                           <#list itemGroup as item>
                                                <tr><td>${item.name!}</td><td>${item.type}</td><td>${item.required?string('Y', '')!}</td><td>${item.example!}</td><td>${item.description!}</td></tr>
                                                <#if item.childItems?size &gt; 0>
                                                <tr><td></td><td colspan="4">
                                                <table class="table table-bordered">
                                                    <thead>
                                                        <tr><th width="100">Name</th><th width="80">DataType</th><th width="80">NotNull</th><th width="120">Example</th><th>Description</th>
                                                    </thead>
                                                    <tbody>
                                                    <#list item.childItems as childItem>
                                                        <tr><td>${childItem.name!}</td><td>${childItem.type}</td><td>${item.required?string('Y', '')!}</td><td>${childItem.example!}</td><td>${childItem.description!}</td></tr>
                                                    </#list>
                                                </tbody>
                                                </table>
                                                        </td></tr>
                                                </#if>
                                           </#list>
                                           </table>
                                        </#if>
                                   </#list>
                               </div>
                           </div>
                       </div>
                   </#list>
                </div>
            </div>
        </div>
    </div>
</#list>
</div>

</body>
</html>