var Topology = function () {
    return function () {
        // 获取下拉列表
        function c(a) {
            $("#Subimglist").html("");
            if (a.length > 0) {
                for (var b = 0; b < a.length; b++) {
                    chartName = a[b].chartName;
                    id = a[b].id;
                    var f = "<option value='" + id + "'>" + chartName + "</option>";
                    $("#Subimglist").append(f)
                }
            }
        }
        this.getData = function (a, g, f) {
            $("body").showLoading();
            $.ajax({
                type: "GET",
                data: g,
                async: false,
                url: a,
                dataType: "json",
                error: MyUtil.ajaxError,
                beforeSend: MyUtil.ajaxBeforeSend(),
                complete: MyUtil.ajaxComplete()
            });
        };
    }
}();

var timer; //设置定时器
var lvShanTimer; //绿闪
var getChartId = getUrlParam("chartId");

jQuery(document).ready(function (a) {
    a.cookie("head-menu", "menu-t2");
    a.cookie("left-menu", "DistributDiagram");
    var e = new Topology;
    null != a.cookie("stationId") && void 0 !== a.cookie("stationId") || Substation.DOMOperator.getSubstation();
    void 0 != a.cookie("stationId") && (e.getData("/station/chart/query", "stationId=" + a.cookie("stationId"), "default"), a("#StationName").html(a.cookie("subName")),


        a(document).keydown(function (a) {
            27 === a.keyCode && window.location.reload()
        }),

        $("#allDOM").click(function () {
            var chartId = $("#Subimglist").val()
            window.open("/static/distribute.html?full=true&chartId=" + chartId);
        }),
        a("#Distopology").overscroll(),
        a("#Distopology").on("mousewheel DOMMouseScroll", function (b) {
            0 < (b.originalEvent.wheelDelta && (0 < b.originalEvent.wheelDelta ? 1 : -1) || b.originalEvent.detail && (0 < b.originalEvent.detail ? -1 : 1)) ? c(a("svg"), 1) : c(a("svg"), -1)
        }),
        a("#myModal").on("shown.bs.modal", function () {
            Substation.DOMOperator.pagenation("/station/query", 1, 8)
        }),
        a("#myModal").on("hide.bs.modal", function () {
            a("#tableSubName").html("");
            a(".substationlist").val("")
        }), a("#yesBtn").click(function (b) {
        b = selectedInfoModal;
        Substation.DOMOperator.yesBtnClick(b);
        a("#myModal").modal("hide");
        e.getData("/station/chart/query", "stationId=" + a.cookie("stationId"), "default");     // 加载第一张svg
        // 切换站点后，立即展示对应svg
        s = a("#Subimglist").val();
        getChartId = s;
        clearInterval(timer);
        getChartImg();
    }))

    // =========================9-27 短信登录验证码  去掉获取按钮
    // $('#getCode').click( () => {
    //     $('#getCode input').attr('disabled', true)
    //     $(".axc-success").show(function () {
    //         $(this).html('登录密码验证中... ...')
    //         $(this).css({
    //             'color': 'red',
    //             'font-size': '19px'})
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: '/distribute/control/send_code',
    //         contentType: "application/json; charset=utf-8",
    //         data: JSON.stringify({ dataId: $('#myModalCode').attr('k_id') , value: $('#myModalCode').attr('kSwitch')  }),
    //         dataType: "json",
    //         success: function (res) {
    //             if (res.result) {
    //                 // $(".axc-success").hide()
    //                 $(".axc-success").show(function () {
    //                     $(this).html('登录密码发送成功！')
    //                     $(this).css({
    //                         'color': 'red',
    //                         'font-size': '19px'})
    //                 })
    //             } else {
    //                 $(".axc-success").show(function () {
    //                     $(this).html(res.msg)
    //                     $(this).css({
    //                         'color': 'red',
    //                         'font-size': '19px'
    //                     })
    //                 })
    //             }
    //         },
    //         error: function (message) {
    //             $(".axc-success").show(function () {
    //                 $(this).html('登录密码发送失败！请联系管理员！')
    //                 $(this).css({
    //                     'color': 'red',
    //                     'font-size': '19px'
    //                 })
    //             })
    //         }
    //     });
    // })
    // 分闸、合闸的弹窗信息验证
    $('#confirmCode').click(() => {
        if (ExecutorV == GuarderV) {
            $(".axc-success").show(function () {
                $(this).html('警告：执行人和监护人不能相同！')
                $(this).css({
                    'color': 'red',
                    'font-size': '19px'
                })
            })
        } else if ($('#codeInput').val() == '' || $('#meterNumber').val() == '') {
            $(".axc-success").show(function () {
                $(this).html('警告：请将登录密码和柜体编号填写完整！')
                $(this).css({
                    'color': 'red',
                    'font-size': '19px'
                })
            })
        } else {
            $('#confirmCode').attr('disabled', true)
            $.ajax({
                type: "POST",
                url: '/distribute/control',
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({ dataId: $('#myModalCode').attr('k_id'), value: $('#myModalCode').attr('kSwitch'), verifyCode: $('#codeInput').val(), devNo: $('#meterNumber').val(), executor: ExecutorV, guarder:GuarderV  }),
                dataType: "json",
                success: function (res) {
                    if (res.result) {
                        $(".axc-success").show(function () {
                            $(this).html('命令下达成功，等待执行结果。。。')
                            $(this).css({
                                'color': 'red',
                                'font-size': '19px'
                            })
                        })
                        $.ajax({
                            type: "POST",
                            url: '/distribute/control/wait_state_change',
                            contentType: "application/json; charset=utf-8",
                            data: JSON.stringify({ dataId: $('#myModalCode').attr('k_id') , value: $('#myModalCode').attr('kSwitch')  }),
                            dataType: "json",
                            success: function (res) {
                                if (res.result) {
                                    $(".axc-success").show(function () {
                                        $(this).html(res.msg)
                                        $(this).css({
                                            'color': 'red',
                                            'font-size': '19px'
                                        })
                                    })
                                } else {
                                    $(".axc-success").show(function () {
                                        $(this).html('')
                                        $(this).css({
                                            'color': 'red',
                                            'font-size': '19px'
                                        })
                                    })
                                    $('#myModalCodeError').modal().show()
                                    $('#myModalCodeErrorCont').html(res.msg)
                                }
                            }
                        })
                        // $('#myModalCode').modal('hide')
                    } else {
                        $(".axc-success").show(function () {
                            $(this).html('警告：' + res.msg)
                            $(this).css({
                                'color': 'red',
                                'font-size': '19px'
                            })
                        })
                    }
                },
                error: function (message) {
                    // $('#myModalCode').modal('hide')
                    $(".axc-success").show(function () {
                        $(this).html('操作失败！请联系管理员！')
                        $(this).css({
                            'color': 'red',
                            'font-size': '19px'
                        })
                    })
                }
            });
        }
    })
    // ============================9-27 短信登录密码
});

