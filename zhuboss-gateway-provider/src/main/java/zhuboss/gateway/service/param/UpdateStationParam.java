package zhuboss.gateway.service.param;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.PrimaryKey;

@Data
public class UpdateStationParam extends SaveStationParam {
    @PrimaryKey
    private Integer id;
}
