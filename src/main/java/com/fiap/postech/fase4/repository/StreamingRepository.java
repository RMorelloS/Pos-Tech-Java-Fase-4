package com.fiap.postech.fase4.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fiap.postech.fase4.model.VideoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

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


    public String delete(UUID videoId, String usuarioLogado){
        VideoModel video = dynamoDBMapper.load(VideoModel.class, videoId);
        if(!video.getAutor().equals(usuarioLogado)){
            return "Usuário sem permissão de excluir vídeo de outros usuários!";
        }else {
            dynamoDBMapper.delete(video);
        }
        return "Video deletado com sucesso!";

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

    public List<VideoModel> obterVideosPorCategorias(List<Map.Entry<String, Integer>> listaCategorias) {

        List<VideoModel> videos = new ArrayList<>();
        for(var categoria : listaCategorias) {
            DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();

            Map<String, String> expressionAttributeNames = new HashMap<>();
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            expressionAttributeNames.put("#attr", "categoriasTratado");
            expressionAttributeValues.put(":val", new AttributeValue(categoria.getKey().toLowerCase()));
            String filterExpression = "attribute_exists(#attr) AND contains(#attr, :val)";

            dynamoDBScanExpression = dynamoDBScanExpression
                    .withExpressionAttributeNames(expressionAttributeNames)
                    .withExpressionAttributeValues(expressionAttributeValues)
                    .withFilterExpression(filterExpression);


           var lista = dynamoDBMapper.scan(VideoModel.class, dynamoDBScanExpression);
           for (var item : lista){
               videos.add(item);
           }
        }
        return videos;
    }

    public List<VideoModel> getVideosByUser(String user) {
        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();

        Map<String, String> expressionAttributeNames = new HashMap<>();
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

        expressionAttributeNames.put("#attr", "autor");
        expressionAttributeValues.put(":val", new AttributeValue(user));
        String filterExpression = "attribute_exists(#attr) AND contains(#attr, :val)";

        dynamoDBScanExpression = dynamoDBScanExpression
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withFilterExpression(filterExpression);

        var resultado = dynamoDBMapper.scan(VideoModel.class, dynamoDBScanExpression);
        List<VideoModel> videos = new ArrayList<>();
        for(var item : resultado){
            videos.add(item);
        }

        return videos;
    }
}
