package zhuboss.gateway.service.vo;

import lombok.Data;

@Data
public class CheckUserApp {
    private boolean browser;
    private boolean cfg;

    public CheckUserApp(boolean browser, boolean cfg) {
        this.browser = browser;
        this.cfg = cfg;
    }
}
