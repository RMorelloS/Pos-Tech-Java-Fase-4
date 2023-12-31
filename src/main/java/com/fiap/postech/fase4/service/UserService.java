package com.fiap.postech.fase4.service;

import com.fiap.postech.fase4.model.EstatisticasUsuarioModel;
import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.repository.StreamingRepository;
import com.fiap.postech.fase4.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.security.core.context.SecurityContext;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static com.fiap.postech.fase4.service.StatusFavoritoEnum.StatusFavorito.ADICIONAR_FAVORITO;
import static com.fiap.postech.fase4.service.StatusFavoritoEnum.StatusFavorito.REMOVER_FAVORITO;

@Service
public class UserService {
    @Autowired
    @Getter
    @Setter
    private UserRepository userRepository;

    @Autowired
    private StreamingService streamingService;
    public Mono<String> obterIdUsuarioLogado() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        return ((UserDetails) authentication.getPrincipal()).getUsername();
                    }
                    return authentication.getPrincipal().toString();
                });
    }

    public Mono<UserModel> obterUsuarioLogado(){
        Mono<UserModel> usuario = userRepository.getUserByLogin(obterIdUsuarioLogado());
        return usuario;
    }

    public Mono<String> adicionarFavorito(String videoId) {
        return obterUsuarioLogado()
                .flatMap(usuario -> {
                    var listaFavoritos = usuario.getVideosFavoritos();
                    listaFavoritos = atualizaFavoritos(listaFavoritos, videoId);
                    usuario.setVideosFavoritos(listaFavoritos);
                    return userRepository.atualizarUsuario(usuario)
                            .then(Mono.just("Atualizado com sucesso!"))
                            .onErrorReturn("Falha ao atualizar");
                });

    }

    public ArrayList<UUID> atualizaFavoritos(ArrayList<UUID> listaFavoritos, String videoId){
        if (listaFavoritos == null) {
            listaFavoritos = new ArrayList<>();
        }
        var buscaVideo = listaFavoritos.stream().filter(it ->
                it.equals(UUID.fromString(videoId))).collect(Collectors.toList());
        if(buscaVideo.size() != 0) {
            listaFavoritos.remove(UUID.fromString(videoId));
            streamingService.atualizaFavoritos(videoId, REMOVER_FAVORITO);
        }else {
            listaFavoritos.add(UUID.fromString(videoId));
            streamingService.atualizaFavoritos(videoId, ADICIONAR_FAVORITO);
        }
        return listaFavoritos;
    }

    public Mono<List<VideoModel>> obterVideosRecomendados() {
        var usuarioLogado = obterUsuarioLogado();
        Mono<List<VideoModel>> videosRecomendados = usuarioLogado.map(usuario -> {
            var listaFavoritos = usuario.getVideosFavoritos();
            if(listaFavoritos == null){
                listaFavoritos = new ArrayList<>();
            }
            List<Map.Entry<String, Integer>> listaCategorias = streamingService.obterListaCategorias(listaFavoritos);
            return streamingService.obterVideosPorCategorias(listaCategorias);
        });
        return videosRecomendados;
    }


    public Mono<String> criarConta(String login, String chave, String email) {
        UserModel novoUsuario = new UserModel();
        novoUsuario.setEmail(email);
        novoUsuario.setUserLogin(login);
        novoUsuario.setUserKey(chave);
        novoUsuario.setRole("USER");
        var retorno = userRepository.criarUsuario(novoUsuario);
        return Mono.just(retorno);
    }

    public Mono<EstatisticasUsuarioModel> obterEstatisticas() {
        var usuarioLogado = obterUsuarioLogado();
        return usuarioLogado.map(usuario -> {
            EstatisticasUsuarioModel estatisticasUsuarioModel = new EstatisticasUsuarioModel();
            List<VideoModel> listaVideos = userRepository.obterVideosUsuario(usuario);
            estatisticasUsuarioModel.setQtdeVideos(listaVideos.stream().count());
            estatisticasUsuarioModel.setQtdeVideosFavoritados(contaFavoritos(listaVideos));
            estatisticasUsuarioModel.setMediaVisualizacoes(mediaVisualizacoes(listaVideos));
            return estatisticasUsuarioModel;
        });

    }

    private int mediaVisualizacoes(List<VideoModel> listaVideos) {
        int qtdeVisualizacoes = 0;
        for(var video : listaVideos){
            qtdeVisualizacoes += video.getVisualizacoesUsuarios();
        }
        if((int) listaVideos.stream().count() == 0) return 0;
        return Math.floorDiv(qtdeVisualizacoes, (int) listaVideos.stream().count());
    }

    public int contaFavoritos(List<VideoModel> listaVideos) {
        int qtdeFavoritos = 0;
        for(var video : listaVideos){
            if(video.getQtdeFavoritos() != 0) qtdeFavoritos++;
        }
        return qtdeFavoritos;
    }


}
