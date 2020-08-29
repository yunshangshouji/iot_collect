package zhuboss.gateway.po;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

@Data
@Table("sys_dict_data")
public class SysDictDataPO extends AbstractPO {
  @PrimaryKey
  private String dictType;

  @PrimaryKey
  private String itemValue;

  private String itemName;

  private Integer sort;

  private String status;

  private String remark;

}