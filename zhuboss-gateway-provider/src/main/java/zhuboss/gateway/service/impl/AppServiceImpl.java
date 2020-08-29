package zhuboss.gateway.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.StationService;
import zhuboss.gateway.service.param.*;
import zhuboss.gateway.service.vo.CheckUserApp;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.Date;
import java.util.List;

@Service
public class AppServiceImpl implements AppService {
    @Autowired
    AppPOMapper appPOMapper;
    @Autowired
    StationPOMapper stationPOMapper;
    @Autowired
    StationService stationService;
    @Autowired
    UserPOMapper userPOMapper;
    @Autowired
    UserAppPOMapper userAppPOMapper;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    GatewayService gatewayService;

    public CheckUserApp checkUserApp(Integer userId, Integer appId) {
        AppPO appPO = appPOMapper.selectByPK(appId);
        if(appPO.getUserId().equals(userId)){
            return new CheckUserApp(true,true);
        }
        QueryClauseBuilder queryClauseBuilder = new QueryClauseBuilder()
                .andEqual(UserAppPO.Fields.USER_ID,userId)
                .andEqual(UserAppPO.Fields.APP_ID,appId);
        UserAppPO userAppPO = userAppPOMapper.selectOneByClause(queryClauseBuilder);
        return new CheckUserApp(userAppPO!=null,
                userAppPO!=null && userAppPO.getCfgFlag()==1);
    }

    @Override
    @Cacheable(value = CacheConstants.app,key = "#appId")
    public AppPO getCachableAppPO(Integer appId) {
        return appPOMapper.selectByPK(appId);
    }

    @Override
    @Cacheable(value = CacheConstants.gw_lost_seconds,key = "#appId")
    public Integer getGwLostSeconds(Integer appId) {
        AppPO appPO = appPOMapper.selectByPK(appId);
        return appPO.getGwLostSeconds();
    }

    @Override
    public Date getGwLostTime(Integer appId) {
        return DateUtils.addSeconds(new Date(), - this.getGwLostSeconds(appId));
    }

    @Override
    @Transactional
    public void add(SaveAppParam add, Integer userId) {
        AppPO insert = new AppPO();
        BeanMapper.copy(add,insert);
        insert.setUserId(userId);
        //默认上周周期
        insert.setCycleSeconds(10);
        insert.setGwLostSeconds(30);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        appPOMapper.insert(insert);

        UserAppPO userAppPO = new UserAppPO();
        userAppPO.setAppId(insert.getAppId());
        userAppPO.setUserId(userId);
        userAppPO.setOwnerFlag(1);
        userAppPO.setCfgFlag(1);
        userAppPO.setPushFlag(add.getPushFlag());
        userAppPO.setCreateTime(insert.getCreateTime());
        userAppPO.setModifyTime(userAppPO.getCreateTime());
        userAppPOMapper.insert(userAppPO);

        //根站点
        StationPO rootStation = new StationPO();
        rootStation.setAppId(insert.getAppId());
        rootStation.setText(insert.getAppName());
        rootStation.setPid(0+"");
        rootStation.setCreateTime(new Date());
        stationPOMapper.insert(rootStation);
    }

    @Override
    @Transactional
    public void update(SaveAppParam update, Integer userId) {
        AppPO appPO = appPOMapper.selectByPK(update.getAppId());
        Assert.isTrue(appPO.getUserId().equals(userId));
        //项目名称
        appPO.setAppName(update.getAppName());
        appPOMapper.updateByPK(appPO);
        //推送
        UserAppPO userAppPO = userAppPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserAppPO.Fields.APP_ID,update.getAppId()).andEqual(UserAppPO.Fields.OWNER_FLAG,1));
        userAppPO.setPushFlag(update.getPushFlag());
        userAppPOMapper.updateByPK(userAppPO);
        //站点根节点名称
        StationPO rootStationPO = stationService.getRootStation(update.getAppId());
        rootStationPO.setText(update.getAppName());
        stationPOMapper.updateByPK(rootStationPO);
    }

    @Override
    public void updateCfg(Integer appId, SaveAppCfgParam update) {
        AppPO old = appPOMapper.selectByPK(appId);
        BeanMapper.copy(update,old);
        old.setModifyTime(new Date());
        appPOMapper.updateByPK(old);

        //刷新设备
        List<CollectorPO> collectorPOList = collectorPOMapper.selectByClause(new QueryClauseBuilder().andEqual(CollectorPO.Fields.APP_ID,appId));
        for(CollectorPO collectorPO : collectorPOList){
            gatewayService.ifCollectorChange(collectorPO.getId(),null);
        }
    }

    @Override
    @Transactional
    public void delete(Integer appId) {
        userAppPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(UserAppPO.Fields.APP_ID,appId));
        appPOMapper.deleteByPK(appId);
    }

    @Override
    public void addAppUser(AddAppUserParam addAppUserParam,Integer userId) {
        AppPO appPO = appPOMapper.selectByPK(addAppUserParam.getAppId());
        if(!appPO.getUserId().equals(userId)){
            throw new BussinessException("非自己的项目无法操作");
        }
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.MAIL,addAppUserParam.getMail()));
        if(userPO == null){
            throw new BussinessException("邮箱不存在"+addAppUserParam.getMail());
        }
        if(userPO.getId().equals(userId)){
            throw new BussinessException("无需添加项目拥有者为成员");
        }
        //是否已经添加过
        Integer count = userAppPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(UserAppPO.Fields.USER_ID,userPO.getId()).andEqual(UserAppPO.Fields.APP_ID,addAppUserParam.getAppId()));
        if(count>0){
            throw new BussinessException("已经存在，不能重复添加");
        }
        UserAppPO userAppPO = new UserAppPO();
        userAppPO.setAppId(addAppUserParam.getAppId());
        userAppPO.setUserId(userPO.getId());
        userAppPO.setOwnerFlag(0);
        userAppPO.setCfgFlag(addAppUserParam.getCfgFlag()?1:0);
        userAppPO.setPushFlag(addAppUserParam.getPushFlag() ? 1 : 0);
        userAppPO.setCreateTime(new Date());
        userAppPO.setModifyTime(userAppPO.getCreateTime());
        userAppPOMapper.insert(userAppPO);
    }

    @Override
    public void updateAppUser(UpdateAppUserParam updateAppUserParam, Integer userId) {
        UserAppPO userAppPO = userAppPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserAppPO.Fields.ID,updateAppUserParam.getId()));
        AppPO appPO = appPOMapper.selectByPK(userAppPO.getAppId());
        Assert.isTrue(appPO.getUserId().equals(userId));
        userAppPO.setCfgFlag(updateAppUserParam.getCfgFlag()?1:0);
        userAppPO.setPushFlag(updateAppUserParam.getPushFlag()?1:0);
        userAppPO.setModifyTime(new Date());
        userAppPOMapper.updateByPK(userAppPO);
    }

    @Override
    public void deleteAppUser(Integer id, Integer userId) {
        UserAppPO userAppPO = userAppPOMapper.selectByPK(id);
        AppPO appPO = appPOMapper.selectByPK(userAppPO.getAppId());
        if(!appPO.getUserId().equals(userId)){
            throw new BussinessException("非自己的项目无法操作");
        }

        userAppPOMapper.deleteByPK(id);

    }

}
