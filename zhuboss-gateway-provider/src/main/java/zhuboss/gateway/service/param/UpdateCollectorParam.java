package zhuboss.gateway.service.param;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.PrimaryKey;

@Data
public class UpdateCollectorParam extends SaveCollectorParam {
    @PrimaryKey
    private Integer id;
}
