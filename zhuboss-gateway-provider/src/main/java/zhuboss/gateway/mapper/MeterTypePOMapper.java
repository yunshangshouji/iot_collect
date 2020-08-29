package zhuboss.gateway.mapper;

import zhuboss.gateway.po.MeterTypePO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;

import java.util.List;

public interface MeterTypePOMapper extends BaseMapper<MeterTypePO,Integer> {

}
