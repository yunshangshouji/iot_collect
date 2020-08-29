package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.UserVarPOMapper;
import zhuboss.gateway.po.UserVarPO;
import zhuboss.gateway.service.UserVarService;
import zhuboss.gateway.service.param.AddUserVarParam;
import zhuboss.gateway.service.param.UpdateUserVarParam;

@Service
public class UserVarServiceImpl implements UserVarService {
    @Autowired
    UserVarPOMapper userVarPOMapper;

    @Override
    public void add(Integer appId, AddUserVarParam addParam) {
        UserVarPO insert = new UserVarPO();
        BeanMapper.copy(addParam,insert);
        insert.setAppId(appId);
        userVarPOMapper.insert(insert);
    }

    @Override
    public void update(UpdateUserVarParam updateParam) {
        UserVarPO update = userVarPOMapper.selectByPK(updateParam.getId());
        BeanMapper.copy(updateParam,update);
        userVarPOMapper.updateByPK(update);
    }

    @Override
    public void delete(Integer appId, Integer stationVarId) {
        userVarPOMapper.deleteByClause(new QueryClauseBuilder()
                .andEqual(UserVarPO.Fields.APP_ID,appId)
                .andEqual(UserVarPO.Fields.ID,stationVarId)
        );
    }

    @Override
    public void setValue(Integer userVarId, String value) {
        UserVarPO userVarPO = userVarPOMapper.selectByPK(userVarId);
        if(userVarPO == null){
            throw new BussinessException("变量ID"+userVarId+"不存在");
        }
        userVarPO.setVal(value);
        userVarPOMapper.updateByPK(userVarPO);
    }
}
