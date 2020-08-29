package zhuboss.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.framework.utils.ObjectId;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.adapter.bean.Dlt6452007MeterType;
import zhuboss.gateway.adapter.bean.Dlt645MeterType;
import zhuboss.gateway.adapter.bean.MeterType;
import zhuboss.gateway.adapter.bean.ModbusMeterType;
import zhuboss.gateway.dict.MeterTreeTypeEnum;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.facade.constants.InterfaceTypeEnum;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.AppCycleService;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.param.AddCollectorMeterParam;
import zhuboss.gateway.service.param.SaveCollectorMeterParam;
import zhuboss.gateway.service.param.UpdateCollectorMeterParam;
import zhuboss.gateway.service.vo.CachedCollector;
import zhuboss.gateway.spring.cache.CacheConstants;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.channel.task.AbstractTask;
import zhuboss.gateway.tx.channel.task.DeviceRequestMessage;
import zhuboss.gateway.tx.channel.task.MyStack;
import zhuboss.gateway.tx.channel.task.dlt645.Dlt645ReadTask;
import zhuboss.gateway.tx.channel.task.modbus.ModbusReadTask;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackage;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MeterServiceImpl implements MeterService {
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    TxMeterPOMapper txMeterPOMapper;
    @Autowired
    MeterTreePOMapper meterTreePOMapper;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    GatewayService gatewayService;
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    CollectorService collectorService;
    @Autowired
    AppCycleService appCycleService;
    @Autowired
    UserPOMapper userPOMapper;

    void commonCheck(String interfaceType, SaveCollectorMeterParam saveCollectorMeterParam){
        if(interfaceType.equals(InterfaceTypeEnum.COM.getCode())){
            Assert.isTrue(saveCollectorMeterParam.getComPort() != null);
            Assert.isTrue(saveCollectorMeterParam.getAddr()!=null);
        }else if(interfaceType.equals(InterfaceTypeEnum.TCP.getCode())){
            Assert.isTrue(StringUtils.hasText(saveCollectorMeterParam.getHost()) && saveCollectorMeterParam.getPort() != null);
            Assert.isTrue(saveCollectorMeterParam.getAddr()!=null);
        }else if(interfaceType.equals(InterfaceTypeEnum.PLC.getCode())){
            Assert.isTrue(StringUtils.hasText(saveCollectorMeterParam.getHost()) && saveCollectorMeterParam.getPort() != null);
            saveCollectorMeterParam.setAddr(0l);
        }
    }

    @Override
    @CacheEvict(value= CacheConstants.collectors,allEntries = true,beforeInvocation=true)
    @Transactional
    public void add(AddCollectorMeterParam addCollectorMeterParam) {
        commonCheck(addCollectorMeterParam.getInterfaceType(),addCollectorMeterParam);

        MeterPO insert = new MeterPO();
        BeanMapper.copy(addCollectorMeterParam,insert);
        //主表
        CollectorPO collectorPO = collectorPOMapper.selectByPK(addCollectorMeterParam.getCollectorId());
        insert.setAppId(collectorPO.getAppId());
        insert.setOnlineFlag(0);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        meterPOMapper.insert(insert);
        //通讯表
        TxMeterPO txMeterPO = new TxMeterPO();
        txMeterPO.setMeterId(insert.getId());
        txMeterPOMapper.insert(txMeterPO);
        //设备树表
        MeterTreePO meterTreePO = new MeterTreePO();
        meterTreePO.setAppId(collectorPO.getAppId());
        meterTreePO.setType(MeterTreeTypeEnum.METER.name());
        meterTreePO.setStationId(collectorPO.getStationId());
        meterTreePO.setMeterId(insert.getId());
        meterTreePO.setPid("0"); //直属节点
        meterTreePO.setCreateTime(new Date());
        meterTreePOMapper.insert(meterTreePO);
        //下发配置
        gatewayService.ifCollectorChange(addCollectorMeterParam.getCollectorId(),null);
    }

    @Override
    @CacheEvict(value=CacheConstants.collectors,allEntries = true,beforeInvocation=true)
    public void update(UpdateCollectorMeterParam updateCollectorMeterParam) {
        MeterPO update = meterPOMapper.selectByPK(updateCollectorMeterParam.getId());
        commonCheck(update.getInterfaceType(),updateCollectorMeterParam);

        BeanMapper.copy(updateCollectorMeterParam,update);
        update.setModifyTime(new Date());
        meterPOMapper.updateByPK(update);

        //下发配置
        gatewayService.ifCollectorChange(update.getCollectorId(),null);
    }

    @Override
    @CacheEvict(value=CacheConstants.collectors,allEntries = true,beforeInvocation=true)
    @Transactional
    public void delete(Integer id) {
        MeterPO delete = meterPOMapper.selectByPK(id);
        meterPOMapper.deleteByPK(id);
        txMeterPOMapper.deleteByPK(id);
        //下发配置
        gatewayService.ifCollectorChange(delete.getCollectorId(),null);
    }

    @Override
    public MeterPO select(Integer collectorId, Integer comPort,Integer loraAddr,String ip,Integer port, Long addr) {
        QueryClauseBuilder queryClauseBuilder = new QueryClauseBuilder()
                .andEqual(MeterPO.Fields.COLLECTOR_ID,collectorId)
                ;
        if(comPort!=null && comPort>0){ //串口
            queryClauseBuilder.andEqual(MeterPO.Fields.COM_PORT,comPort);
            queryClauseBuilder.andEqual(MeterPO.Fields.ADDR,addr);
        }else if(loraAddr!=null){
            queryClauseBuilder.andEqual(MeterPO.Fields.INTERFACE_TYPE,InterfaceTypeEnum.LORA.getCode());
            queryClauseBuilder.andEqual(MeterPO.Fields.LORA_ADDR,loraAddr);
            queryClauseBuilder.andEqual(MeterPO.Fields.ADDR,addr);
        }else {
            //TCP、PLC
            queryClauseBuilder.andEqual(MeterPO.Fields.HOST,ip).andEqual(MeterPO.Fields.PORT,port);
            //TCP
            if(addr != null){
                queryClauseBuilder.andEqual(MeterPO.Fields.ADDR,addr);
            }
        }
        List<MeterPO> meterPOList = meterPOMapper.selectByClause(queryClauseBuilder);
        if(meterPOList.size() == 1){
            return meterPOList.get(0);
        }
        return null;
    }

    @Override
    public void saveRead(Integer collectorMeterId, Date reportTime,Date readTime,boolean success,String content) {
        Integer onlineFlag = txMeterPOMapper.getOnlineFlag(collectorMeterId);

        Map<String,Object> map = new HashMap<>();
        map.put(TxMeterPO.Fields.METER_ID.name(),collectorMeterId);
        map.put(TxMeterPO.Fields.LAST_MSG_ID.name(),ObjectId.get());
        map.put(TxMeterPO.Fields.LAST_REPORT_TIME.name(),reportTime);
        map.put(TxMeterPO.Fields.LAST_READ_TIME.name(),readTime);
        if(success){ //成功
            map.put(TxMeterPO.Fields.ONLINE_FLAG.name(),1);
            map.put(TxMeterPO.Fields.LAST_VALUES.name(),content);
            map.put(TxMeterPO.Fields.LAST_ERROR_MSG.name(),null);
            if(onlineFlag!=null &&onlineFlag ==0){ //上一条为失败
                map.put(TxMeterPO.Fields.OFFLINE_TIME.name(),null);
            }
        }else{ //失败
            //注意：不管是超时、还是数据转换失败，只要未正常读取，统一按仪表离线处理
            map.put(TxMeterPO.Fields.ONLINE_FLAG.name(),0);
            map.put(TxMeterPO.Fields.LAST_VALUES.name(),null);
            map.put(TxMeterPO.Fields.LAST_ERROR_MSG.name(),content);
            if(onlineFlag!=null &&onlineFlag ==1){ //上一条为成功
                map.put(TxMeterPO.Fields.OFFLINE_TIME.name(),new Date());
            }
        }

        txMeterPOMapper.insertOrUpdate(map);

        //保存到历史表


    }

    @Override
    public void saveMeterOffline(Integer collectorMeterId) {
        Map<String,Object> map = new HashMap<>();
        map.put(TxMeterPO.Fields.METER_ID.name(),collectorMeterId);
        map.put(TxMeterPO.Fields.ONLINE_FLAG.name(),0);
        map.put(TxMeterPO.Fields.OFFLINE_TIME.name(),new Date());
        txMeterPOMapper.insertOrUpdate(map);
    }

    @Override
    public void saveMeterOnline(Integer collectorMeterId) {
        Map<String,Object> map = new HashMap<>();
        map.put(TxMeterPO.Fields.METER_ID.name(),collectorMeterId);
        map.put(TxMeterPO.Fields.ONLINE_FLAG.name(),1);
        map.put(TxMeterPO.Fields.OFFLINE_TIME.name(),null);
        txMeterPOMapper.insertOrUpdate(map);
    }

    @Override
    public JsonResponse read(String taskUUID, Integer collectorMeterId,boolean waitResp) throws InterruptedException {
        MeterPO meterPO = meterPOMapper.selectByPK(collectorMeterId);
        String devNo = meterPO.getDevNo();
        CollectorPO collectorPO = collectorPOMapper.selectByPK(meterPO.getCollectorId());
        Channel channel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
        if(channel == null){
            return new JsonResponse(false,"设备不在线");
        }

        /**
         * 存入任务队列
         */
        MeterType meterType = meterTypeFactory.load(meterPO.getMeterTypeId());
        AbstractTask  readTask;
        if(meterType instanceof ModbusMeterType){
            readTask = new ModbusReadTask(meterPO.getAddr().intValue(), ((ModbusMeterType)meterType).getReadInfos());
        }else if(meterType instanceof Dlt645MeterType){
                readTask = new Dlt645ReadTask(meterType instanceof Dlt6452007MeterType ? ProtocolEnum.DLT2007:ProtocolEnum.DLT1997,
                        meterPO.getAddr(),
                        ((Dlt645MeterType)meterType).getDlt645VarList());
        }else{
            throw new RuntimeException("Unsupport" + meterType);
        }

        if(CollectorTypeEnum.isRAW(collectorPO.getCollectorType())){
            MyStack<DeviceRequestMessage> taskStack = ChannelKeys.readAttr(channel, ChannelKeys.REQUEST_STACK);
            if (taskStack == null) {
                return new JsonResponse(false,"taskStack为空");
            }
            taskStack.push(readTask);
        }else if(collectorPO.getCollectorType().equals(CollectorTypeEnum.ZHUBOSS.getCode())){
            // 智能网关
            ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,readTask);
            ZhubossDataPackage zhubossDataPackage = new ZhubossDataPackage(ZhubossPackageType.READ, meterPO.getComPort()+","+ meterPO.getAddr());
            channel.writeAndFlush(zhubossDataPackage);
        }

        if(!waitResp){
            return new JsonResponse(true,"执行成功!");
        }

        /**
         * 定时器轮询获得响应结果
         */
        synchronized (readTask){
            readTask.wait(10*1000); // 最大等待时间20秒
        }

        Map<String,Object> values;
        if(readTask instanceof ModbusReadTask){
            values = ((ModbusReadTask)readTask).getValues();
        }else if(readTask instanceof Dlt645ReadTask){
            values = ((Dlt645ReadTask)readTask).getValues();
        }else{
            throw new RuntimeException("Unsupport" + readTask);
        }
        if(values.size() == 0){
            return new JsonResponse(false,"应答超时或读取失败，请检查抄表读数核对执行结果");
        }
        return new JsonResponse(true, JSON.toJSONString(values));
    }

    @Override
    public void setCycleSeconds(MeterPO meterPO) {
        CachedCollector collectorPO = collectorService.getCachedCollector(meterPO.getDevNo());
        if(collectorPO.getCollector().getCollectorType().equals(CollectorTypeEnum.ZHUBOSS.getCode())){
            meterPO.setCycleSeconds(appCycleService.getJsonCycleSeconds(collectorPO.getCollector(), meterPO.getAppId(),
                    meterPO.getStationId() //缓存key不能为null
            ));
        }else if(CollectorTypeEnum.isRAW(collectorPO.getCollector().getCollectorType())){
            meterPO.setCycleSeconds(appCycleService.getRawCycleSeconds(collectorPO.getCollector(), meterPO.getAppId(),
                    meterPO.getStationId() , //缓存key不能为null
                    meterPO.getMeterKindId(), meterPO.getMeterTypeId()));
        }
    }

    @Override
    @Cacheable(value = CacheConstants.plc_dev_po,key= "#userName+#password+#devId")
    public MeterPO selectCacheableValidateByDevNO(String userName,String password,Integer devId) {
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.MAIL,userName));
        if(userPO == null){
            throw new BussinessException("用户不存在" + userName);
        }
        if(!DigestUtils.md5Hex(password).equals(userPO.getLoginPwd())){
            throw new BussinessException("密码错误");
        }
        return meterPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.ID,devId).andEqual(MeterPO.Fields.INTERFACE_TYPE,InterfaceTypeEnum.PLC.getCode())
                .andSQL("(EXISTS(SELECT 1 FROM user_app WHERE user_id = "+userPO.getId()+" AND app_id = meter.`app_id`) " +
                        " OR  EXISTS(SELECT 1 FROM app WHERE app_id = meter.`app_id` AND app.`user_id` = "+userPO.getId()+"))") //合法性检查
        );
    }

    @Override
    public MeterPO getMeterPoByRefId(Integer appId, String refId) {
        return meterPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.APP_ID,appId).andEqual(MeterPO.Fields.REF_ID,refId));
    }
}
