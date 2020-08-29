package zhuboss.gateway.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import zhuboss.gateway.po.TableColumn;
import zhuboss.gateway.service.vo.HisDataQueryCondition;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HisDataMapper {

    @Select("SELECT COUNT(*) FROM information_schema.`TABLES` WHERE table_name = #{tableName}")
    boolean checkExistsTable(@Param("tableName") String tableName);

    @Update("CREATE TABLE `${tableName}` (\n" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
            "  `data_date` date DEFAULT NULL,\n" +
            "  `read_time` datetime DEFAULT NULL,\n" +
            "  `meter_id` int(11) DEFAULT NULL,\n" +
            "  `create_time` datetime DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_data_readtime` (`app_id`,`read_time`,`meter_id`),\n" +
            "  KEY `idx_data_date` (`app_id`,`data_date`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8")
    int createTable(@Param("tableName") String tableName);

    @Update("drop table ${tableName}")
    int dropTable(@Param("tableName") String tableName);

    @Update("${sql}")
    int executeSQL(@Param("sql") String sql);

    @Update("truncate table ${tableName}")
    int truncateTable(@Param("tableName") String tableName);

    @Select("SELECT column_name AS columnName,data_type AS dataType FROM information_schema.`COLUMNS` WHERE table_name = #{tableName} AND column_name REGEXP \"^c[0-9]+$\"")
    List<TableColumn> queryTableColumns(@Param("tableName") String tableName);

    List<Map<String,Object>> query(@Param("tableName")String tableName ,@Param("cols") List<String> cols,@Param("conditions") List<HisDataQueryCondition> conditions,@Param("orderBy") String orderBy,@Param("start") Integer start, @Param("limit")Integer limit);

    Integer count(@Param("tableName")String tableName, @Param("conditions") List<HisDataQueryCondition> conditions);

    boolean checkRecordExists(@Param("tableName")String tableName, @Param("readTime") Date readTime,@Param("meterId") Integer meterId);

    int insert( @Param("tableName") String tableName,@Param("readTime") Date readTime, @Param("meterId") Integer meterId, @Param("cols") List<String> cols, @Param("vals") List<Object> vals);

    BigDecimal queryTableBytes(@Param("id") Integer meterKindId);

    BigDecimal queryYesterdayPercent(@Param("id") Integer meterKindId);
}
