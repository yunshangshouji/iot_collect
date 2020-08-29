function createJsonPPromise(url, data, method, isNeedFilter) {
    if (!data) {
        data = {};
    }
    if (!method) {
        method = "get";
    }
    if (!isNeedFilter) {
        isNeedFilter = false;
    }
    return new Promise(function(resolve, reject) {
        $.ajax({
            url: url,
            type: method,
            data: data,
            dataType: "jsonp",
            jsonpCallback: data.jsonpCallback,
            success: function(data) {
                if (isNeedFilter) {
                    if (data.result === 1) {
                        resolve(data);
                    } else {
                        reject(data.msg);
                    }
                } else {
                    resolve(data);
                }
            },
            fail: function(err) {
                reject(err);
            }
        });
    });
}

function createAjaxPromise(url, data, method) {
    if (!method) {
        method = "get";
    }
    if (!data) {
        return new Promise(function(resolve, reject) {
            $.ajax({
                url: url,
                type: method,
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function(data) {
                    resolve(data);
                },
                fail: function(err) {
                    reject(err);
                }
            });
        });
    }
    return new Promise(function(resolve, reject) {
            $.ajax({
                url: url,
                type: method,
                data: JSON.stringify(data),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function(data) {
                    resolve(data);
                },
                fail: function(err) {
                    reject(err);
                }
            });
        });

}

function getCookie(key) {
    var result = document.cookie.match(new RegExp("(^| )" + key + "=([^;]*)"));
    //document.write(result);
    return result != null ? decodeURIComponent(result[2]) : null;
}
//  expire 传入天数， 如7天，就7, 24小时为1
function setCookie(key, value, domain, expire) {
    var expires = new Date();
    if (!domain) {
        domain = document.cookie.match(/(^| )_openbanner\.domain=([^;]*)(;|$)/);
        domain && (domain = domain[2]);
    }
    /* 三个月 x 一个月当作 30 天 x 一天 24 小时 x 一小时 60 分 x 一分 60 秒 x 一秒 1000 毫秒 */
    if (!expire) {
        expires = expires.setTime(
            expires.getTime() + 12 * 30 * 24 * 60 * 60 * 1000
        );
    }
    var domainStr = domain && domain != "null" ? ";domain=" + domain : "";
    var expireMs = expire && expire != "null" ? new Date().getTime() + expire * 24 * 60 * 60 * 1000 : expires;
    var nDate = new Date(expireMs);
    document.cookie = key +
                "=" + encodeURIComponent(value) +
                ";expires=" + nDate.toGMTString() +
                ";path=/" + domainStr;
}
function deleteCookie(key, domain) {
    var exp = new Date();
    domain = domain ? domain : window.sDomain;
    exp.setTime(exp.getTime() - 1);
    document.cookie = key + "=; expires=" + exp.toGMTString() + "; path=/;domain=" + domain;
}


// props
function hideProps () {
    setTimeout(function(){
        $('#js-prompt-cont').html('');
        $('#js-pop-prompt').hide();
    }, 3000);
}

// show head menu
function initHeaderMenu() {
    var dHeadMenu = $('#js-head-menu');
    var dMenuWrap = $('#js-menu-wraper');
    var dMask = $('.js-mask');
    var dClose = dMenuWrap.find('.js-close-menu');
    dHeadMenu.click(function(){
        // console.log('a1');
        dMenuWrap.addClass('show');
        dMask.show();
    });
    dClose.click(function(){
        dMenuWrap.removeClass('show');
        dMask.hide();
    });
    dMask.click(function(){
        dMenuWrap.removeClass('show');
        dMask.hide();
    });
}
// localStorage 操作
/**
 * 存储sessionStorage
 */
function setStore(name, content) {
    if (!name) return;
    if (typeof content !== 'string') {
        content = JSON.stringify(content);
    }
    window.localStorage.setItem(name, content);
};

/**
 * 获取sessionStorage
 */
function getStore(name) {
    if (!name) return;
    return window.localStorage.getItem(name);
};

/**
 * 删除sessionStorage
 */
 function removeStore (name) {
    if (!name) return;
    window.localStorage.removeItem(name);
};

// 获取url中的某个key的值
function getUrlParam (paramName){
    let paramValue = '',
        isFound = false,
        searchUrl = location.search;
    if (searchUrl.indexOf('?') === 0 && searchUrl.indexOf('=') > 0) {
        let arrSource = unescape(searchUrl).substring(1, searchUrl.length).split('&'),
            i = 0;
        while (i < arrSource.length && !isFound) {
            if (arrSource[i].indexOf('=') > 0) {
                if (arrSource[i].split('=')[0].toLowerCase() === paramName.toLowerCase()) {
                    paramValue = arrSource[i].split('=')[1];
                    isFound = true;
                }
            }
            i++;
        }
    }
    return paramValue;
};

function initAjaxSetup () {
    $.ajaxSetup({
        cache: false,
        complete: function (XMLHttpRequest, textStatus) {
            if (textStatus == 'error') {
                var sessionStatus = XMLHttpRequest.getResponseHeader('SessionStatus');
                switch (sessionStatus) {
                    case 'not_login':
                        window.location.href="login.html";
                        break;
                    case 'not_station':
                        window.location.href="index.html";
                        break;
                    default:
                        break;
                }
            }
        }
    })
    //设置全局 jQuery Ajax全局参数
    /*$.ajaxSetup({
        dataType:"json",
        error:function(XMLHttpRequest,textStatus,errorThrown){
            switch(XMLHttpRequest.status){
                case(500):
                    // '服务器系统内部错误''
                    break;
                case(401):
                    // '未登录'
                    window.location.href="login.html";
                    break;
                case(403):
                    // "无权限执行此操作"
                    break;
                case(408):
                    // "请求超时"
                    break;
                default:
                    // "未知错误"
            }
        },
        success:function(data){
            // alert('操作成功！');
        }
    });*/
}


$(function(){
    // 所有ajax进行拦截
    initAjaxSetup();
})