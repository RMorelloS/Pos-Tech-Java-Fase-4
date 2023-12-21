package com.fiap.postech.fase4.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasUsuarioModel {
    private long qtdeVideos;
    private int mediaVisualizacoes;
    private int qtdeVideosFavoritados;
}