function c(a, c) {
    var b = a.width(),
        d = a.height();
    switch (c) {
        case 1:
            a.width(1.1 * b);
            a.height(1.1 * d);
            break;
        case - 1: a.width(b / 1.1),
            a.height(d / 1.1)
    }
}

// --------------------------------------------------- add by zhuzhengquan

$(function (a) {
    getChartImg();
    if (getChartId) {
        $("#Subimglist").val(getChartId);
    }
    a("#Subimglist").change(function (b) {
        b = a("#Subimglist").val();
        getChartId = b;
        clearInterval(timer);
        getChartImg();

    })
});
function getChartImg() {

    var full = getUrlParam("full")
    var chartId = getChartId ? getChartId : $("#Subimglist").val();
    $.ajax({
        type: "GET",
        async: false,
        url: '/chart/chart?chartId=' + chartId,
        success: function (res) {
            if (full) {
                var b = $(window).width(),
                    g = $(window).height();
                $("#distributeWrap").html("");
                $("#distributeWrap").css("overflow", "hidden");
                $("#distributeWrap").append('<input type="hidden" id="Subimglist" value="' + chartId + '"><div id="Distopology" class="col-md-12 distribut-draw" style="background:#000;width:' + b + "px;height:" + g + 'px;overflow:scroll;"></div>');
            }
            $(".distribut-draw").html("");
            $(".distribut-draw").html(res);

            $("#Distopology").on("mousewheel DOMMouseScroll",
                function (b) {
                    0 < (b.originalEvent.wheelDelta && (0 < b.originalEvent.wheelDelta ? 1 : -1) || b.originalEvent.detail && (0 < b.originalEvent.detail ? -1 : 1)) ? c($("svg"), 1) : c($("svg"), -1)
                });
            $("#div").overscroll();
            //提取变量刷新SVG
            refreshVar();
            //启动定时器，每5秒钟刷新一次
            // setTimeout(refreshVar, 1000 * 5)
            timer = setInterval(refreshVar, 1000 * 5)
            lvShanTimer = setInterval(lvShanFun, 1000 * 0.5)
        },
        error: function (message) {
            $.messager.alert('失败',"请求失败("+message.status+message.statusText+")",'error');
        }
    });
}
var map;
var lvShan = [];
function refreshVar() {
    // var getChartId = getUrlParam("chartId");
    var chartId = getChartId ? getChartId : $("#Subimglist").val();
    $.ajax({
        type: "GET",
        async: false,
        url: '/distribute/var?chartId=' + chartId,
        dataType: "json",
        success: function (res) {
            map = res;
            lvShan = [];
            //TODO 刷新SVG
            getChildNode($(".distribut-draw")[0]);
        },
        error: function (message) {
            $.messager.alert('失败',"请求失败("+message.status+message.statusText+")",'error');
        }
    });
}

