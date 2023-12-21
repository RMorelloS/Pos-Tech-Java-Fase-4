package com.fiap.postech.fase4.repository;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.model.VideoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository

public class UserRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;



    public Mono<UserModel> getUserByLogin(Mono<String> userLoginMono) {
        try {
            return userLoginMono.flatMap(userLogin ->
                    Mono.fromCallable(() -> dynamoDBMapper.load(UserModel.class, userLogin))
            );
        }catch(Exception e){
            throw e;
        }
    }

    public Mono<Void> atualizarUsuario(UserModel usuarioAtualizado) {
        return Mono.fromRunnable(() -> dynamoDBMapper.save(usuarioAtualizado));
    }

    public String criarUsuario(UserModel novoUsuario) {
        dynamoDBMapper.save(novoUsuario);
        return "Criado com sucesso";
    }

    public List<VideoModel> obterVideosUsuario(UserModel usuario) {
        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();

        Map<String, String> expressionAttributeNames = new HashMap<>();
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

        expressionAttributeNames.put("#attr", "autor");
        expressionAttributeValues.put(":val", new AttributeValue(usuario.getUserLogin()));
        String filterExpression = "attribute_exists(#attr) AND contains(#attr, :val)";

        dynamoDBScanExpression = dynamoDBScanExpression
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withFilterExpression(filterExpression);
        return dynamoDBMapper.scan(VideoModel.class, dynamoDBScanExpression);
    }
}
