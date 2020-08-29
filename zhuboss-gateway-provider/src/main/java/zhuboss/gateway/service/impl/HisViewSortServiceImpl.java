package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.HisViewSortPOMapper;
import zhuboss.gateway.po.HisViewSortPO;
import zhuboss.gateway.po.SummaryPO;
import zhuboss.gateway.service.HisViewSortService;
import zhuboss.gateway.service.param.AddHisViewSortParam;
import zhuboss.gateway.service.param.UpdateHisViewSortParam;
import zhuboss.gateway.util.SortablePOUtil;

import java.util.Date;

@Service
public class HisViewSortServiceImpl implements HisViewSortService {
    @Autowired
    HisViewSortPOMapper hisViewSortPOMapper;
    
    @Override
    public void add(AddHisViewSortParam addHisViewSortParam, Integer appId) {
        HisViewSortPO insert = new HisViewSortPO();
        BeanMapper.copy(addHisViewSortParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        insert.setAppId(appId);
        Integer count = hisViewSortPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(SummaryPO.Fields.APP_ID,appId));
        insert.setSeq(count + 1);
        hisViewSortPOMapper.insert(insert);
    }

    @Override
    public void update(UpdateHisViewSortParam updateHisViewSortParam) {
        HisViewSortPO update = hisViewSortPOMapper.selectByPK(updateHisViewSortParam.getId());
        BeanMapper.copy(updateHisViewSortParam,update);
        update.setModifyTime(new Date());
        hisViewSortPOMapper.updateByPK(update);
    }

    @Override
    public void delete(Integer appId, Integer id) {
        hisViewSortPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(HisViewSortPO.Fields.APP_ID,appId).andEqual(HisViewSortPO.Fields.ID,id));
    }

    @Override
    public void changeOrder(Integer hisViewSortId, Integer num) {
        HisViewSortPO hisViewColumnPO = hisViewSortPOMapper.selectByPK(hisViewSortId);
        SortablePOUtil.sort(hisViewSortPOMapper,HisViewSortPO.Fields.VIEW_ID,hisViewColumnPO,num);
    }
}
