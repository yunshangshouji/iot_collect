// 请求 menu url
function getMenu() {
	let sUrl = '/auth/myMenu';
	// sUrl += '?aliveFlag=' + 1 + '&limit=' + 10 + '&start=' + sCollectorId + '&stationId=' + stationId;
	createAjaxPromise(sUrl, null, 'GET').then(function (res) {
		// console.log('res 1--------------1:', res);
		aMenus = res;
		if (res.length > 0) {
			renderMenu(res);
		}
	}, function (msg) {
		alert(msg);
	});
}
var aMenus = [];
var aCurSubMenu = [];
var iCurMenuId = '';

// 渲染一级菜单
function renderMenu() {
	var dDom = $('#js-nav-menu');
	var sHtml = '';
	/*
		<li class="menu-t1 nav-select">
			<a link="">
				<span></span>
				概况
			</a>
		</li>
	*/
	var sSelectedCss = '';
	aMenus && aMenus.forEach((menu, index) => {
		if (index == 0) {
			sSelectedCss = 'nav-select';
			iCurMenuId = menu.id;
			aCurSubMenu = menu.children || [];
		} else {
			sSelectedCss = '';
		}
		sHtml += '<li class="' + sSelectedCss + ' js-nav-select menu-t' + (index + 1) + '" id="' + menu.id + '">' +
			'<a href="javascript:;">' +
			'<span></span>' +
			menu.text +
			'</a>' +
			'</li>';
	});
	dDom.html(sHtml);
	renderSubMenu();
	initMenuClick();
}
// 渲染二级菜单
function renderSubMenu() {
	var dDom = $('#js-left-menu');
	var sHtml = '';
	/* <li class=""><span link="SubstationStatus">变配电站状态</span></li> */
	if (aCurSubMenu.length) {
		aCurSubMenu.forEach((submenu) => {
			sHtml += '<li pid="' + submenu.pid + '" class="js-submenu" surl="' + submenu.url + '">' +
				'<span>' + submenu.text + '</span>' +
				'</li>';
		});
		dDom.html(sHtml);
	} else {
		dDom.html(sHtml);
		dDom.hide();
	}
	initSubMenuClick();
}

// 一级菜单点击后，修改二级菜单
function initMenuClick() {
	var dDom = $('.js-nav-select');
	dDom.click(function () {
		var pId = $(this).attr('id');
		$('.js-nav-select').removeClass('nav-select');
		$(this).addClass('nav-select');
		aMenus && aMenus.forEach((menu, index) => {
			if (menu.id == pId) {
				iCurMenuId = menu.id;
				aCurSubMenu = menu.children || [];
			}
		});
		renderSubMenu();
	})
}

// 初始化二级菜单的点击事件， 点了后打开相应的iframe
function initSubMenuClick() {
	var dDom = $('#js-left-menu').find('.js-submenu');
	var dIFrame = $("#js-iframe");
	dDom.click(function () {
		var sUrl = $(this).attr('surl');
		dIFrame.attr('src', sUrl);
		// 默认项
		$(dDom).removeClass('active')
		$(this).addClass('active')
	});
	// 默认项
	dIFrame.attr('src', $(dDom).first().attr('surl'));
	$(dDom).first().addClass('active')
}
// 获取分部列表
function getBranch() {
	var b = "/common/sys/biz.js";
	var c = "dict=Branch";
	$.getJSON(b, c, function (c) {
		c.forEach(function (item) {
			$('#branchList').append('<li data-id="'+item.value+'"><a role="menuitem" tabindex="-1">'+item.text+'</a></li>')
			// $('#branchId').append("<option value='" + item.value + "'>" + item.text + "</option>" )
		})
	})
}

// 获取用户列表
function getUserinfo() {
	var b = "/userinfo";
	$.getJSON(b, null, function (c) {
		$("#loginName").text(c.realName);
		$("#branchId").html(c.currentBranchName+'<span class="caret"></span>');
		$("#branchId").attr("data-id", c.currentBranchId);
		$('.logo-name').html(c.currentBranchName)
	})
}
// 进入分部
function enterBranch(){
	var branchId = $("#branchId").data("id");
	$.ajax({
		async : true,
		type : "get",
		url : "/chooseBranch?branchId=" + branchId,
		success: function (data) {
			window.location.reload()
		},
		error:function (data) {
			alert(data);
		}
	});
}


$(function () {

	getMenu();
	getBranch(); //获取分部
	getUserinfo(); //获取用户信息
	if (localStorage.getItem('userType') == 0) {    // 企业用户
		$('#branchId').hide()
	} else {
		// $('#branchId').show()
		$('.branch-box').hide()
		var userInfo = JSON.parse(localStorage.getItem('userType'))
		// console.log(userInfo.branchId)  // userInfo.branchName
		if (userInfo.branchId == 1) {              // 总部
			$('#branchId').attr('disabled', false)
		} else {                                    // 分部
			// $('#branchId').attr('disabled', true)
			$('.branch-box').hide()
		}
	}
	
	// 进入分部
	// $("#enterBranch").click(function() {
	// 	enterBranch();
	// })

	$("body").on("click","#branchList li",function () {
		$("#branchId").attr("data-id", $(this).data("id"));
		$("#branchId").html($(this).children("a").text()+'<span class="caret"></span>');
		enterBranch();
	})

});