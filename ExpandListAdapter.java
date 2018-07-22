package com.example.kowshick.travelmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpandListAdapter extends BaseExpandableListAdapter {
    String[] groupNanes={"Expanditure","Moments","More on Event"};
    String[][] childNames={{"Add New Expense","View All Expense","Add More Budget"},{"Take a Photo","View All Moments"},{"Edit Event","Delete Event"}};
    Context contex;
    TextView budget;
    Event fc=new Event();
    private String[] instructions=null;
    private List<Expense>events=new ArrayList<>();
    private List<Expense>exs=new ArrayList<>();
    FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference rootRef;
    private double balance=0;
    ExpenseAdapter adapter;

    ExpandListAdapter(Context context,Event fc,List<Expense>events,TextView budget)
    {
        this.contex=context;
        this.fc=fc;
        this.events=events;
        this.budget=budget;
    }

    @Override
    public int getGroupCount() {
        return groupNanes.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childNames[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupNanes[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childNames[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView textView=new TextView(contex);
        textView.setText(groupNanes[groupPosition]);
        textView.setPadding(100,0,0,0);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLUE);
        return textView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, final View convertView, ViewGroup parent) {
        final TextView textView=new TextView(contex);
        textView.setText(childNames[groupPosition][childPosition]);
        textView.setPadding(100,0,0,0);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference("Events");

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text=textView.getText().toString();
                    if (text.equals("Add New Expense")) {
                        LayoutInflater li = LayoutInflater.from(contex);
                        View promptsView = li.inflate(R.layout.expense_row, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                contex);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText amount = (EditText) promptsView
                                .findViewById(R.id.amountEt);
                        final EditText comment = (EditText) promptsView
                                .findViewById(R.id.commentEt);

                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Save",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // get user input and set it to result
                                                // edit text
                                                String am = amount.getText().toString();
                                                String com = comment.getText().toString();
                                                try{
                                                    if(am.equals(null)||com.equals(null)){
                                                        amount.setText("Fill the field");
                                                    }
                                                    else {
                                                        DatabaseReference expenseRef = rootRef.child("Expense");

                                                        String idd=rootRef.push().getKey();
                                                        Expense ex = new Expense(idd, com, Double.parseDouble(am));
                                                        expenseRef.child(currentUser.getUid()).child(fc.getId()).child(idd).setValue(ex);
                                                    }
                                                }
                                                catch (Exception e){

                                                }

                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    }
                    else if(text.equals("Add More Budget")){
                        LayoutInflater li = LayoutInflater.from(contex);
                        View promptsView = li.inflate(R.layout.more_budget, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                contex);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText amount = (EditText) promptsView
                                .findViewById(R.id.moreBudget);

                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Save",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // get user input and set it to result
                                                // edit text
                                                //String am = amount.getText().toString();
                                                String am = amount.getText().toString();
                                               final double balan=Double.parseDouble(am);
                                                       rootRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                                               balance=0;
                                                               for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                                                   Event ev=snapshot.getValue(Event.class);
                                                                   if(ev.getId().equals(fc.getId())) {
                                                                       double amountBg=balan+ev.getBudget();
                                                                       snapshot.getRef().child("budget").setValue(amountBg);
                                                                       Toast.makeText(contex, "Budget Updated", Toast.LENGTH_SHORT).show();
                                                                      // balance = amountBg;
                                                                       budget.setText("Budget: "+String.valueOf(amountBg));
                                                                   }
                                                               }

                                                           }

                                                           @Override
                                                           public void onCancelled(DatabaseError databaseError) {

                                                           }
                                                       });

                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();


                    }

                    else if(text.equals("View All Expense")){
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(contex);
                        LayoutInflater li = LayoutInflater.from(contex);
                        View con = li.inflate(R.layout.expense_list, null);
                        alertDialog.setView(con);
                        alertDialog.setTitle("Expense List");
                        final ListView lv = (ListView) con.findViewById(R.id.listEx);
                        alertDialog
                                .setCancelable(false).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        if(events.size()>0) {
                            adapter = new ExpenseAdapter(contex, events);
                            lv.setAdapter(adapter);
                            alertDialog.show();
                        }
                        else{
                            Toast.makeText(contex, "List is Empty", Toast.LENGTH_SHORT).show();

                        }

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(contex);
                                builder.setTitle("Choose option");
                                builder.setMessage("Update or delete expense?");
                                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //go to update activity
                                        LayoutInflater li = LayoutInflater.from(contex);
                                        View promptsView = li.inflate(R.layout.expense_row, null);
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                contex);

                                        // set prompts.xml to alertdialog builder
                                        alertDialogBuilder.setView(promptsView);

                                        final EditText amount = (EditText) promptsView
                                                .findViewById(R.id.amountEt);
                                        final EditText comment = (EditText) promptsView
                                                .findViewById(R.id.commentEt);
                                        final TextView exTv=(TextView) promptsView.findViewById(R.id.exTV);
                                        amount.setText(String.valueOf(events.get(position).getAmount()));
                                        comment.setText(events.get(position).getExpenseName());

                                        alertDialogBuilder
                                                .setCancelable(false)
                                                .setPositiveButton("Save",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                // get user input and set it to result
                                                                // edit text
                                                                DatabaseReference expenseRef = rootRef.child("Expense");
                                                                String exId = events.get(position).getId();
                                                                String am = amount.getText().toString();
                                                                String com = comment.getText().toString();
                                                                String idd=rootRef.push().getKey();
                                                                Expense ex = new Expense(exId, com, Double.parseDouble(am));
                                                                expenseRef.child(currentUser.getUid()).child(fc.getId()).child(exId).setValue(ex);
                                                                Toast.makeText(contex, "Value updated", Toast.LENGTH_SHORT).show();
                                                                expenseRef.child(currentUser.getUid()).child(fc.getId()).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        events.clear();
                                                                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                                            Expense ex = d.getValue(Expense.class);
                                                                            events.add(ex);

                                                                        }
                                                                        adapter.swap(events);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });

                                                            }
                                                        })
                                                .setNegativeButton("Cancel",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });

                                        // create alert dialog
                                        AlertDialog alertDialog = alertDialogBuilder.create();

                                        // show it
                                        alertDialog.show();



                                    }
                                });
                                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(contex);
                                        builder.setTitle("Are You sure?");
                                        builder.setMessage("Yes or No?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DatabaseReference expenseRef = rootRef.child("Expense");
                                                String exId = events.get(position).getId();
                                                expenseRef.child(currentUser.getUid()).child(fc.getId()).child(exId).removeValue();
                                                Toast.makeText(contex, "Value Deleted", Toast.LENGTH_SHORT).show();
                                                events.remove(position);
                                               adapter.notifyDataSetChanged();

                                            }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            }
                        });

                    }


                    else if(text.equals("Edit Event")){
                        LayoutInflater li = LayoutInflater.from(contex);
                        View promptsView = li.inflate(R.layout.update_row, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                contex);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setTitle("Update Event..");
                        alertDialogBuilder.setView(promptsView);

                        final EditText upName = (EditText) promptsView
                                .findViewById(R.id.upEventName);
                        final EditText upLoc = (EditText) promptsView
                                .findViewById(R.id.upStartLocationName);
                        final EditText upDes = (EditText) promptsView
                                .findViewById(R.id.upDestination);
                        final EditText upDepar = (EditText) promptsView
                                .findViewById(R.id.upDeparture);
                        final EditText upBudget = (EditText) promptsView
                                .findViewById(R.id.upBudget);
                        final EditText upCre = (EditText) promptsView
                                .findViewById(R.id.upCre);
                        upName.setText(fc.getEventName());
                        upLoc.setText(fc.getStartLocation());
                        upBudget.setText(String.valueOf(fc.getBudget()));
                        upDepar.setText(fc.getDeparture());
                        upCre.setText(fc.getCreateDate());
                        upDes.setText(fc.getDestination());
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Save",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // get user input and set it to result
                                                // edit text
                                                String upNa=upName.getText().toString();
                                                String locup = upLoc.getText().toString();
                                                String bu=upBudget.getText().toString();
                                                String depar=upDepar.getText().toString();
                                                String upC= upCre.getText().toString();
                                                String des=upDes.getText().toString();
                                                Event ev=new Event(fc.getId(),upNa,locup,des,depar,upC,Double.parseDouble(bu));
                                                rootRef.child(currentUser.getUid()).child(fc.getId()).setValue(ev);
                                                Toast.makeText(contex, "Value Updated", Toast.LENGTH_SHORT).show();
                                                Intent in=new Intent(contex,NavigationDrawer.class);
                                                contex.startActivity(in);

                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();



                    }
                    else if(text.equals("Delete Event")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(contex);
                        builder.setTitle("Are You sure?");
                        builder.setMessage("Yes or No?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rootRef.child(currentUser.getUid()).child(fc.getId()).removeValue();
                                Toast.makeText(contex, "Value Deleted", Toast.LENGTH_SHORT).show();
                                Intent in=new Intent(contex,NavigationDrawer.class);
                                contex.startActivity(in);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }

                     else if(text.equals("Take a Photo")){
                        Intent intent=new Intent(contex,TakePhotoActivity.class);
                        intent.putExtra("evid", fc).putExtra("usid", currentUser.getUid());
                        contex.startActivity(intent);
                    }

                    else if(text.equals("View All Moments")){
                        Intent intnt=new Intent(contex,ViewMomentsActivity.class);
                        intnt.putExtra("eventid", fc).putExtra("userid", currentUser.getUid());
                        contex.startActivity(intnt);

                    }

                }


            });



        return textView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }




}





























