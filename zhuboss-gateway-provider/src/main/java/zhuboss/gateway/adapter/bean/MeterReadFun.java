package zhuboss.gateway.adapter.bean;

import com.googlecode.aviator.Expression;
import lombok.Data;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MeterReadFun {
	private Method method ; //方法
	private Object[] args; //参数
//	private BigDecimal ratio; //系数
    private String[] ratioVar; //系统变量

	private Expression expression;

	private Boolean newLoop; //是否03新指令
}
