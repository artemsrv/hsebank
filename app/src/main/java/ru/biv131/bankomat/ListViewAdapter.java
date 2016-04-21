package ru.biv131.bankomat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class ListViewAdapter extends CursorAdapter {

    public ListViewAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_list_order_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewAction = (TextView) view.findViewById(R.id.textViewaction);
        TextView textViewTime = (TextView) view.findViewById(R.id.textViewtime);

        textViewAction.setText(cursor.getString(2));
        textViewTime.setText(cursor.getString(3));

    }
}
