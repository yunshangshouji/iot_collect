package zhuboss.gateway.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.gateway.facade.api.CollectorFacade;
import zhuboss.gateway.facade.api.MeterFacade;
import zhuboss.gateway.facade.api.param.CollectorParam;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.api.param.MeterParam;
import zhuboss.gateway.facade.vo.ApiResult;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/meter",method = RequestMethod.POST)
@Api(description = "设备")
public class MeterRest {
    @Autowired
    MeterFacade meterFacade;

    @RequestMapping(value = "save")
    @ApiOperation("同步设备")
    public ApiResult save(@RequestBody @Valid MeterParam saveParam){
        return meterFacade.saveMeter(saveParam);
    }

    @RequestMapping(value = "delete")
    @ApiOperation("删除设备")
    public ApiResult delete(DeleteParam deleteParam){
        return meterFacade.deleteMeter(deleteParam);
    }

}
