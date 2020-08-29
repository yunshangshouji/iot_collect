package zhuboss.gateway.service.param;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.PrimaryKey;

@Data
public class UpdateCollectorMeterParam extends SaveCollectorMeterParam {
    @PrimaryKey
    private Integer id;
}
