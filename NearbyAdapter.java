package com.example.kowshick.travelmate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class NearbyAdapter extends ArrayAdapter<NearbyResponse.Result> {
    private Context context;
    private List<NearbyResponse.Result>places;

    public NearbyAdapter(@NonNull Context context,List<NearbyResponse.Result>places) {
        super(context,R.layout.nearby_row, places);
        this.context=context;
        this.places=places;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.nearby_row,parent,false);
        TextView nameTv,addressTv;
        nameTv=convertView.findViewById(R.id.placeNameTv);
        addressTv=convertView.findViewById(R.id.addressTv);
        nameTv.setText(places.get(position).getName());
        addressTv.setText(places.get(position).getVicinity());
        return convertView;
    }
}
