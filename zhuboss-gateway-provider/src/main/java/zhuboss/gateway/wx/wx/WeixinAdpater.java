package zhuboss.gateway.wx.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import zhuboss.framework.exception.BussinessException;
import zhuboss.gateway.wx.vo.TemplateMessageDataItem;
import zhuboss.gateway.wx.vo.WechatRemoteException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(WxConfig.class)
public class WeixinAdpater implements InitializingBean{
	private final WxConfig wxConfig;

	public WeixinAdpater(WxConfig wxConfig) {
		this.wxConfig = wxConfig;
	}

	String accessToken;
	
	HttpClient httpClient;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(200);

		this.httpClient = new DefaultHttpClient(connectionManager);
		
	}

	public  List<String> getAllOpenId(){
		String url = "https://api.weixin.qq.com/cgi-bin/user/get";
		List<NameValuePair> nameValuePairList = null;
		nameValuePairList = Arrays.asList(new NameValuePair[]{new BasicNameValuePair("next_openid","")});
		 Map<String,Object>   results = this.getWithAccessToken(url, nameValuePairList);
		 if(results.containsKey("errcode")){
			 throw new RuntimeException(results.toString());
		 }
		 List<String> openIdList = (List)((Map)results.get("data")).get("openid");
		 return openIdList;
	}
	
	public Map<String,Object> getUserInfo(String openId){
		String url="https://api.weixin.qq.com/cgi-bin/user/info";
		Map<String,Object> result = this.getWithAccessToken(url, Arrays.asList(
					new NameValuePair[]{
							new BasicNameValuePair("openid",openId),
							new BasicNameValuePair("lang","zh_CN")}));
		return result;
	}

	/**
	 * 发送模板消息
	 * @param openId
	 * @param templateId
	 * @param detailUrl
	 */
	public void sendTemplateMessage(String openId,String templateId,String detailUrl,Map<String, TemplateMessageDataItem> data ){
		Map<String,Object> map = new HashMap<>();
		map.put("touser",openId);
		map.put("template_id",templateId);
		map.put("url",detailUrl);
		map.put("topcolor","#FF0000");
		map.put("data",data);
		String content = JSON.toJSONString(map);
		JSONObject jsonObject = this.postWithAccessToken("https://api.weixin.qq.com/cgi-bin/message/template/send",null,content);
		Integer errcode = jsonObject.getInteger("errcode");
		if(errcode != 0){
			throw new BussinessException("发送消息失败："+jsonObject.getString("errmsg"));
		}
	}
	
	private String genAccessToken(){
		String access_token = null;
		String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+wxConfig.getAppId()+"&secret="+wxConfig.getSecret();
		HttpGet get = new HttpGet(url  );
		Map<String,Object>  result = null;
		try {
			HttpResponse response = httpClient.execute(get);
			String responseText = EntityUtils.toString(
					response.getEntity(), HTTP.UTF_8);
			
			result =JSON.parseObject(responseText, HashMap.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} 

		
		String ACCESS_TOKEN_KEY = "access_token";
		if(result.containsKey(ACCESS_TOKEN_KEY)){
			access_token = (String)result.get(ACCESS_TOKEN_KEY);
			this.accessToken = access_token;
			log.info("access_token:"+access_token);
		}else{
			throw new RuntimeException(JSON.toJSONString(result));
		}
		this.accessToken = access_token ;
		return access_token;
	}
	
	public String getAccessToken(){
		if(this.accessToken ==null){
			return this.genAccessToken();
		}
		return this.accessToken;
	}
	
	public Map<String,Object>  getWithAccessToken(String path, List<NameValuePair> parameters){
		String url = path + "?access_token="+this.getAccessToken();
		if(parameters!=null && parameters.size()>0){
			String queryParamPart = URLEncodedUtils.format(parameters, "UTF-8");
			url = url + "&" + queryParamPart;
		}
		HttpGet get = new HttpGet(url  );
		try {
			HttpResponse response = httpClient.execute(get);
			
			if(response.getEntity() == null) return null;
			String responseText = EntityUtils.toString(
					response.getEntity(), HTTP.UTF_8);
			if(response.getStatusLine().getStatusCode() == 404){
				System.out.println(responseText);
				throw new RuntimeException("path not found");
			}
			Map<String,Object>  result = JSON.parseObject(responseText, HashMap.class);
			if(result.containsKey("errcode")){
				Integer errcode = (Integer)result.get("errcode");
				if(errcode == 40001 || errcode==42001){
					this.genAccessToken();
					return this.getWithAccessToken(path, parameters);
				}else{
					//TODO throw new exception
				}
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} 
	}
	
	public Map<String,Object> createMenu(List<Map<String,Object>> bottomItems){
		final String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("button", bottomItems);
		String jsonMenu = JSON.toJSONString(map);
		Map<String,Object> result = postWithAccessToken(menu_create_url,null,jsonMenu);
		log.info("create menu result:" + result);
		return result;
	}
	
	public void deleteMenu(){
		final String menu_del_url = "https://api.weixin.qq.com/cgi-bin/menu/delete";
		  postWithAccessToken(menu_del_url,null,"");
	}
	
	public JSONObject  queryMenu(){
		final String menu_query_url = "https://api.weixin.qq.com/cgi-bin/menu/get";
		JSONObject result = postWithAccessToken(menu_query_url,null,"");
		return result;
	}
	
	public JSONObject postWithAccessToken(String path, List<NameValuePair> parameters, String content) {
		String url = path + "?access_token="+this.getAccessToken();
		if(parameters!=null && parameters.size()>0){
			String queryParamPart = URLEncodedUtils.format(parameters, "UTF-8");
			url = url + "&" + queryParamPart;
		}
		HttpPost post = new HttpPost(url);
		try {
			StringEntity entityTemplate = new StringEntity(content, "text/html",
					HTTP.UTF_8);
			entityTemplate.setContentEncoding(HTTP.UTF_8);
			post.setEntity(entityTemplate);
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			String responseText = EntityUtils.toString(entity, HTTP.UTF_8);
			JSONObject result = JSON.parseObject(responseText);
			if(result.containsKey("errcode")){
				Integer errcode = (Integer)result.get("errcode");
				if(errcode == 40001 || errcode==42001){
					this.genAccessToken();
					return this.postWithAccessToken(path, parameters,content);
				}else{
					//TODO throw new exception
				}
			}
			return result;
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}

	public JSONObject genQrcode(String sceneStr) throws WechatRemoteException {
		final String menu_create_url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
		String json = "{\"expire_seconds\": 2592000, \"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \""+sceneStr+"\"}}}";
		JSONObject result = postWithAccessToken(menu_create_url,null,json);
		log.info("create qrcode result:" + result);
		if(result.containsKey("errcode")){
			String errcode = String.valueOf(result.get("errcode"));
			String errmsg = (String)result.get("errmsg");
			throw new WechatRemoteException(errcode,errmsg);
		}
		return result;
	}

	public String getOpenId(String code){
		String getOpenIdURL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + wxConfig.getAppId()
				+ "&secret=" + wxConfig.getSecret()
				+ "&code=" + code
				+ "&grant_type=authorization_code";
		log.info("getOpenIdURL:" + getOpenIdURL);
		String responseText = this.get(getOpenIdURL);
		log.info("getOpenIdResponseText:" + responseText);
		String openId = JSON.parseObject(responseText).getString("openid");
		return openId;
	}


	private String get(String url) {
		HttpGet get = new HttpGet(url);
		try {
			get.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
			HttpResponse response = httpClient.execute(get);
			if(response.getEntity() == null) return null;
			String responseText = EntityUtils.toString(
					response.getEntity(), HTTP.UTF_8);

			if(response.getStatusLine().getStatusCode() == 404){
				log.error(responseText);
				throw new RuntimeException("path not found["+get.getURI()+"]");
			}

			return responseText;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}

	public WxConfig getWxConfig() {
		return wxConfig;
	}
}
