package zhuboss.gateway.service.vo;

import lombok.Data;

@Data
public class DevInfoControlLoop {
    private int cmd;
    private int startAddr;
    private int num;

    public DevInfoControlLoop() {
    }

    public DevInfoControlLoop(int cmd, int startAddr, int num) {
        this.cmd = cmd;
        this.startAddr = startAddr;
        this.num = num;
    }
}