function lvShanFun() {
    lvShan.forEach(function (obj) {
        if(obj.style.display == 'none'){
            obj.style.display = 'block';
        }else{
            obj.style.display = 'none';
        }
    });
}
// //获取url中的参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}


function getChildNode(node) {
    //先找到子结点
    var nodeList = node.childNodes;
    for (var i = 0; i < nodeList.length; i++) {
        var childNode = nodeList[i];
        var k_id;
        checkNode(childNode);
        getChildNode(childNode);
    }
}

function checkNode(childNode) {
    if (childNode.attributes == undefined || ((k_id = childNode.getAttribute('k_id')) == null && (k_diagram = childNode.getAttribute('k_diagram')) == null)) {
        return;
    }
    //子画面
    if (k_diagram != undefined) {
        if (k_diagram != undefined && childNode.getAttribute("addClickHandleFlag") !== "1") {
            childNode.setAttribute("addClickHandleFlag", "1")
            childNode.addEventListener('click', function () {
                showDiagram(this)
            }, false);
            childNode.style.cursor = "pointer";
        }
        return;
    }
    //
    if (map[k_id] == undefined) {
        return;
    }
    if(k_id.indexOf("8000-")>-1 && map[k_id]==2  && childNode.getAttribute('k_show')==2){
        lvShan.push(childNode);
    }
    //弹框，查看更多内容
    var kMore = childNode.getAttribute('k_more');
    if (kMore != undefined && childNode.getAttribute("addClickHandleFlag") !== "1") {
        childNode.setAttribute("addClickHandleFlag", "1")
        childNode.addEventListener('click', function () {
            showMore(this)
        }, false);
        childNode.style.cursor = "pointer";
    }
    //
    var kZb = childNode.getAttribute('k_zb');
    if (kZb != undefined ) {
        if(childNode.getAttribute("addClickHandleFlag") !== "1"){
            childNode.setAttribute("addClickHandleFlag", "1")
            childNode.addEventListener('click', function () {
                // console.log(555555)
                zb(this)
            }, false);
            childNode.style.cursor = "pointer";
        }
        return
    }
    //
    if (childNode.nodeName == 'text') { //text节点(遥测)
        childNode.innerHTML = map[k_id];

    } else { //g节点(开关量)
        //点击事件
        var kSwitch = childNode.getAttribute('k_switch');
        if (kSwitch != undefined && childNode.getAttribute("addClickHandleFlag") !== "1") {
            childNode.setAttribute("addClickHandleFlag", "1");
            childNode.setAttribute('ostroke',childNode.style.stroke); //原始颜色
            childNode.addEventListener('click', function () {
                doSwitch(this)
            }, false);
            childNode.addEventListener('mousedown', function (e) {
                if(e.button != 2 || childNode.getAttribute('k_id').indexOf("U-") == 0 ) return; //非鼠标右键，非自定义变量
                var action = (childNode.style.stroke == "blue")?"to_remote":"to_local";
                var actionName = (childNode.style.stroke == "blue")?"复位":"置位";
                if(window.confirm("确定要"+ actionName +"吗")){
                    $.ajax({
                        type: "POST",
                        url: '/distribute/'+action,
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({
                            dataId: childNode.getAttribute('k_id'),
                            stationId:$.cookie("stationId"),
                            val:map[childNode.getAttribute('k_id')]
                        }),
                        dataType: "json",
                        success: function (res) {
                            if (res.result) {
                                alert('操作成功！');
                            }
                        }
                    });
                }
            }, true);
            childNode.style.cursor = "pointer";
        }
        //显示控制
        var val = map[k_id];
        if(map.local[k_id] != undefined){
            val = map.local[k_id];
            childNode.style.stroke = 'blue'; //置位的开关显示蓝色
        }else if(childNode.style.stroke != childNode.getAttribute('ostroke')){
            childNode.style.stroke = childNode.getAttribute('ostroke');
        }
        var kShow = childNode.getAttribute('k_show');
        if (kShow != undefined) {
            if (kShow == val) {
                childNode.style.display = 'block';
            } else {
                childNode.style.display = 'none';
            }
        }
    }
}

