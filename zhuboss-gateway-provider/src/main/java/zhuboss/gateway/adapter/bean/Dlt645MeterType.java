package zhuboss.gateway.adapter.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Dlt645MeterType extends MeterType {

    private List<Dlt645Var> dlt645VarList = new ArrayList<>();

}
