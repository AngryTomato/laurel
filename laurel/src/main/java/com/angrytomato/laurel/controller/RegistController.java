package com.angrytomato.laurel.controller;

import com.angrytomato.laurel.config.LaurelSecurity;
import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.UserService;
import com.angrytomato.laurel.util.AESUtils;
import com.angrytomato.laurel.util.RSAUtils;
import com.angrytomato.laurel.util.RegExpUtils;
import com.angrytomato.laurel.util.UuidUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RegistController {
    @Autowired
    private UserService userService;

    @Autowired
    private LaurelSecurity laurelSecurity;

    @RequestMapping(value = "/regist", method = RequestMethod.GET)
    public String signUpPage(Model model) {
        Map<String, Object> tips = new HashMap<>();
        tips.put("display", false);
        tips.put("result", true);
        tips.put("message", "");
        model.addAttribute("tips", tips);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String regist(HttpServletRequest request, Model model) {
        String username = request.getParameter("username");
        String rawpassword = request.getParameter("password");
        String email = request.getParameter("email");


        //结果
        Map<String, Object> tips = new HashMap<>();
        tips.put("display", true);
        tips.put("result", false);
        tips.put("message", "注册失败");

        //用户名、密码、邮箱不为空(或null)，且三者均符合正则
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(rawpassword) && !StringUtils.isEmpty(email) && RegExpUtils.checkUsername(username) && RegExpUtils.checkPassword(rawpassword) && RegExpUtils.checkEmail(email)) {//正则校验通过
            //生成对称秘钥
            byte[] encryptedAesKey = null;
            byte[] encryptedIv = null;
            try {
                byte[] aesKey = AESUtils.genKey("AES", 256);
                byte[] iv = AESUtils.genRandomIv();
                byte[] publicKey = Base64.decodeBase64(laurelSecurity.getBase64PublicKey());
                encryptedAesKey = RSAUtils.encryptData(aesKey, publicKey);//加密对称秘钥
                encryptedIv = RSAUtils.encryptData(iv, publicKey);//加密向量
            } catch (Exception e) {
                e.printStackTrace();
                encryptedAesKey = null;
                encryptedIv = null;
            }

            if (null == encryptedAesKey || null == encryptedIv) {//生成秘钥失败
                tips.put("result", false);
                tips.put("message", "注册失败");
            } else {
                User user = new User();
                user.setUsername(username);
                user.setPassword(userService.encodePassword(rawpassword));
                user.setEmail(email);
                user.setIsDeleted(false);
                user.setEncryptKey(encryptedAesKey);
                user.setIv(encryptedIv);
                user.setCreateTime(new Timestamp(System.currentTimeMillis()));
                user.setUuid(UuidUtils.genUuid());

                if(!userService.existsByUsername(user.getUsername())) {//用户名不存在
                    if(userService.save(user)) {//注册写入数据库成功
                        tips.put("result", true);
                        tips.put("message", "注册成功");
                    } else {//注册写入数据库失败
                        tips.put("result", false);
                        tips.put("message", "注册失败");
                    }
                } else {//用户名已经存在
                    tips.put("result", false);
                    tips.put("message", "用户已经存在");
                }
            }
        } else {//正则校验失败
            tips.put("result", false);
            tips.put("message", "填写的账户、密码或邮箱不符合规范");
        }

        model.addAttribute("tips", tips);

        return "signup";
    }
}
