package com.usst.demo.web.group;

import com.usst.demo.repo.GrouppingRepository;
import com.usst.demo.vo.Groupping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/groupping")
public class GrouppingController {
    @Autowired
    GrouppingRepository grouppingRepository;
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
}
