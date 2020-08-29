package zhuboss.gateway.tx.channel.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyStack<T extends DeviceRequestMessage> {

	public ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue <>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Map<String,T> messageMap = new HashMap<>();

    /**
     * 尾部添加
     * @param item
     */
	public synchronized void push(T   item) {
		this.messageMap.put(item.getHashAddr(), item);
		queue.offer(item);
	}

    /**
     * 头部弹出
     * @return
     */
	public synchronized T pop(){
		T message = queue.poll();
		if(message !=null){
			this.messageMap.remove(message.getHashAddr());
		}
		return message;
	}

	public synchronized boolean contains(T msg){
		//注意，不能用queue.contains，这是对象equals，不是hash比较
		return messageMap.containsKey(msg.getHashAddr());
	}

	public Map<String, T> getMessageMap() {
		return messageMap;
	}
}
