package com.example.kowshick.travelmate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.provider.Settings.System.DATE_FORMAT;
import static junit.framework.Assert.assertEquals;

public class EventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event>events;
    private int count=0;

    public EventAdapter(@NonNull Context context,List<Event>events) {
        super(context, R.layout.event_list, events);
        this.context=context;
        this.events=events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        count++;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.event_list,parent,false);
        TextView evName=convertView.findViewById(R.id.eventNameTv);
        TextView strt=convertView.findViewById(R.id.startOnTv);
        TextView crate=convertView.findViewById(R.id.createTv);
        TextView days=convertView.findViewById(R.id.daysLeftTv);
        evName.setText("Name: "+events.get(position).getEventName());
        crate.setText("Created Date: "+events.get(position).getCreateDate());
        strt.setText("Start Date: "+events.get(position).getDeparture());
        //days
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String creatDate = df.format(c.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            //startDate = dateFormat.parse(events.get(position).getCreateDate());
            startDate=dateFormat.parse(creatDate);
            endDate = dateFormat.parse(events.get(position).getDeparture());
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(numberOfDays<0) {
            days.setText("Date over");

        }
        else {
            days.setText(String.valueOf(numberOfDays) + " days Left");
        }
        return convertView;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }


}
