package zhuboss.gateway.wx.service;

public interface ISubscribeService {
	
	void logSubscribe(String openid, String qrscene);
	
	void logUnsubscribe(String openid);
	
}
