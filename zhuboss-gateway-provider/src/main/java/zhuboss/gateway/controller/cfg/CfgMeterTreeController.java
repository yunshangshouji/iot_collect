package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.tree.CommonTree;
import zhuboss.gateway.controller.console.param.ChangePidParam;
import zhuboss.gateway.controller.console.param.UpdateDevInfoParam;
import zhuboss.gateway.dict.MeterTreeTypeEnum;
import zhuboss.gateway.mapper.AppPOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.MeterTreePOMapper;
import zhuboss.gateway.mapper.StationPOMapper;
import zhuboss.gateway.po.AppPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.MeterTreePO;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.service.MeterTreeService;
import zhuboss.gateway.service.param.AddMeterTreeParam;
import zhuboss.gateway.service.param.UpdateMeterTreeParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.util.MeterUtil;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cfg/meter_tree")
@Slf4j
public class CfgMeterTreeController {
    @Autowired
    MeterTreePOMapper meterTreePOMapper;
    @Autowired
    StationPOMapper stationPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    AppPOMapper appPOMapper;
    @Autowired
    MeterTreeService meterTreeService;

    @GetMapping("/meter_info")
    public @ResponseBody Object view(@RequestParam("meterId") Integer meterId){
        Map<String,Object> result = new HashMap<>();
        MeterPO meterPO = meterPOMapper.selectByPK(meterId);
        return meterPO;
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddMeterTreeParam addMeterTreeParam) {
        meterTreeService.add(UserSession.getAppId(),addMeterTreeParam);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateMeterTreeParam updateMeterTreeParam) {
        meterTreeService.update(UserSession.getAppId(),updateMeterTreeParam);
        //
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            meterTreeService.deleteById(UserSession.getAppId(),id);
        }
        return new JsonResponse();
    }

    @RequestMapping("changePid")
    @ApiOperation("节点拖拽")
    @WriteAction
    public JsonResponse changePid(@RequestBody ChangePidParam changePidParam) {
        meterTreeService.changePid(UserSession.getAppId(),changePidParam);
        return new JsonResponse();
    }

    @RequestMapping("updateDevInfo")
    @WriteAction
    public JsonResponse updateDevInfo(@RequestBody UpdateDevInfoParam updateDevInfoParam){
        MeterPO meterPO = meterPOMapper.selectByPK(updateDevInfoParam.getId());
        Assert.isTrue(meterPO.getAppId().equals(UserSession.getAppId()));
        meterPO.setDevName(updateDevInfoParam.getDevName());
        meterPOMapper.updateByPK(meterPO);
        return new JsonResponse();
    }


}
