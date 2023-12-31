package com.fiap.postech.fase4.config;

import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.model.VideoModel;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserModelGenerator {
    public static final UserModel generateUserModel(){
       return new UserModel("user1", "senha123",
               "USER", "admin@admin.com", new ArrayList<UUID>());

    }
}
