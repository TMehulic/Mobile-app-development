package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountContentProvider extends ContentProvider {


    private static final UriMatcher uM;
    private static final int ALLROWS=1;

    static {
        uM = new UriMatcher(UriMatcher.NO_MATCH);
        uM.addURI("rma.provider.account","elements",ALLROWS);
    }

    TransactionDBOpenHelper tHelper;

    @Override
    public boolean onCreate() {
        tHelper = new TransactionDBOpenHelper(getContext(),
                TransactionDBOpenHelper.DATABASE_NAME,null,
                TransactionDBOpenHelper.DATABASE_VERSION);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArg, @Nullable String sortOrder) {
        SQLiteDatabase database;
        try{
            database=tHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=tHelper.getReadableDatabase();
        }
        String groupby=null;
        String having=null;
        SQLiteQueryBuilder squery = new SQLiteQueryBuilder();

//        switch (uM.match(uri)){
//            case ALLROWS:
//                String idRow = uri.getPathSegments().get(1);
//                squery.appendWhere(TransactionDBOpenHelper.ACCOUNT_ID+"="+idRow);
//            default:break;
//        }
        squery.setTables(TransactionDBOpenHelper.ACCOUNT_TABLE);
        Cursor cursor = squery.query(database,projection,selection,selectionArg,groupby,having,sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
//        switch (uM.match(uri)){
//            case MOVIEID:
//                return "vnd.android.cursor.dir/vnd.rma.elemental";
//            default:
//                throw new IllegalArgumentException("Unsuported uri: "+uri.toString());
//        }
        return  null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase database;
        try{
            database=tHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=tHelper.getReadableDatabase();
        }
        long id = database.insert(TransactionDBOpenHelper.ACCOUNT_TABLE, null, contentValues);
        return uri.buildUpon().appendPath(String.valueOf(id)).build();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database;

        try{
            database = tHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database = tHelper.getReadableDatabase();
        }

//        switch(uM.match(uri)){
//            case ACCOUNTID:
//                String idRow = uri.getPathSegments().get(1);
//                s = TransactionDBOpenHelper.ACCOUNT_ID+"="+idRow;
//                break;
//            default:
//                throw new IllegalArgumentException("Unsuported uri: "+uri.toString());
//        }

        int rowsUpdated = database.update(TransactionDBOpenHelper.ACCOUNT_TABLE, contentValues, s, strings);

        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsUpdated;
    }
}
