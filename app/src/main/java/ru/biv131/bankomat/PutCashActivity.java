package ru.biv131.bankomat;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PutCashActivity extends AppCompatActivity {

    Button mButtonPutCash;
    EditText mEditTextCash;
    int curId, curPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_cash);
        mButtonPutCash = (Button) findViewById(R.id.btn_put_cash);
        mEditTextCash = (EditText) findViewById(R.id.edittext_put_cash);
        if (getIntent().getExtras() != null) {
            curId = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_ID);
            curPos = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_POS);
        }
        mButtonPutCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextCash.getText().toString().trim().length() > 0 && !mEditTextCash.getText().toString().equals("0")) {
                    final ProgressDialog progressDialog = ProgressDialog.show(PutCashActivity.this, "Выполнение операции", "Пожалуйста, подождите", true);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            DataBase mDataBase = new DataBase(PutCashActivity.this);
                            SQLiteDatabase dbReader = mDataBase.getWritableDatabase();
                            Cursor cur = dbReader.query("Cards", null, null, null, null, null, null);
                            cur.moveToPosition(curPos);
                            double balance = cur.getDouble(cur.getColumnIndex("balance"));
                            ContentValues newBalance = new ContentValues();
                            newBalance.put("balance", balance + Double.valueOf(mEditTextCash.getText().toString()));
                            dbReader.update("Cards", newBalance, "id=" + curId, null);

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("idcard", curId);
                            contentValues.put("action", "Пополнение счета на " + mEditTextCash.getText().toString() + " руб.");
                            contentValues.put("time", DataBase.getTime());
                            dbReader.insert("History", null, contentValues);
                            Cursor cur2 = dbReader.query("History", null, null, null, null, null, null);
                            cur2.close();

                            AlertDialog.Builder builder = new AlertDialog.Builder(PutCashActivity.this);
                            builder.setCancelable(false)
                                    .setTitle("Успешно")
                                    .setMessage("Операция выполнена.На счет зачислено " + mEditTextCash.getText().toString() + " руб.")
                                    .setPositiveButton("ОК",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                            builder.create().show();
                            cur.close();
                            mDataBase.close();

                        }
                    }, 1500);
                } else {
                    Toast.makeText(PutCashActivity.this, "Операция не выполнена. Деньги не были внесены.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
