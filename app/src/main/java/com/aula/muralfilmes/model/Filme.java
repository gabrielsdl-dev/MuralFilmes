package com.aula.muralfilmes.model;

import java.util.Date;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

public class Filme {
    //ATENÇÃO: liberar comentario apos implementar o firebase

    @DocumentId
    private String id;

    private String titulo;
    private String genero;
    private String indicadoPor;
    private long votos;

    //ATENÇÃO: liberar comentario apos implementar o firebase
    @ServerTimestamp
    private Date criadoEm;


    public Filme() {
        // Construtor vazio exigido pelo Firestore
    }

    public Filme(String titulo, String genero, String indicadoPor) {
        this.titulo = titulo;
        this.genero = genero;
        this.indicadoPor = indicadoPor;
        this.votos = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getIndicadoPor() {
        return indicadoPor;
    }

    public void setIndicadoPor(String indicadoPor) {
        this.indicadoPor = indicadoPor;
    }

    public long getVotos() {
        return votos;
    }

    public void setVotos(long votos) {
        this.votos = votos;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }
}
