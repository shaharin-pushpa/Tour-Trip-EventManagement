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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>{
    private Context context;
    CurrentWeatherFragments cc=new CurrentWeatherFragments();
    private String unit;
    private List<ForcastWeatherResponse.ListFor> weathers;
    public WeatherAdapter(@NonNull Context context,List<ForcastWeatherResponse.ListFor>weathers,String s) {
        this.context=context;
        this.weathers=weathers;
        this.unit=s;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.forcast_row,parent,false);
        return new WeatherViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        long datetime = weathers.get(position).getDt();
        Date date = new Date(datetime*1000);
        // SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMMM yyyy");
        String dateText = df2.format(date);
        SimpleDateFormat df1 = new SimpleDateFormat("hh aa");
        String dateText1 = df1.format(date);
        //String dateText=String.valueOf(date);
        try {
            //holder.dayTv.setText(weathers.get(position).getDt());
            holder.timeTv.setText("Time: "+dateText1);
            holder.dayTv.setText("Date: "+dateText);
            if (unit.equals("metric")) {
                holder.minTemTv.setText("Min: " + String.valueOf(weathers.get(position).getMain().getTempMin())+"° C");
                holder.maxtempTv.setText("Max: " + String.valueOf(weathers.get(position).getMain().getTempMax())+"° C");
            }
            else{
                holder.minTemTv.setText("Min: " + String.valueOf(weathers.get(position).getMain().getTempMin())+"° F");
                holder.maxtempTv.setText("Max: " + String.valueOf(weathers.get(position).getMain().getTempMax())+"° F");
            }
            for(int i=0;i<weathers.size();i++){
                String photoUri = CurrentWeatherFragments.IMAGE_URL+weathers.get(position).getWeather().get(i).getIcon()+".png";

                Picasso.get().load(Uri.parse(photoUri)).into(holder.imgIcon);
            }

        }
        catch (Exception e){

        }


    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }


    public class WeatherViewHolder extends RecyclerView.ViewHolder{
        TextView minTemTv,dateTv,dayTv,maxtempTv,timeTv;
        ImageView imgIcon;
        public WeatherViewHolder(View itemView) {
            super(itemView);
            minTemTv=itemView.findViewById(R.id.forcastMin);
            maxtempTv=itemView.findViewById(R.id.forcastMax);
            dayTv=itemView.findViewById(R.id.forcastDay);
            timeTv=itemView.findViewById(R.id.forcastTime);
            imgIcon=itemView.findViewById(R.id.forcastImg);
        }
    }


}

