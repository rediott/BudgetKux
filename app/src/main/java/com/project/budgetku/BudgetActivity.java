package com.project.budgetku;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BudgetActivity extends AppCompatActivity {
    private TextView  totalBudgetAmountTextView;
    private RecyclerView recyclerView;

    private FloatingActionButton fab;

    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());

        loader = new ProgressDialog(this);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View view){
                additem();
            }
        });
    }

    private void additem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.itemSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String budgetAmount = amount.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Tolong Masukkan Dana");
                    return;
                }
                if (budgetItem.equals("Pilih Item")){
                    Toast.makeText(BudgetActivity.this, "Tolong Pilih Item", Toast.LENGTH_SHORT).show();;
                }
                else {
                    loader.setMessage("Menambahkan Item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months = Months.monthsBetween(epoch,now);

                    Data data = new Data(budgetItem,date,id,null,Integer.parseInt(budgetAmount),months.getMonths());

                    budgetRef.child(id).setValue(data).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this,"Budget Sukses Ditambah",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(BudgetActivity.this,"Budget Gagal Ditambahkan",Toast.LENGTH_SHORT).show();
                        }
                        loader.dismiss();

                    });


                }
                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetRef,Data.class).build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemAmount("Jumlah Dana Yang Dialokasikan: Rp"+model.getAmount());
                holder.setDate("Pada: " + model.getDate());
                holder.setItemName("Item: "+ model.getItem());

                holder.note.setVisibility(View.GONE);

                switch (model.getItem()){
                    case "Transportasi": holder.imageView.setImageResource(R.drawable.sedan);
                        break;
                    case "Pangan": holder.imageView.setImageResource(R.drawable.eating);
                        break;
                    case "Kebutuhan Rumah": holder.imageView.setImageResource(R.drawable.sedan);
                        break;
                    case "Sandang": holder.imageView.setImageResource(R.drawable.sedan);
                        break;
                    case "Personal": holder.imageView.setImageResource(R.drawable.sedan);
                        break;
                    case "Edukasi": holder.imageView.setImageResource(R.drawable.sedan);
                        break;
                    case "Rekreasi": holder.imageView.setImageResource(R.drawable.sedan);
                        break;
                    case "Kesehatan": holder.imageView.setImageResource(R.drawable.sedan);
                        break;
                    case "lain - Lain": holder.imageView.setImageResource(R.drawable.sedan);
                        break;




                }


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout,parent,false);

                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ImageView imageView;
        public TextView note;

        public  MyViewHolder(@NonNull View itemView){
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            note= itemView.findViewById(R.id.note);


        }
        public  void setItemName (String itemName){
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }
        public  void setItemAmount (String itemAmount){
            TextView item = mView.findViewById(R.id.amount);
            item.setText(itemAmount);
        }
        public  void setDate (String itemDate){
            TextView date = mView.findViewById(R.id.date);

        }
    }
}