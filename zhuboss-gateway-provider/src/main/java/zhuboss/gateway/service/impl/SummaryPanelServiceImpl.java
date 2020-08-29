package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.SummaryPanelItemPOMapper;
import zhuboss.gateway.mapper.SummaryPanelPOMapper;
import zhuboss.gateway.po.SummaryPO;
import zhuboss.gateway.po.SummaryPanelItemPO;
import zhuboss.gateway.po.SummaryPanelPO;
import zhuboss.gateway.service.SummaryPanelService;
import zhuboss.gateway.service.param.AddSummaryPanelParam;
import zhuboss.gateway.service.param.UpdateSummaryPanelParam;
import zhuboss.gateway.util.SortablePOUtil;

import java.util.Date;

@Service
public class SummaryPanelServiceImpl implements SummaryPanelService {
    @Autowired
    SummaryPanelPOMapper summaryPanelPOMapper;
    @Autowired
    SummaryPanelItemPOMapper summaryPanelItemPOMapper;

    @Override
    public void add(AddSummaryPanelParam addSummaryParam, Integer appId) {
        SummaryPanelPO insert = new SummaryPanelPO();
        BeanMapper.copy(addSummaryParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        insert.setAppId(appId);
        Integer count = summaryPanelPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(SummaryPanelPO.Fields.SUMMARY_ID,addSummaryParam.getSummaryId()));
        insert.setSeq(count + 1);
        summaryPanelPOMapper.insert(insert);
    }

    @Override
    public void update(UpdateSummaryPanelParam updateSummaryParam) {
        SummaryPanelPO update = summaryPanelPOMapper.selectByPK(updateSummaryParam.getId());
        BeanMapper.copy(updateSummaryParam,update);
        update.setModifyTime(new Date());
        summaryPanelPOMapper.updateByPK(update);
    }

    @Override
    @Transactional
    public void delete(Integer appId, Integer id) {
        summaryPanelItemPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(SummaryPanelItemPO.Fields.APP_ID,appId).andEqual(SummaryPanelItemPO.Fields.SUMMARY_PANEL_ID,id));
        summaryPanelPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(SummaryPanelPO.Fields.APP_ID,appId).andEqual(SummaryPanelPO.Fields.ID,id));
    }

    @Override
    public void changeOrder(Integer summaryPanelId, Integer num) {
        SummaryPanelPO summaryPO = summaryPanelPOMapper.selectByPK(summaryPanelId);
        SortablePOUtil.sort(summaryPanelPOMapper,SummaryPanelPO.Fields.SUMMARY_ID,summaryPO,num);
    }
}
