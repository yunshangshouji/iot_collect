package zhuboss.gateway.spring.activemq;

import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreAddressConfiguration;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@ConditionalOnProperty(name = "mqqt.service.port", matchIfMissing = false)
public class ArtemisBroker implements InitializingBean {
    @Value("${mqqt.service.port}")
    Integer port;

    EmbeddedActiveMQ embedded = new EmbeddedActiveMQ();
    @Override
    public void afterPropertiesSet() throws Exception {
        Configuration configuration = new ConfigurationImpl();
        configuration.setSecurityEnabled(false); //当前server不启用身份验证
        configuration.addAcceptorConfiguration("mqtt","tcp://0.0.0.0:"+port+"?tcpSendBufferSize=1048576;tcpReceiveBufferSize=1048576;protocols=MQTT;useEpoll=false");

        //以下配置避免出现警告没有dead letter queue
        configuration.addAddressConfiguration(createAddressConfiguration("DLQ"));
        configuration.addAddressConfiguration(createAddressConfiguration("ExpiryQueue"));
        configuration.addAddressesSetting("#",
                new AddressSettings()
                        .setDeadLetterAddress(SimpleString.toSimpleString("DLQ"))
                        .setExpiryAddress(SimpleString.toSimpleString("ExpiryQueue")));

        embedded.setConfiguration(configuration);

        embedded.start();

    }

    private CoreAddressConfiguration createAddressConfiguration(String name) {
        return new CoreAddressConfiguration().setName(name)
                .addRoutingType(RoutingType.ANYCAST)
                .addQueueConfiguration(new CoreQueueConfiguration().setName(name)
                        .setRoutingType(RoutingType.ANYCAST));
    }
}
