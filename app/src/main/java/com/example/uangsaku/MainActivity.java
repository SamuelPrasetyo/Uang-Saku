package com.example.uangsaku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText amountEditText;
    private Spinner typeTransaction;
    private Button addTransactionButton;
    private TextView balanceTextView;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactions;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountEditText = findViewById(R.id.amountEditText);
        typeTransaction = findViewById(R.id.typeTransaction); // Updated variable initialization
        addTransactionButton = findViewById(R.id.addTransactionButton);
        balanceTextView = findViewById(R.id.balanceTextView);
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);

        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactions);

        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTransactions.setAdapter(transactionAdapter);

        databaseHelper = new DatabaseHelper(this);

        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = Double.parseDouble(amountEditText.getText().toString());
                String type = typeTransaction.getSelectedItem().toString(); // Updated line

                if (databaseHelper.addTransaction(amount, type)) {
                    amountEditText.setText("");
                    updateBalance();
                }
            }
        });

        updateBalance();
    }

    private void updateBalance() {
        Cursor cursor = databaseHelper.getAllTransactions();
        double balance = 0;

        while (cursor.moveToNext()) {
            double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
            String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));

            if (type.equals("income")) {
                balance += amount;
                Transaction transaction = new Transaction("Pendapatan", amount);
                transactions.add(transaction);
                transactionAdapter.notifyDataSetChanged();
            } else if (type.equals("expense")) {
                balance -= amount;
                Transaction transaction = new Transaction("Pengeluaran", amount);
                transactions.add(transaction);
                transactionAdapter.notifyDataSetChanged();
            }
        }

        balanceTextView.setText("Saldo: Rp. " + balance);
        cursor.close();
    }
}