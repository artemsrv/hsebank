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

public class TransactionActivity extends AppCompatActivity {

    Button mButtonOk;
    EditText mEditTextCard, mEditTextSum;
    int curId, curPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        mButtonOk = (Button) findViewById(R.id.btn_transaction_ok);
        mEditTextCard = (EditText) findViewById(R.id.edit_text_transaction_card);
        mEditTextSum = (EditText) findViewById(R.id.edit_text_transaction_sum);
        if (getIntent().getExtras() != null) {
            curId = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_ID);
            curPos = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_POS);
        }
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextCard.getText().toString().trim().length() > 0 && mEditTextSum.getText().toString().trim().length() > 0 && !mEditTextSum.getText().toString().equals("0")) {
                    final ProgressDialog progressDialog = ProgressDialog.show(TransactionActivity.this, "Выполнение операции", "Пожалуйста, подождите", true);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            DataBase mDataBase = new DataBase(TransactionActivity.this);
                            SQLiteDatabase dbReader = mDataBase.getWritableDatabase();
                            Cursor cur = dbReader.query("Cards", null, null, null, null, null, null);
                            cur.moveToPosition(curPos);
                            double balance = cur.getDouble(cur.getColumnIndex("balance"));
                            ContentValues newBalance = new ContentValues();
                            newBalance.put("balance", balance - Double.valueOf(mEditTextSum.getText().toString()));
                            dbReader.update("Cards", newBalance, "id=" + curId, null);

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("idcard", curId);
                            contentValues.put("action", "Перевод на карту " + mEditTextCard.getText().toString() + " \n" + mEditTextSum.getText().toString() + " руб.");
                            contentValues.put("time", DataBase.getTime());
                            dbReader.insert("History", null, contentValues);
                            Cursor cur2 = dbReader.query("History", null, null, null, null, null, null);
                            cur2.close();

                            AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
                            builder.setCancelable(false)
                                    .setTitle("Успешно")
                                    .setMessage("Операция выполнена. Со счета снято " + mEditTextSum.getText().toString() + " руб.")
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
                    Toast.makeText(TransactionActivity.this, "Операция не выполнена. Проверьте правильность введеных данных", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
