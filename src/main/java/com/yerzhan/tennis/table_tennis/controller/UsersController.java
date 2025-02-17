package com.yerzhan.tennis.table_tennis.controller;

import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.service.impl.UserDetailServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final UserDetailServiceImpl userDetailService;


    @GetMapping("/login")
    public String showLoginForm() {
    return "login";
    }


    @GetMapping("/profile")
    public String showProfile(Model model){
        Users authenticatedUser = userDetailService.getAuthenticatedUsers();
        model.addAttribute("user",authenticatedUser);
        return "profile";
    }


    @GetMapping("/register")
    public String showRegistrationForm(){
    return "register";
    }


    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password){
    userDetailService.createUser(username,password);
    return "redirect:/login";
    }


}







//@PostMapping("/login")
//    public String loginUser(@RequestParam String username, HttpSession session){
//        session.setAttribute("username",username);
//        return "redirect:/profile/" + username;
//    }
//

//

//
//
//    @GetMapping("/profile/{username}")
//    public String showProfile(@PathVariable String username,Model model){
//        model.addAttribute("username",username);
//        return "profile";
//    }