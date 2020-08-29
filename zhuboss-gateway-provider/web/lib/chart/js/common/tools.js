var Substation = {
    Common: {
        showDataOnSVG: function (a, b) {
            function c(a) {
                var b = new Map,
                    c;
                if (a != null) {
                    for (var i in a) {
                        c = $("#text" + i);
                        c.text(a[i]);
                    }

                }

            }
           
            function d(a, b, c) {
                h = [];
                $.each(b,
                    function (a, b) {
                        var d;
                        if ("P" == c && ("P" == b.fParamCode || "Q" == b.fParamCode || "S" == b.fParamCode || "PF" == b.fParamCode)) {
                            switch (b.fParamCode) {
                                case "P":
                                    d = "有功功率";
                                    break;
                                case "Q":
                                    d = "无功功率";
                                    break;
                                case "S":
                                    d = "视在功率";
                                    break;
                                case "PF":
                                    d = "功率因数"
                            }
                            f(b, d)
                        }
                        if ("I" == c && b.fParamCode.substring(0, 1) == c) {
                            switch (b.fParamCode) {
                                case "Ia":
                                    d = "A相电流";
                                    break;
                                case "Ib":
                                    d = "B相电流";
                                    break;
                                case "Ic":
                                    d = "C相电流"
                            }
                            f(b, d)
                        }
                        if ("U" == c && b.fParamCode.substring(0, 1) == c) {
                            switch (b.fParamCode) {
                                case "Ua":
                                    d = "A相电压";
                                    break;
                                case "Ub":
                                    d = "B相电压";
                                    break;
                                case "Uc":
                                    d = "C相电压";
                                    break;
                                case "Uab":
                                    d = "AB线电压";
                                    break;
                                case "Ubc":
                                    d = "BC线电压";
                                    break;
                                case "Uca":
                                    d = "CA线电压"
                            }
                            f(b, d)
                        }
                        if ("E" == c && b.fParamCode.substring(0, 1) == c) {
                            switch (b.fParamCode) {
                                case "EPI":
                                    d = "正向有功总电能"
                            }
                            f(b, d)
                        }
                        if ("UnB" == c && ("VUB" == b.fParamCode || "CUB" == b.fParamCode)) {
                            switch (b.fParamCode) {
                                case "VUB":
                                    d = "电压三相不平衡";
                                    break;
                                case "CUB":
                                    d = "电流三相不平衡"
                            }
                            f(b, d)
                        }
                        if ("Max" == c && "MD" == b.fParamCode) {
                            switch (b.fParamCode) {
                                case "MD":
                                    d = "当月最大需量"
                            }
                            f(b, d)
                        }
                    });
                $("#detailTable").html("");
                $("#detailTable").html("\x3ctable id\x3d'table'\x3e\x3c/table\x3e");
                $("#table").bootstrapTable({
                    data: h,
                    columns: [[{
                        field: "fParamCode",
                        title: "参数",
                        colspan: 1,
                        rowspan: 3,
                        valign: "middle",
                        align: "center"
                    },
                    {
                        field: "fValue",
                        title: "最新值",
                        colspan: 1,
                        rowspan: 3,
                        valign: "middle",
                        align: "center"
                    },
                    {
                        field: "",
                        title: "当日极值",
                        colspan: 4,
                        rowspan: 1,
                        valign: "middle",
                        align: "center"
                    },
                    {
                        field: "avg",
                        title: "平均值",
                        colspan: 1,
                        rowspan: 3,
                        valign: "middle",
                        align: "center"
                    }], [{
                        field: "",
                        title: "最大值",
                        colspan: 2,
                        rowspan: 1,
                        valign: "middle",
                        align: "center"
                    },
                    {
                        field: "",
                        title: "最小值",
                        colspan: 2,
                        rowspan: 1,
                        valign: "middle",
                        align: "center"
                    }], [{
                        field: "max",
                        title: "数值",
                        valign: "middle",
                        align: "center"
                    },
                    {
                        field: "maxTime",
                        title: "发生时间",
                        valign: "middle",
                        align: "center"
                    },
                    {
                        field: "min",
                        title: "数值",
                        valign: "middle",
                        align: "center"
                    },
                    {
                        field: "minTime",
                        title: "发生时间",
                        valign: "middle",
                        align: "center"
                    }]]
                });
                $("#table").css("white-space", "nowrap");
                $("#table").css("background-color", "black");
                $("#table th").css("background-color", "black")
            }
            function f(a, b) {
                var c = {};
                c.fParamCode = b;
                "undefined" != typeof a.fValue && (c.fValue = parseFloat(a.fValue).toFixed(2));
                "undefined" != typeof a.min && (c.min = parseFloat(a.min).toFixed(2));
                "undefined" != typeof a.max && (c.max = parseFloat(a.max).toFixed(2));
                "undefined" != typeof a.maxTime && (c.maxTime = a.maxTime.substring(0, 16));
                "undefined" != typeof a.minTime && (c.minTime = a.minTime.substring(0, 16));
                "undefined" != typeof a.avg && (c.avg = parseFloat(a.avg).toFixed(2));
                h.push(c)
            }
            if ("updata" == a) {
                var k = $.cookie("stationId"),
                    g = $("#Subimglist").val();
                $.getJSON("Subimg/getSubimgInfo", "fSubid\x3d" + k + "\x26fCustomname\x3d" + g,
                    function (a) {
                        c(a.SvgInfo)
                    })
            } else c(b);
            var h = []
        },
        openNewTab: function (a) {
            var b = document.createElement("a");
            b.id = "newLink";
            b.target = "_blank";
            b.href = a;
            document.body.appendChild(b);
            b.click();
            document.body.removeChild(b)
        },
        upDate: function () {
            var a = $("#daycalendarBox").val(),
                a = new Date((new Date(a)).getTime() - 864E5);
            $("#dayCalendar").datetimepicker("update", a)
        },
        downDate: function () {
            var a = $("#daycalendarBox").val(),
                a = new Date((new Date(a)).getTime() + 864E5);
            $("#dayCalendar").datetimepicker("update", a)
        },
        upMonth: function () {
            var a = $("#daycalendarBox").val(),
                a = new Date(a),
                b = a.getFullYear(),
                c = a.getMonth();
            if (0 == c) var e = a.getFullYear() - 1 + "-12";
            0 < c && 10 > c && (c = "0" + c, e = b + "-" + c);
            10 <= c && (e = b + "-" + c);
            $("#daycalendarBox").val(e)
        },
        downMonth: function () {
            var a = $("#daycalendarBox").val(),
                a = new Date(a),
                b = a.getFullYear(),
                c = a.getMonth(),
                a = 11 == c ? a.getFullYear() + 1 + "-01" : 0 <= c && 7 >= c ? b + "-" + ("0" + (c + 2)) : b + "-" + (c + 2);
            $("#daycalendarBox").val(a)
        },
        upperJSONKey: function (a) {
            for (var b in a) a[b.toUpperCase()] != a[b] && (a[b.toUpperCase()] = a[b], delete a[b]);
            return a
        },
        refreshIlValue: function () {
            function a(a) {
                $(".overlimit-h.ping-h").html("");
                var b = a.leakageCurrentValues,
                    c = b.length,
                    e = "-";
                if (0 < c) {
                    $(".overlimit-h.ping-h").empty();
                    for (var g = 0; g < c; g++) {
                        "1" == a.leakageCurrentValues[g].fIlwarning && (e = '\x3cimg src\x3d"app/image/big-redbell.gif"\x3e');
                        "0" == a.leakageCurrentValues[g].fIlwarning && (e = '\x3cimg src\x3d"app/image/big-greenbell.png"\x3e');
                        var h = "--",
                            l = "--",
                            m = "--",
                            n = "--";
                        void 0 != b[g].fIl && (h = b[g].fIl);
                        void 0 != b[g].fTempa && (l = b[g].fTempa);
                        void 0 != b[g].fTempb && (m = b[g].fTempb);
                        void 0 != b[g].fTempb && (n = b[g].fTempc);
                        h = '\x3cdiv class\x3d"ping"\x3e\x3cdiv class\x3d"p-circle"\x3e' + e + '\x3c/div\x3e\x3cdiv\x3e\x3c/div\x3e\x3cdiv class\x3d"p-contain" value\x3d"' + b[g].fMeterName + '" id\x3d"' + b[g].fMetercode + '"\x3e\x3ch4\x3e' + b[g].fMeterName + '\x3c/h4\x3e\x3cdiv id\x3d"p-contain"\x3e\x3cp\x3e\x3cimg src\x3d"app/image/ping1.png"\x3e漏\x26nbsp;\x26nbsp;电\x26nbsp;\x26nbsp;流：' + h + 'mA\x3c/p\x3e\x3cp\x3e\x3cimg src\x3d"app/image/ping2.png"\x3e线缆温度：A：' + l + '℃\x3c/p\x3e\x3cp class\x3d"p-contain-BC"\x3eB：' + m + '℃\x3c/p\x3e\x3cp class\x3d"p-contain-BC"\x3eC：' + n + '℃\x3c/p\x3e\x3c/div\x3e\x3cbutton class\x3d"search search-romote search-sig s-s-monitor searchBtn searchList" type\x3d"button" data-toggle\x3d"modal" data-target\x3d"#myModal" data-target\x3d"#myModal"\x3e查询\x3c/button\x3e\x3c/div\x3e\x3c/div\x3e';
                        $(".overlimit-h.ping-h").append(h)
                    }
                } else $(".overlimit-h.ping-h").html("没有找到匹配的记录").css("textAlign", "center");
                $(document).on("click", ".searchList",
                    function () {
                        var a = $(this),
                            b = a.parent("div")[0].id,
                            a = a.parent("div").attr("value");
                        selectedInfo.fMetercode = b;
                        selectedInfo.fMeterName = a;
                        $(".searchList").attr("data-target", "#myModal")
                    })
            }
            function b(a) {
                $(".overlimit-h.ping-h").html("");
                $(".overlimit-h.ping-h").html("\x3cdiv id\x3d'tableDataLeakageMonitor'\x3e\x3ctable id\x3d'dataTable'\x3e\x3c/table\x3e\x3c/div\x3e");
                $("#dataTable").attr("data-height", $(".overlimit-h").height());
                var b = [];
                $.each(a.leakageCurrentValues,
                    function (a, c) {
                        var d = {};
                        d.fMetercode = c.fMetercode;
                        d.fMeterName = c.fMeterName;
                        d.fIl = c.fIl;
                        d.tempA = c.fTempa;
                        d.tempB = c.fTempb;
                        d.tempC = c.fTempc;
                        "1" == c.fIlwarning && (d.alarmState = '\x3cimg src\x3d"app/image/bell-red.gif"\x3e');
                        "0" == c.fIlwarning && (d.alarmState = '\x3cimg src\x3d"app/image/bell-green.png"\x3e');
                        d.historyData = '\x3cbutton class\x3d"search search-romote search-sig s-s-monitor searchBtn" type\x3d"button" data-toggle\x3d"modal"\x3e查询\x3c/button\x3e';
                        b.push(d)
                    });
                Substation.DOMOperator.generateTable($("#tableDataLeakageMonitor\x3etable"), [[{
                    field: "fMeterName",
                    title: "监测点名称",
                    rowspan: 2
                },
                {
                    field: "fIl",
                    title: "漏电流(mA)",
                    rowspan: 2
                },
                {
                    title: "线缆温度(℃)",
                    valign: "middle",
                    align: "center",
                    colspan: 3
                },
                {
                    field: "alarmState",
                    title: "漏电报警状态",
                    rowspan: 2
                },
                {
                    field: "historyData",
                    title: "历史数据",
                    rowspan: 2
                }], [{
                    field: "tempA",
                    title: "A",
                    valign: "middle",
                    align: "center"
                },
                {
                    field: "tempB",
                    title: "B",
                    valign: "middle",
                    align: "center"
                },
                {
                    field: "tempC",
                    title: "C",
                    valign: "middle",
                    align: "center"
                }]], b, !0);
                $("#tableDataLeakageMonitor").on("click-row.bs.table",
                    function (a, b, c) {
                        $(".currentSelect").css("background", "white").removeClass("currentSelect");
                        c.css("background", "#cee4f9").addClass("currentSelect");
                        $(".searchBtn").attr("data-target", "#myModal");
                        selectedInfo = b
                    })
            }
            var c = "fSubid\x3d" + $.cookie("stationId");
            $.getJSON("main/energySecurity/leakageMonitor", c,
                function (c) {
                    "Title" == $(".row-leakage").find(".active").attr("name") ? a(c) : b(c)
                })
        },
        addZero: function (a) {
            return 10 > a ? "0" + a : a
        },
        setSelect: function (a, b) {
            $.each(a,
                function (a, e) {
                    $(e)[0].value == b && $(e).attr("selected", !0)
                })
        },
        encryptByMD5: function (a) {
            return MD5(a)
        },
        isDelete: function () {
            return confirm("是否要删除当前选中数据？")
        },
        getUnitOfParam: function (a) {
            switch (a) {
                case "I":
                    a = "A";
                    break;
                case "U":
                case "UL":
                case "Voltage":
                    a = "V";
                    minValue = 200;
                    break;
                case "fFr":
                    a = "Hz";
                    break;
                case "P":
                    a = "kW";
                    break;
                case "Q":
                    a = "kVar";
                    break;
                case "S":
                    a = "kVA";
                    break;
                case "UnBalance":
                case "UnB":
                    a = "%";
                    break;
                default:
                    a = ""
            }
            return a
        }
    },
    ObjectOperation: {
        dateTimeFormat: function (a, b) {
            var c = b.getFullYear(),
                e = b.getMonth() + 1,
                d = b.getDate(),
                f = b.getHours(),
                k = b.getMinutes(),
                g = b.getSeconds(),
                e = Substation.Common.addZero(e),
                d = Substation.Common.addZero(d),
                f = Substation.Common.addZero(f),
                k = Substation.Common.addZero(k),
                g = Substation.Common.addZero(g),
                h = "";
            switch (a) {
                case "TIMEONLY":
                    h = f + ":" + k + ":" + g;
                    break;
                case "DATEONLY":
                    h = c + "-" + e + "-" + d;
                    break;
                case "DATETIME":
                    h = c + "-" + e + "-" + d + " " + f + ":" + k + ":" + g;
                    break;
                case "YEARMONTH":
                    h = c + "-" + e;
                    break;
                case "YEAR":
                    h = c;
                    break;
                case "DATE":
                    h = d;
                    break;
                case "HOUR":
                    h = f;
                    break;
                case "MINUTE":
                    h = k
            }
            return h
        },
        addZero: function (a) {
            return 10 > a ? "0" + a : a
        },
        getAnyDateAgo: function (a, b) {
            var c = a.getTime();
            return new Date(c - 864E5 * (b - 1))
        },
        getDateFromStringDate: function (a) {
            if (void 0 != a) {
                var b = a.split(" ");
                a = b[0].split("-");
                b = b[1].split(":");
                return new Date(a[0], a[1] - 1, a[2], b[0], b[1], b[2])
            }
        },
        isArrayEquals: function (a, b) {
            a.sort();
            b.sort();
            if (a.length != b.length) return !1;
            for (var c = 0; c < a.length; c++) if (typeof a[c] != typeof b[c] || a[c] != b[c]) return !1;
            return !0
        }
    },
    DOMOperator: {
        selectAppender: function (a, b, c, e) {
            b.html("");
            $.each(a,
                function (a, f) {
                    b.append("\x3coption value\x3d" + f[e] + "\x3e" + f[c] + "\x3c/option\x3e")
                });
            return this
        },
        initDateTimePicker: function (a, b, c, e, d) {
            a = null != d || void 0 != d ? Substation.ObjectOperation.dateTimeFormat(d, a) : Substation.ObjectOperation.dateTimeFormat("DATEONLY", a);
            c.val(a);
            null != e || void 0 != e ? b.datetimepicker(e) : b.datetimepicker({
                format: "yyyy-mm-dd",
                language: "zh-CN",
                weekStart: 1,
                todayBtn: 1,
                todayHighlight: 1,
                autoclose: 1,
                startView: 2,
                minView: 2,
                forceParse: 0,
                pickerPosition: "bottom-left"
            });
            return this
        },
        initDateTimePicker2: function (a, b, c, e, d) {
            a = null != d || void 0 != d ? Substation.ObjectOperation.dateTimeFormat(d, a) : Substation.ObjectOperation.dateTimeFormat("YEARMONTH", a);
            c.val(a);
            null != e || void 0 != e ? b.datetimepicker(e) : b.datetimepicker({
                format: "yyyy-mm",
                language: "zh-CN",
                autoclose: 1,
                startView: 3,
                minView: 3,
                forceParse: 0,
                pickerPosition: "bottom-left"
            });
            return this
        },
        lastMonthInitDate: function () {
            var a = new Date,
                b = a.getFullYear(),
                a = a.getMonth();
            if (0 == a) var b = b - 1,
                c = new Date(b, 12, 0),
                c = c.getDate(),
                e = b + "-12-01",
                d = b + "-12-" + c;
            else c = new Date(b, a, 0),
                c = c.getDate(),
                9 > a && (e = b + "-0" + a + "-01", d = b + "-0" + a + "-" + c),
                10 <= a && (e = b + "-" + a + "-01", d = b + "-" + a + "-" + c);
            Substation.DOMOperator.initDateTimePicker(new Date(e), $("#startDate"), $("#startDateBox"));
            Substation.DOMOperator.initDateTimePicker(new Date(d), $("#endDate"), $("#endDateBox"))
        },
        timeCompare: function () {
            var a = $("#startDateBox").val(),
                b = $("#endDateBox").val(),
                c = (new Date(a)).getFullYear(),
                e = (new Date(a)).getMonth() + 1,
                d = new Date(a),
                d = (new Date(b)).getTime() - d.getTime(),
                d = parseInt(d / 864E5),
                c = (new Date(c, e, 0)).getDate();
            return a > b ? !1 : d >= c ? !1 : !0
        },
        //获取电站
        getSubstation: function () {
            var a, b, c, e,
                // var a, b, c, e = $("#loginName")[0].innerText.substring(5),
                d = localStorage.getItem(e);
            c = null != d && void 0 != d ? d : "";
            $.ajax({
                type: "GET",
                url: "/station/query?companyId=&stationName=&page=1&rows=10",
                async: !1,
                contentType: "application/json",
                dataType: "json",
                success: function (d) {
                    var f = 0;
                    // console.log(d.rows)
                    0 < d.rows.length && ($.each(d.rows,
                        function (d, e) {
                            c == e.id && (a = e.id, b = e.stationName, f = 1)
                        }), 0 == f && (a = d.rows[0].id, b = d.rows[0].stationName), $.cookie("stationId", a), $.cookie("subName", b), localStorage.setItem(e, a))
                }
            })
        },
        pagenation: function (a, b, c) {
            function e(a, b) {
                $("#tableSubName").html("\x3ctable\x3e\x3c/table\x3e");
                $("#tableSubName\x3etable").attr("data-height", $(".substation-list").height());
                var c = [];
                $.each(a.rows,
                    function (a, b) {
                        var d = {};
                        d.fSubid = b.id;
                        d.fSubname = b.stationName;
                        d.fAddress = b.companyName;
                        c.push(d)
                    });
                Substation.DOMOperator.tableSubstationName($("#tableSubName\x3etable"), [{
                    field: "fSubname",
                    title: "变配电站名称"
                },
                {
                    field: "fAddress",
                    title: "公司"
                }], c, !0, 8, !0, [4, 8]);
                $("#tableSubName").on("click-row.bs.table",
                    function (a, b, c) {
                        $(".insideSelect").css("background", "white").removeClass("insideSelect");
                        c.css("background", "#cee4f9").addClass("insideSelect");
                        selectedInfoModal = b
                    });
                d(a, b)
            }
            function d(a, b) {
                BootstrapPagination($("#table_pagination"), {
                    layoutScheme: "firstpage,prevgrouppage,prevpage,pagenumber,nextpage,nextgrouppage,lastpage",
                    total: a.total,
                    pageSize: a.pageSize,
                    pageIndex: a.prePage,
                    pageGroupSize: 5,
                    pageInputTimeout: 800,
                    pageSizeList: [5, 10, 20, 50, 100, 200],
                    pageChanged: function (a, c) {
                        a += 1;
                        if ("normal" == b) var d = "station/query?companyId=&stationName=&page=1&rows=10",
                            f = "pageNo\x3d" + a + "\x26pageSize\x3d" + c;
                        else d = $(".substationlist").val(),
                            f = "page=" + a + "&rows=" + c + "&stationName=" + encodeURI(d),
                            d = "/station/query";
                        $.ajax({
                            type: "GET",
                            url: d,
                            data: f,
                            async: !1,
                            dataType: "json",
                            success: function (a) {
                                e(a)
                            }
                        })
                    }
                });
                $("#tableSubName").on("click-row.bs.table",
                    function (a, b, c) {
                        $(".insideSelect").css("background", "white").removeClass("insideSelect");
                        c.css("background", "#cee4f9").addClass("insideSelect");
                        selectedInfoModal = b
                    })
            }
            $.getJSON(a, "page=" + b + "&rows=" + c,
                function (a) {
                    e(a, "normal")
                });
            $("#refreshBtn").off("click").on("click",
                function () {
                    var a = $(".substationlist").val(),
                        a = "page=1&rows=8&stationName=" + a;
                    $.ajax({
                        url: "/station/query",
                        type: "GET",
                        data: a,
                        success: function (a) {
                            e(a, "fuzzy")
                        }
                    })
                })
        },
        yesBtnClick: function (a) {
            var b = a.fSubid;
            a = a.fSubname;
            $.cookie("stationId", b, { expires: 1 });
            $.cookie("subName", a);
            $("#StationName").html(a);
            $("#StationName").attr("value", b)
        },
        chartSize: function (a, b) {
            a.removeAttr("_echarts_instance_");
            a.html("");
            a.css("width", "100%");
            a.css("height", "100%");
            void 0 != b && a.height(a.height() - b)
        },
        checkAllTree: function (a) {
            function b(a, c) {
                var d = JSON.stringify(a);
                // console.log('/////////////////////////')
                // console.log(d);
                /nodes/.test(d) && $.each(a.nodes,
                    function (a, d) {
                        c.treeview("checkNode", [d.nodeId]);
                        b(d, c)
                    })
            }
            function c(a, b) {
                var d = JSON.stringify(a);
                // console.log('/////////////////////////')
                // console.log(d);
                /nodes/.test(d) && $.each(a.nodes,
                    function (a, d) {
                        b.treeview("uncheckNode", [d.nodeId]);
                        c(d, b)
                    })
            }
            $("#treeview").html("");
            var e = [];
            if (null == a || 0 == a.length) return e;
            Substation.DOMOperator.treeGenerate(a, $("#treeview"), {
                showIcon: !1,
                showCheckbox: !0,
                showBorder: !0,
                levels: 2
            });
            null != e && (e = []);
            $("#treeview").on("nodeChecked",
                function (a, c) {
                    0 > $.inArray(c.id, e) && e.push(c.id);
                    $("#multiState").prop("checked") ? b(c, $("#treeview")) : $("#treeview").treeview("checkNode", [c.nodeId, {
                        silent: !0
                    }])
                });
            $("#treeview").on("nodeUnchecked",
                function (a, b) {
                    if (b.id == e[0]) e.shift();
                    else if ($.inArray(b.id, e)) {
                        var d = $.inArray(b.id, e);
                        e.splice(d, 1)
                    }
                    $("#multiState").prop("checked") ? c(b, $("#treeview")) : $("#treeview").treeview("uncheckNode", [b.nodeId, {
                        silent: !0
                    }])
                });
            $("#treeview").treeview("checkAll");
            return e
        },
        // 三相不平衡.html  tree
        checkAllTreeIm: function (a) {
            // console.log(a[0].id)
            function b(a, c) {
                var d = JSON.stringify(a);
                // console.log('/////////////////////////')
                // console.log(d);
                /nodes/.test(d) && $.each(a.nodes,
                    function (a, d) {
                        c.treeview("checkNode", [d.nodeId]);
                        b(d, c)
                    })
            }
            function c(a, b) {
                var d = JSON.stringify(a);
                // console.log('/////////////////////////')
                /nodes/.test(d) && $.each(a.nodes,
                    function (a, d) {
                        // console.log(d.nodeId);
                        b.treeview("uncheckNode", [d.nodeId]);
                        c(d, b)
                    })
            }
            $("#treeview").html("");
            var e = [];
            if (null == a || 0 == a.length) return e;
            Substation.DOMOperator.treeGenerate(a, $("#treeview"), {
                showIcon: !1,
                showCheckbox: !0,
                showBorder: !0,
                levels: 2
            });
            null != e && (e = []);
            $("#treeview").on("nodeChecked",
                function (a, c) {
                    0 > $.inArray(c.id, e) && e.push(c.id);
                    $("#multiState").prop("checked") ? b(c, $("#treeview")) : $("#treeview").treeview("checkNode", [c.nodeId, {
                        silent: !0
                    }])
                });
            $("#treeview").on("nodeUnchecked",
                function (a, b) {
                    if (b.id == e[0]) e.shift();
                    else if ($.inArray(b.id, e)) {
                        var d = $.inArray(b.id, e);
                        e.splice(d, 1)
                    }
                    $("#multiState").prop("checked") ? c(b, $("#treeview")) : $("#treeview").treeview("uncheckNode", [b.nodeId, {
                        silent: !0
                    }])
                });
            $("#treeview").treeview("checkNode", [0]);
            return e
        },
        treeGenerate: function (a, b, c) {
            var e = [],
                d = RegExp('\\,\\"nodes\\"\\:\\[\\]', "g");
            if (null != a) for (var f = 0; f < a.length; f++) {
                if (0 == f) {
                    var k = {
                        expanded: !0
                    };
                    a[f].state = k
                }
                1 == a[f].fChecked && (k = {
                    checked: !0,
                    expanded: !0
                },
                    a[f].state = k, $.each(a[f].nodes,
                        function (b, c) {
                            1 == c.fChecked && (a[f].nodes[b].state = k)
                        }));
                var g = JSON.stringify(a[f]);
                // console.log(a[f])
                // console.log(g)
                e.push(JSON.parse(g.replace(d, "")))
            }
            // console.log(e)
            null != c || void 0 != c ? (c.levels = 1, c.data = e, b.treeview(c)) : b.treeview({
                data: e,
                showIcon: !0,
                showCheckbox: !1,
                showBorder: !0,
                levels: 1,
                multiSelect: !1,
                highlightSearchResults: !1
            });
            return this
        },
        checkTreeChildNode: function (a, b) {
            var c = JSON.stringify(a);
            /nodes/.test(c) && $.each(a.nodes,
                function (a, c) {
                    b.treeview("checkNode", [c.nodeId]);
                    Substation.DOMOperator.checkTreeChildNode(c, b)
                })
        },
        unCheckTreeChildNode: function (a, b) {
            var c = JSON.stringify(a);
            /nodes/.test(c) && $.each(a.nodes,
                function (a, c) {
                    b.treeview("uncheckNode", [c.nodeId]);
                    Substation.DOMOperator.unCheckTreeChildNode(c, b)
                })
        },
        generateTable: function (a, b, c, e, d, f, k, g) {
            f = 50;
            f = null == d || void 0 == d ? 50 : d;
            if (null == k || void 0 == k) k = [10, 25, 50, 100, "All"];
            e ? "eventTable" == a.get(0).id ? a.bootstrapTable({
                height: g,
                striped: !0,
                classes: "table table-border",
                pagination: !0,
                cardView: !1,
                pageSize: f,
                columns: b,
                pageList: k,
                rowStyle: function (a, b) {
                    return a.isRed ? {
                        css: {
                            color: "black"
                        }
                    } : {
                            css: {
                                color: "red"
                            }
                        }
                },
                data: c
            }) : a.bootstrapTable({
                height: g,
                striped: !0,
                classes: "table table-border",
                pagination: !0,
                cardView: !1,
                pageSize: f,
                columns: b,
                pageList: k,
                data: c
            }) : a.bootstrapTable({
                height: g,
                striped: !0,
                editable: !0,
                classes: "table table-border",
                columns: b,
                data: c
            })
        },
        tableSubstationName: function (a, b, c, e, d, f, k, g) {
            if (null == k || void 0 == k) k = [10, 25, 50, 100, "All"];
            e ? a.bootstrapTable({
                height: g,
                striped: !0,
                classes: "table table-border",
                pagination: !1,
                sidePagination: "server",
                cardView: !1,
                pageSize: null == d || void 0 == d ? 50 : d,
                columns: b,
                pageList: k,
                data: c
            }) : a.bootstrapTable({
                height: g,
                striped: !0,
                classes: "table table-border",
                columns: b,
                data: c
            })
        },
        modalGenerate: function (a, b) {
            a.before('\x3cdiv class\x3d"modal fade" id\x3d"modalForm" tabindex\x3d"-1" role\x3d"dialog" aria-labelledby\x3d"myModalLabel" aria-hidden\x3d"true"\x3e\x3cdiv class\x3d"modal-dialog modal-list"\x3e\x3cdiv class\x3d"modal-content modal-alarm"\x3e\x3cdiv class\x3d"modal-header"\x3e\x3ca class\x3d"close" data-dismiss\x3d"modal"\x3ex\x3c/a\x3e\x3ch3 class\x3d"modal-title" id\x3d"myModalLabel"\x3e' + b + '\x3c/h3\x3e\x3c/div\x3e\x3cul class\x3d"nav nav-tabs" role\x3d"tablist"\x3e\x3cli role\x3d"presentation" class\x3d"active"\x3e\x3ca href\x3d"#home" aria-controls\x3d"home" role\x3d"tab" data-toggle\x3d"tab" id\x3d"signalEvent"\x3e遥信变位\x3c/a\x3e\x3c/li\x3e\x3cli role\x3d"presentation"\x3e\x3ca href\x3d"#profile" aria-controls\x3d"profile" role\x3d"tab" data-toggle\x3d"tab" id\x3d"overLimitEvent"\x3e遥测越限\x3c/a\x3e\x3c/li\x3e\x3cli role\x3d"presentation"\x3e\x3ca href\x3d"#messages" aria-controls\x3d"messages" role\x3d"tab" data-toggle\x3d"tab" id\x3d"platformRunEvent"\x3e平台运行\x3c/a\x3e\x3c/li\x3e\x3c/ul\x3e\x3cdiv class\x3d"modal-body list-body"\x3e\x3c/div\x3e\x3cspan id\x3d"showEveLog"\x3e\x3c/span\x3e\x3cnav id\x3d"page" class\x3d"pagination pagination-s"\x3e\x3c/nav\x3e\x3c/div\x3e\x3c/div\x3e\x3cdiv class\x3d"add-a"\x3e\x3c/div\x3e\x3c/div\x3e')
        },
        getWaining: function (a, b, c, e, d, f, k) {
            function g(a, b, c) {
                var d = [],
                    e;
                switch (a) {
                    case "signalEvent":
                        e = "main/getWarningMessageSignalEvents";
                        d = [{
                            field: "fSubname",
                            title: "变配电站名称"
                        },
                        {
                            field: "fStarttime",
                            title: "发生时间"
                        },
                        {
                            field: "fMetername",
                            title: "设备名称"
                        },
                        {
                            field: "fAlarmtype",
                            title: "事件类型"
                        },
                        {
                            field: "fAlarmdesc",
                            title: "描述"
                        }];
                        break;
                    case "overLimitEvent":
                        e = "main/getWarningMessageOverLimitEvents";
                        d = [{
                            field: "fSubname",
                            title: "变配电站名称"
                        },
                        {
                            field: "fStarttime",
                            title: "发生时间"
                        },
                        {
                            field: "fMetername",
                            title: "设备名称"
                        },
                        {
                            field: "fAlarmtype",
                            title: "事件类型"
                        },
                        {
                            field: "fAlarmdesc",
                            title: "描述"
                        }];
                        break;
                    case "platformRunEvent":
                        e = "main/getWarningMessagePlatformRunEvents",
                            d = [{
                                field: "fSubname",
                                title: "变配电站名称"
                            },
                            {
                                field: "fStarttime",
                                title: "发生时间"
                            },
                            {
                                field: "fDevicename",
                                title: "设备名称"
                            },
                            {
                                field: "fAlarmtype",
                                title: "事件类型"
                            },
                            {
                                field: "fAlarmdesc",
                                title: "描述"
                            }]
                }
                void 0 != c ? h(b, d) : $.getJSON(e, b,
                    function (a) {
                        h(a, d)
                    })
            }
            function h(a, b) {
                c.html('\x3ctable id\x3d"warningTab"\x3e\x3c/table\x3e');
                var e = [];
                null != a.WarningMessage.list && 0 < a.WarningMessage.list.length && (e = a.WarningMessage.list);
                $("#warningTab").bootstrapTable({
                    height: d,
                    pageSize: 10,
                    cache: !1,
                    columns: b,
                    data: e,
                    rowStyle: function (a) {
                        return a.fIsread ? "black" == f ? {
                            css: {
                                color: "black"
                            }
                        } : {
                                css: {
                                    color: "white"
                                }
                            } : "black" == f ? {
                                css: {
                                    color: "red"
                                }
                            } : {
                                    css: {
                                        color: "#bf060d"
                                    }
                                }
                    }
                });
                $("#warningTab").css("white-space", "nowrap");
                null != a.WarningMessage && $("#showEveLog").html("显示第 " + a.WarningMessage.startRow + " 到 " + a.WarningMessage.endRow + " 条记录,总共 " + a.WarningMessage.total + " 条记录,共" + a.WarningMessage.pages + "页");
                l(a.WarningMessage)
            }
            function l(a) {
                BootstrapPagination($("#page"), {
                    layoutScheme: "firstpage,prevgrouppage,prevpage,pagenumber,nextpage,nextgrouppage,lastpage",
                    total: a.total,
                    pageSize: a.pageSize,
                    pageIndex: a.prePage,
                    pageGroupSize: 5,
                    pageInputTimeout: 800,
                    pageSizeList: [5, 10, 20, 50, 100, 200],
                    pageChanged: function (a) {
                        var b;
                        switch ($(".nav.nav-tabs\x3eli[class\x3dactive]")[0].textContent) {
                            case "遥信变位":
                                b = "signalEvent";
                                break;
                            case "遥测越限":
                                b = "overLimitEvent";
                                break;
                            case "平台运行":
                                b = "platformRunEvent"
                        }
                        g(b, "\x26pageNo\x3d" + (a + 1) + "\x26pageSize\x3d" + e)
                    }
                })
            }
            $.each($(".nav.nav-tabs\x3eli"),
                function (b, c) {
                    $(c).removeAttr("class");
                    $(c).children("a").attr("id") == a && $(c).attr("class", "active")
                });
            g(a, k, "1");
            b.off("click").click(function () {
                var a = $(this).children("a").attr("id");
                g(a, "pageNo\x3d1\x26pageSize\x3d" + e)
            })
        },
        exportTable: function (a, b) {
            a.tableExport({
                type: "xlsx",
                fileName: b
            })
        },
        generateVideo: function (a, b, c) {
            $("#" + a).html("");
            $("#" + a).append('\x3cvideo id\x3d"video-' + c + '" poster\x3d"" style\x3d"width:100%;height:100%" controls playsInline webkit-playsinline autoplay\x3e\x3csource src\x3d"rtmp://rtmp.open.ys7.com/openlive/' + b + '" type\x3d"" /\x3e\x3csource src\x3d"http://http://hls.open.ys7.com/openlive/' + b + '.m3u8" type\x3d"application/x-mpegURL" /\x3e');
            new EZUIPlayer("video-" + c)
        }
    },
    Validator: {
        setErrorStyle: function (a, b) {
            $(":text").css({
                border: "1px solid #ababab",
                "box-shadow": "0px 0px 0px #ababab"
            });
            $(".megbox").remove();
            a.css({
                border: "1px solid red",
                "box-shadow": "0px 0px 2px red"
            }); (new MessageBox(a.get(0), a.get(0).id, b)).Show()
        },
        setFocus: function (a) {
            var b = "megbox_" + a.get(0).id;
            0 < $("#" + b).length && $("#" + b).remove();
            a.css({
                border: "1px solid #ababab",
                "box-shadow": "0px 0px 0px #ababab"
            })
        },
        validate: function (a, b, c) {
            var e = a.attr("validator");
            if (void 0 != e) {
                var d = a.val(),
                    e = e.split(";"),
                    f = !1;
                $.each(e,
                    function (e, g) {
                        if (!f) switch (g.split(":")[0]) {
                            case "required":
                                "" == d && (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "不允许为空！"));
                                break;
                            case "number":
                                var h = /^[0-9]*[0-9][0-9]*$/;
                                h.test(d) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "必须非负整数！"));
                                break;
                            case "int":
                                h = /^\d+$/;
                                h.test(d) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "必须整数！"));
                                break;
                            case "litter":
                                h = /^0.([1-9][0-9]?|[0-9][1-9])|1$/;
                                h.test(d) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "必须为(0,1]的小数！"));
                                break;
                            case "long":
                                /^(\-|\+)?(((\d|[1-9]\d|1[0-7]\d|0{1,3})\.\d{0,6})|(\d|[1-9]\d|1[0-7]\d|0{1,3})|180\.0{0,6}|180)$/.test(d) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "格式不正确！"));
                                break;
                            case "lat":
                                /^-?((0|[1-8]?[0-9]?)(([.][0-9]{1,9})?)|90(([.][0]{1,9})?))$/.test(d) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "格式不正确！"));
                                break;
                            case "Diff":
                                /^[A-Za-z0-9]+$/.test(d) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "格式不正确！"));
                                break;
                            case "float":
                                h = /^(-)?\d+(\.\d+)?$/;
                                null == h.exec(d) && (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "必须为数字！"));
                                break;
                            case "min":
                                h = parseInt(g.split(":")[1]);
                                d.length < h && (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "长度应大于" + h + "位！"));
                                break;
                            case "max":
                                h = parseInt(g.split(":")[1]);
                                d.length > h && (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "长度应小于" + h + "位！"));
                                break;
                            case "space":
                                0 < $.trim(d).indexOf(" ") && (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "格式错误，请检查其中是否包含空格！"));
                                break;
                            case "Phone":
                                0 >= d.length || (h = d.split(";"), $.each(h,
                                    function (d, e) {
                                        PhoneNum = e;
                                        /^1[34578]\d{9}$/.test(PhoneNum) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "格式不正确！"))
                                    }));
                                break;
                            case "transform":
                                0 >= d.length || (h = d.split(";"), $.each(h,
                                    function (d, e) {
                                        / ^1[0 - 9] + $ /.test(e) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "必须为1开头！"))
                                    }));
                                break;
                            case "noSpecial":
                                (new RegExp(/[\r\n\t]/g)).test(d) && (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "含特殊字符"));
                                break;
                            case "limit":
                                RegExp("^[一-龥A-Za-z0-9]+$").test(d) || (f = !0, void 0 != c && $("#" + c).tab("show"), Substation.Validator.setErrorStyle(a, b + "格式不正确！"))
                        }
                    });
                return !f
            }
        }
    }
};