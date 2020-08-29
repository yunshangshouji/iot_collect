package zhuboss.gateway.controller.common;

import com.alibaba.fastjson.JSON;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/common/sys/")
@Slf4j
public class SysDictController {
  @Autowired
  SysDictDataPOMapper sysDictDataPOMapper;
  @Autowired
  MeterKindPOMapper meterKindPOMapper;
  @Autowired
  MeterTypePOMapper meterTypePOMapper;
  @Autowired
  AppPOMapper appPOMapper;
  @Autowired
  Dlt645POMapper dlt645POMapper;

  /**
   * 加上缓存
   * @return
   * @throws IOException
   */
  @GetMapping("dict.js")
  public void js(HttpServletResponse response) throws IOException {
    StringBuffer sb = new StringBuffer("var dict = new Object();\n");
    List<SysDictDataPO> dictDataList = sysDictDataPOMapper.selectByClause(new QueryClauseBuilder().andEqual("status", "1").sort("dict_type").sort("sort"));

    /**
     * 系统app
     */
    List<AppPO> appPOList = appPOMapper.selectByClause(new QueryClauseBuilder().sort(AppPO.Fields.APP_ID));
    for(AppPO appPO : appPOList){
      SysDictDataPO sysDictDataPO = new SysDictDataPO();
      sysDictDataPO.setDictType("app");
      sysDictDataPO.setItemValue(appPO.getAppId()+"");
      sysDictDataPO.setItemName(appPO.getAppId()+"."+appPO.getAppName());
      dictDataList.add(sysDictDataPO);
    }
    /**
     * 动态字典，表类别
     */
    if(UserSession.getAppId() != null){
      /**
       * 仪表类别
       */
      List<MeterKindPO> meterKindPOList = meterKindPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterKindPO.Fields.APP_ID, UserSession.getAppId()).andEqual(MeterKindPO.Fields.PLC_FLAG,0)
              .sort(MeterKindPO.Fields.KIND_CODE));
      List<SysDictDataPO> allList = new ArrayList<>();
      for(MeterKindPO meterKindPO : meterKindPOList){
        SysDictDataPO sysDictDataPO = new SysDictDataPO();
        sysDictDataPO.setDictType("meterkind");
        sysDictDataPO.setItemValue(meterKindPO.getId()+"");
        sysDictDataPO.setItemName(meterKindPO.getKindName());
        dictDataList.add(sysDictDataPO);
      }
      List<MeterKindPO> meterKindPLCPOList = meterKindPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterKindPO.Fields.APP_ID, UserSession.getAppId()).andEqual(MeterKindPO.Fields.PLC_FLAG,1)
              .sort(MeterKindPO.Fields.KIND_CODE));
      for(MeterKindPO meterKindPO : meterKindPLCPOList){
        SysDictDataPO sysDictDataPO = new SysDictDataPO();
        sysDictDataPO.setDictType("meterkind_plc");
        sysDictDataPO.setItemValue(meterKindPO.getId()+"");
        sysDictDataPO.setItemName(meterKindPO.getKindName());
        dictDataList.add(sysDictDataPO);
        allList.add(sysDictDataPO);
      }

      /**
       * 表型号
       */
      List<MeterTypePO> meterTypePOList = meterTypePOMapper.selectByClause(new QueryClauseBuilder().andSQL("EXISTS(SELECT 1 FROM `meter_kind` WHERE id = meter_type.`meter_kind_id` AND plc_flag = 0)"));
      for(MeterTypePO meterTypePO : meterTypePOList){
        SysDictDataPO sysDictDataPO = new SysDictDataPO();
        sysDictDataPO.setDictType("metertype");
        sysDictDataPO.setItemValue(meterTypePO.getId()+"");
        sysDictDataPO.setItemName(meterTypePO.getId()+"."+meterTypePO.getTypeName());
        dictDataList.add(sysDictDataPO);
      }
      meterTypePOList = meterTypePOMapper.selectByClause(new QueryClauseBuilder().andSQL("EXISTS(SELECT 1 FROM `meter_kind` WHERE id = meter_type.`meter_kind_id` AND plc_flag = 1)"));
      for(MeterTypePO meterTypePO : meterTypePOList){
        SysDictDataPO sysDictDataPO = new SysDictDataPO();
        sysDictDataPO.setDictType("metertype_plc");
        sysDictDataPO.setItemValue(meterTypePO.getId()+"");
        sysDictDataPO.setItemName(meterTypePO.getId()+"."+meterTypePO.getTypeName());
        dictDataList.add(sysDictDataPO);
      }

    }

    /**
     * DLT645-2007数据项
     */
    List<Dlt645PO> dlt645POList = dlt645POMapper.selectByClause(new QueryClauseBuilder());
    for(Dlt645PO dlt645PO : dlt645POList){
      SysDictDataPO sysDictDataPO = new SysDictDataPO();
      sysDictDataPO.setDictType("dlt645");
      sysDictDataPO.setItemValue(dlt645PO.getItem2007());
      sysDictDataPO.setItemName(dlt645PO.getItemName()+"("+dlt645PO.getItem2007()+"/"+dlt645PO.getItem1997()+")");
      dictDataList.add(sysDictDataPO);
    }


    String dictType = dictDataList.get(0).getDictType();
    List<Item> list = new ArrayList<>();
    for(SysDictDataPO dataDomain : dictDataList) {
      if(dictType.equals(dataDomain.getDictType())) {
        list.add(new Item(dataDomain.getItemValue(),dataDomain.getItemName()));
      }else {
        //上个字典结束
        sb.append("dict[\""+dictType+"\"]=" + JSON.toJSONString(list) +";\n");
        //新的字典开始
        dictType = dataDomain.getDictType();
        list = new ArrayList<>();
        list.add(new Item(dataDomain.getItemValue(),dataDomain.getItemName()));
      }
    }
    //最后一个字典
    sb.append("dict[\""+dictType+"\"]=" + JSON.toJSONString(list) +";\n");

    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-Type", "application/x-javascript");
    response.getOutputStream().write(sb.toString().getBytes());
    response.getOutputStream().flush();
  }


}
