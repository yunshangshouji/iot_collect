package zhuboss.gateway.controller.browser;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.facade.vo.GridTable;
import zhuboss.gateway.mapper.LogLostPOMapper;
import zhuboss.gateway.mapper.LogOverLimitPOMapper;
import zhuboss.gateway.mapper.LogSignalPOMapper;
import zhuboss.gateway.mapper.LogWritePOMapper;
import zhuboss.gateway.po.*;
import zhuboss.gateway.spring.web.filter.UserSession;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/log")
@Slf4j
public class BrowseLogController {
    @Autowired
    LogLostPOMapper logLostPOMapper;
    @Autowired
    LogOverLimitPOMapper logOverLimitPOMapper;
    @Autowired
    LogWritePOMapper logWritePOMapper;
    @Autowired
    LogSignalPOMapper logSignalPOMapper;

    @ApiOperation("离线日志")
    @GetMapping("lost/query")
    public GridTable<LogLostPO> lostQuery(@RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
                                      @RequestParam(value="rows",defaultValue="20") Integer rows,
                                      Integer stationId,
                                      Date beginDate,
                                      Date endDate
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder().andEqual(LogMeter.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page,rows,null, null).sort(LogLostPO.Fields.ID, ESortOrder.DESC);
        if(stationId != null){
            qcb.andEqual(LogMeter.Fields.STATION_ID,stationId);
        }
        if(beginDate != null){
            qcb.andGreatEqual(LogMeter.Fields.HAPPEN_TIME,beginDate);
        }
        if(endDate != null){
            qcb.andLessEqual(LogMeter.Fields.HAPPEN_TIME,endDate);
        }
        List<LogLostPO> logLostPOList = logLostPOMapper.selectByClause(qcb);
        Integer count = logLostPOMapper.selectCountByClause(qcb);
        return new GridTable<>(logLostPOList,count);

    }

    @ApiOperation("越限日志")
    @GetMapping("over_limit/query")
    public GridTable<LogOverLimitPO> overLimitQuery(@RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
                                                    @RequestParam(value="rows",defaultValue="20") Integer rows,
                                                    Integer stationId,
                                                    Date beginDate,
                                                    Date endDate
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder().andEqual(LogMeter.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page,rows,null, null).sort(BaseOverLimitPO.Fields.ID, ESortOrder.DESC);
        if(stationId != null){
            qcb.andEqual(LogMeter.Fields.STATION_ID,stationId);
        }
        if(beginDate != null){
            qcb.andGreatEqual(LogMeter.Fields.HAPPEN_TIME,beginDate);
        }
        if(endDate != null){
            qcb.andLessEqual(LogMeter.Fields.HAPPEN_TIME,endDate);
        }
        List<LogOverLimitPO> logOverLimitPOList = logOverLimitPOMapper.selectByClause(qcb);
        Integer count = logOverLimitPOMapper.selectCountByClause(qcb);
        return new GridTable<>(logOverLimitPOList,count);
    }

    @ApiOperation("遥控日志")
    @GetMapping("write/query")
    public GridTable<LogWritePO> controlQuery(@RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
                                              @RequestParam(value="rows",defaultValue="20") Integer rows,
                                              Integer stationId,
                                              Date beginDate,
                                              Date endDate
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder().andEqual(LogMeter.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page,rows,null, null).sort(BaseOverLimitPO.Fields.ID, ESortOrder.DESC);
        if(stationId != null){
            qcb.andEqual(LogMeter.Fields.STATION_ID,stationId);
        }
        if(beginDate != null){
            qcb.andGreatEqual(LogMeter.Fields.HAPPEN_TIME,beginDate);
        }
        if(endDate != null){
            qcb.andLessEqual(LogMeter.Fields.HAPPEN_TIME,endDate);
        }
        List<LogWritePO> logControlPOList = logWritePOMapper.selectByClause(qcb);
        Integer count = logWritePOMapper.selectCountByClause(qcb);
        return new GridTable<>(logControlPOList,count);
    }

    @ApiOperation("遥信变位日志")
    @GetMapping("signal/query")
    public GridTable<LogSignalPO> signalQuery(@RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
                                              @RequestParam(value="rows",defaultValue="20") Integer rows,
                                              Integer stationId,
                                              Date beginDate,
                                              Date endDate
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder().andEqual(LogMeter.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page,rows,null, null).sort(BaseOverLimitPO.Fields.ID, ESortOrder.DESC);
        if(stationId != null){
            qcb.andEqual(LogMeter.Fields.STATION_ID,stationId);
        }
        if(beginDate != null){
            qcb.andGreatEqual(LogMeter.Fields.HAPPEN_TIME,beginDate);
        }
        if(endDate != null){
            qcb.andLessEqual(LogMeter.Fields.HAPPEN_TIME,endDate);
        }
        List<LogSignalPO> logControlPOList = logSignalPOMapper.selectByClause(qcb);
        Integer count = logSignalPOMapper.selectCountByClause(qcb);
        return new GridTable<>(logControlPOList,count);
    }

}
