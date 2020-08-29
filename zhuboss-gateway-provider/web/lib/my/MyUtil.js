Date.prototype.Format = function(fmt)
{ //author: meizz
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
};

MyUtil = function(){
    return {
        genDevNo:function(){
            return Math.floor(Math.random()*10000000000);
        },
        genPwd:function(){
            var r = ['0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z','+','-'];
            var s='';
            for(var i=0;i<6;i++){
                s += r[Math.floor(Math.random()*10000000000)%r.length];
            }
            return s;
        },
        removeNull: function (obj) {
            for (var property in obj) {
                if (obj[property] == null || obj[property] == '') delete obj[property];
            }
            return obj;
        },

        dictFormatter: function(dictType) {
            var me = this;
            return function(value,row,index) {
                return MyUtil.toDictName(value, dictType);
            };
        },

        dictFormatterYNY:function(){
            var me = this;
            return function (value,row,index) {
                if(value == undefined){
                    return '';
                }
                var iconClass = value==1? "icon-ok" : "icon-clear";
                return '<div style="width: 100%;" class="'+iconClass+'">&nbsp;</div>';
            };
        },

        dictFormatterYNN:function(){
            var me = this;
            return function (value,row,index) {
                if(value == undefined){
                    return '';
                }
                var iconClass = value==1? "icon-ok2" : "icon-cancel";
                return '<div style="width: 100%;" class="'+iconClass+'">&nbsp;</div>';
            };
        },

        autoGenTreeId : function(tree){
            if(Array.isArray(tree)){
                for(var i in tree){
                    this.autoGenTreeId(tree[i]);
                }
            }else{
                tree.id = Math.floor(Math.random()*10000000000);
                if(tree.children!=undefined){
                    this.autoGenTreeId(tree.children);
                }
            }
        },

        toDictName: function (value,dictType) {
            if (value==undefined || !dict[dictType]) {
                return value;
            }
            var text = '';
            var arr = value.toString().split(',');
            for (var v in arr) {
                for (var i = 0; i < dict[dictType].length; i++) {
                    if (dict[dictType][i]['value'] == arr[v]) {
                        if (text != '') text += ',';
                        text += dict[dictType][i]['text'];
                    }
                }
            }
            if(text=='') return value;
            return text;
        },

        dateFormatter: function(format){
            return function (value, rec, index) {
                if (value == undefined) {
                    return "";
                }
                /*json格式时间转js时间格式*/
                // value = value.substr(1, value.length - 2);
                var dateValue =  new Date(Date.parse(value.replace(/-/g,"/")));
                if (dateValue.getFullYear() < 1900) {
                    return "";
                }

                return dateValue.Format(format);
            };
        },

        ajaxError:function (message) {
            $.messager.alert('失败',"请求失败("+message.status+message.statusText+")",'error');
        },
        ajaxBeforeSend:function () {
            $.messager.progress({
                title: '提示',
                msg: '正在处理，请稍等。。。'
            });
        },
        ajaxComplete:function () {
            $.messager.progress('close');
        },

        picDialog: function(type,rowid,closeCallback){
            $("<div id='pic-dialog'> </div>").dialog({
                title: '图片',
                closable: true,
                resizable:true,
                modal:true,
                width: 1000,
                height: 650,
                content: '<iframe id="testOne" style="overflow-x:hidden;overflow-y:auto;width:100%;height:98%;padding: 0px;" scrolling="yes" frameborder=0 src="'+'/static/upload.html?type='+type+'&rowid='+rowid+'&_rd='+Math.random()+'"></iframe>',
                buttons: [{
                    text: '取消',
                    iconCls: 'icon-cancel',
                    handler: function () {
                        $('#pic-dialog').dialog('destroy');
                        if(closeCallback){
                            closeCallback();
                        }
                    }
                }],
                // 点击右上角的“X”图标时
                onClose: function () {
                    $(this).dialog('destroy');
                    if(closeCallback){
                        closeCallback();
                    }
                }
            });
        },

        imgShow: function (_this){
            var outerdiv="#outerdiv",innerdiv= "#innerdiv",bigimg="#bigimg";
            if($(outerdiv).length == 0){
                $("body").append('<div id="outerdiv" style="position:fixed;top:0;left:0;background:rgba(0,0,0,0.7);z-index:2;width:100%;height:100%;display:none;">' +
                    '<div id="innerdiv" style="position:absolute;">' +
                    '<img id="bigimg" style="border:5px solid #fff;" src="" /></div></div>');
            }
            var src = _this.attr("src");//获取当前点击的pimg元素中的src属性
            $(bigimg).attr("src", src);//设置#bigimg元素的src属性

            /*获取当前点击图片的真实大小，并显示弹出层及大图*/
            $("<img/>").attr("src", src).load(function(){
                var windowW = $(window).width();//获取当前窗口宽度
                var windowH = $(window).height();//获取当前窗口高度
                var realWidth = this.width;//获取图片真实宽度
                var realHeight = this.height;//获取图片真实高度
                var imgWidth, imgHeight;
                var scale = 0.8;//缩放尺寸，当图片真实宽度和高度大于窗口宽度和高度时进行缩放
                if(realHeight>windowH*scale) {//判断图片高度
                    imgHeight = windowH*scale;//如大于窗口高度，图片高度进行缩放
                    imgWidth = imgHeight/realHeight*realWidth;//等比例缩放宽度
                    if(imgWidth>windowW*scale) {//如宽度扔大于窗口宽度
                        imgWidth = windowW*scale;//再对宽度进行缩放userLoaderImpl
                    }
                } else if(realWidth>windowW*scale) {//如图片高度合适，判断图片宽度
                    imgWidth = windowW*scale;//如大于窗口宽度，图片宽度进行缩放
                    imgHeight = imgWidth/realWidth*realHeight;//等比例缩放高度
                } else {//如果图片真实高度和宽度都符合要求，高宽不变
                    imgWidth = realWidth;
                    imgHeight = realHeight;
                }
                $(bigimg).css("width",imgWidth);//以最终的宽度对图片缩放

                var w = (windowW-imgWidth)/2;//计算图片与窗口左边距
                var h = (windowH-imgHeight)/2;//计算图片与窗口上边距
                $(innerdiv).css({"top":h, "left":w});//设置#innerdiv的top和left属性
                $(outerdiv).fadeIn("fast");//淡入显示#outerdiv及.pimg
            });

            $(outerdiv).click(function(){//再次点击淡出消失弹出层
                $(this).fadeOut("fast");
            });
        },

        openTab:function (page,func,obj,src) {
            var title = '';
            if(obj){
                title += "【"+ obj+"】";
            }
            title += page + "-" + func;

            window.parent.openTab(title,src);
        },

        //获取url中的参数
        getUrlParam: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg);  //匹配目标参数
            if (r != null) return unescape(r[2]); return null; //返回参数值
        }
    };
}();

