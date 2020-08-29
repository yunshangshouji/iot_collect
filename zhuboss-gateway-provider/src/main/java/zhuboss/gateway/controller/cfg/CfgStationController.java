package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.tree.CommonTree;
import zhuboss.gateway.controller.console.param.ChangePidParam;
import zhuboss.gateway.controller.console.param.UpdateDevInfoParam;
import zhuboss.gateway.dict.MeterTreeTypeEnum;
import zhuboss.gateway.mapper.StationPOMapper;
import zhuboss.gateway.po.AppPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.service.StationService;
import zhuboss.gateway.service.param.*;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.util.MeterUtil;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cfg/station")
public class CfgStationController {
    @Autowired
    StationPOMapper stationPOMapper;
    @Autowired
    StationService stationService;

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddStationParam addStationParam) {
        stationService.add(UserSession.getAppId(),addStationParam);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateStationParam updateStationParam) {
        stationService.update(UserSession.getAppId(),updateStationParam);
        //
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            stationService.delete(UserSession.getAppId(),id);
        }
        return new JsonResponse();
    }

    @RequestMapping("changePid")
    @ApiOperation("节点拖拽")
    @WriteAction
    public JsonResponse changePid(@RequestBody ChangePidParam changePidParam) {
        stationService.changePid(UserSession.getAppId(),changePidParam);
        return new JsonResponse();
    }

}
