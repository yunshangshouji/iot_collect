package zhuboss.gateway.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.UserPOMapper;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.service.UserService;
import zhuboss.gateway.service.param.*;
import zhuboss.gateway.util.mail.IEmailSendService;
import zhuboss.gateway.util.mail.SendEmailMessage;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserPOMapper userPOMapper;
    @Autowired
    IEmailSendService emailSendService;

    @Override
    public void add(AddUserParam addUserParam) {
        UserPO insert = new UserPO();
        BeanMapper.copy(addUserParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(new Date());
        userPOMapper.insert(insert);
    }

    @Override
    public void update(UpdateUserParam updateUserParam) {
        UserPO userPO = userPOMapper.selectByPK(updateUserParam.getId());
        BeanMapper.copy(updateUserParam,userPO);
        userPO.setModifyTime(new Date());
        userPOMapper.updateByPK(userPO);

    }

    @Override
    public void setPwd(SetUserPwdParam setUserPwdParam) {
        UserPO userPO = userPOMapper.selectByPK(setUserPwdParam.getUserId());
        userPO.setLoginPwd(DigestUtils.md5Hex(setUserPwdParam.getLoginPwd()));
        userPOMapper.updateByPK(userPO);
    }

    @Override
    @Transactional
    public void register(UserRegisterParam userRegisterParam,String registerIp) throws UnsupportedEncodingException, MessagingException {
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder()
                .andEqual(UserPO.Fields.MAIL,userRegisterParam.getMail())
        );
        if(userPO != null && userPO.getValidFlag() == 1){
            throw new BussinessException("当前邮箱已注册，不能重复注册"+userRegisterParam.getMail());
        }
        //未验证的邮箱，删除重新写入
        if(userPO!= null && userPO.getValidFlag() == 0){
            userPOMapper.deleteByPK(userPO.getId());
        }
        //
        UserPO insert = new UserPO();
        insert.setMail(userRegisterParam.getMail());
        insert.setLoginPwd(DigestUtils.md5Hex(userRegisterParam.getLoginPwd()));
        insert.setAliveFlag(1);
        insert.setValidFlag(0);
        insert.setVerifyCode(getRand(6));
        insert.setCreateTime(new Date());
        userPOMapper.insert(insert);
        //发送邮件
        SendEmailMessage sendEmailMessage = new SendEmailMessage();
        sendEmailMessage.setAddress(userRegisterParam.getMail());
        sendEmailMessage.setSubject("用户激活");
        sendEmailMessage.setContent("您的激活验证码："+insert.getVerifyCode()+",谢谢注册！");
        emailSendService.sendEmail(sendEmailMessage);

    }

    @Override
    public Integer validte(UserValidateParam validateParam) {
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.MAIL,validateParam.getMail()));
        if(userPO == null){
            throw new BussinessException("邮箱"+ userPO.getMail() +"尚未注册");
        }
        if(userPO.getValidFlag() == 1){
            throw new BussinessException("用户已激活，不能重复激活");
        }
        if(!userPO.getVerifyCode().equals(validateParam.getVerifyCode())){
            throw new BussinessException("验证码不一致，激活失败!");
        }
        userPO.setValidFlag(1);
        userPO.setModifyTime(new Date());
        userPOMapper.updateByPK(userPO);
        return userPO.getId();
    }

    @Override
    public void modifyUserInfo(ModifyUserInfo modifyUserInfo,Integer userId) {
        UserPO userPO = userPOMapper.selectByPK(userId);
        userPO.setNickName(modifyUserInfo.getNickName());
        userPO.setModifyTime(new Date());
        userPOMapper.updateByPK(userPO);
    }

    @Override
    public void modifyUserPwd(ModifyUserPwd modifyUserPwd,Integer userId) {
        UserPO userPO = userPOMapper.selectByPK(userId);
        if(!userPO.getLoginPwd().equals(DigestUtils.md5Hex(modifyUserPwd.getOldLoginPwd()))){
            throw new BussinessException("旧密码验证失败");
        }
        userPO.setLoginPwd(DigestUtils.md5Hex(modifyUserPwd.getNewLoginPwd()));
        userPOMapper.updateByPK(userPO);
    }

    @Override
    public void resetPwd(Integer userId, String pwd) {
        UserPO userPO = userPOMapper.selectByPK(userId);
        userPO.setLoginPwd(DigestUtils.md5Hex(pwd));
        userPOMapper.updateByPK(userPO);
    }

    /**
     * 得到一个n位的随机数 第一位不能为0
     *
     * @param n
     *            位数
     * @return
     */
    public String getRand(int n) {
        Random rnd = new Random();
        String pass = "0";
        int x = rnd.nextInt(9);
        /** 过滤第一位为0 */
        while (x == 0) {
            x = rnd.nextInt(9);
        }
        pass = String.valueOf(x);
        for (int i = 1; i < n; i++) {
            pass = pass + String.valueOf(rnd.nextInt(9));
        }
        return pass;
    }

}
