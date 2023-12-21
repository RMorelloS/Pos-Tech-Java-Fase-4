package com.fiap.postech.fase4.controller;

import com.fiap.postech.fase4.model.EstatisticasUsuarioModel;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.service.StreamingService;
import com.fiap.postech.fase4.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/usuario")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StreamingService streamingService;
    @PostMapping("/adicionarFavorito")
    public Mono<String> adicionarFavorito(@RequestPart(value="videoId", required = false) String videoId,
                                                  ServerHttpResponse response){
        return userService.adicionarFavorito(videoId)
                .flatMap(message -> {
                    return Mono.just("redirect:/listarVideos");
                });
    }

    @GetMapping("/recomendados")
    public Mono<String> videosRecomendados(Model model){
        var videosRecomendados = userService.obterVideosRecomendados();
        return videosRecomendados.flatMap(videos -> {
            model.addAttribute("videos", videos);
            model.addAttribute("usuarioLogado", userService.obterIdUsuarioLogado());
            return Mono.just("recomendados");
        });
    }

    @PostMapping("/criarConta")
    public Mono<String> criarConta(@RequestPart(value="login", required = false) String login,
                                   @RequestPart(value="chave", required = false) String chave,
                                   @RequestPart(value="email", required = false) String email,
                                   Model model) {
        return userService.criarConta(login, chave, email)
                .flatMap(resultado -> {
                    model.addAttribute("retorno", resultado);
                    return Mono.just("redirect:/login");
                });
    }

    @GetMapping("/registrarUsuario")
    public Mono<String> criarConta(){
        return Mono.just("registrar_usuario");
    }


    @GetMapping("/estatisticas")
    public Mono<String> estatisticasVideo(Model model){
        Mono<EstatisticasUsuarioModel> estatisticas = userService.obterEstatisticas();
        return userService.obterIdUsuarioLogado().map(usuarioLogado -> {
            var videos = streamingService.getVideosByUser(usuarioLogado);
            model.addAttribute("videos", videos);
            model.addAttribute("estatisticas", estatisticas);
            model.addAttribute("usuarioLogado", userService.obterIdUsuarioLogado());
            return "estatisticas";
        });
    }

}
