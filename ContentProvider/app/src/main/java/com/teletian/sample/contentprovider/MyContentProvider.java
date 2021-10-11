package com.teletian.sample.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.teletian.sample.contentprovider";
    private Context context;
    private SQLiteDatabase db = null;
    private static final UriMatcher sUriMatcher;

    static {
        // 使用 UriMatcher 注册 URI
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 若 URI 资源路径 = content://com.teletian.sample.contentprovider/user，则返回注册码 1
        sUriMatcher.addURI(AUTHORITY, "user", 1);
    }

    @Override
    public boolean onCreate() {
        context = getContext();

        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();

        // 初始化数据
        db.execSQL("delete from user");
        db.execSQL("insert into user values(1,'Jack');");
        db.execSQL("insert into user values(2,'Rose');");

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String table = getTableName(uri);
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        String table = getTableName(uri);
        db.insert(table, null, contentValues);
        context.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // 不提供
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        // 不提供
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // 不提供
        return null;
    }

    /**
     * 根据 URI 匹配注册码，从而匹配 ContentProvider 中相应的表名
     */
    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case 1:
                tableName = DBHelper.USER_TABLE_NAME;
                break;
        }
        return tableName;
    }
}
