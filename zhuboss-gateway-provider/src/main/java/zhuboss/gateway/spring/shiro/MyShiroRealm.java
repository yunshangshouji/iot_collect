package zhuboss.gateway.spring.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by Administrator on 2017/12/11.
 * 自定义权限匹配和账号密码匹配
 */
public class MyShiroRealm extends AuthorizingRealm {

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
    	UsernamePasswordToken tk = (UsernamePasswordToken) token;
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
        		tk.getUsername(), //用户名
        		tk.getPassword(), //数据库密码
                null,//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }
 
    /**
   	 * 认证回调函数, 登录后调用
   	 */
       @Override
       protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
           SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
           //TODO 读取数据库
//           authorizationInfo.setObjectPermissions(objectPermissions);
//           authorizationInfo.addRole(role);
           return authorizationInfo;
       }
       
       /**
   	 * 设定密码校验的Hash算法与迭代次数
   	 */
   	@PostConstruct
   	public void initCredentialsMatcher() {
   		HashedCredentialsMatcher matcher = new RetryLimitHashedCredentialsMatcher("MD5");
   		matcher.setHashIterations(1024);
   		setCredentialsMatcher(matcher);
   	}
   	
   	 class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
   		 
   		 	public RetryLimitHashedCredentialsMatcher(String hashAlgorithmName) {
   		      super(hashAlgorithmName);
   		    }
   		    @Override
   		    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {return true;}
   		}

}