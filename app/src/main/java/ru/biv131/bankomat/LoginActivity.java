package ru.biv131.bankomat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    Button mButtonLogin;
    DataBase mDataBase;
    EditText mEditTextNumber, mEditTextPassword;
    public static final String NAME_KEY_ID = "curId";
    public static final String NAME_KEY_POS = "curPos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDataBase = new DataBase(this);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mEditTextNumber = (EditText) findViewById(R.id.edit_text_number);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_password);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flagentry = false;
                int curId = 0;
                int curPos = 0;

                SQLiteDatabase dbReader = mDataBase.getWritableDatabase();
                Cursor cur = dbReader.query("Cards", null, null, null, null, null, null);
                Log.d("cur", String.valueOf(cur.getCount()));
                int numberIndex = cur.getColumnIndex("number");
                int passwordIndex = cur.getColumnIndex("password");
                int idIndex = cur.getColumnIndex("id");

                while (cur.moveToNext()) {
                    Log.d("DataBase", "number = " + cur.getString(numberIndex) + ", password = " + cur.getString(passwordIndex));
                    if (mEditTextNumber.getText().toString().equals(cur.getString(numberIndex))
                            && mEditTextPassword.getText().toString().equals(cur.getString(passwordIndex))) {
                        Toast.makeText(LoginActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                        curId = cur.getInt(idIndex);
                        curPos = cur.getPosition();
                        flagentry = true;
                        break;
                    } else {
                        if (cur.getPosition() == cur.getCount() - 1)
                            Toast.makeText(LoginActivity.this, "Номер карты и пароль недействительны", Toast.LENGTH_LONG).show();
                    }
                }

                cur.close();
                mDataBase.close();

                if (flagentry) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(NAME_KEY_ID, curId);
                    intent.putExtra(NAME_KEY_POS, curPos);
                    startActivity(intent);
                }
            }
        });

    }

}
