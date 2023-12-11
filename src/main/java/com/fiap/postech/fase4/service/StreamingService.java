package com.fiap.postech.fase4.service;

import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.repository.StreamingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StreamingService {
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private StreamingRepository streamingRepository;

    @Autowired
    private VideoUploadService videoUploadService;

    private  final String FORMAT="classpath:videos/%s.mp4";

    private  final Path basePath = Paths.get("./src/main/resources/videos/");

    public String salvarVideo(String titulo, String descricao,
                              Mono<FilePart> video, String categorias) {

        List<String> listaCategorias = Arrays.asList(categorias.split(";"));
        List<String> listaCategoriasTratado = listaCategorias.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());


        VideoModel videoModel = new VideoModel();
        videoModel.setTitulo(titulo);
        videoModel.setDescricaoVideo(descricao);
        videoModel.setTituloTratado(titulo.toLowerCase());
        videoModel.setDataPublicacao(LocalDate.now());
        videoModel.setVideoURL("http://ricardoflix.com/" + titulo.replaceAll("[-+.^:,]",""));
        videoModel.setCategorias(listaCategorias);
        videoModel.setCategoriasTratado(listaCategoriasTratado);

        streamingRepository.salvarVideo(videoModel);

        video.doOnNext(fp -> System.out.println("Received file: " + fp.filename()))
                .flatMap(fp -> fp.transferTo(basePath.resolve(fp.filename())))
                .then();

        videoUploadService.uploadObject(video, videoModel.getVideoId());

        return "listar_videos";
    }

    public Mono<Resource> getVideo(String title){
        return Mono.fromSupplier(() ->resourceLoader
                .getResource(String.format(FORMAT, title)));
    }


    public List<VideoModel> getVideos(String tituloVideo, String categoriaVideo, LocalDate dataPublicacaoVideo) {
        return streamingRepository.getVideos(tituloVideo, categoriaVideo, dataPublicacaoVideo);
    }

    public String deletarVideo(String videoId){
        videoUploadService.delete(videoId);
        return streamingRepository.delete(UUID.fromString(videoId));
    }
}
