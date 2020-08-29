package zhuboss.gateway.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.gateway.facade.api.CollectorFacade;
import zhuboss.gateway.facade.api.param.CollectorParam;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.vo.ApiResult;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/collector",method = RequestMethod.POST)
@Api(description = "通讯网关")
public class CollectorRest {
    @Autowired
    CollectorFacade collectorFacade;

    @RequestMapping(value = "save")
    @ApiOperation("同步网关")
    public ApiResult save(@RequestBody @Valid CollectorParam saveParam){
        return collectorFacade.saveCollector(saveParam);
    }

    @RequestMapping(value = "delete")
    @ApiOperation("删除网关")
    public ApiResult delete(DeleteParam deleteParam){
        return collectorFacade.deleteCollector(deleteParam);
    }

}
