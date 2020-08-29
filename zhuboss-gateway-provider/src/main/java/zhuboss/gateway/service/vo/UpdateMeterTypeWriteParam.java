package zhuboss.gateway.service.vo;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.PrimaryKey;

@Data
public class UpdateMeterTypeWriteParam extends SaveMeterTypeWriteParam {
    @PrimaryKey
    private Integer id;
}
