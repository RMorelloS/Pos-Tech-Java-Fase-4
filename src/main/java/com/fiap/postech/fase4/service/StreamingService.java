package com.fiap.postech.fase4.service;

import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.repository.StreamingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StreamingService {
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private StreamingRepository streamingRepository;

    @Autowired
    private VideoUploadService videoUploadService;


    private  final Path basePath = Paths.get("./src/main/resources/videos/");

    public String salvarVideo(String titulo, String descricao,
                              Mono<FilePart> video, String categorias, String autor) {

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
        videoModel.setAutor(autor);

        streamingRepository.salvarVideo(videoModel);

        video.doOnNext(fp -> System.out.println("Received file: " + fp.filename()))
                .flatMap(fp -> fp.transferTo(basePath.resolve(fp.filename())))
                .then();

        videoUploadService.uploadObject(video, videoModel.getVideoId());

        return "listar_videos";
    }

    public VideoModel getVideoById(UUID videoId){
        VideoModel video = streamingRepository.getVideoById(videoId);
        return video;
    }

    public List<Map.Entry<String, Integer>> obterListaCategorias(ArrayList<UUID> listaFavoritos) {
        Map<String, Integer> categorias = new HashMap<String, Integer>();
        for (var favorito : listaFavoritos) {
            var video = getVideoById(favorito);
            if (video != null) {
                var categoriasVideo = video.getCategorias();
                for (var categoria : categoriasVideo) {
                    categorias.putIfAbsent(categoria, 0);
                    categorias.compute(categoria, (k, v) -> (v == null) ? 1 : v + 1);
                }
            }
        }
        List<Map.Entry<String, Integer>> categoriasOrdenado = new ArrayList<>();
        if (!categorias.isEmpty()) {
            categoriasOrdenado = new ArrayList<>(categorias.entrySet());

            // Ordenação da lista usando um comparador personalizado
            Collections.sort(categoriasOrdenado, Comparator.comparingInt(Map.Entry::getValue));

        }
         categoriasOrdenado = categoriasOrdenado.subList(0, Math.min(2, categoriasOrdenado.size()));
        return categoriasOrdenado;
    }
    public List<VideoModel> getVideos(String tituloVideo, String categoriaVideo, LocalDate dataPublicacaoVideo) {
        return streamingRepository.getVideos(tituloVideo, categoriaVideo, dataPublicacaoVideo);
    }

    public List<VideoModel> getVideosByUser(String user) {
        return streamingRepository.getVideosByUser(user);
    }

    public String deletarVideo(String videoId, String usuario){

        videoUploadService.delete(videoId);
        return streamingRepository.delete(UUID.fromString(videoId), usuario);

    }

    public List<VideoModel> obterVideosPorCategorias(List<Map.Entry<String, Integer>> listaCategorias) {
        return streamingRepository.obterVideosPorCategorias(listaCategorias);
    }

    public void atualizarViews(String videoId) {
        var video = streamingRepository.getVideoById(UUID.fromString(videoId));
        if(video != null) {
            video.setVisualizacoesUsuarios(video.getVisualizacoesUsuarios() + 1);
        }
        streamingRepository.salvarVideo(video);
    }

    public void atualizaFavoritos(String videoId, StatusFavoritoEnum.StatusFavorito statusFavorito) {
        var video = getVideoById(UUID.fromString(videoId));
        if(statusFavorito == StatusFavoritoEnum.StatusFavorito.ADICIONAR_FAVORITO){
            video.setQtdeFavoritos(video.getQtdeFavoritos() + 1);
        }else{
            video.setQtdeFavoritos(video.getQtdeFavoritos() - 1);
        }
        streamingRepository.salvarVideo(video);
    }
}
