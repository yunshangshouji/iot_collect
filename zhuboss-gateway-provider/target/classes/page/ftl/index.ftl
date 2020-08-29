<html>
<head>
<title>控制台首页</title>
</head>
<body>

总网关数:${list?size}

<table>
<tr>
	<td>网关编号</td>
	<td>来源IP</td>
	<td></td>
	<td></td>
</tr>
<#list list as row>
<tr>
	<td>${row.devNo!}</td>
	<td>${row.ip}</td>
	<td><#if row.text??>${row.text}</#if></td>
	<td><a target="_blank" href="/debug/${row.devNo!}">debug</a></td>
</tr>
</#list>
</table>

</body>
</html>