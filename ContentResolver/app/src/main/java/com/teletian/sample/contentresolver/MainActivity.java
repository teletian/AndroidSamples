package com.teletian.sample.contentresolver;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri uri_user = Uri.parse("content://com.teletian.sample.contentprovider/user");

        ContentValues values = new ContentValues();
        values.put("_id", 3);
        values.put("name", "Jeff");

        ContentResolver resolver = getContentResolver();
        resolver.insert(uri_user, values);

        Cursor cursor = resolver.query(uri_user, new String[]{"_id", "name"}, null, null, null);
        while (cursor.moveToNext()) {
            Log.i(TAG, "query book:" + cursor.getInt(0) + " " + cursor.getString(1));
        }
        cursor.close();
    }
}