package com.fiap.postech.fase4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario")
public class UserController {
    @PostMapping("/adicionarFavorito")
    public String adicionarFavorito(){
        return null;
    }
}
