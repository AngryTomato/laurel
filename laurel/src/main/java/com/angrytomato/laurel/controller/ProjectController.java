package com.angrytomato.laurel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.angrytomato.laurel.domain.Storage;
import com.angrytomato.laurel.service.ProjectService;
import com.angrytomato.laurel.service.UserService;
import com.angrytomato.laurel.vo.Project;
import com.angrytomato.laurel.vo.ProjectDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "/user/projects", method = RequestMethod.GET)
    public String projectsPage(Principal principal, Model model) {
        String username = principal.getName();
        List<Project> projectList = new ArrayList<>();
        if (userService.existsByUsername(username)) {//用户名存在
            Long userId = userService.findByUsername(username).getId();
            List<Storage> storageList = projectService.findByUserId(userId);
            for (int i = 0; i < storageList.size(); i++) {
                Storage storage = storageList.get(i);

                Project project = new Project();
                project.setId(i+1);
                project.setProjectName(storage.getProjectName());
                project.setStorageId(storage.getId());
                project.setUsername(storage.getStorageUsername());
                project.setDecryptedPassword(projectService.decrypt(storage.getEncryptPassword(), username));

                projectList.add(project);
            }
        }

        model.addAttribute("projects", projectList);

        return "projects";
    }

    @ResponseBody
    @RequestMapping(value = "/projects/details", method = RequestMethod.POST)
    public JSONObject details(@RequestBody String requestBody, Principal principal) {
        String username = principal.getName();
        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        String idStr = jsonObject.getString("id");

        ProjectDetail projectDetail = new ProjectDetail();

        if (!StringUtils.isEmpty(idStr)) {
            Long id = Long.parseLong(idStr);
            if (userService.existsByUsername(username)) {//用户名存在
                Long userId = userService.findByUsername(username).getId();
                Storage storage = projectService.findByIdAndUserId(id, userId);
                if (storage != null) {
                    projectDetail.setId(storage.getId());
                    projectDetail.setProjectName(storage.getProjectName());
                    projectDetail.setUsername(storage.getStorageUsername());
                    projectDetail.setDecryptedPassword(projectService.decrypt(storage.getEncryptPassword(), username));
                    projectDetail.setSite(storage.getSite());
                    projectDetail.setRemark(storage.getDescription());
                }
            }
        }
        return JSONObject.parseObject(JSON.toJSONString(projectDetail));
    }

    @ResponseBody
    @RequestMapping(value = "/projects/details/update", method = RequestMethod.POST)
    public JSONObject updateDetails(@RequestBody String requestBody, Principal principal) {
        String username = principal.getName();
        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        String idStr = jsonObject.getString("id");
        String project = jsonObject.getString("project");
        String storageUsername = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String site = jsonObject.getString("site");
        String remark = jsonObject.getString("remark");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", false);
        resultMap.put("message", "保存失败");
        resultMap.put("class", "alert alert-danger");

        //id、project、storageUsername、password一定不能为空
        if (!StringUtils.isEmpty(idStr) && !StringUtils.isEmpty(project) && !StringUtils.isEmpty(storageUsername) && !StringUtils.isEmpty(password)) {
            if (userService.existsByUsername(username)) {
                Long userId = userService.findByUsername(username).getId();//获取用户id
                if (StringUtils.isEmpty(site)) {//防止出现null的情况
                    site = "";
                }
                if (StringUtils.isEmpty(remark)) {//防止出现null的情况
                    remark = "";
                }

                byte[] encryptedPassword = projectService.encrypt(password, username);//加密密码

                Storage storage = projectService.findByIdAndUserId(Long.parseLong(idStr), userId);
                if (null != storage) {//如果存在该条记录
                    storage.setProjectName(project);
                    storage.setStorageUsername(storageUsername);
                    storage.setEncryptPassword(encryptedPassword);
                    storage.setSite(site);
                    storage.setDescription(remark);
                    storage.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    if (projectService.save(storage)) {//更新到数据库成功
                        resultMap.put("result", true);
                        resultMap.put("message", "保存成功,3s后自动跳转到查询页");
                        resultMap.put("class", "alert alert-success");
                    }
                }
            }
        }
        return JSONObject.parseObject(JSON.toJSONString(resultMap));
    }

    @RequestMapping(value = "/projects/search", method = RequestMethod.GET)
    public String search(HttpServletRequest request, Principal principal, Model model) {
        String username = principal.getName();
        String criteria = request.getParameter("query");

        List<Project> projectList = new ArrayList<>();
        if (userService.existsByUsername(username)) {//用户名存在
            Long userId = userService.findByUsername(username).getId();
            List<Storage> storageList = new ArrayList<>();
            if (StringUtils.isEmpty(criteria)) {//如果查询条件为空
                storageList = projectService.findByUserId(userId);
            } else {
                storageList = projectService.findByCriteria(userId, criteria);
            }

            for (int i = 0; i < storageList.size(); i++) {
                Storage storage = storageList.get(i);

                Project project = new Project();
                project.setId(i+1);
                project.setProjectName(storage.getProjectName());
                project.setStorageId(storage.getId());
                project.setUsername(storage.getStorageUsername());
                project.setDecryptedPassword(projectService.decrypt(storage.getEncryptPassword(), username));

                projectList.add(project);
            }
        }
        model.addAttribute("projects", projectList);

        return "projects";
    }

    @ResponseBody
    @RequestMapping(value = "/projects/details/delete", method = RequestMethod.POST)
    public JSONObject deleteDetails(@RequestBody String requestBody, Principal principal) {
        String username = principal.getName();
        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        String idStr = jsonObject.getString("id");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", false);
        resultMap.put("message", "删除失败");
        resultMap.put("class", "alert alert-danger");

        //id一定不能为空
        if (!StringUtils.isEmpty(idStr)) {
            if (userService.existsByUsername(username)) {
                Long userId = userService.findByUsername(username).getId();//获取用户id

                Storage storage = projectService.findByIdAndUserId(Long.parseLong(idStr), userId);
                if (null != storage) {//如果存在该条记录
                    storage.setIsDeleted(true);
                    storage.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    if (projectService.save(storage)) {//更新到数据库成功
                        resultMap.put("result", true);
                        resultMap.put("message", "删除成功,3s后自动跳转到查询页");
                        resultMap.put("class", "alert alert-success");
                    }
                }
            }
        }
        return JSONObject.parseObject(JSON.toJSONString(resultMap));
    }
}
