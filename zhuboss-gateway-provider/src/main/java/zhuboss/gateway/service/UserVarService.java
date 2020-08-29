package zhuboss.gateway.service;


import zhuboss.gateway.service.param.AddUserVarParam;
import zhuboss.gateway.service.param.UpdateUserVarParam;

public interface UserVarService {
    
    void add(Integer appId, AddUserVarParam addParam);

    void update(UpdateUserVarParam updateParam);

    void delete(Integer appId, Integer userVarId);

    void setValue(Integer userVarId, String value);
    
}
