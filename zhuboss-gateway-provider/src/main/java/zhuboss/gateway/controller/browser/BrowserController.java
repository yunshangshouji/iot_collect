package zhuboss.gateway.controller.browser;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.controller.common.AppController;
import zhuboss.gateway.mapper.ChartPOMapper;
import zhuboss.gateway.mapper.HisViewPOMapper;
import zhuboss.gateway.mapper.MeterKindPOMapper;
import zhuboss.gateway.mapper.SummaryPOMapper;
import zhuboss.gateway.po.ChartPO;
import zhuboss.gateway.po.HisViewPO;
import zhuboss.gateway.po.MeterKindPO;
import zhuboss.gateway.po.SummaryPO;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/browser")
public class BrowserController {
    @Autowired
    SummaryPOMapper summaryPOMapper;
    @Autowired
    HisViewPOMapper hisViewPOMapper;
    @Autowired
    ChartPOMapper chartPOMapper;
    @Autowired
    AppController appController;

    @ApiOperation("页面导航菜单")
    @RequestMapping("menu")
    public Map<String,Object>  menu(@RequestParam(required = true) Integer appId) {
        Map<String,Object> result = new HashMap<>();

        JsonResponse<Map<String,Object>> response = appController.loginApp(appId,false);
        result.putAll(response.getData());

        //图形
        List<ChartPO> chartPOList = chartPOMapper.selectByClause(new QueryClauseBuilder().andEqual(ChartPO.Fields.APP_ID,UserSession.getAppId()));
        List<Map<String,Object>> menuItemList = new ArrayList<>();
        result.put("menus",menuItemList);
        for(int i=0;i<chartPOList.size();i++){
            Map<String,Object> map = new HashMap<>();
            map.put("text",chartPOList.get(i).getChartName());
            map.put("iconCls","icon-zhu_chart");
            map.put("url","/static/browser/distribute.html?full=true&chartId="+chartPOList.get(i).getId());
            menuItemList.add(map);
        }

        //表格
        QueryClauseBuilder qcb = new QueryClauseBuilder()
                .andEqual(SummaryPO.Fields.APP_ID, UserSession.getAppId())
                .sort(SummaryPO.Fields.SEQ) ;
        List<SummaryPO> summaryPOList = summaryPOMapper.selectByClause(qcb);
        for(int i=0;i<summaryPOList.size();i++){
            Map<String,Object> map = new HashMap<>();
            map.put("text",summaryPOList.get(i).getTitle());
            map.put("iconCls","icon-zhu_grid");
            map.put("url","immediate_data.html?summaryId="+summaryPOList.get(i).getId());
            menuItemList.add(map);
        }

        //历史类别
        List<HisViewPO> hisViewPOList = hisViewPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(HisViewPO.Fields.APP_ID,UserSession.getAppId())
                .sort(HisViewPO.Fields.SEQ));
        List<Map<String,Object>> hisViewList = new ArrayList<>();
        for(int i=0;i<hisViewPOList.size();i++){
            Map<String,Object> map = new HashMap<>();
            map.put("text",hisViewPOList.get(i).getTitle());
            map.put("url","his_data_view.html?hisViewId="+hisViewPOList.get(i).getId());
            hisViewList.add(map);
        }
        result.put("hisViews",hisViewList);
        return result;
    }
}
