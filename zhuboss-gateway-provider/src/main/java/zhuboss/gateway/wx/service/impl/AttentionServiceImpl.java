package zhuboss.gateway.wx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.gateway.mapper.AttentionPOMapper;
import zhuboss.gateway.wx.po.AttentionPO;
import zhuboss.gateway.wx.service.IAttentionService;

import java.util.Date;

@Service("attentionService")
public class AttentionServiceImpl implements IAttentionService {
	@Autowired
	AttentionPOMapper attentionPOMapper;
	

	@Override
	public String insert(AttentionPO attentionPO) {
		attentionPO.setCreateTime(new Date());
		 attentionPOMapper.insert(attentionPO);
		 return attentionPO.getOpenid();
	}

	@Override
	public void update(AttentionPO attentionPO) {
		attentionPO.setModifyTime(new Date());
		attentionPOMapper.updateByPK(attentionPO);
	}

	@Override
	public void delete(String id) {
		attentionPOMapper.deleteByPK(id);
	}

}
