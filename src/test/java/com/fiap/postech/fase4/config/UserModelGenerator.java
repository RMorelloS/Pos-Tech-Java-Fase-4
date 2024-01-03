package com.fiap.postech.fase4.config;

import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.service.UserService;
import java.util.ArrayList;
import java.util.UUID;

public class UserModelGenerator {
    public static final UserModel generateUserModel(){
       return new UserModel("user1", "senha123",
               "USER", "admin@admin.com", new ArrayList<UUID>());

    }

}
