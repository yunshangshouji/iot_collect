package zhuboss.gateway.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.gateway.facade.api.AlarmConfigFacade;
import zhuboss.gateway.facade.api.param.AlarmConfigParam;
import zhuboss.gateway.facade.api.param.CollectorParam;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.vo.ApiResult;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/alarm_config",method = RequestMethod.POST)
@Api(description = "报警条件")
public class AlarmConfigRest {
    @Autowired
    AlarmConfigFacade alarmConfigFacade;

    @RequestMapping(value = "save")
    @ApiOperation("同步条件")
    public ApiResult save(@RequestBody @Valid AlarmConfigParam saveParam){
        return alarmConfigFacade.saveAalrmConfig(saveParam);
    }

    @RequestMapping(value = "delete")
    @ApiOperation("删除条件")
    public ApiResult delete(DeleteParam deleteParam){
        return alarmConfigFacade.deleteAlarmConfig(deleteParam);
    }

}
