package zhuboss.gateway.service;

import zhuboss.gateway.po.AppPO;
import zhuboss.gateway.service.param.*;
import zhuboss.gateway.service.vo.CheckUserApp;

import java.util.Date;

public interface AppService {

    CheckUserApp checkUserApp(Integer userId, Integer appId);

    AppPO getCachableAppPO(Integer appId);

    /**
     * 网关离线不活动秒数
     * @param appId
     * @return
     */
    Integer getGwLostSeconds(Integer appId);

    Date getGwLostTime(Integer appId);

    void add(SaveAppParam add, Integer userId);

    void update(SaveAppParam update, Integer userId);

    void updateCfg(Integer appId,SaveAppCfgParam update);

    void delete(Integer appId);

    void addAppUser(AddAppUserParam addAppUserParam,Integer userId);

    void updateAppUser(UpdateAppUserParam updateAppUserParam, Integer userId);

    void deleteAppUser(Integer id, Integer userId);

}
