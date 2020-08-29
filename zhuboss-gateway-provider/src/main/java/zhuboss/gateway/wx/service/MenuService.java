package zhuboss.gateway.wx.service;

import zhuboss.gateway.wx.po.MenuPO;

import java.util.Map;


public interface MenuService {
	
	
	Integer insert(MenuPO columnItemPO);
	
	void update(MenuPO columnItemPO);
	
	void delete(Integer id);
	
	/**
	 * 生成菜单到微信服务器
	 */
	Map<String, Object> genWeixinMenu();
	
	Map<String,Object> viewWeixinMenu();
}
