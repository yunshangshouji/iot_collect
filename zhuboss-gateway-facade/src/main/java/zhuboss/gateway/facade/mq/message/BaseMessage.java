package zhuboss.gateway.facade.mq.message;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseMessage implements Serializable {
    private String id;
    private Integer collectorId;
    private Integer meterId;
    private Date happenTime;

    public abstract String getMessageType();

    public Date getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Date happenTime) {
        this.happenTime = happenTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(Integer collectorId) {
        this.collectorId = collectorId;
    }

    public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }
}
