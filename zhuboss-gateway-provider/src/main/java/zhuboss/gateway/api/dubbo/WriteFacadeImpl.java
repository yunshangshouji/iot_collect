package zhuboss.gateway.api.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.adapter.bean.MeterType;
import zhuboss.gateway.adapter.bean.ModbusMeterType;
import zhuboss.gateway.facade.api.WriteFacade;
import zhuboss.gateway.facade.api.param.WriteParam;
import zhuboss.gateway.facade.vo.ApiResult;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.mapper.MeterKindWritePOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.MeterTypeWritePOMapper;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.MeterKindWritePO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.MeterTypeWritePO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.service.MeterWriteService;
import zhuboss.gateway.tx.channel.MyChannelGroup;

@Service(interfaceClass = WriteFacade.class)
@Component
@Slf4j
public class WriteFacadeImpl implements WriteFacade {
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    CollectorService collectorService;
    @Autowired
    MeterService meterService;
    @Autowired
    MeterKindWritePOMapper meterKindWritePOMapper;
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    MeterTypeWritePOMapper meterTypeWritePOMapper;
    @Autowired
    GatewayService gatewayService;
    @Autowired
    MeterWriteService meterWriteService;

    @Override
    public ApiResult write(WriteParam writeParam) {
        MeterPO meterPO = meterService.getMeterPoByRefId(writeParam.getAppid(),writeParam.getMeterRefId());
        if(meterPO == null){
            return new ApiResult<>(false,"关联设备ID不存在:"+writeParam.getMeterRefId());
        }
        CollectorPO collectorPO = collectorPOMapper.selectByPK(meterPO.getCollectorId());
        Channel channel = MyChannelGroup.allChannels.findChannelByDevNo(collectorPO.getDevNo());
        if(channel == null){
            return  new ApiResult(false,"网关不在线");
        }
        //
        MeterKindWritePO meterKindWritePO = meterKindWritePOMapper.selectOneByClause(new QueryClauseBuilder()
                .andEqual(MeterKindWritePO.Fields.METER_KIND_ID, meterPO.getMeterKindId())
                .andEqual(MeterKindWritePO.Fields.TARGET_CODE,writeParam.getTargetCode())
        );
        if(meterKindWritePO == null){
            return new ApiResult(false,"写入编号"+writeParam.getTargetCode()+"不存在");
        }
        MeterTypeWritePO meterTypeWritePO = meterTypeWritePOMapper.selectOneByClause(new QueryClauseBuilder()
                .andEqual(MeterTypeWritePO.Fields.METER_TYPE_ID, meterPO.getMeterTypeId())
                .andEqual(MeterTypeWritePO.Fields.METER_KIND_WRITE_ID,meterKindWritePO.getId())
        );
        if(meterTypeWritePO == null ){
            return new ApiResult(false,"当前设备型号未定义该操作："+writeParam.getTargetCode());
        }
        //
        MeterType meterType = meterTypeFactory.load(meterPO.getMeterTypeId());
        if(meterType instanceof ModbusMeterType){
            ModbusMeterType modbusMeterType = (ModbusMeterType)meterType;
            //提交
            try {
                JsonResponse jsonResponse = meterWriteService.write(writeParam.getTaskUUID(),meterTypeWritePO.getId(), meterPO.getId(),writeParam.getWriteDataHex(),writeParam.isWaitResp());
                return new ApiResult(jsonResponse.getResult(),jsonResponse.getMsg());
            } catch (InterruptedException e) {
                log.error(e.getMessage(),e);
                return new ApiResult(false,"发生异常");
            }
        }else{
            throw new RuntimeException("Un support " + meterType);
        }

    }

}
