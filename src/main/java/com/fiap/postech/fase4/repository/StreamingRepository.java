package com.fiap.postech.fase4.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.Attribute;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.service.VideoUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class StreamingRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;


    public VideoModel salvarVideo(VideoModel video){
        try {
            dynamoDBMapper.save(video);
        }catch(Exception e){
            throw e;
        }
        return video;

    }

    public VideoModel getVideoById(UUID videoId){
        VideoModel video = dynamoDBMapper.load(VideoModel.class, videoId);
        return video;
    }


    public String delete(UUID videoId){
        VideoModel video = dynamoDBMapper.load(VideoModel.class, videoId);
        dynamoDBMapper.delete(video);
        return "Video deletado com sucesso!";
    }
    public String update(VideoModel video){

        try {
            this.delete(video.getVideoId());
           salvarVideo(video);

        }catch(Exception e){
            throw e;
        }
        return "Atualizado com sucesso!";
    }

    public List<VideoModel> getVideos(String tituloVideo,
                                      String categoriaVideo,
                                      LocalDate dataPublicacaoVideo) {
        if( (tituloVideo == "" || tituloVideo == null)
             && (categoriaVideo == "" || categoriaVideo == null)
                && (dataPublicacaoVideo == null)){
            return dynamoDBMapper.scan(VideoModel.class, new DynamoDBScanExpression());
        }

        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();

        Map<String, String> expressionAttributeNames = new HashMap<>();
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

        String filterExpression = "";

        if(tituloVideo != ""){
            expressionAttributeNames.put("#attr", "tituloTratado");
            expressionAttributeValues.put(":val", new AttributeValue(tituloVideo.toLowerCase()));
            filterExpression = "contains(#attr, :val)";
        }

        if(categoriaVideo != ""){
            expressionAttributeNames.put("#attr1", "categoriasTratado");
            expressionAttributeValues.put(":val1", new AttributeValue(categoriaVideo.toLowerCase()));
            if(!filterExpression.isEmpty()) filterExpression += " and ";
            filterExpression += "contains(#attr1, :val1)";
        }

        if(dataPublicacaoVideo != null){
            expressionAttributeNames.put("#attr2", "dataPublicacao");
            expressionAttributeValues.put(":val2", new AttributeValue(String.valueOf(dataPublicacaoVideo)));
            if(!filterExpression.isEmpty()) filterExpression += " and ";
            filterExpression += "contains(#attr2, :val2)";
        }

        dynamoDBScanExpression = dynamoDBScanExpression
                                    .withExpressionAttributeNames(expressionAttributeNames)
                                    .withExpressionAttributeValues(expressionAttributeValues)
                                    .withFilterExpression(filterExpression);
        return dynamoDBMapper.scan(VideoModel.class, dynamoDBScanExpression);
    }

}
