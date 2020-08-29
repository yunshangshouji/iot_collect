package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.HisViewColumnPOMapper;
import zhuboss.gateway.mapper.HisViewPOMapper;
import zhuboss.gateway.mapper.HisViewSortPOMapper;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.HisViewService;
import zhuboss.gateway.service.param.AddHisViewParam;
import zhuboss.gateway.service.param.UpdateHisViewParam;
import zhuboss.gateway.util.SortablePOUtil;

import java.util.Date;

@Service
public class HisViewServiceImpl implements HisViewService {
    @Autowired
    HisViewPOMapper hisViewPOMapper;
    @Autowired
    HisViewColumnPOMapper hisViewColumnPOMapper;
    @Autowired
    HisViewSortPOMapper hisViewSortPOMapper;
    
    @Override
    public void add(AddHisViewParam addHisViewParam, Integer appId) {
        HisViewPO insert = new HisViewPO();
        BeanMapper.copy(addHisViewParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        insert.setAppId(appId);
        Integer count = hisViewPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(SummaryPO.Fields.APP_ID,appId));
        insert.setSeq(count + 1);
        hisViewPOMapper.insert(insert);
    }

    @Override
    public void update(UpdateHisViewParam updateHisViewParam) {
        HisViewPO update = hisViewPOMapper.selectByPK(updateHisViewParam.getId());
        BeanMapper.copy(updateHisViewParam,update);
        update.setModifyTime(new Date());
        hisViewPOMapper.updateByPK(update);
    }

    @Override
    public void delete(Integer appId, Integer id) {
        hisViewSortPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(HisViewSortPO.Fields.VIEW_ID,id));
        hisViewColumnPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(HisViewColumnPO.Fields.VIEW_ID,id));
        hisViewPOMapper.deleteByPK(id);
    }

    @Override
    public void changeOrder(Integer hisViewId, Integer num) {
        HisViewPO hisViewPO = hisViewPOMapper.selectByPK(hisViewId);
        SortablePOUtil.sort(hisViewPOMapper,HisViewPO.Fields.APP_ID,hisViewPO,num);
    }
}
