package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.SummaryPOMapper;
import zhuboss.gateway.mapper.SummaryPanelItemPOMapper;
import zhuboss.gateway.mapper.SummaryPanelPOMapper;
import zhuboss.gateway.po.SummaryPO;
import zhuboss.gateway.po.SummaryPanelPO;
import zhuboss.gateway.service.SummaryService;
import zhuboss.gateway.service.param.AddSummaryParam;
import zhuboss.gateway.service.param.UpdateSummaryParam;
import zhuboss.gateway.util.SortablePOUtil;

import java.util.Date;

@Service
public class SummaryServiceImpl implements SummaryService {
    @Autowired
    SummaryPOMapper summaryPOMapper;
    @Autowired
    SummaryPanelPOMapper summaryPanelPOMapper;
    @Autowired
    SummaryPanelItemPOMapper summaryPanelItemPOMapper;

    @Override
    public void add(AddSummaryParam addSummaryParam, Integer appId) {
        SummaryPO insert = new SummaryPO();
        BeanMapper.copy(addSummaryParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        insert.setAppId(appId);
        Integer count = summaryPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(SummaryPO.Fields.APP_ID,appId));
        insert.setSeq(count + 1);
        summaryPOMapper.insert(insert);
    }

    @Override
    public void update(UpdateSummaryParam updateSummaryParam) {
        SummaryPO update = summaryPOMapper.selectByPK(updateSummaryParam.getId());
        BeanMapper.copy(updateSummaryParam,update);
        update.setModifyTime(new Date());
        summaryPOMapper.updateByPK(update);
    }

    @Override
    @Transactional
    public void delete(Integer appId,Integer id) {
        summaryPanelItemPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(SummaryPanelPO.Fields.SUMMARY_ID,id));
        summaryPanelPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(SummaryPanelPO.Fields.SUMMARY_ID,id));
        summaryPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(SummaryPO.Fields.APP_ID,appId).andEqual(SummaryPO.Fields.ID,id));
    }

    @Override
    public void changeOrder(Integer summaryId, Integer num) {
        SummaryPO summaryPO = summaryPOMapper.selectByPK(summaryId);
        SortablePOUtil.sort(summaryPOMapper,SummaryPO.Fields.APP_ID,summaryPO,num);
    }
}
