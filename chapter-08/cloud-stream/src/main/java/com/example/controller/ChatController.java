package com.example.controller;

import java.util.Collections;

import com.example.controller.vm.UserVM;
import com.example.controller.vm.UsersStatisticVM;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class ChatController {

    @GetMapping
    public String index(Model model) {

        model.addAttribute("messages", Collections.emptyList());
        UserVM emptyUser = new UserVM("", "");
        model.addAttribute("statistic", new UsersStatisticVM(emptyUser, emptyUser));

        return "chat";
    }
}
