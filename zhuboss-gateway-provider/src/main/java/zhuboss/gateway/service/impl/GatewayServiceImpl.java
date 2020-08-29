package zhuboss.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ListMultimap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.adapter.bean.*;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.dict.ValueType;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.facade.constants.InterfaceTypeEnum;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.mapper.MeterAlarmPOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.MeterTypeWritePOMapper;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.service.*;
import zhuboss.gateway.spring.cache.CacheConstants;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackage;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo.*;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo.ZhubossRegisterParam;
import zhuboss.gateway.util.JavaUtil;

import java.util.*;
import java.util.concurrent.*;

@Service
@Slf4j
public class GatewayServiceImpl implements GatewayService {
    @Autowired
    CollectorService collectorService;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    MeterTypeWritePOMapper meterTypeWritePOMapper;
    @Autowired
    MeterKindReadService meterKindReadService;
    @Autowired
    AppCycleService appCycleService;
    @Autowired
    MeterAlarmPOMapper meterAlarmPOMapper;
    @Autowired
    MeterAlarmService meterAlarmService;

    // 向设备同步配置的任务队列
    public ConcurrentLinkedQueue<String> tasks = new ConcurrentLinkedQueue <>();
    ExecutorService fixedThreadPool;
    BlockingQueue<Runnable> queue;

    public GatewayServiceImpl(){
        queue = new LinkedBlockingQueue<Runnable>(1000);
        fixedThreadPool = new ThreadPoolExecutor( //参考Executors.newFixedThreadPool(3)
                1, //keep in the pool, even if they are idle
                3, //the maximum number of threads
                30, //30秒后回收空闲线程
                TimeUnit.SECONDS,
                queue
        );
    }

    @Override
    public JsonResponse checkRegisterResult(ZhubossRegisterParam zhubossRegisterParam) {
        CollectorPO collectorPO = collectorService.getCollectorPO(zhubossRegisterParam.getDevNo());
        if(collectorPO == null){
            return new JsonResponse(false,"dev no " + zhubossRegisterParam.getDevNo() +" not exists");
        }
        if(!collectorPO.getSecretKey().equals(zhubossRegisterParam.getKey())){
            return new JsonResponse(false,"secret error for " + zhubossRegisterParam.getDevNo() +" input "+ zhubossRegisterParam.getKey());
        }
        return new JsonResponse(true,"register success");
    }

