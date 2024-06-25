package com.example.galeria;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> photos = new ArrayList<>();
    MainAdapter mainAdapter;
    static int RESULT_TAKE_PICTURE = 1;
    static int RESULT_REQUEST_PERMISSION = 2;
    String currentPhotoPath;

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

        List<String> permissions = new ArrayList<>();//lista de permissoes
        permissions.add(Manifest.permission.CAMERA);
        checkForPermissions(permissions);//chama o metodo passando a permissao requerida como parametro

        Toolbar toolbar = findViewById(R.id.tbMain);//obtem o elemento tbMain
        setSupportActionBar(toolbar);//indica tbMain como actionBar padrao da tela

        //acessam o diretório “Pictures” (Enviroment.PICTURES)
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //leem a lista de fotos ja salvas
        File[] files = dir.listFiles();
        //adicionam as fotos na lista de fotos
        for(int i = 0; i < files.length; i++) {
            photos.add(files[i].getAbsolutePath());
        }

        // cria o MainAdapter e seta no RecycleView
        mainAdapter = new MainAdapter(MainActivity.this, photos);
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);

        //calculam quantas colunas de fotos cabem na tela
        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this, w);
        //configuram o RecycleView para exibir as fotos em GRID de acordo com o numero de colunas calculado acima
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);
    }

    //cria inflador de menu que cria opcoes de menu definidas no arquivo de menu passado e as adiciona no menu da Activity
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;

    }

    //metodo executado toda vez que um item da ToolBar for selecionado, se o usuario clique no icone da camera sera executado o codigo que dispara a camera
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opCamera){
            dispatchTakePictureIntent();
        return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //metodo chamado dentro do onBindViewHolder em MainAdapter quando o usuario clica em uma foto
    public void startPhotoActivity(String photoPath) {
        Intent i = new Intent(MainActivity.this,PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);
    }

    //metodo que dispara a app de câmera
    private void dispatchTakePictureIntent (){

        //cria arquivo vazio dentro da pasta Pictures e caso o arquivo nao possa ser criado exibe uma mensagem para o usuario
        File f = null;
        try {
            f = createImageFile();
        } catch (IOException e){
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }

        currentPhotoPath = f.getAbsolutePath();//salva o local do arquivo

        if(f != null) {
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "com.example.galeria.fileprovider", f);//gera endereco URI para o arquivo de foto
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//Intent para disparar a app de câmera
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);//passa URI para a camera pelo intent
            startActivityForResult(i, RESULT_TAKE_PICTURE);//a app inicia e espera o resultado, no caso a foto
        }
    }

    //cria o arquivo que vai guardar a imagem
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
    }

    //metodo chamado depois que a app retorna algo para a aplicacao
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_TAKE_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {//se a foto foi tirada
                photos.add(currentPhotoPath);//o local da foto é adicionado na lista
                mainAdapter.notifyItemInserted(photos.size()-1);//MainAdapter é avisado
            }

            else {// se não for tirada deleta o arquivo que foi criado para armazenar a foto
                File f = new File(currentPhotoPath);
                f.delete();
            }
        }
    }

    //aceita como entrada uma lista de permissoes
    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();

        //verifica permissoes
        for(String permission : permissions) {
            // se a permissao nao for confirmada ela é adicionada em uma lista
            if(!hasPermission(permission)) {
                permissionsNotGranted.add(permission);
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(permissionsNotGranted.size() > 0) {
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);//requisita ao usuario as permissoes nao concedidas
            }
        }
    }

    //verifica se uma permissao foi concedida
    private boolean hasPermission(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    //metodo chamado apos o usuario conceder ou nao as permissoes requisitadas
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION) {
            //verifica se cada permissao foi confirmada
            for(String permission : permissions) {
                if(!hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }

        // se tiver alguma permissao nao confirmada
        if(permissionsRejected.size() > 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    //se ela for realmente necessaria ela é avisada ao usuario
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //pede a permissao novamente
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }

                    }).create().show();
                }
            }
        }
    }
}