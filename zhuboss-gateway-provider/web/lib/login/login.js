$('.btn').click(function (e) {
    if($("input[name='username']").val()==""){
        $('.error-msg').text('请输入用户名');
        $("input[name='username']").focus();
        $('.first').find('span').show();
        return false;
    }else if($("input[name='password']").val()==""){
        $('.error-msg').text('请输入密码');
        $("input[name='password']").focus();
        $('.second').find('span').show();
        return false;
    }else{
        var user={};
        user.username=$("input[name='username']").val()
        user.password=$("input[name='password']").val()
        user.validCode=$("input[name='validCode']").val()
        e.preventDefault();
        $.ajax({
            type:"POST",
            url:"/login/ajaxLogin",
            data:JSON.stringify(user),
            contentType:"application/json",
            success:function (data) {
               if(data.result==true){
                   localStorage.setItem("username",user.username);
                   localStorage.setItem("password",user.password);
                   var redirectUrl = MyUtil.getUrlParam("redirectUrl");
                   if(redirectUrl){
                       location.href = redirectUrl;
                   }else{
                       location.href = data.data;
                   }
               }else{
                   $('.error-msg').text(data.msg);
               }
            }
        })
    }
})
$('.input input').keydown(function () {
    $(this).parent().find('span').hide();
    $('.error-msg').text('');
});
//ajax 请求验证码
$("input[name='validCode']").blur(function () {
    $.post('/login/validLogonCode', { validCode :  $("input[name='validCode']").val() },
        function(data){
            if(data && !data.result){
                $('.error-msg').text( data.msg? data.msg : '验证码错误');
            }
        } ,"json");
});
//加载二维码
$("#code").on('click',function(){
    $(this).attr('src','/login/validImg.jpg?_random=' + Math.random());
});
$(document).ready(function () {
    $("#code").attr('src','/login/validImg.jpg?_random=' + Math.random());
});
//兼容placeholder
(function($){
    $.fn.placeholder=function (options) {
        var defaults = {
                pColor: "#ccc",
                pActive: "#999",
                pFont: "14px",
                activeBorder: "#080",
                posL: 18,
                zIndex: "99"
            },
            opts = $.extend(defaults, options);
        return this.each(function () {
            if ("placeholder" in document.createElement("input")) return;
            $(this).parent().css("position", "relative");

            //不支持placeholder的浏览器
            var $this = $(this),
                msg = $this.attr("placeholder"),
                iH = $this.outerHeight(),
                iW = $this.outerWidth(),
                iX = $this.position().left,
                iY = $this.position().top,
                oInput = $("<label>", {
                    "class": "placeholderCss",
                    "text": msg,
                    "css": {
                        "position": "absolute",
                        "left": iX + "px",
                        "top": iY + "px",
                        "width": iW - opts.posL + "px",
                        "padding-left": opts.posL + "px",
                        "height": iH + "px",
                        "line-height": iH + "px",
                        "color": opts.pColor,
                        "font-size": opts.pFont,
                        "z-index": opts.zIndex,
                        "cursor": "text"
                    }
                }).insertBefore($this);
            //初始状态就有内容
            var value = $this.val();
            if (value.length > 0) {
                oInput.hide();
            };

            //
            $this.on("focus", function () {
                var value = $(this).val();
                if (value.length > 0) {
                    oInput.hide();
                }
                oInput.css("color", opts.pActive);
                //

                if(navigator.userAgent.indexOf("Trident/4.0") > 0){
                    var myEvent = "propertychange";
                }else{
                    var myEvent = "input";
                }

                $(this).on(myEvent, function () {
                    var value = $(this).val();
                    if (value.length == 0) {
                        oInput.show();
                    } else {
                        oInput.hide();
                    }
                });

            }).on("blur", function () {
                var value = $(this).val();
                if (value.length == 0) {
                    oInput.css("color", opts.pColor).show();
                }
            });
            //
            oInput.on("click", function () {
                $this.trigger("focus");
                $(this).css("color", opts.pActive)
            });
            //
            $this.filter(":focus").trigger("focus");
        });
    }
})(jQuery);

$(":input[placeholder]").each(function(){
    $(this).placeholder();
});