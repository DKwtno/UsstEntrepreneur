package com.usst.demo.web.user;

import com.usst.demo.repo.UserDataRepository;
import com.usst.demo.vo.User;
import com.usst.demo.vo.UserData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/{uid}")
public class UserDataController {

    private UserDataRepository userDataRepository;
    @Autowired
    public UserDataController(UserDataRepository userDataRepository){
        this.userDataRepository = userDataRepository;
    }
    @RequestMapping(method = RequestMethod.GET)
    public String userPage(Model model, @PathVariable("uid") Integer id){
        System.out.println("用户id:"+id);
        UserData userData = userDataRepository.getUserData(id);
        if(userData==null){
            System.out.println("错了！userData为空!");
        }
        model.addAttribute("userData",userData);
        model.addAttribute("webId",id);
        return "/user/userinfo.html";
    }
    @RequestMapping(value = "/updatedata", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object updateUserData(@RequestBody UserData userData, HttpServletRequest request,
                                 @PathVariable("uid") Integer id, HttpServletResponse response){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null || user.getUid() != id){
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"不能修改！");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        Map<String, String> param = new HashMap();
        param.put("data","成功！");
        return JSONObject.fromObject(param);
    }
}
