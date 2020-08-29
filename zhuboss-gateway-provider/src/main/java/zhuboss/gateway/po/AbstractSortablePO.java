package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;

/**
 * 应用于需要排序的PO对象
 */
@Data
@FieldNameConstants(asEnum = true)
public abstract class AbstractSortablePO extends AbstractPO{

    public abstract Integer get_Id();
    public abstract Integer get_Seq();
    public abstract void set_Seq(Integer seq);
    public abstract Object get_GroupId();

}
