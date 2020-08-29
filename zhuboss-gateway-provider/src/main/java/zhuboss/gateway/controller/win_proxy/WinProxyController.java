package zhuboss.gateway.controller.win_proxy;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.controller.vo.WinProxyCollector;
import zhuboss.gateway.controller.vo.WinProxyDev;
import zhuboss.gateway.controller.vo.WinProxyLoginResult;
import zhuboss.gateway.facade.constants.InterfaceTypeEnum;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.MeterTypePO;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.service.SysDictService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/win_proxy")
public class WinProxyController {
    @Autowired
    AppPOMapper appPOMapper ;
    @Autowired
    UserPOMapper userPOMapper;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    MeterTypePOMapper meterTypePOMapper;

    @RequestMapping("query")
    public WinProxyLoginResult login(@RequestParam(required = true) String userName,@RequestParam(required = true) String loginPwd){
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.MAIL,userName));
        if(userPO == null){
            return new WinProxyLoginResult(false,"用户不存在",null);
        }
        if(!DigestUtils.md5Hex(loginPwd).equals(userPO.getLoginPwd())){
            return new WinProxyLoginResult(false,"密码错误",null);
        }

        List<CollectorPO> collectorPOList = collectorPOMapper.selectByClause(new QueryClauseBuilder()
                .andSQL("EXISTS(SELECT 1 FROM meter WHERE collector_id = collector.`id` AND meter.`interface_type` = 'plc' )") //存在挂载设备
                .andSQL("(EXISTS(SELECT 1 FROM user_app WHERE user_id ="+ userPO.getId() +" AND app_id = collector.`app_id`) OR " +
                        "      EXISTS(SELECT 1 FROM app WHERE app_id = collector.`app_id` AND app.`user_id` = "+userPO.getId()+"))") //当前用户存在权限
        );
        List<WinProxyCollector> collectors = new ArrayList<>();
        for(CollectorPO collectorPO : collectorPOList){
            WinProxyCollector winProxyCollector = new WinProxyCollector();
            collectors.add(winProxyCollector);
            winProxyCollector.setCollectorNo(collectorPO.getDevNo());
            winProxyCollector.setCollectorName(collectorPO.getDevName());
            List<MeterPO> meterPOList = meterPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.COLLECTOR_ID,collectorPO.getId()).andEqual(MeterPO.Fields.INTERFACE_TYPE, InterfaceTypeEnum.PLC.getCode()));
            List<WinProxyDev> devs = new ArrayList<>();
            winProxyCollector.setDevs(devs);
            for(MeterPO meterPO : meterPOList){
                WinProxyDev winProxyDev = new WinProxyDev();
                devs.add(winProxyDev);
                winProxyDev.setId(meterPO.getId());
                winProxyDev.setName(meterPO.getDevName());
                MeterTypePO meterTypePO = meterTypePOMapper.selectByPK(meterPO.getMeterTypeId());
                winProxyDev.setPlcType(meterTypePO.getTypeName());
                winProxyDev.setIp(meterPO.getHost());
                winProxyDev.setPort(meterPO.getPort());
            }
            winProxyCollector.setDevs(devs);
        }
        return new WinProxyLoginResult(true,"登录成功",collectors);
    }

}
