package com.fiap.postech.fase4.repository;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.model.VideoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository

public class UserRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;



    public UserModel getUserByLogin(String userLogin){
        UserModel user = dynamoDBMapper.load(UserModel.class, userLogin);
        return user;
    }
}
