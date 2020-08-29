package zhuboss.gateway.controller.browser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.MeterKindReadPOMapper;
import zhuboss.gateway.po.MeterKindReadPO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tx/read")
public class TxController {
    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;

    @RequestMapping(value="/record",method = {RequestMethod.GET})
    @ApiOperation(value = "最近读数")
    public GridTable<Map<String,Object>> query(
            @RequestParam(required = true) Integer meterKindId,
            String json
    ) {
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId)
                .sort(MeterKindReadPO.Fields.TARGET_CODE)
        );
        GridTable<Map<String,Object>> table = new GridTable();
        List<Map<String,Object>> rows = new ArrayList<>();
        table.setRows(rows);
        if(StringUtils.hasText(json)){
            JSONObject jsonObject = JSON.parseObject(json);
            for(MeterKindReadPO meterKindReadPO : meterKindReadPOList){
                Double val = jsonObject.getDouble(meterKindReadPO.getTargetCode());
                if(val == null){
                    continue;
                }
                Map<String,Object> row = new HashMap<>();
                row.put("targetCode", meterKindReadPO.getTargetCode());
                row.put("targetName", meterKindReadPO.getTargetName());
                row.put("unit", meterKindReadPO.getUnit());
                row.put("val",val);
                rows.add(row);
            }
        }
        //
        table.setTotal(rows.size());
        return table;
    }

}
