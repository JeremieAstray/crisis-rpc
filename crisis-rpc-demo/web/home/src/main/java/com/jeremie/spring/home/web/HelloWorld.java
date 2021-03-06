package com.jeremie.spring.home.web;


import com.jeremie.spring.adol.service.AdolService;
import com.jeremie.spring.home.entity.User;
import com.jeremie.spring.home.service.UserService;
import com.jeremie.spring.rpc.RpcContext;
import com.jeremie.spring.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.concurrent.Future;

/**
 * @author guanhong 15/7/27 下午6:58.
 */

@Controller
public class HelloWorld extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private AdolService adolService;

    @ResponseBody
    @RequestMapping("/")
    public String home() {
        return "HelloWorld";
    }

    @RequestMapping("/testvm")
    public String test(Model model, Long id) throws Exception {
        if (id == null) {
            model.addAttribute("user", "null");
            model.addAttribute("vmchange", "vmchangetest");
            return "test";
        }
        String test2 = "guanhong对了~测一下中文";
        model.addAttribute("vmtest", test2);
        //model.addAttribute("vmchange", "vmchange");
        model.addAttribute("vmchange", "vmchangetest");
        User user = userService.getById(id);
        String test = userService.testGetString();
        userService.updateUserById("test", 1L);
        //测试基本类型
        String[] stringArray = userService.testStringArray();
        long[] longArray = userService.testlongArray();
        Long num = userService.testLong();
        if (stringArray != null) {
            Arrays.asList(stringArray).forEach(System.out::println);
        } else {
            System.out.println("get null StringArray");
        }
        if (longArray != null) {
            for (long a : longArray) {
                System.out.println(a);
            }
        } else {
            System.out.println("get null longArray");
        }
        if (num != 0) {
            System.out.println(num);
        } else {
            System.out.println("get null number");
        }
        model.addAttribute("testGetString", test);
        model.addAttribute("user", user.getUsername());
        model.addAttribute("adol", adolService.getSomethingTest());

        userService.getById(33);
        Future<User> userFuture = RpcContext.getContext().getFuture();
        model.addAttribute("user2", userFuture.get().toString());

        return "test";
    }

    @RequestMapping("/updateUser")
    public String updateUser(RedirectAttributes redirectAttributes, Long id, String name) throws Exception {
        if (id == null) {
            id = 1l;
        }
        if (name == null || "".equals(name)) {
            name = "guanhong!";
        }
        userService.updateUserById(name, id);
        redirectAttributes.addAttribute("id", id);
        return "redirect:/templates/testvm";
    }

    @ResponseBody
    @RequestMapping("/invalidUser")
    public String invalidUser(Long id) throws Exception {
        userService.deleteUser(id);
        return "success";
    }

}
