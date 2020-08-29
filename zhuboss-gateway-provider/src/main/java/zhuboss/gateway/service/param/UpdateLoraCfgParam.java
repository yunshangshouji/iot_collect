package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateLoraCfgParam {

    @NotNull
    private Integer collectorId;

   /* *//**
     * 信道
     *//*
    @NotNull
    private Integer loraChan;

    *//**
     * 速率
     *//*
    @NotNull
    private Integer loraSped;*/

    /**
     * 传输模式
     */
    @NotNull
    private Integer loraTransMode;

}
