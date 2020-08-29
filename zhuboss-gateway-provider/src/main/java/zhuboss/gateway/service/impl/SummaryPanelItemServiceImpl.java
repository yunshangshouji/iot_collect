package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.SummaryPanelItemPOMapper;
import zhuboss.gateway.mapper.SummaryPanelPOMapper;
import zhuboss.gateway.po.SummaryPO;
import zhuboss.gateway.po.SummaryPanelItemPO;
import zhuboss.gateway.po.SummaryPanelPO;
import zhuboss.gateway.service.SummaryPanelItemService;
import zhuboss.gateway.service.param.AddSummaryPanelItemParam;
import zhuboss.gateway.util.SortablePOUtil;

import java.util.Date;

@Service
public class SummaryPanelItemServiceImpl implements SummaryPanelItemService {
    @Autowired
    SummaryPanelItemPOMapper summaryPanelItemPOMapper;
    @Autowired
    SummaryPanelPOMapper summaryPanelPOMapper;

    @Override
    public void add(AddSummaryPanelItemParam addSummaryItemParam, Integer appId) {
        SummaryPanelItemPO insert = new SummaryPanelItemPO();
        BeanMapper.copy(addSummaryItemParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        insert.setAppId(appId);
        SummaryPanelPO summaryPanelPO = summaryPanelPOMapper.selectByPK(addSummaryItemParam.getSummaryPanelId());
        insert.setSummaryId(summaryPanelPO.getSummaryId());
        Integer count = summaryPanelItemPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(SummaryPanelItemPO.Fields.SUMMARY_PANEL_ID,addSummaryItemParam.getSummaryPanelId()));
        insert.setSeq(count + 1);
        summaryPanelItemPOMapper.insert(insert);
    }

    @Override
    public void delete(Integer appId, Integer id) {
        summaryPanelItemPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(SummaryPanelItemPO.Fields.APP_ID,appId).andEqual(SummaryPanelItemPO.Fields.ID,id));
    }

    @Override
    public void changeOrder(Integer summaryItemId, Integer num) {
        SummaryPanelItemPO summaryPanelItemPO = summaryPanelItemPOMapper.selectByPK(summaryItemId);
        SortablePOUtil.sort(summaryPanelItemPOMapper,SummaryPanelItemPO.Fields.SUMMARY_PANEL_ID,summaryPanelItemPO,num);
    }
}
