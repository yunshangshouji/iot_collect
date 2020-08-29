package zhuboss.gateway.adapter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.JavaUtil;
import zhuboss.gateway.adapter.bean.*;
import zhuboss.gateway.adapter.rulefun.MeterReader;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.Dlt645Service;
import zhuboss.gateway.service.MeterKindReadService;
import zhuboss.gateway.service.MeterWriteService;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MeterTypeFactory {
	static  MeterTypeFactory instance; //spring context初始化
	@Autowired
	MeterTypePOMapper meterTypePOMapper;
	@Autowired
	MeterTypeReadPOMapper meterTypeReadPOMapper;
	@Autowired
	MeterTypeReadTargetPOMapper meterTypeReadTargetPOMapper;
	@Autowired
	MeterTypeWritePOMapper meterTypeWritePOMapper;
	@Autowired
	MeterTypeDltPOMapper meterTypeDltPOMapper;
	@Autowired
	MeterKindReadService meterKindReadService;
	@Autowired
	MeterWriteService meterWriteService;
	@Autowired
	Dlt645Service dlt645Service;
	@Autowired
	CollectorPOMapper collectorPOMapper;
	@Autowired
	MeterTypePlcReadPOMapper meterTypePlcReadPOMapper;

//	Map<Integer,MeterType> meterTypeMap = new LinkedHashMap<>();

	@Cacheable(value = CacheConstants.meter_type, key = "#meterTypeId")
	public MeterType load(Integer meterTypeId) {
			MeterTypePO meterTypePO =  meterTypePOMapper.selectByPK(meterTypeId);
			if(meterTypePO.getProtocol().equals(ProtocolEnum.MODBUS.getCode())){
				return loadModbusMeterType(meterTypePO);
			}else if(meterTypePO.getProtocol().equals(ProtocolEnum.DLT2007.getCode()) || meterTypePO.getProtocol().equals(ProtocolEnum.DLT1997.getCode())){
				return loadDlt645MeterType(meterTypePO);
			}else{
				//PLC
				return loadPlcMeterType(meterTypePO);
			}
	}

	private ModbusMeterType loadModbusMeterType(MeterTypePO meterTypePO){
		ModbusMeterType meterType = new ModbusMeterType();
		meterType.setName(meterTypePO.getTypeName());
		meterType.setBaudRate(meterTypePO.getBaudRate());
		meterType.setParity(meterTypePO.getParity());
		meterType.setByteSize(meterTypePO.getByteSize());
		meterType.setStopBits(meterTypePO.getStopBits());
		meterType.setReadMillSeconds(meterTypePO.getReadMillSeconds());
		/**
		 * 转换
		 */
		List<MeterTypeReadPO> meterTypeReadPOList = meterTypeReadPOMapper.selectByClause(new QueryClauseBuilder()
				.andEqual(MeterTypeReadPO.Fields.METER_TYPE_ID,meterTypePO.getId())
				.sort(MeterTypeReadPO.Fields.SEQ)
				.sort(MeterTypeReadPO.Fields.START_ADDR)
		);
		for(MeterTypeReadPO meterTypeReadPO :meterTypeReadPOList){
			if(meterTypeReadPO.getStartAddr() == null){
				continue;
			}
			ReadInfo readInfo = new ReadInfo();
			meterType.getReadInfos().add(readInfo);
			readInfo.setReadId(meterTypeReadPO.getId());
			readInfo.setCmd(meterTypeReadPO.getCmd());
			readInfo.setStartAddr(meterTypeReadPO.getStartAddr());
			readInfo.setLen(meterTypeReadPO.getLen());
			//
			byte[] addr = JavaUtil.hexStringToBytes(meterTypeReadPO.getStartAddrHex());
			if(addr.length ==1){
				addr = new byte[]{0,addr[0]};
			}
			byte[] endAddrHex = JavaUtil.hexStringToBytes(meterTypeReadPO.getEndAddrHex());
//			int len = (int) RunFun.BigEndianUnsigned(endAddrHex) - (int)meterTypeReadPO.getStartAddr() + 1 ;
			byte[] lenByte = JavaUtil.int2Bytes(meterTypeReadPO.getLen());
			readInfo.setReadCommand(new byte[]{
					addr[0], //start addr high
					addr[1], //start addr low
					lenByte[2], //data len high
					lenByte[3] //data len low
			});
			List<MeterTypeReadTargetPO> meterTypeReadTargetPOList = meterTypeReadTargetPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterTypeReadTargetPO.Fields.READ_ID,meterTypeReadPO.getId()));
			for (MeterTypeReadTargetPO meterTypeReadTargetPO : meterTypeReadTargetPOList){
				ProfileInfo profileInfo = new ProfileInfo();
				readInfo.getProfileInfos().add(profileInfo);
				profileInfo.setName(meterTypeReadTargetPO.getTargetCode());
				profileInfo.setAddr(meterTypeReadTargetPO.getAddr());
				profileInfo.setValueType(meterTypeReadTargetPO.getValueType());
				boolean isSignal = meterKindReadService.isSignal(meterTypePO.getMeterKindId(),meterTypeReadTargetPO.getTargetCode());
				if(isSignal){
					profileInfo.setSignal(1);
				}
				//TODO num
				profileInfo.setRatioVar(meterTypeReadTargetPO.getRatiovar());
			}
		}

		//
		return  meterType;
	}

	private PlcMeterType loadPlcMeterType(MeterTypePO meterTypePO){
		List<MeterTypePlcReadPO> meterTypePlcReadPOList = meterTypePlcReadPOMapper.selectByClause(new QueryClauseBuilder()
				.andEqual(MeterTypePlcReadPO.Fields.METER_TYPE_ID,meterTypePO.getId())
				.sort(MeterTypePlcReadPO.Fields.ADDR, ESortOrder.DESC)
		);
		if(meterTypePlcReadPOList.size() ==  0){
			return null;
		}
		PlcMeterType plcMeterType = new PlcMeterType();
		plcMeterType.setProtocol(meterTypePO.getProtocol());
		plcMeterType.setName(meterTypePO.getTypeName());
		plcMeterType.setRemark(meterTypePO.getRemark());
		for(MeterTypePlcReadPO meterTypePlcReadPO : meterTypePlcReadPOList){
			ProfileInfo profileInfo = new ProfileInfo();
			profileInfo.setName(meterTypePlcReadPO.getTargetCode());
			profileInfo.setAddr2(meterTypePlcReadPO.getAddr());
			profileInfo.setValueType(meterTypePlcReadPO.getValueType());
			boolean isSignal = meterKindReadService.isSignal(meterTypePO.getMeterKindId(),meterTypePlcReadPO.getTargetCode());
			if(isSignal){
				profileInfo.setSignal(1);
			}
			profileInfo.setRatioVar(meterTypePlcReadPO.getRatiovar());
			plcMeterType.getProfileInfos().add(profileInfo);
		}
		return plcMeterType;
	}
	private Dlt645MeterType loadDlt645MeterType(MeterTypePO meterTypePO){
		Map<Integer,Integer> scaleMap;
		Dlt645MeterType meterType;
		if(meterTypePO.getProtocol().equalsIgnoreCase(ProtocolEnum.DLT2007.getCode())){
			meterType = new Dlt6452007MeterType();
			scaleMap = dlt645Service.getScaleMap(ProtocolEnum.DLT2007);
		}else{
			meterType = new Dlt6451997MeterType();
			scaleMap = dlt645Service.getScaleMap(ProtocolEnum.DLT1997);
		}
		meterType.setName(meterTypePO.getTypeName());
		meterType.setBaudRate(meterTypePO.getBaudRate());
		meterType.setParity(meterTypePO.getParity());
		meterType.setByteSize(meterTypePO.getByteSize());
		meterType.setStopBits(meterTypePO.getStopBits());
		meterType.setReadMillSeconds(meterTypePO.getReadMillSeconds());
		List<MeterTypeDltPO> meterTypeDltPOList = meterTypeDltPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterTypeDltPO.Fields.METER_TYPE_ID,meterTypePO.getId()));
		if(meterTypeDltPOList.size() == 0){
			return null;
		}
		for(MeterTypeDltPO meterTypeDltPO : meterTypeDltPOList){
			Integer code;
			if(meterTypePO.getProtocol().equalsIgnoreCase(ProtocolEnum.DLT2007.getCode())){
				byte[] bytes = JavaUtil.hexStringToBytes(meterTypeDltPO.getDlt2007());
				Assert.isTrue(bytes.length == 4);
				code = JavaUtil.bytes2int(bytes);
			}else{
				byte[] bytes = JavaUtil.hexStringToBytes(meterTypeDltPO.getDlt1997());
				Assert.isTrue(bytes.length == 2);
				code = JavaUtil.bytes2int(new byte[]{0,0,bytes[0],bytes[1]});
			}
			meterType.getDlt645VarList().add(new Dlt645Var(meterTypeDltPO.getTargetCode(),code,scaleMap.get(code)));
		}
		return meterType;
	}
    /**
     * 解析终端返回的数据包
     * @param meterTypeId
     * @param data
     * @return
     * @throws Exception
     */
    public void parseRead(Integer meterTypeId,Integer readId,byte[] data,Map<String,Object> readMap) throws Exception{
        ModbusMeterType meterType = (ModbusMeterType)this.load(meterTypeId);
        ReadInfo readInfo = meterType.findByReadId(readId);
        parseRead(readInfo,data,readMap);
    }

	public void parseRead(ReadInfo readInfo,byte[] data,Map<String,Object> readMap) throws Exception{
		/**
		 * 应用
		 */
		//响应结果对象
		MeterReader meterReader = new MeterReader();
		meterReader.setStartAddr(readInfo.getStartAddr());
		meterReader.setLen(readInfo.getLen());
		meterReader.setData(data);
		Iterator<ProfileInfo>  iterator = readInfo.getProfileInfos().iterator();
		while(iterator.hasNext()) {
			ProfileInfo profileInfo = iterator.next();
			String name = profileInfo.getName();
			BigDecimal result = null;
			try{
			    String methodName = (readInfo.getCmd() == 3 || readInfo.getCmd() == 4) ? profileInfo.getValueType() : "BIT" ; //0x01,0x02
				Method method = MeterReader.class.getMethod(methodName,int.class);
				Object obj = method.invoke(meterReader, profileInfo.getAddr());
				result = new BigDecimal(obj.toString());
			}catch (Exception e){
				log.error("读取变量失败"+ name+"," +e.getMessage());
				throw new RuntimeException(e);
			}
			if(StringUtils.hasText(profileInfo.getRatioVar() )){
				for(String ratio : profileInfo.getRatioVar().split(",")){
					//简单相乘
					BigDecimal x;
					if(ratio.matches("[0-9|\\.]+")){ //数值
						x = new BigDecimal(ratio);
					}else{ //变量
						x = (BigDecimal) readMap.get(ratio);
					}
					if(x == null){
						throw new RuntimeException("x is null,readId:"+readInfo.getReadId()+",name:"+name+",ratio:"+ratio+",ratioVar:"+ JSON.toJSONString(profileInfo.getRatioVar())+",map:"+JSON.toJSONString(readMap)+",readRules:"+JSON.toJSONString(readInfo));
					}
					result = result.multiply(x);
				}
			}
			readMap.put(name, result);
		}
	}

}
