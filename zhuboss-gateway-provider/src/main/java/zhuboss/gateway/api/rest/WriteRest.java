package zhuboss.gateway.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.gateway.facade.api.WriteFacade;
import zhuboss.gateway.facade.api.param.WriteParam;
import zhuboss.gateway.facade.vo.ApiResult;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/write",method = RequestMethod.POST)
@Api(description = "写操作(遥控、遥调)")
public class WriteRest {
    @Autowired
    WriteFacade writeFacade;

    @RequestMapping(value = "save")
    @ApiOperation("写操作")
    public ApiResult write(@RequestBody @Valid WriteParam writeParam){
        return writeFacade.write(writeParam);
    }

}
