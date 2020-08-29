package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.Table;

@Data
@Table("dlt645")
@FieldNameConstants(asEnum = true)
public class Dlt645PO extends AbstractPO {
    private String item2007;
    private String item1997;
    private String itemName;
    private Integer scale2007;
    private Integer scale1997;
}
