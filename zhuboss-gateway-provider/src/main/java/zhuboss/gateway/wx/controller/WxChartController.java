package zhuboss.gateway.wx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.facade.vo.GridTable;
import zhuboss.gateway.mapper.ChartPOMapper;
import zhuboss.gateway.po.ChartPO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("wx_web/chart")
@Slf4j
public class WxChartController {
	@Autowired
	ChartPOMapper chartPOMapper;
	
	@RequestMapping("query")
	public GridTable<Map<String,Object>> query(Integer offset, Integer limit,
											   Integer appId) {
		GridTable<Map<String,Object>> gridTable = new GridTable<>();
		//TODO check openid 权限
		QueryClauseBuilder qcb = new QueryClauseBuilder()
				.andEqual(ChartPO.Fields.APP_ID, appId)
				.sort(ChartPO.Fields.APP_ID);
		List<ChartPO> chartPOList = chartPOMapper.selectByClause(qcb);
		List<Map<String,Object>> list = new ArrayList<>();
		for(ChartPO chartPO : chartPOList){
			Map<String,Object> map = new HashMap<>();
			map.put("id",chartPO.getId());
			map.put("chartName",chartPO.getChartName());
			list.add(map);
		}
		Integer count = chartPOMapper.selectCountByClause(qcb);
		gridTable.setRows(list);
		gridTable.setTotal(count);
		return gridTable;
	}


}
