package zhuboss.gateway.controller.cfg;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.adapter.bean.MeterType;
import zhuboss.gateway.adapter.bean.ModbusMeterType;
import zhuboss.gateway.adapter.bean.ReadInfo;
import zhuboss.gateway.mapper.MeterTypePOMapper;
import zhuboss.gateway.mapper.MeterTypeReadPOMapper;
import zhuboss.gateway.po.MeterTypePO;
import zhuboss.gateway.po.MeterTypeReadPO;
import zhuboss.gateway.service.MeterTypeReadService;
import zhuboss.gateway.service.param.AddMeterTypeReadParam;
import zhuboss.gateway.service.param.UpdateMeterTypeReadParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.tx.meter.modbus.ModbusMessage;
import zhuboss.gateway.util.JavaUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cfg/meterTypeRead")
public class MeterTypeReadController {
    @Autowired
    MeterTypePOMapper meterTypePOMapper;
    @Autowired
    MeterTypeReadService meterTypeReadService;
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    MeterTypeReadPOMapper meterTypeReadPOMapper;

    @RequestMapping("query")
    public GridTable<MeterTypeReadPO> query(
            @RequestParam(value="start",defaultValue="0")  Integer start,
            @RequestParam(value="limit",defaultValue="100") Integer limit,
            @RequestParam(value = "meterTypeId",required = true) Integer meterTypeId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.andEqual(MeterTypeReadPO.Fields.APP_ID, UserSession.getAppId());
        qcb.page(start, limit).sort(MeterTypeReadPO.Fields.SEQ).sort(MeterTypeReadPO.Fields.CMD).sort(MeterTypeReadPO.Fields.START_ADDR) //null排后面
                .sort(MeterTypeReadPO.Fields.SEQ)
                .sort(MeterTypeReadPO.Fields.START_ADDR);
        if(meterTypeId !=null){
            qcb.andEqual("meter_type_id",meterTypeId);
        }
        List<MeterTypeReadPO> list = meterTypeReadPOMapper.selectByClause(qcb);
        Integer cnt = meterTypeReadPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody AddMeterTypeReadParam param) {
        MeterTypePO meterTypePO = meterTypePOMapper.selectByPK(param.getMeterTypeId());
        Assert.isTrue(meterTypePO.getAppId().equals(UserSession.getAppId()));
        meterTypeReadService.add(param);
        return new JsonResponse();
    }

    @GetMapping("changeOrder")
    @WriteAction
    public JsonResponse changeOrder(Integer readId,Integer num){
        meterTypeReadService.changeOrder(readId,num);
        return new JsonResponse();
    }

//    @RequestMapping("update")
//    public JsonResponse update(@RequestBody UpdateMeterTypeReadParam param) {
//        meterTypeReadService.update(param);
//        return new JsonResponse();
//    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            MeterTypeReadPO meterTypeReadPO = meterTypeReadPOMapper.selectByPK(id);
            Assert.isTrue(meterTypeReadPO.getAppId().equals(UserSession.getAppId()));
            meterTypeReadService.delete(id);
        }
        return new JsonResponse();
    }

    /**
     * 生成modbus命令
     * @param meterTypeReadId
     * @param addr
     * @return
     */
    @RequestMapping(value = "command")
    public JsonResponse genCommand(@RequestParam(required = true) Integer meterTypeReadId,
                                   @RequestParam(required = true) Integer addr){
        byte add = (byte)(int)addr;
        //
        MeterTypeReadPO meterTypeReadPO = meterTypeReadPOMapper.selectByPK(meterTypeReadId);
        MeterType meterType = meterTypeFactory.load(meterTypeReadPO.getMeterTypeId());
        if(meterType instanceof ModbusMeterType){
            ModbusMeterType modbusMeterType = (ModbusMeterType)meterType;
            for(ReadInfo readInfo : modbusMeterType.getReadInfos()){
                if(readInfo.getReadId() != meterTypeReadId){
                    continue;
                }
                ModbusMessage modbusMessage = new ModbusMessage((byte)(int)addr,(byte) readInfo.getCmd(),readInfo.getReadCommand());
                byte[] bytes = modbusMessage.getEncodeBytes();
                String s = JavaUtil.bytesToHexString(bytes);
                return new JsonResponse(true,s);
            }
        }else{
            throw new BussinessException("Un support " + meterType);
        }

        return new JsonResponse(false,"未知错误");
    }

    @RequestMapping(value = "parse")
    public JsonResponse parse(@RequestParam(required = true) Integer meterTypeReadId,
                              @RequestParam(required = true) String hexStr) throws Exception {
        MeterTypeReadPO meterTypeReadPO = meterTypeReadPOMapper.selectByPK(meterTypeReadId);
        hexStr = hexStr.replaceAll("\\s","");
        byte[] bytes = JavaUtil.hexStringToBytes(hexStr.substring(6));//去掉地址、功能码、长度
        Map<String,Object> results = new HashMap<>();
        meterTypeFactory.parseRead(meterTypeReadPO.getMeterTypeId(),meterTypeReadId,bytes,results);
        String s = JSON.toJSONString(results);
        return new JsonResponse(true,s);
    }
}
