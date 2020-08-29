package zhuboss.gateway.wx.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.MenuPOMapper;
import zhuboss.gateway.wx.po.MenuPO;
import zhuboss.gateway.wx.service.MenuService;
import zhuboss.gateway.wx.wx.WeixinAdpater;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("menuService")
@Slf4j
public class MenuServiceImpl implements MenuService {
	@Autowired
	MenuPOMapper menuPOMapper;
	@Autowired
	WeixinAdpater weixinAdapter;
	

	@Override
	public Integer insert(MenuPO columnItemPO) {
		 menuPOMapper.insert(columnItemPO);
		 return columnItemPO.getId();
	}

	@Override
	public void update(MenuPO columnItemPO) {
		menuPOMapper.updateByPK(columnItemPO);
	}

	@Override
	public void delete(Integer id) {
		menuPOMapper.deleteByPK(id);
	}

	@Override
	public Map<String, Object> genWeixinMenu() {
		List<MenuPO> menuPOList = menuPOMapper.selectByClause(new QueryClauseBuilder().andEqual("level", 1).sort("seq", ESortOrder.ASC));
		List<Map<String,Object>> menuItemList = new ArrayList<Map<String,Object>>();
		for(MenuPO menuPO : menuPOList){
			Map<String,Object> menuItem = menuPO2MenuItem(menuPO);
			menuItemList.add(menuItem);
		}
		log.info(JSON.toJSONString(menuItemList));
		return weixinAdapter.createMenu(menuItemList);
	}

	private Map<String,Object> menuPO2MenuItem(MenuPO menuPO){
		Map<String,Object> menuItem = new HashMap<String,Object>();
		menuItem.put("name", menuPO.getName());
		if(menuPO.getType().equals("dir")){
			menuItem.put("type" , "dir");
			List<MenuPO> childMenuPOList = menuPOMapper.selectByClause(new QueryClauseBuilder().andEqual("pid", menuPO.getId()).andEqual("level", 2));
			List<Map<String,Object>> childMenuItemList = new ArrayList<Map<String,Object>>();
			for(MenuPO chilMenuPO : childMenuPOList){
				childMenuItemList.add(menuPO2MenuItem(chilMenuPO));
			}
			if(childMenuItemList.size()>0){
				menuItem.put("sub_button",childMenuItemList);
			}
		}else if(menuPO.getType().equals("view")){
			menuItem.put("K_" , menuPO.getId());
			menuItem.put("type" , "view");
			menuItem.put("url",menuPO.getUrl().startsWith("http")?menuPO.getUrl():(menuPO.getUrl()));
		}else if(menuPO.getType().equals("click")){
			menuItem.put("key","K_"+menuPO.getId());
			menuItem.put("type", "click");
		}
		return menuItem;
	}

	@Override
	public Map<String,Object> viewWeixinMenu() {
		return weixinAdapter.queryMenu();
	}

}
