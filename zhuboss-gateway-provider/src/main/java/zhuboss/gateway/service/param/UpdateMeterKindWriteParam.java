package zhuboss.gateway.service.param;


import lombok.Data;
import zhuboss.framework.mybatis.mapper.PrimaryKey;

@Data
public class UpdateMeterKindWriteParam extends SaveMeterKindWriteParam {
    @PrimaryKey
    private Integer id;
}
