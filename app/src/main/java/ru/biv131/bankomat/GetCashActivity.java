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
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GetCashActivity extends AppCompatActivity {

    Button mGetCash500, mGetCash1000, mGetCash2000, mGetCash3000, mGetCash5000, mGetOtherCash;
    int curId, curPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_cash);

        mGetCash500 = (Button) findViewById(R.id.btn_get_cash_500);
        mGetCash1000 = (Button) findViewById(R.id.btn_get_cash_1000);
        mGetCash2000 = (Button) findViewById(R.id.btn_get_cash_2000);
        mGetCash3000 = (Button) findViewById(R.id.btn_get_cash_3000);
        mGetCash5000 = (Button) findViewById(R.id.btn_get_cash_5000);
        mGetOtherCash = (Button) findViewById(R.id.btn_get_other_cash);

        if (getIntent().getExtras() != null) {
            curId = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_ID);
            curPos = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_POS);
        }

    }

    public void OnClickCash(View v) {
        switch (v.getId()) {
            case R.id.btn_get_cash_500:
                withdrawMoney(500);
                break;
            case R.id.btn_get_cash_1000:
                withdrawMoney(1000);
                break;
            case R.id.btn_get_cash_2000:
                withdrawMoney(2000);
                break;
            case R.id.btn_get_cash_3000:
                withdrawMoney(3000);
                break;
            case R.id.btn_get_cash_5000:
                withdrawMoney(5000);
                break;
            case R.id.btn_get_other_cash:
                final EditText editText = new EditText(this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false)
                        .setMessage("Введите сумму")
                        .setTitle("Снятие наличных");

                builder.setView(editText);


                builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        withdrawMoney(Integer.parseInt(editText.getText().toString()));
                    }
                });

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.show();
                TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                break;
        }

    }

    private void withdrawMoney(final int m) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Выполнение операции", "Пожалуйста, подождите", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                DataBase mDataBase = new DataBase(GetCashActivity.this);
                SQLiteDatabase dbReader = mDataBase.getWritableDatabase();
                Cursor cur = dbReader.query("Cards", null, null, null, null, null, null);
                cur.moveToPosition(curPos);
                double balance = cur.getDouble(cur.getColumnIndex("balance"));
                if (m > balance) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GetCashActivity.this);
                    builder.setCancelable(false)
                            .setTitle("Внимание")
                            .setMessage("Недостаточно средств")
                            .setPositiveButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                    builder.create().show();
                } else {
                    ContentValues newBalance = new ContentValues();
                    newBalance.put("balance", balance - m);
                    dbReader.update("Cards", newBalance, "id=" + curId, null);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("idcard", curId);
                    contentValues.put("action", "Снятие " + m + " руб.");
                    contentValues.put("time", DataBase.getTime());
                    dbReader.insert("History", null, contentValues);
                    Cursor cur2 = dbReader.query("History", null, null, null, null, null, null);
                    cur2.close();

                    AlertDialog.Builder builder = new AlertDialog.Builder(GetCashActivity.this);
                    builder.setCancelable(false)
                            .setTitle("Успешно")
                            .setMessage("Операция выполнена. Со счета снято " + m + " руб.")
                            .setPositiveButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                    builder.create().show();
                }
                cur.close();
                mDataBase.close();
            }
        }, 1500);


    }
}
