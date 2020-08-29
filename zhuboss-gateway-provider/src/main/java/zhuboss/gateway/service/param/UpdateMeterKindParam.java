package zhuboss.gateway.service.param;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.PrimaryKey;

@Data
public class UpdateMeterKindParam extends SaveMeterKindParam {
    @PrimaryKey
    private Integer id;
}
