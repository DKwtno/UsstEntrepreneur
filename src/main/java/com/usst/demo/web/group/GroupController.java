package com.usst.demo.web.group;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.usst.demo.repo.GroupRepository;
import com.usst.demo.util.Message;
import com.usst.demo.vo.Group;
import com.usst.demo.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 考虑使用AOP对每一个名为doXXX的方法调用前都检查session中是否包含User
 */
@Controller
@RequestMapping({"/user/{uid}/group","/group"})
public class GroupController {
    @Autowired
    GroupRepository groupRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String myGroup(@PathVariable("uid")Integer userId, Model model, HttpServletRequest request){
        User user = (User)(request.getSession().getAttribute("user"));
        if(user==null || user.getUid()!=userId){
            //错误访问
            return "redirect:/";
        }
        List<Group> groups = groupRepository.findAgreedGroupsByUserId(userId);
        model.addAttribute("grouplist", groups);
        return "/group/mygroup.html";
    }

    @RequestMapping(value = "/{gid}" , method = RequestMethod.GET)
    public String manageMyGroup(Model model, HttpServletRequest request, @PathVariable("gid") Integer groupId){
        Group group = groupRepository.findGroupByGroupId(groupId);
        if(!checkLogin(request, group.getCaptainId())){
            Message message = new Message(Message.PERMISSION_LIMITED,"只有队长有该操作权限！");
            model.addAttribute("msg",message);
            return "/error/permission.html";
        }
        model.addAttribute("mygroup",group);
        model.addAttribute("members",group.getMembers());
        return "/group/manageGroup.html";
    }

    private boolean checkLogin(HttpServletRequest request, Integer userId) {
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return  false;
        return user.getUid()==userId;
    }


    @RequestMapping(value = "/setup", method = RequestMethod.GET)
    public String setUpGroup(HttpServletRequest request){
        if(request.getSession().getAttribute("user")==null) {
            //没有登录
            return "redirect:/register";
        }
        return "/group/setup.html";
    }
    @RequestMapping(value = "/setup", method = RequestMethod.POST)
    public String addGroup(@Valid @ModelAttribute Group group,HttpServletRequest request){
        group.setEstablishDate(new Date());
        Integer uid = ((User)(request.getSession().getAttribute("user"))).getUid();
        group.setCaptainId(uid);
        groupRepository.createGroup(group);
        return "redirect:/user/"+uid+"/group";
    }
}
