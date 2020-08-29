package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.HisDataMapper;
import zhuboss.gateway.mapper.MeterKindReadPOMapper;
import zhuboss.gateway.po.MeterKindReadPO;
import zhuboss.gateway.po.TableColumn;
import zhuboss.gateway.service.DDLService;

import java.util.Iterator;
import java.util.List;

@Service
public class DDLServiceImpl implements DDLService {
    @Autowired
    HisDataMapper hisDataMapper;
    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;

    private String getDataType(MeterKindReadPO hisViewColumnPO){
        return (hisViewColumnPO.getScale()== null || hisViewColumnPO.getScale() == 0)? "INT" : "DECIMAL(16,6)" ;
    }
    @Override
    public String getTableName(Integer meterKindId){
        return "his_data_"+meterKindId;
    }
    @Override
    public String getColName(Integer meterKindReadId){
        return "c" + meterKindReadId;
    }
    @Override
    public void syncColumns(Integer meterKindId) {
        String tableName = getTableName(meterKindId);
        boolean existsTable = hisDataMapper.checkExistsTable(tableName);
        if(!existsTable){
            hisDataMapper.createTable(tableName);
        }
        List<TableColumn> existsTableColumnList = hisDataMapper.queryTableColumns(tableName);
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId)
                .andEqual(MeterKindReadPO.Fields.PERSIST_FLAG,1)
        );

        /**
         * 同步列
         */
        StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE `"+tableName+"`   \n");
        Iterator<TableColumn> existsIterator = existsTableColumnList.iterator();
        boolean first = true;
        while(existsIterator.hasNext()){
            TableColumn tableColumn = existsIterator.next();
            Iterator<MeterKindReadPO> meterKindReadPOIterator = meterKindReadPOList.iterator();
            boolean find = false;
            while(meterKindReadPOIterator.hasNext()){
                MeterKindReadPO meterKindReadPO = meterKindReadPOIterator.next();
                String columnName = getColName(meterKindReadPO.getId());
                if( tableColumn.getColumnName().equals(columnName)){
                    find = true;
                    //类型不一致
                    if(!tableColumn.getDataType().equals(getDataType(meterKindReadPO))){
                        if(!first){
                            sb.append(",");
                        }
                        first = false;
                        sb.append("\nCHANGE `"+ columnName +"` `"+columnName+"` " + getDataType(meterKindReadPO) +" NULL");
                    }
                    meterKindReadPOIterator.remove();
                    break;
                }
            }
            if(!find){
                //drop column
                if(!first){
                    sb.append(",");
                }
                first = false;
                sb.append("\nDROP COLUMN `"+tableColumn.getColumnName()+"`");
            }
        }
        //add column 需要添加的列
        for(MeterKindReadPO meterKindReadPO : meterKindReadPOList){
            if(!first){
                sb.append(",");
            }
            first = false;
            sb.append("\nADD COLUMN " + getColName(meterKindReadPO.getId()) + " " + getDataType(meterKindReadPO)+" NULL ");
        }
        sb.append(";");
        if(first != true){
            hisDataMapper.executeSQL(sb.toString());
        }
    }

    @Override
    public void dropIfExists(Integer hisViewId) {
        String tableName = getTableName(hisViewId);
        boolean existsTable = hisDataMapper.checkExistsTable(tableName);
        if(existsTable){
            hisDataMapper.dropTable(tableName);
        }
    }

    @Override
    public void truncateTable(Integer meterKindId) {
        hisDataMapper.truncateTable(getTableName(meterKindId));
    }
}
