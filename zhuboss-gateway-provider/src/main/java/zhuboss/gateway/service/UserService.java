package zhuboss.gateway.service;

import zhuboss.gateway.service.param.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface UserService {

    void add(AddUserParam addUserParam);

    void update(UpdateUserParam updateUserParam);

    void setPwd(SetUserPwdParam setUserPwdParam);

    void register(UserRegisterParam userRegisterParam,String registerIp) throws UnsupportedEncodingException, MessagingException;

    Integer validte(UserValidateParam validateParam);

    void modifyUserInfo(ModifyUserInfo modifyUserInfo,Integer userId);

    void modifyUserPwd(ModifyUserPwd modifyUserPwd,Integer userId);

    void resetPwd(Integer userId,String pwd);

    String getRand(int n);
}
