package com.example.kowshick.travelmate;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<Moments>moments;

    public ImageAdapter(@NonNull Context context, List<Moments> moments) {
        this.context = context;
        this.moments = moments;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.image_row,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.imgTxt.setText(moments.get(position).getCaptions());
        String photoUri = moments.get(position).getPhotourl();
        Picasso.get().load(Uri.parse(photoUri)).into(holder.imgIcon);

    }

    @Override
    public int getItemCount() {
        return moments.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView imgTxt;
        ImageView imgIcon;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imgTxt=itemView.findViewById(R.id.imTxt);
            imgIcon=itemView.findViewById(R.id.imageTa);
        }
    }
}
