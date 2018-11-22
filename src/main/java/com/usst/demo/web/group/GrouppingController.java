package com.usst.demo.web.group;

import com.usst.demo.repo.GroupRepository;
import com.usst.demo.repo.GrouppingRepository;
import com.usst.demo.repo.PersonalityTagRepository;
import com.usst.demo.util.Message;
import com.usst.demo.vo.Group;
import com.usst.demo.vo.Groupping;
import com.usst.demo.vo.Tag;
import com.usst.demo.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/groupping")
public class GrouppingController {
    @Autowired
    GrouppingRepository grouppingRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    PersonalityTagRepository personalityTagRepository;
    @RequestMapping(method = RequestMethod.GET)
    /**
     * method:1是按照时间，最新的在前
     */
    public String showGroupping(Model model, @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "30")Integer rows,
                                @RequestParam(defaultValue = "1") Integer method){
        List<Groupping> grouppings = grouppingRepository.getGrouppings(page,rows,method);
        model.addAttribute("grouppingdata",grouppings);
        return "/groupping/groupping_info.html";
    }

    @RequestMapping(value = "/{gid}/recruit", method = RequestMethod.GET)
    public String recruit(Model model, HttpServletRequest request,
                          @PathVariable("gid")Integer groupId){
        Group group = groupRepository.findGroupByGroupId(groupId);
        //错误groupid
        if(group==null){
            Message message = new Message(Message.NOT_FOUND,"错误的队伍ID！");
            model.addAttribute("msg",message);
            return "/error/notfound.html";
        }
        //不是队长进行的操作
        if(!checkLogin(request,group.getCaptainId())){
            Message message = new Message(Message.PERMISSION_LIMITED,"只有队长有该操作权限！");
            model.addAttribute("msg",message);
            return "/error/permission.html";
        }
        model.addAttribute("group",group);
        model.addAttribute("persontags",personalityTagRepository.getAllTags());
        return "groupping/recruit.html";
    }

    @RequestMapping(value = "/{gid}/recruit", method = RequestMethod.POST)
    public String doRecruit(@Valid @ModelAttribute Groupping groupping,
                            @PathVariable("gid")Integer groupId){
        //fieldTags的size为1，但是第一个tag为null，可能以后会出BUG
        System.out.println(groupping.getPersonalTags().size());
        for(Tag tag:groupping.getPersonalTags())
            System.out.println(tag.getTagName());
        return "redirect:/";
    }

    private boolean checkLogin(HttpServletRequest request, Integer userId) {
        User user = (User) request.getSession().getAttribute("user");
        if(user==null)
            return  false;
        return user.getUid()==userId;
    }
}
