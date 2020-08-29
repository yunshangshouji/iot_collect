package zhuboss.gateway.controller.browser;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.DateUtil;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.SummaryPOMapper;
import zhuboss.gateway.mapper.SummaryPanelItemPOMapper;
import zhuboss.gateway.mapper.SummaryPanelPOMapper;
import zhuboss.gateway.po.MeterValues;
import zhuboss.gateway.po.SummaryPO;
import zhuboss.gateway.po.SummaryPanelItemPO;
import zhuboss.gateway.po.SummaryPanelPO;
import zhuboss.gateway.spring.web.filter.LoginFilter;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/browser/summary")
public class SummaryBrowserController {
    @Autowired
    SummaryPOMapper summaryPOMapper;
    @Autowired
    SummaryPanelPOMapper summaryPanelPOMapper;
    @Autowired
    SummaryPanelItemPOMapper summaryPanelItemPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;

    @GetMapping("render")
    public Map<String,Object> renderSummary(Integer summaryId){
        SummaryPO summaryPO = summaryPOMapper.selectByPK(summaryId);
        Map<String,Object> result = new HashMap<>();
        result.put("refreshInterval",summaryPO.getRefreshInterval());
        result.put("showHeader",summaryPO.getShowHeader());
        result.put("showDevName",summaryPO.getShowDevName());
        result.put("refreshTime",DateUtil.sdf_yyyyMMddhhmmss.format(new Date()));
        //数据
        Map<String,Object> valueMap = this.loadSummary(summaryId);
        //渲染
        List<SummaryPanelPO> summaryPanelPOList = summaryPanelPOMapper.selectByClause(new QueryClauseBuilder().andEqual(SummaryPanelPO.Fields.SUMMARY_ID,summaryId));
        List<SummaryPanelItemPO> summaryPanelItemPOList = summaryPanelItemPOMapper.selectByClause(new QueryClauseBuilder().andEqual(SummaryPanelItemPO.Fields.SUMMARY_ID,summaryId));
        List<Map<String,Object>> panels = new ArrayList<>();
        for(SummaryPanelPO summaryPanelPO : summaryPanelPOList){
            Map<String,Object> panel = new HashMap<>();
            panel.put("title",summaryPanelPO.getTitle());
            List<Map<String,Object>> itemList = new ArrayList<>();
            panel.put("list",itemList);
            for(SummaryPanelItemPO summaryPanelItemPO : summaryPanelItemPOList){
                if(!summaryPanelItemPO.getSummaryPanelId().equals(summaryPanelPO.getId())){
                    continue;
                }
                Map<String,Object> item = new HashMap<>();
                item.put("panelItemId",summaryPanelItemPO.getId());
                item.put("targetName",summaryPanelItemPO.getTargetName());
                item.put("unit",summaryPanelItemPO.getUnit());
                item.put("devName",summaryPanelItemPO.getDevNullName());
                item.put("value",valueMap.get(summaryPanelItemPO.getId()+""));
                itemList.add(item);
            }
            panels.add(panel);
        }
        result.put("panels",panels);
        return result;
    }

    @GetMapping("load")
    @ApiOperation("返回整个摘要的键值对")
    public Map<String,Object> loadSummary(Integer summaryId){
        List<SummaryPanelItemPO> summaryPanelItemPOList = summaryPanelItemPOMapper.selectByClause(new QueryClauseBuilder().andEqual(SummaryPanelItemPO.Fields.SUMMARY_ID,summaryId));
        List<MeterValues> meterValuesList = meterPOMapper.getMeterValuesBySummaryId(summaryId);
        Map<String,Object> results = new HashMap<>();
        for(SummaryPanelItemPO summaryPanelItemPO : summaryPanelItemPOList){
            for(MeterValues meterValues : meterValuesList){
                if(meterValues.getMeterId().equals(summaryPanelItemPO.getMeterId())){
                    results.put(summaryPanelItemPO.getId()+"",meterValues.getValue(summaryPanelItemPO.getTargetCode()));
                    break;
                }
            }
        }
        results.put("refreshTime", DateUtil.sdf_yyyyMMddhhmmss.format(new Date()));
        return results;
    }

}
