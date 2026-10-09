package com.aula.muralfilmes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.muralfilmes.adapter.FilmeAdapter;
import com.aula.muralfilmes.model.Filme;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FieldValue;

public class MainActivity extends AppCompatActivity {

    private String meuNome;

    private FirebaseFirestore db;
    private CollectionReference filmesRef;

    private RecyclerView listaFilmes;
    private TextView txtVazio;

    private FilmeAdapter adapter;
    private List<Filme> filmes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Carregar dados do usuario
        carregarNome();

        // Ação do botão de adicionar
        FloatingActionButton fab = findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(v -> mostrarDialogoNovoFilme());


        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        filmesRef = db.collection("filmes");

        // Configurar RecyclerView
        listaFilmes = findViewById(R.id.listaFilmes);
        txtVazio = findViewById(R.id.txtVazio);

        listaFilmes.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FilmeAdapter(filmes, new FilmeAdapter.OnFilmeClickListener() {
            @Override
            public void onVotar(Filme filme) {
                votarFilme(filme);
            }

            @Override
            public void onExcluir(Filme filme) {
                excluirFilme(filme);
            }
        });

        listaFilmes.setAdapter(adapter);

        observarFilmes();




    }

    private void votarFilme(Filme filme) {

        if (filme.getId() == null) {
            Toast.makeText(this,
                    "Não foi possível identificar o filme",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        filmesRef.document(filme.getId())
                .update("votos", FieldValue.increment(1))
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Erro ao votar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }


    private void excluirFilme(Filme filme) {

        if (filme.getId() == null) {
            Toast.makeText(this,
                    "Filme não identificado",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (meuNome == null ||
                !meuNome.equals(filme.getIndicadoPor())) {

            Toast.makeText(this,
                    "Você só pode excluir filmes indicados por você!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Excluir filme")
                .setMessage("Deseja realmente excluir " + filme.getTitulo() + "?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir", (dialog, which) -> {

                    filmesRef.document(filme.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this,
                                        "Filme excluído com sucesso!",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this,
                                        "Erro ao excluir: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .show();
    }


    private void observarFilmes() {
        filmesRef.orderBy("votos", Query.Direction.DESCENDING)
                .addSnapshotListener(this, (snapshot, erro) -> {

                    if (erro != null) {
                        Toast.makeText(this,
                                "Erro ao carregar filmes: " + erro.getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    filmes.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot documento
                            : snapshot.getDocuments()) {

                        Filme filme = documento.toObject(Filme.class);

                        if (filme != null) {
                            filmes.add(filme);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (filmes.isEmpty()) {
                        txtVazio.setVisibility(View.VISIBLE);
                    } else {
                        txtVazio.setVisibility(View.GONE);
                    }
                });
    }



    private void carregarNome() {
        SharedPreferences localNome = getSharedPreferences("nomeUsuario", MODE_PRIVATE);
        meuNome = localNome.getString("nomeUsuario", null);

        if (meuNome != null && !meuNome.equals("")) {
            Toast.makeText(this, "Bem-vindo(a) " + meuNome, Toast.LENGTH_LONG).show();
            return;
        }

        // Ativar a caixa de texto para digitar o nome
        EditText campo = new EditText(this);
        campo.setHint("Seu nome");

        new AlertDialog.Builder(this)
                .setTitle("Quem é você")
                .setMessage("Vai aparecer ao lado dos filmes que você indicar.")
                .setView(campo)
                .setCancelable(false)
                .setPositiveButton("Pronto",(dialogInterface, i) -> {
                    meuNome = campo.getText().toString();
                    localNome.edit().putString("nomeUsuario", meuNome).apply();

                }).show();

    }


    private void mostrarDialogoNovoFilme() {
        View form = LayoutInflater.from(this).inflate(R.layout.tela_filme, null);
        EditText campoTitulo = form.findViewById(R.id.campoTitulo);
        EditText campoGenero = form.findViewById(R.id.campoGenero);

        new AlertDialog.Builder(this)
                .setTitle("Indicar filme")
                .setView(form)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Adicionar", (d, w) -> {
                    String titulo = campoTitulo.getText().toString().trim();
                    String genero = campoGenero.getText().toString().trim();

                    if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(genero)) {
                        Toast.makeText(this, "Preencha título e gênero", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cadastrarFilme(titulo, genero);

                })
                .show();
    }

    private void cadastrarFilme(String titulo, String genero) {

        Filme novoFilme = new Filme(titulo, genero, meuNome);

        filmesRef.add(novoFilme)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this,
                            "Filme cadastrado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Erro ao cadastrar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}