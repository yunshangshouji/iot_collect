package zhuboss.gateway.wx.service;


import zhuboss.gateway.wx.po.AttentionPO;

public interface IAttentionService {
	
	String insert(AttentionPO attentionPO);
	
	void update(AttentionPO attentionPO);
	
	void delete(String id);
}
