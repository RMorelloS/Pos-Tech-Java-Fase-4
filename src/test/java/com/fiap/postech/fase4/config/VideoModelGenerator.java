package com.fiap.postech.fase4.config;

import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.service.StreamingService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VideoModelGenerator {

    public static final VideoModel generateVideoModel() {
        var categorias = new ArrayList<String>();
        categorias.add("Categoria 1");
        categorias.add("Categoria 2");

        var categoriasTratado = categorias.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return new VideoModel(UUID.randomUUID(),
                "URL", "Descrição", "titulotratado",
                LocalDate.now(), "Titulo",
                categorias, categoriasTratado,
                50, 50, "admin");
    }

    public static final FilePart generateFilepart() throws IOException {

        ClassPathResource resource = new ClassPathResource("videos/sample.mp4");
        FilePart filePart = createFilePartFromResource(resource.getFile());
        return filePart;
    }

    private static final int BUFFER_SIZE = 1024; // Tamanho do buffer

    private static FilePart createFilePartFromResource(File file) throws IOException {
        Path path = file.toPath();
        List<DataBuffer> dataBuffers = readDataBuffersFromFile(path);

        // Criar um Flux<DataBuffer> a partir da lista de DataBuffers
        Flux<DataBuffer> dataBufferFlux = Flux.defer(() -> Flux.fromIterable(dataBuffers));

        // Criando um FilePart simulado com o conteúdo do arquivo
        return new FilePart() {
            @Override
            public String filename() {
                return file.getName();
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return null;
            }

            @Override
            public String name() {
                return null;
            }

            @Override
            public HttpHeaders headers() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("file", file.getName());
                return headers;
            }

            @Override
            public Flux<DataBuffer> content() {
                return dataBufferFlux;
            }
        };
    }

    // Lê os dados do arquivo em buffers
    private static List<DataBuffer> readDataBuffersFromFile(Path path) throws IOException {
        byte[] fileBytes = Files.readAllBytes(path);
        ByteBuffer byteBuffer = ByteBuffer.wrap(fileBytes);

        List<DataBuffer> dataBuffers = new ArrayList<>();
        while (byteBuffer.hasRemaining()) {
            int bufferSize = Math.min(BUFFER_SIZE, byteBuffer.remaining());
            byte[] buffer = new byte[bufferSize];
            byteBuffer.get(buffer);
            DataBuffer dataBuffer = createDataBuffer(buffer);
            dataBuffers.add(dataBuffer);
        }

        return dataBuffers;
    }

    // Cria um DataBuffer a partir de um array de bytes
    private static DataBuffer createDataBuffer(byte[] bytes) {
        return new DefaultDataBufferFactory().wrap(bytes);
    }


    public static VideoModel salvarVideo(VideoModel video, StreamingService streamingService) throws IOException {
        return streamingService.salvarVideo(video.getTitulo(),
                video.getDescricaoVideo(),
                Mono.just(VideoModelGenerator.generateFilepart()),
                video.getCategorias().stream()
                        .collect(Collectors.joining(";")),
                video.getAutor());
    }
}