function showDiagram(node) {
    var chartId = node.getAttribute('k_diagram');
    window.location.href="/static/distribute.html?chartId=" + chartId

}

function showMore(node) {
    var kId = node.getAttribute('k_id');
    $.ajax({
        type: "GET",
        url: '/distribute/show_more?kid=' + kId,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (res) {
            //TODO 弹框渲染，弹框title为devName，一维table,除meterKind显示全部列，其它只显示前3列
            // alert(JSON.stringify(res));
            if (res) {
                if (res.meterKind == 'E') {
                    var html = '';
                    $('#myModaldetail').modal().show();
                    var items = res.items;
                    res.devName != undefined ? $('#devName').html("回路名称：" + res.devName) : $('#devName').html("");
                    if (items && items.length > 0) {
                        items.forEach(function (item, index) {
                            html += '<tr>'
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.itemName != undefined ? item.itemName : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.item != undefined ? item.item : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.current != undefined ? item.current : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.avg != undefined ? item.avg : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.max != undefined ? item.max : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.maxTime != undefined ? item.maxTime : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.min != undefined ? item.min : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.minTime != undefined ? item.minTime : '-') + '</td>';
                            html += '</tr>'
                        })
                        $('#chartTable').html(html);
                    }
                } else {
                    var html = '';
                    $('#myModaldetail1').modal().show();
                    var items = res.items;
                    res.devName != undefined ? $('#devName1').html("回路名称：" + res.devName) : $('#devName1').html("");
                    if (items && items.length > 0) {
                        items.forEach(function (item, index) {
                            html += '<tr>'
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.itemName != undefined ? item.itemName : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.item != undefined ? item.item : '-') + '</td>';
                            html += '<td style="text-align: center; vertical-align: middle; ">' + (item.current != undefined ? item.current : '-') + '</td>';
                            html += '</tr>'
                        })
                        $('#chartTable1').html(html);
                    }
                }
            }
        },
        error: function (message) {
            $.messager.alert('失败',"请求失败("+message.status+message.statusText+")",'error');
        }
    });
}

