package zhuboss.gateway.po;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("sys_error_log")
public class SysErrorLogPO extends AbstractPO {
    @PrimaryKey
    private Long id;
    private String type;
    private Date createTime;
    private String message;
    private String content;
}
