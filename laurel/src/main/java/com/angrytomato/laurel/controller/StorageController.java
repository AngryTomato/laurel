package com.angrytomato.laurel.controller;

import com.angrytomato.laurel.domain.Storage;
import com.angrytomato.laurel.domain.User;
import com.angrytomato.laurel.service.ProjectService;
import com.angrytomato.laurel.service.UserService;
import com.angrytomato.laurel.util.UuidUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
public class StorageController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "/user/storage", method = RequestMethod.GET)
    public String storagePage(Model model) {
        Map<String, Object> tips = new HashMap<>();
        tips.put("display", false);
        tips.put("result", true);
        tips.put("message", "");
        model.addAttribute("tips", tips);
        return "addStoragePassword";
    }

    @RequestMapping(value = "/storage", method = RequestMethod.POST)
    public String storage(HttpServletRequest request, Principal principal, Model model) {
        String username = principal.getName();//获取用户名

        String projectName = request.getParameter("project");
        String storageUsername = request.getParameter("username");
        String storagePassword = request.getParameter("password");
        String site = request.getParameter("site");
        String description = request.getParameter("remark");

        Map<String, Object> tips = new HashMap<>();
        tips.put("display", true);
        tips.put("result", false);
        tips.put("message", "新增项目失败");

        if (userService.existsByUsername(username)) {//用户名存在
            Long userId = userService.findByUsername(username).getId();//获取用户id
            //项目名，账号和密码不能为空
            if (!StringUtils.isEmpty(projectName) && !StringUtils.isEmpty(storageUsername) && !StringUtils.isEmpty(storagePassword)) {
                byte[] encryptedPassword = projectService.encrypt(storagePassword, username);//加密密码

                if(StringUtils.isEmpty(site)) {//如果为空串或者null
                    site = "";
                }

                if(StringUtils.isEmpty(description)) {//如果为空串或者null
                    description = "";
                }
                Storage storage = new Storage();
                storage.setCreateTime(new Timestamp(System.currentTimeMillis()));
                storage.setDescription(description);
                storage.setEncryptPassword(encryptedPassword);
                storage.setIsDeleted(false);
                storage.setSite(site);
                storage.setStorageUsername(storageUsername);
                storage.setUserId(userId);
                storage.setUuid(UuidUtils.genUuid());
                storage.setProjectName(projectName);

                if(projectService.save(storage)) {//存入数据库成功
                    tips.put("result", true);
                    tips.put("message", "新增项目成功");
                }
            }
        }

        model.addAttribute("tips", tips);
        return "addStoragePassword";
    }


}
