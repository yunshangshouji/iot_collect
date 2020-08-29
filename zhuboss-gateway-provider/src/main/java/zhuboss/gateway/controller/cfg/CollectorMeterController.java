package zhuboss.gateway.controller.cfg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.DateUtil;
import zhuboss.gateway.facade.constants.InterfaceTypeEnum;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.mapper.TxMeterPOMapper;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.TxMeterPO;
import zhuboss.gateway.service.*;
import zhuboss.gateway.service.param.AddCollectorMeterParam;
import zhuboss.gateway.service.param.SaveCollectorMeterParam;
import zhuboss.gateway.service.param.UpdateCollectorMeterParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/collector/meter")
@Slf4j
public class CollectorMeterController {
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    TxMeterPOMapper txMeterPOMapper;
    @Autowired
    MeterService meterService;
    @Autowired
    AppCycleService appCycleService;
    @Autowired
    CollectorService collectorService;
    @Autowired
    GatewayService gatewayService;
    @Autowired
    AppService appService;

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddCollectorMeterParam addCollectorMeterParam) {
        CollectorPO collectorPO = collectorPOMapper.selectByPK(addCollectorMeterParam.getCollectorId());
        Assert.isTrue(collectorPO.getAppId().equals(UserSession.getAppId()));
        meterService.add(addCollectorMeterParam);
        return new JsonResponse();
    }


    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateCollectorMeterParam updateCollectorMeterParam) {
        MeterPO meterPO = meterPOMapper.selectByPK(updateCollectorMeterParam.getId());
        Assert.isTrue(meterPO.getAppId().equals(UserSession.getAppId()));
        if(meterPO.getInterfaceType().equals(InterfaceTypeEnum.COM.getCode())){
            Assert.isTrue(updateCollectorMeterParam.getComPort() != null);
        }else if(meterPO.getInterfaceType().equals(InterfaceTypeEnum.TCP.getCode())){
            Assert.isTrue(StringUtils.hasText(updateCollectorMeterParam.getHost()) && updateCollectorMeterParam.getPort() != null);
        }

        meterService.update(updateCollectorMeterParam);
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            MeterPO meterPO = meterPOMapper.selectByPK(id);
            Assert.isTrue(meterPO.getAppId().equals(UserSession.getAppId()));
            meterService.delete(id);
        }
        return new JsonResponse();
    }

    @RequestMapping("enable")
    @WriteAction
    public JsonResponse enable(@RequestBody List<Integer> ids) {
        MeterPO meterPO = null;
        for(Integer id : ids) {
            meterPO = meterPOMapper.selectByPK(id);
            Assert.isTrue(meterPO.getAppId().equals(UserSession.getAppId()));
            meterPO.setEnabled(1);
            meterPOMapper.updateByPK(meterPO);
        }
        gatewayService.ifCollectorChange(null, meterPO.getDevNo());
        return new JsonResponse();
    }

    @RequestMapping("disable")
    @WriteAction
    public JsonResponse disable(@RequestBody List<Integer> ids) {
        MeterPO meterPO = null;
        for(Integer id : ids) {
            meterPO = meterPOMapper.selectByPK(id);
            Assert.isTrue(meterPO.getAppId().equals(UserSession.getAppId()));
            meterPO.setEnabled(0);
            meterPOMapper.updateByPK(meterPO);
            //采集数据表状态
            TxMeterPO txMeterPO = txMeterPOMapper.selectByPK(id);
            if(txMeterPO != null){
                TxMeterPO disable = new TxMeterPO();
                disable.setMeterId(id);
                txMeterPOMapper.updateByPK(disable);
            }
        }
        gatewayService.ifCollectorChange(null, meterPO.getDevNo());
        return new JsonResponse();
    }



}
