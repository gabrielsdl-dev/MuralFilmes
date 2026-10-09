package com.aula.muralfilmes.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.aula.muralfilmes.model.Filme;
import java.util.List;

import com.aula.muralfilmes.R;

public class FilmeAdapter extends RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder> {


    private List<Filme> listaFilmes;
    private OnFilmeClickListener listener;

    public interface OnFilmeClickListener {
        void onVotar(Filme filme);
        void onExcluir(Filme filme);
    }

    public FilmeAdapter(List<Filme> listaFilmes, OnFilmeClickListener listener) {
        this.listaFilmes = listaFilmes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilmeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filme, parent, false);

        return new FilmeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FilmeViewHolder holder, int position) {

        Filme filme = listaFilmes.get(position);

        holder.txtTitulo.setText(filme.getTitulo());
        holder.txtGenero.setText(filme.getGenero());
        holder.txtIndicadoPor.setText("Indicado por: " + filme.getIndicadoPor());
        holder.txtVotos.setText(String.valueOf(filme.getVotos()));

        holder.btnVotar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVotar(filme);
            }
        });

        holder.btnExcluir.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExcluir(filme);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFilmes.size();
    }

    public static class FilmeViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo, txtGenero, txtIndicadoPor, txtVotos;
        ImageButton btnVotar, btnExcluir;

        public FilmeViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtGenero = itemView.findViewById(R.id.txtGenero);
            txtIndicadoPor = itemView.findViewById(R.id.txtIndicadoPor);
            txtVotos = itemView.findViewById(R.id.txtVotos);

            btnVotar = itemView.findViewById(R.id.btnVotar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}