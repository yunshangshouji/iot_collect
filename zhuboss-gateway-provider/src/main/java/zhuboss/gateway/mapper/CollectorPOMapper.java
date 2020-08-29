package zhuboss.gateway.mapper;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.framework.mybatis.mapper.BaseSqlProvider;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.po.CollectorPO;

import java.util.List;

public interface CollectorPOMapper extends BaseMapper<CollectorPO, Integer> {

    @SelectProvider(
            method = "selectByClause",
            type = BaseSqlProvider.class
    )
    List<CollectorPO> selectByClause2(QueryClauseBuilder var1);

}
