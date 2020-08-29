<html>
<head>
<title>控制台首页</title>
</head>
<body>

采集器:${devNo}
<hr>

<input type="radio" id="dataType1" name="dataType" value="hex" checked>16进制Hex</input>
<input type="radio" id="dataType2" name="dataType" value="ascii" >ASCII</input>
<textarea id="sendText" style="width: 90%;height:200px;">
</textarea>
<input type="button" value="发送" onClick="doSend();"/>

<hr>
通讯内容: 按F12打开控制台，查看WebSocket Messages !

</body>
</html>

<script>
function doSend(){
    if(document.getElementById('dataType1').checked){
        ws.send(document.getElementById('sendText').value);
    }else{
        ws.send('ascii:'+document.getElementById('sendText').value);
    }
}

var ws = new WebSocket("wss://"+ window.location.host +"/echo");

ws.onopen = function(evt) { 
  console.log("Connection open ..."); 
  ws.send("cid:${devNo}");
};

ws.onmessage = function(evt) {
  console.log( "Received Message: " + evt.data);
};

ws.onclose = function(evt) {
  console.log("Connection closed.");
};

</script>