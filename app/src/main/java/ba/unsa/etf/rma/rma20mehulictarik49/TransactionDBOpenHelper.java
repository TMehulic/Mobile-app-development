package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TransactionDBOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="SpiralaTMTest.db";
    public static final int DATABASE_VERSION=4;


    public TransactionDBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TransactionDBOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //TRANSACTION DB

    public static final String TRANSACTION_TABLE="transactions";
    public static final String TRANSACTION_ID="id";
    public static final String TRANSACTION_INTERNAL_ID="internalId";
    public static final String TRANSACTION_TITLE="title";
    public static final String TRANSACTION_AMOUNT="amount";
    public static final String TRANSACTION_DATE="date";
    public static final String TRANSACTION_END_DATE="endDate";
    public static final String TRANSACTION_DESCRIPTION="itemDescription";
    public static final String TRANSACTION_INTERVAL="transactionInterval";
    public static final String TRANSACTION_TYPE="transactionType";
    public static final String TRANSACTION_EDIT="transactionEdit";
    public static final String TRANSACTION_ADD="transactionAdd";
    public static final String TRANSACTION_DELETE="transactionDelete";
    private static final String TRANSACTION_TABLE_CREATE=
            "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE
                    +" (" + TRANSACTION_INTERNAL_ID
                    +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +TRANSACTION_ID + " INTEGER, "      //svakako ce se kod dodavanja na api staviti unique, a kod offline je bitn internal
                    +TRANSACTION_DATE+" TEXT NOT NULL, "
                    +TRANSACTION_TITLE+" TEXT NOT NULL, "
                    +TRANSACTION_AMOUNT+" REAL, "
                    +TRANSACTION_DESCRIPTION+" TEXT, "
                    +TRANSACTION_INTERVAL+" INTEGER, "
                    +TRANSACTION_END_DATE+" TEXT, "
                    +TRANSACTION_TYPE+" TEXT NOT NULL,  "
                    +TRANSACTION_EDIT+" INTEGER, "
                    +TRANSACTION_ADD+" INTEGER, "
                    +TRANSACTION_DELETE+" INTEGER);";

    private static final String TRANSACTION_TABLE_DROP="DROP TABLE IF EXISTS "+TRANSACTION_TABLE;


    //BUDGET DB

    public static final String ACCOUNT_TABLE="account";
    public static final String ACCOUNT_ID="id";
    public static final String ACCOUNT_BUDGET="budget";
    public static final String ACCOUNT_TOTAL_LIMIT="totalLimit";
    public static final String ACCOUNT_MONTH_LIMIT="monthLimit";
    private static final String ACCOUNT_TABLE_CREATE=
            "CREATE TABLE IF NOT EXISTS "+ ACCOUNT_TABLE
                +" ("+ACCOUNT_ID+" INTEGER PRIMARY KEY, "
                +ACCOUNT_BUDGET+" REAL, "
                +ACCOUNT_TOTAL_LIMIT+" REAL, "
                +ACCOUNT_MONTH_LIMIT+" REAL);";

    private static final String ACCOUNT_TABLE_DROP="DROP TABLE IF EXISTS "+ACCOUNT_TABLE;



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRANSACTION_TABLE_CREATE);
        db.execSQL(ACCOUNT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TRANSACTION_TABLE_DROP);
        db.execSQL(ACCOUNT_TABLE_DROP);
        onCreate(db);
    }
}
