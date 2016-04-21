package ru.biv131.bankomat;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    Button mButtonGetCash, mButtonPutCash, mButtonBalance, mButtonTransaction;
    ImageView mImageViewSecret;
    int curId, curPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageViewSecret = (ImageView) findViewById(R.id.imageview_for_secretactivity);
        mImageViewSecret.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecretActivity.class);
                intent.putExtra(LoginActivity.NAME_KEY_ID, curId);
                intent.putExtra(LoginActivity.NAME_KEY_POS, curPos);
                startActivity(intent);
                return false;
            }
        });
        mButtonGetCash = (Button) findViewById(R.id.btn_get_cash);
        mButtonBalance = (Button) findViewById(R.id.btn_get_balance);
        mButtonPutCash = (Button) findViewById(R.id.btn_put_cash);
        mButtonPutCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PutCashActivity.class);
                intent.putExtra(LoginActivity.NAME_KEY_ID, curId);
                intent.putExtra(LoginActivity.NAME_KEY_POS, curPos);
                startActivity(intent);
            }
        });
        mButtonTransaction = (Button) findViewById(R.id.btn_transaction);
        mButtonTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                intent.putExtra(LoginActivity.NAME_KEY_ID, curId);
                intent.putExtra(LoginActivity.NAME_KEY_POS, curPos);
                startActivity(intent);
            }
        });

        if (getIntent().getExtras() != null) {
            curId = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_ID);
            curPos = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_POS);
        }

        mButtonBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false)
                        .setTitle("Баланс")
                        .setMessage("Текущий баланс: " + getBalance() + " руб.")
                        .setPositiveButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                builder.create().show();
            }
        });
        mButtonGetCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GetCashActivity.class);
                intent.putExtra(LoginActivity.NAME_KEY_ID, curId);
                intent.putExtra(LoginActivity.NAME_KEY_POS, curPos);
                startActivity(intent);
            }
        });
    }

    public double getBalance() {
        double balance = 0;

        DataBase mDataBase = new DataBase(this);
        SQLiteDatabase dbReader = mDataBase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("idcard", curId);
        contentValues.put("action", "Запрос баланса");
        contentValues.put("time", DataBase.getTime());
        dbReader.insert("History", null, contentValues);
        Cursor cur2 = dbReader.query("History", null, null, null, null, null, null);
        cur2.close();
        Cursor cur = dbReader.query("Cards", null, null, null, null, null, null);
        cur.moveToPosition(curPos);
        balance = cur.getDouble(cur.getColumnIndex("balance"));
        cur.close();
        mDataBase.close();
        return balance;
    }
}
