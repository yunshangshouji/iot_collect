package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.HisViewColumnPOMapper;
import zhuboss.gateway.po.HisViewColumnPO;
import zhuboss.gateway.po.HisViewSortPO;
import zhuboss.gateway.po.SummaryPO;
import zhuboss.gateway.service.HisViewColumnService;
import zhuboss.gateway.service.param.AddHisViewColumnParam;
import zhuboss.gateway.service.param.UpdateHisViewColumnParam;
import zhuboss.gateway.util.SortablePOUtil;

import java.util.Date;

@Service
public class HisViewColumnServiceImpl implements HisViewColumnService {
    @Autowired
    HisViewColumnPOMapper hisViewColumnPOMapper;
    
    @Override
    public void add(AddHisViewColumnParam addHisViewColumnParam, Integer appId) {
        HisViewColumnPO insert = new HisViewColumnPO();
        BeanMapper.copy(addHisViewColumnParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        insert.setAppId(appId);
        Integer count = hisViewColumnPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(HisViewColumnPO.Fields.APP_ID,appId));
        insert.setSeq(count + 1);
        hisViewColumnPOMapper.insert(insert);
    }

    @Override
    public void update(UpdateHisViewColumnParam updateHisViewColumnParam) {
        HisViewColumnPO update = hisViewColumnPOMapper.selectByPK(updateHisViewColumnParam.getId());
        BeanMapper.copy(updateHisViewColumnParam,update);
        update.setModifyTime(new Date());
        hisViewColumnPOMapper.updateByPK(update);
    }

    @Override
    public void delete(Integer appId, Integer id) {
        hisViewColumnPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(HisViewColumnPO.Fields.APP_ID,appId).andEqual(HisViewColumnPO.Fields.ID,id));
    }

    @Override
    public void changeOrder(Integer hisViewColumnId, Integer num) {
        HisViewColumnPO hisViewColumnPO = hisViewColumnPOMapper.selectByPK(hisViewColumnId);
        SortablePOUtil.sort(hisViewColumnPOMapper,HisViewColumnPO.Fields.VIEW_ID,hisViewColumnPO,num);
    }
}
