package zhuboss.gateway.util;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.springframework.stereotype.Component;

@Component
public class HttpClientUtil {

    HttpClient httpClient;

    public HttpClientUtil() {
        ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
        connectionManager.setMaxTotal(2000);
        connectionManager.setDefaultMaxPerRoute(200);
        this.httpClient = new DefaultHttpClient(connectionManager);

    }

    public HttpClient getHttpClient() {
        return httpClient;
    }
}
