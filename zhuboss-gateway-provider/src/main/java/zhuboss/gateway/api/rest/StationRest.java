package zhuboss.gateway.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.gateway.facade.api.StationFacade;
import zhuboss.gateway.facade.api.param.CollectorParam;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.api.param.StationParam;
import zhuboss.gateway.facade.vo.ApiResult;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/station",method = RequestMethod.POST)
@Api(description = "站点")
public class StationRest {
    @Autowired
    StationFacade stationFacade;

    @RequestMapping(value = "save")
    @ApiOperation("同步站点")
    public ApiResult save(@RequestBody @Valid StationParam saveParam){
        return stationFacade.saveStation(saveParam);
    }

    @RequestMapping(value = "delete")
    @ApiOperation("删除站点")
    public ApiResult delete(DeleteParam deleteParam){
        return stationFacade.deleteStation(deleteParam);
    }

}
