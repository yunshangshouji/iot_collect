package zhuboss.gateway.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.framework.mybatis.mapper.BaseSqlProvider;
import zhuboss.gateway.po.AppPO;

public interface AppPOMapper extends BaseMapper<AppPO,Integer> {
    @InsertProvider(
            method = "insert",
            type = BaseSqlProvider.class
    )
    @Options(
            keyProperty = "appId",
            useGeneratedKeys = true
    )
    void insert(AppPO insert);
}
