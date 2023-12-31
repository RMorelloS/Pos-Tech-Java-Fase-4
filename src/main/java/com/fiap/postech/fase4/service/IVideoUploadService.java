package com.fiap.postech.fase4.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IVideoUploadService {
    public Mono<String> uploadObject(Mono<FilePart> video, UUID videoId);
}