function zb(node){
    // console.log(node)
    // console.log(1)
    var kId = node.getAttribute('k_id');
    var devId = kId.substring(5);
    $('#popupAlarmModal').modal().show()
    //弹框 '/static/popup_alarm.html?devId=' + devId
    $('#popupIframe').attr("src", '/static/popup_alarm.html?devId=' + devId)
}
// 获取监护人、执行人
ExecutorV = ''
GuarderV = ''
function getExactor () {
    $.ajax({
        type: "GET",
        async: false,
        url: '/station/operator/query?stationId=' + $.cookie("stationId"),
        dataType: "json",
        success: function (res) {
            ExecutorV = res.rows[0].id
            GuarderV = res.rows[0].id
            var Options = ''
            for (var h = 0; h < res.rows.length; h++) {
                Options += '<option value="'+ res.rows[h].id +'">'+ res.rows[h].fullName +'</option>'
            }
            $('#selectExecutor').html(Options)
            $('#selectGuarder').html(Options)
        },
        error: function (message) {

        }
    });
}
// 登录密码弹窗
function doSwitch(node) {
    var k_id = node.getAttribute('k_id');
    var kSwitch = node.getAttribute('k_switch'); //1->0 0->1

    //if 自定义变量
    if(k_id.indexOf("U-") == 0 ){
        $.ajax({
            type: "POST",
            url: '/distribute/control/var',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({ dataId: k_id.substring(2), value: kSwitch }),
            dataType: "json",
            success: function (res) {
                $(".axc-success").show(function () {
                    $(this).html('操作成功！')
                    $(this).css({
                        'color': 'red',
                        'font-size': '19px'
                    })
                })
            },
            error: function (message) {
                // $('#myModalCode').modal('hide')
                $(".axc-success").show(function () {
                    $(this).html('操作失败！请联系管理员！')
                    $(this).css({
                        'color': 'red',
                        'font-size': '19px'
                    })
                })
            }
        });
        return
    }

    //已置位的开关
    if(node.style.stroke == 'blue'){
        $.ajax({
            type: "POST",
            url: '/distribute/control/var_local',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({ stationId:$.cookie("stationId"),dataId: k_id, value: kSwitch }),
            dataType: "json",
            success: function (res) {
                $(".axc-success").show(function () {
                    $(this).html('操作成功！')
                    $(this).css({
                        'color': 'red',
                        'font-size': '19px'
                    })
                })
            },
            error: function (message) {
                // $('#myModalCode').modal('hide')
                $(".axc-success").show(function () {
                    $(this).html('操作失败！请联系管理员！')
                    $(this).css({
                        'color': 'red',
                        'font-size': '19px'
                    })
                })
            }
        });
        return
    }

    var operate = (kSwitch == 0 ? " 分闸 " : " 合闸 ");
    $('#myModalCode').modal().show()
    $('#myModalCode').attr('k_id', k_id)
    $('#myModalCode').attr('kSwitch', kSwitch)
    // $('#getCode input').attr('disabled', false)   // 去掉获取短信验证码按钮
    $('#confirmCode').attr('disabled', false)
    $(".axc-success").html('')
    $('#codeInput').val('')
    $('#meterNumber').val('')
    // 执行人、监护人change、仪表柜体编号
    getExactor()
    var sel = document.getElementById("selectExecutor");
    sel.onchange = function () {
        ExecutorV = this.value
    }
    var sel1 = document.getElementById("selectGuarder");
    sel1.onchange = function () {
        GuarderV = this.value
    }
    $('#myModalLabelCode').html("您确定要" + operate + "吗？(如若确定，请进行信息验证：)")
        
}

