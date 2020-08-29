package zhuboss.gateway.service.impl;

import org.springframework.util.StringUtils;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.mapper.AppCyclePOMapper;
import zhuboss.gateway.mapper.AppPOMapper;
import zhuboss.gateway.po.AppCyclePO;
import zhuboss.gateway.po.AppPO;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.service.AppCycleService;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.spring.cache.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AppCycleServiceImpl implements AppCycleService {
    @Autowired
    AppPOMapper appPOMapper;
    @Autowired
    AppCyclePOMapper appCyclePOMapper;

    @Override
    @Cacheable(value = CacheConstants.cycle_seconds,key="#appId + #stationId +#meterKindId +#meterTypeId")
    public Integer getCycleSeconds(Integer appId, Integer stationId,Integer meterKindId,Integer meterTypeId) {
        List<AppCyclePO> appCyclePOList = appCyclePOMapper.selectByClause(
                new QueryClauseBuilder()
                        .andEqual(AppCyclePO.Fields.APP_ID,appId)
        );
        this.sortReportLevel(appCyclePOList);
        AppCyclePO matched = null;
        int level = 0;
        for(AppCyclePO appCyclePO : appCyclePOList){
            //全匹配【优先级最高】
            if(appCyclePO.getLevel() ==1 && appCyclePO.getStationId().equals(stationId)  && appCyclePO.getMeterTypeId().equals(meterTypeId)){
                matched = appCyclePO;
                break;
            }
            if(appCyclePO.getLevel() ==2 && appCyclePO.getStationId().equals(stationId) && appCyclePO.getMeterKindId().equals(meterKindId) ){
                matched = appCyclePO;
                break;
            }
            if(appCyclePO.getLevel() ==3 && appCyclePO.getStationId().equals(stationId)){
                matched = appCyclePO;
                break;
            }
            if(appCyclePO.getLevel() ==4 && appCyclePO.getMeterTypeId().equals(meterTypeId)){
                matched = appCyclePO;
                break;
            }
            if(appCyclePO.getLevel() ==5 && appCyclePO.getMeterKindId().equals(meterKindId) ){
                matched = appCyclePO;
                break;
            }
            if(appCyclePO.getLevel() ==6){
                matched = appCyclePO;
                break;
            }
        }

        if(matched == null){
            AppPO appPO = appPOMapper.selectByPK(appId);
            return appPO.getCycleSeconds();
        }else {
            return matched.getCycleSeconds();
        }
    }

    @Override
    public Integer getRawCycleSeconds(CollectorPO collectorPO, Integer appId, Integer stationId, Integer meterKindId, Integer meterTypeId) {
        //网关设置了上报周期
        if(collectorPO != null && collectorPO.getReportPeriod() != null){
            return collectorPO.getReportPeriod();
        }
        return this.getCycleSeconds(appId,stationId,meterKindId,meterTypeId);
    }

    @Override
    public Integer getJsonCycleSeconds(CollectorPO collectorPO, Integer appId, Integer stationId) {
        if(collectorPO != null && collectorPO.getReportPeriod() != null){
            return collectorPO.getReportPeriod();
        }
        return this.getCycleSeconds(appId,stationId,null,null);
    }

    @Override
    public void sortReportLevel(List<AppCyclePO> list) {
        Collections.sort(list, new Comparator<AppCyclePO>() {
            @Override
            public int compare(AppCyclePO o1, AppCyclePO o2) {
                return o1.getLevel() - o2.getLevel();
            }
        });
    }
}
