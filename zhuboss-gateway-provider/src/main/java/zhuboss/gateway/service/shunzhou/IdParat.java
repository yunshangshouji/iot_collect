package zhuboss.gateway.service.shunzhou;

import zhuboss.gateway.facade.constants.TransTypeEnum;

public class IdParat{
    private TransTypeEnum transType;
    private int interNo;
    private int devAddr;

    public IdParat(TransTypeEnum transType, int interNo, int devAddr) {
        this.transType = transType;
        this.interNo = interNo;
        this.devAddr = devAddr;
    }

    public TransTypeEnum getTransType() {
        return transType;
    }
    public int getInterNo() {
        return interNo;
    }
    public int getDevAddr() {
        return devAddr;
    }
}