    @Override
    public ZhubossMeta getDownMeta(String devNo) {
        ZhubossMeta zhubossMeta = new ZhubossMeta();
        CollectorPO collectorPO = collectorService.getCollectorPO(devNo);
        zhubossMeta.setCycleSeconds(appCycleService.getJsonCycleSeconds(collectorPO,collectorPO.getAppId(),collectorPO.getStationId()));
        List<MeterPO> meterPOList = meterPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterPO.Fields.COLLECTOR_ID,collectorPO.getId())
                .andEqual(MeterPO.Fields.ENABLED,1)
                .sort(MeterPO.Fields.COM_PORT)
                .sort(MeterPO.Fields.HOST) //注意给网关下载的是已经排好序的TCP连接
                .sort(MeterPO.Fields.PORT)
                .sort(MeterPO.Fields.ADDR)
        );
        //lora网关检测
        boolean isLora =  meterPOMapper.selectCountByClause(new QueryClauseBuilder()
                .andEqual(MeterPO.Fields.COLLECTOR_ID,collectorPO.getId())
                .andEqual(MeterPO.Fields.ENABLED,1)
                .andEqual(MeterPO.Fields.INTERFACE_TYPE,InterfaceTypeEnum.LORA.getCode())) > 0;
        if(isLora){
            LoraCfg loraCfg = new LoraCfg();
            loraCfg.setLoraChan(0x17);
            loraCfg.setLoraSped(0x05);
            loraCfg.setLoraTransMode(2);
            zhubossMeta.setLoraCfg(loraCfg);
        }

        Map<Integer, Set<String>> meterTypeVars = new HashedMap();
        List<ZhubossMeter> results = new ArrayList<>();
        //一次性把所有的越限条件都加载出来
        Map<Integer, ListMultimap<String,String>> overLimitMap = meterAlarmService.map(collectorPO.getId());
        for(MeterPO meterPO : meterPOList){
            ZhubossMeter zhubossMeter = new ZhubossMeter();
            if(meterPO.getInterfaceType().equals(InterfaceTypeEnum.COM.getCode())){
                // LORA网关不能有串口
                if(isLora){
                    continue;
                }
                zhubossMeter.setType(0);
                zhubossMeter.setComPort(meterPO.getComPort());
                zhubossMeter.setAddr(meterPO.getAddr());
            }else if(meterPO.getInterfaceType().equals(InterfaceTypeEnum.LORA.getCode())){
                zhubossMeter.setType(1);
                zhubossMeter.setLoraAddr(meterPO.getLoraAddr());
                zhubossMeter.setAddr(meterPO.getAddr());
            }else if(meterPO.getInterfaceType().equals(InterfaceTypeEnum.TCP.getCode())){
                zhubossMeter.setType(2);
                zhubossMeter.setIp(meterPO.getHost());
                zhubossMeter.setPort(meterPO.getPort());
                zhubossMeter.setAddr(meterPO.getAddr());
            }else if( meterPO.getInterfaceType().equals(InterfaceTypeEnum.PLC.getCode())){
                zhubossMeter.setType(3);
                zhubossMeter.setIp(meterPO.getHost());
                zhubossMeter.setPort(meterPO.getPort());
            }else{
                throw new RuntimeException("Unsupport " + meterPO.getInterfaceType());
            }

            zhubossMeter.setMeterTypeId(meterPO.getMeterTypeId());
            if(zhubossMeta.getMeterTypeMap().get(zhubossMeter.getMeterTypeId()+"") == null){
                Set<String> varSet = new HashSet<>();
                meterTypeVars.put(zhubossMeter.getMeterTypeId(),varSet);
                ZhubossMeterType zhubossMeterType = new ZhubossMeterType();
                zhubossMeta.getMeterTypeMap().put(zhubossMeter.getMeterTypeId()+"", zhubossMeterType);
                MeterType meterType = meterTypeFactory.load(meterPO.getMeterTypeId());
                //只有串口才有波特率
                if(meterPO.getInterfaceType().equals(InterfaceTypeEnum.COM.getCode())
                        || meterPO.getInterfaceType().equals(InterfaceTypeEnum.TCP.getCode())
                ){
                    //TCP用不到波特率，但串口也可能用到这个型号
                    zhubossMeterType.setBaudRate(meterType.getBaudRate());
                    zhubossMeterType.setParity(meterType.getParity());
                    zhubossMeterType.setByteSize(meterType.getByteSize());
                    zhubossMeterType.setStopBits(meterType.getStopBits());
                    zhubossMeterType.setReadMillSeconds(meterType.getReadMillSeconds());
                }
                if(meterType instanceof ModbusMeterType){
                    zhubossMeterType.setProtocol(ProtocolEnum.MODBUS.getCode());
                    List<ZhubossRead> zhubossReads = new ArrayList<>();
                    zhubossMeterType.setReads(zhubossReads);
                    for(ReadInfo readInfo : ((ModbusMeterType)meterType).getReadInfos()){
                        ZhubossRead zhubossRead = new ZhubossRead();
                        zhubossReads.add(zhubossRead);
                        zhubossRead.setCmd(readInfo.getCmd());
                        zhubossRead.setStartAddr(readInfo.getStartAddr());
                        zhubossRead.setLen(readInfo.getLen());
                        for(ProfileInfo profileInfo : readInfo.getProfileInfos()){
                            varSet.add(profileInfo.getName());
                            ZhubossProfile profile = new ZhubossProfile();
                            zhubossRead.getProfiles().add(profile);
                            profile.setName(profileInfo.getName());
                            profile.setAddr(profileInfo.getAddr());
                            profile.setValueType(ValueType.valueOf(profileInfo.getValueType()).getCode());
                            if(StringUtils.hasText(profileInfo.getRatioVar())){
                                profile.setRatioVar(profileInfo.getRatioVar());
                            }
                            //告警值范围
                            if(meterKindReadService.isSignal(meterPO.getMeterKindId(),profile.getName())){
                                profile.setSignal(1);
                            }
                        }
                    }
                }else if(meterType instanceof Dlt645MeterType){
                    if(meterType instanceof Dlt6452007MeterType){
                        zhubossMeterType.setProtocol(ProtocolEnum.DLT2007.getCode());
                    }else{
                        zhubossMeterType.setProtocol(ProtocolEnum.DLT1997.getCode());
                    }
                    List<Dlt645Var> dlt645VarList = ((Dlt645MeterType)meterType).getDlt645VarList();
                    zhubossMeterType.setDltVars(dlt645VarList);
                    //
                    for(Dlt645Var dlt645Var : dlt645VarList){
                        varSet.add(dlt645Var.getName());
                    }

                }else if(meterType instanceof PlcMeterType){
                    zhubossMeterType.setProtocol(ProtocolEnum.PLC.getCode());
                    zhubossMeterType.setProtocol2(((PlcMeterType) meterType).getProtocol());
                    List<ZhubossProfile> plcVars = new ArrayList<>();
                    zhubossMeterType.setPlcVars(plcVars);
                    List<ProfileInfo> profileInfoList =  ((PlcMeterType)meterType).getProfileInfos();
                    for(ProfileInfo profileInfo : profileInfoList){
                        ZhubossProfile profile = new ZhubossProfile();
                        plcVars.add(profile);
                        profile.setName(profileInfo.getName());
                        profile.setAddr2(profileInfo.getAddr2()); //PLC 字符串地址
                        profile.setValueType(ValueType.valueOf(profileInfo.getValueType()).getCode());
                        if(StringUtils.hasText(profileInfo.getRatioVar())){
                            profile.setRatioVar(profileInfo.getRatioVar());
                        }
                        varSet.add(profileInfo.getName());
                        //告警值范围
                        if(meterKindReadService.isSignal(meterPO.getMeterKindId(),profile.getName())){
                            profile.setSignal(1);
                        }
                    }

                }

            }

            /**
             * 越限条件
             */
            ListMultimap<String,String>  overlimit= overLimitMap.get(meterPO.getId()); //每块仪表的越限条件
            //过滤：不是所有的meter_type都包括所有的target code,不存在的字段不要放在越限检查中
            if(overlimit != null){
                Iterator<Map.Entry<String,String>> iterator = overlimit.entries().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String,String> entry = iterator.next();
                    String targetCode = entry.getKey();
                    String text = entry.getValue();
                    if(!meterTypeVars.get(meterPO.getMeterTypeId()).contains(targetCode)){
                        iterator.remove();
                    }

                }
                if(overlimit!=null && overlimit.size()>0){
                    zhubossMeter.setOverlimit(overlimit);
                }
            }


            /**
             * 遥信
             */
            results.add(zhubossMeter);
        }
        zhubossMeta.setMeters(results);
        return zhubossMeta;
    }

    @Override
    public void doDownMeta(String devNo) {
        Channel channel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
        if(channel == null){
            return;
        }
        CollectorTypeEnum collectorTypeEnum = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_TYPE);
        // 只有智能网关才有下发功能
        if(!collectorTypeEnum.equals(CollectorTypeEnum.ZHUBOSS)){
            return;
        }
        //下发配置文件
        ZhubossMeta downZhubossMeta = this.getDownMeta(devNo);
        String json = JSON.toJSONString(downZhubossMeta);
        byte[] compressBytes = JavaUtil.compress(json.getBytes()); //配置文件JSON使用压缩格式
        channel.writeAndFlush(new ZhubossDataPackage(ZhubossPackageType.DOWN_MEA,compressBytes));
        log.info("下发配置：{}",json);
    }

    @Override
    public void ifCollectorChange(Integer collectorId, String devNo) {
        CollectorPO collectorPO;
        if(collectorId !=null){
            collectorPO = collectorPOMapper.selectByPK(collectorId);
        }else if(StringUtils.hasText(devNo)){
            collectorPO = collectorService.getCachedCollector(devNo).getCollector();
        }else{
            throw new RuntimeException("collectorId、gwNo all null");
        }
        if(collectorPO.getCollectorType().equals(CollectorTypeEnum.ZHUBOSS.getCode()) && !tasks.contains(collectorPO.getDevNo())){
            //添加任务队列
            tasks.offer(collectorPO.getDevNo());
            //添加执行
            fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    String devNo = GatewayServiceImpl.this.tasks.poll();
                    if(devNo == null){
                        return;
                    }
                    GatewayServiceImpl.this.doDownMeta(devNo);
                }
            });
        }
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true,beforeInvocation = true) //下发之前清缓存
    public void ifMeterKindChange(Integer meterKindId) {
        List<String> devNoList = meterPOMapper.queryCollectorsByMeterKind(meterKindId);
        for(String devNo : devNoList){
            this.ifCollectorChange(null,devNo);
        }
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true,beforeInvocation = true) //下发之前清缓存
    public void ifMeterTypeChange(Integer meterTypeId) {
        List<String> devNoList = meterPOMapper.queryCollectorsByMeterType(meterTypeId);
        for(String devNo : devNoList){
            this.ifCollectorChange(null,devNo);
        }
    }

    @Override
    public void ifAppCycleChange(Integer appId) {
        List<CollectorPO> collectorPOList = collectorPOMapper.selectByClause(new QueryClauseBuilder().andEqual(CollectorPO.Fields.APP_ID,appId));
        for(CollectorPO collectorPO : collectorPOList){
            this.ifCollectorChange(collectorPO.getId(),null);
        }
    }

}