/*
* 使用方法:
 * 开启:MaskUtil.mask();
 * 关闭:MaskUtil.unmask();
 *
 * MaskUtil.mask('其它提示文字...');
 */

var MaskUtil = (function(){
    var $mask,$maskMsg;
    var defMsg = '正在处理，请稍待。。。';
    function init(){
        if(!$mask){
            $mask = $("<div  class=\"datagrid-mask mymask\"></div>").appendTo("body");
        }
        if(!$maskMsg){
            $maskMsg = $("<div style='height: auto;' class=\"datagrid-mask-msg mymask\">"+defMsg+"</div>")
                .appendTo("body").css({'font-size':'12px'});
        }
        $mask.css({width:"100%",height:$(document).height()});
        var scrollTop = $(document.body).scrollTop();
        $maskMsg.css({
            left:( $(document.body).outerWidth(true) - 190 ) / 2
            ,top:( ($(window).height() - 45) / 2 ) + scrollTop
        });
    }
    return {
        mask:function(msg){
            init();
            $mask.show();
            $maskMsg.html(msg||defMsg).show();
        }
        ,unmask:function(){
            $mask.hide();
            $maskMsg.hide();
        }
    }
}());

$.ajaxSetup({
    complete: function (XMLHttpRequest, textStatus) {
        if (textStatus == "error") {
            var sessionStatus = XMLHttpRequest.getResponseHeader("SessionStatus");
            if (sessionStatus == "not_login") {
                window.top.location.href = "/static/login.html";
            }
        }
    }
});

/**
 *对Date的扩展，将 Date 转化为指定格式的String
 *月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
 *年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 *例子：
 *(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
 *(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
 */
Date.prototype.format = function (fmt) {
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