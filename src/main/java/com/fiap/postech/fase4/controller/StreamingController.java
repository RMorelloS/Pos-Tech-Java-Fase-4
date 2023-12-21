package com.fiap.postech.fase4.controller;

import com.fiap.postech.fase4.service.StreamingService;
import com.fiap.postech.fase4.service.UserService;
import com.fiap.postech.fase4.service.VideoUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class StreamingController {

    @Autowired
    private StreamingService streamingService;

    @Autowired
    private UserService userService;
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
        streamingService.atualizarViews(videoId);
        model.addAttribute("video", streamingService.getVideoById(UUID.fromString(videoId)));
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
        model.addAttribute("usuarioLogado", userService.obterIdUsuarioLogado());
        model.addAttribute("infoUsuario", userService.obterUsuarioLogado());
        return "listar_videos";
    }

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> upload(@RequestPart("titulo") String titulo,
                         @RequestPart("descricao") String descricao,
                         @RequestPart("fileToUpload") Mono<FilePart> video,
                         @RequestPart("categorias") String categorias){

        var usuario = userService.obterUsuarioLogado();
        return usuario.map(usuarioLogado -> {
            streamingService.salvarVideo(titulo, descricao, video, categorias, usuarioLogado.getUserLogin());
            return "redirect:/listarVideos";
        });
    }


    @GetMapping("/carregarVideos")
    public String carregarVideos(){

        return "carregar_videos";
    }

    @PostMapping("/deletarVideos")
    public Mono<String> deletarVideos(@RequestPart("videoId") String videoId){
        var usuario = userService.obterIdUsuarioLogado();
        return usuario.map( usuarioLogado -> {
            streamingService.deletarVideo(videoId.toString(), usuarioLogado);
            return "redirect:/listarVideos";
        });
    }

    @GetMapping("/login")
    public String loginUser(Model model){

        return "login";
    }




}
