package zhuboss.gateway.adapter.bean;

import com.googlecode.aviator.Expression;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProfileInfo implements Serializable {
    private String name;
    private Integer addr; //for 仪表
    private String addr2;//for plc
    private String valueType;
    private int signal;
    /**
     * 变量、常量系数
     */
    private String ratioVar;
    /**
     * 正常值范围
     */
    private String alarmVal;

    /**
     * 平台二次运算
     */
    private Expression expression;
}
