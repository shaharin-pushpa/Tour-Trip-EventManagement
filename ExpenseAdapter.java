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

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    private Context context;
    private List<Expense> expenses;
    private int count=0;

    public ExpenseAdapter(@NonNull Context context, List<Expense> expenses) {
        super(context, R.layout.exp_row, expenses);
        this.context=context;
        this.expenses=expenses;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        count++;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.exp_row,parent,false);
        TextView exName=convertView.findViewById(R.id.exName);
        TextView am=convertView.findViewById(R.id.am);
        exName.setText(expenses.get(position).getExpenseName());
        am.setText(String.valueOf(expenses.get(position).getAmount()));
        return convertView;
    }

    public void swap(List<Expense>expenses){
        this.expenses=expenses;
        notifyDataSetChanged();
    }
}
