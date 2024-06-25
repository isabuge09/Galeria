package com.example.galeria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo);

        //obtem o caminho da foto que foi enviada via o Intent
        Intent i = getIntent();
        photoPath = i.getStringExtra("photo_path");

        Bitmap bitmap = Util.getBitmap(photoPath);//carrega a foto em um Bitmap
        //seta bitmap no imageview
        ImageView imPhoto = findViewById(R.id.imPhoto);
        imPhoto.setImageBitmap(bitmap);

        Toolbar toolbar = findViewById(R.id.tbPhoto);//obtem o elemento tbPhoto
        setSupportActionBar(toolbar);//define tbPhoto como actionbar padrao da tela

        ActionBar actionBar = getSupportActionBar();//obtem da Activity a ActionBar padrão
        actionBar.setDisplayHomeAsUpEnabled(true);//habilita o botao de voltar na ActionBar

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //cria inflador de menu que cria opcoes de menu definidas no arquivo de menu passado e as adiciona no menu da Activity
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_activity_tb, menu);
        return true;

    }

    //metodo executado toda vez que um item da ToolBar for selecionado, se o usuario clique no icone da compartilhamento sera executado o codigo que compartilha a foto
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opShare){
                sharePhoto();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void sharePhoto() {
        // Codigo para compartilhar a foto
        Uri photoUri = FileProvider.getUriForFile(PhotoActivity.this,"com.example.galeria.fileprovider", new File(photoPath));
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_STREAM, photoUri);
        i.setType("image/jpeg");
        startActivity(i);
    }
}