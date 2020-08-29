package zhuboss.gateway.console.websocket;

import javax.websocket.Session;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WebSocketSessionFactory {
	static final WebSocketSessionFactory sessionSet = new WebSocketSessionFactory();
	
	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String getDateTime() {
		return sdf.format(new Date());
	}
	
	public static WebSocketSessionFactory getInstance() {
		return sessionSet;
	}
	
	Map<String,List<Session>> sessionMap = new HashMap<>(); //采集器-->监听的session
	
	/**
	 * 添加监听
	 * @param collectorId
	 * @param session
	 */
	public synchronized void addListerner(String collectorId,Session session) {
		List<Session> sessionList = sessionMap.get(collectorId);
		if(sessionList == null) {
			sessionList = Collections.synchronizedList(new ArrayList<>()); //线程安全
			sessionMap.put(collectorId, sessionList);
		}
		sessionList.add(session);
	}
	
	/**
	 * 移除监听
	 * @param collectorId
	 * @param session
	 */
	public synchronized void removeListerner(String collectorId,Session session) {
		List<Session> sessionList = sessionMap.get(collectorId);
		if(sessionList != null) {
			sessionList.remove(session);
		}
	}

	public Map<String, List<Session>> getSessionMap() {
		return sessionMap;
	}
}
