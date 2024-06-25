package com.example.galeria;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    MainActivity mainActivity;
    List<String> photos;//lista de fotos salvas

    public MainAdapter(MainActivity mainActivity, List<String> photos) {
        this.mainActivity = mainActivity;
        this.photos = photos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity);//obtemos um inflador de layouts
        View v = inflater.inflate(R.layout.list_item,parent,false);//cria os elementos de interface referentes a um item usando o inflador e guardamos dentro da View
        return new MyViewHolder(v);//retorna a View atraves da funcao
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);// preenche o ImageView com a foto correspondente

        //dimensoes que a imagem vai ter na lista
        int w = (int) mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int) mainActivity.getResources().getDimension(R.dimen.itemHeight);

        Bitmap bitmap = Util.getBitmap(photos.get(position), w, h);// carrega a imagem em um Bitmap
        imPhoto.setImageBitmap(bitmap);//seta o bitmap no imageview
        imPhoto.setOnClickListener(new View.OnClickListener() {

            //quando o usuario clica em cima de uma imagem
            @Override
            public void onClick(View v) {
                mainActivity.startPhotoActivity(photos.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}
