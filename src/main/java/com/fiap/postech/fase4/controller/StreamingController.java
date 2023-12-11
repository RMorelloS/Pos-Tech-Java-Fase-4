package com.fiap.postech.fase4.controller;

import com.fiap.postech.fase4.service.StreamingService;
import com.fiap.postech.fase4.service.VideoUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StreamingController {

    @Autowired
    private StreamingService streamingService;

    @Autowired
    private VideoUploadService videoUploadService;


    @PostMapping("abrirVideo")
    public String getVideo(
            @RequestPart("videoId") String videoId, Model model){
        return "redirect:/mostrarVideo/" + videoId;
    }

    @GetMapping("mostrarVideo/{videoId}")
    public String mostrarVideo(@PathVariable("videoId") String videoId, Model model){
        String videoURL = videoUploadService.getVideoS3URL(videoId);
        model.addAttribute("videoURL", videoURL);
        return "mostrar_video";
    }

    @GetMapping("/listarVideos")
    public String listarVideos(@RequestParam(value="tituloVideo", required=false) String tituloVideo,
                               @RequestParam(value="categoriaVideo", required=false) String categoriaVideo,
                               @RequestParam(value="dataPublicacaoVideo", required=false) LocalDate dataPublicacaoVideo,

                               Model model){
        var videos = streamingService.getVideos(tituloVideo, categoriaVideo, dataPublicacaoVideo);
        model.addAttribute("videos", videos);
        model.addAttribute("tituloVideo", tituloVideo);
        model.addAttribute("categoriaVideo", categoriaVideo);
        model.addAttribute("dataPublicacaoVideo", dataPublicacaoVideo);
        return "listar_videos";
    }

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart("titulo") String titulo,
                         @RequestPart("descricao") String descricao,
                         @RequestPart("fileToUpload") Mono<FilePart> video,
                         @RequestPart("categorias") String categorias){

        streamingService.salvarVideo(titulo, descricao, video, categorias);
        return "redirect:/listarVideos";
    }

    @GetMapping("/carregarVideos")
    public String carregarVideos(){

        return "carregar_videos";
    }

    @PostMapping("/deletarVideos")
    public String deletarVideos(@RequestPart("videoId") String videoId){
        streamingService.deletarVideo(videoId.toString());
        return "redirect:/listarVideos";
    }

    @GetMapping("/login")
    public String loginUser(){
        return "login";
    }

}
