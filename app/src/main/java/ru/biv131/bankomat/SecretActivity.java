package ru.biv131.bankomat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class SecretActivity extends AppCompatActivity {
    int curId, curPos;
    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
        if (getIntent().getExtras() != null) {
            curId = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_ID);
            curPos = getIntent().getExtras().getInt(LoginActivity.NAME_KEY_POS);
        }
        DataBase mDataBase = new DataBase(this);
        SQLiteDatabase dbReader = mDataBase.getWritableDatabase();
        Cursor cur = dbReader.rawQuery("SELECT * FROM History WHERE idcard=?", new String[]{String.valueOf(curId)});
        logCursor(cur);
        ListView listView = (ListView) findViewById(R.id.listView);
        ListViewAdapter listViewAdapter = new ListViewAdapter(this, cur);
        listView.setAdapter(listViewAdapter);
        //cur.close();
        //mDataBase.close();
    }

    void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);
                } while (c.moveToNext());
            }
        } else
            Log.d(LOG_TAG, "Cursor is null");
    }
}
