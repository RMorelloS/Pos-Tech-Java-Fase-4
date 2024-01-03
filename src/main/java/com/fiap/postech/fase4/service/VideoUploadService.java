package com.fiap.postech.fase4.service;

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.fiap.postech.fase4.config.S3Configuration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;



import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

@Service
public class VideoUploadService implements IVideoUploadService{


    @Autowired
    private S3Configuration s3Configuration;


    private S3Client s3Client;


    private String bucketName;
    VideoUploadService(){
        s3Configuration = new S3Configuration();
        bucketName = "postech-streaming-service";
    }
    public String delete(String videoId) {

        try {
            S3Client s3Client = s3Configuration.buildS3Client();
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(videoId)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            return "Deletado com sucesso!";
        }catch(Exception e){
            return e.getMessage();
        }
    }

    public Mono<String> uploadObject(Mono<FilePart> video, UUID videoId) {
        try {

            S3Client s3Client = s3Configuration.buildS3Client();
            Map<String, String> metadata = new HashMap<>();
            metadata.put("videoId", videoId.toString());

            video.flatMapMany(filePart -> {
                String fileName = filePart.filename();
                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(videoId.toString())
                        .build();

                return filePart.content()
                        .buffer(1024 * 1024*512)
                        .flatMapSequential(bufferList -> {
                            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferList.size() * 1024);
                            bufferList.forEach(dataBuffer -> {
                                try {
                                    byteBuffer.put(dataBuffer.asInputStream().readAllBytes());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            byteBuffer.flip();
                            Mono<PutObjectResponse> uploadCompletion = Mono.fromCallable(() -> {
                                s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(byteBuffer));
                                return PutObjectResponse.builder().build();
                            });

                            return uploadCompletion.then();
                        });

            }).subscribe();



            return Mono.just("Carregado com sucesso!");
        }catch(Exception e){
            return Mono.error(e);
        }
    }

    public String getVideoS3URL(String videoId) {


        S3Client s3Client = s3Configuration.buildS3Client();


        s3Client.putObjectAcl(PutObjectAclRequest.builder()
                .bucket(bucketName)
                .key(videoId.toString())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build());

        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(videoId)
                .build();

        URL objectURL = s3Client.utilities().getUrl(request);
        return objectURL.toString();

    }
}
