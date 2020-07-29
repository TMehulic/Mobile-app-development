package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class AccountPresenter implements AccountInteractor.OnAccountGetDone, IAccountPresenter{

    private Context context;
    private AccountInfo view;
    private int method;
    private static final int ADDORDELETE=0;
    private AccountInteractor accountInteractor;

    public AccountPresenter(AccountInfo view, Context context) {
        this.context = context;
        this.view=view;
        method=1;
        accountInteractor=new AccountInteractor();
    }


    public void updateBudget(Account account,double newBudget, int method){
        this.method=method;
        if(ConnectivityBroadcastReceiver.isConnected()){
            new AccountInteractor(this,account,2).execute(String.valueOf(newBudget));
        }else {
            Account account1=getAccountDB();
            double novi=account.getBudget()+newBudget;
            if(account1==null){ //dodaj ovaj
                ContentResolver cr=context.getApplicationContext().getContentResolver();
                ContentValues values=new ContentValues();
                Uri transactionsURI=Uri.parse("content://spiralaTM.provider.account/elements");
                values.put(TransactionDBOpenHelper.ACCOUNT_ID,account.getId());
                values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET,novi);
                values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,account.getMonthLimit());
                values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,account.getTotalLimit());
                cr.insert(transactionsURI,values);
            }else{      //update stari
                ContentResolver cr=context.getApplicationContext().getContentResolver();
                ContentValues values=new ContentValues();
                Uri transactionsURI=Uri.parse("content://spiralaTM.provider.account/elements");
                values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET,novi);
                String where= TransactionDBOpenHelper.ACCOUNT_ID+"="+account.getId();
                cr.update(transactionsURI,values,where,null);
            }
            account.setBudget(novi);
            onDone(account);
        }
    }

    public void updateAccount(Account account){
        if(ConnectivityBroadcastReceiver.isConnected()){
            new AccountInteractor(this,account,1).execute();
        }else{
            Account account1=getAccountDB();
            if(account1==null){ //dodaj ovaj
                ContentResolver cr=context.getApplicationContext().getContentResolver();
                ContentValues values=new ContentValues();
                Uri transactionsURI=Uri.parse("content://spiralaTM.provider.account/elements");
                values.put(TransactionDBOpenHelper.ACCOUNT_ID,account.getId());
                values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET,account.getBudget());
                values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,account.getMonthLimit());
                values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,account.getTotalLimit());
                cr.insert(transactionsURI,values);
            }else{      //update stari
                ContentResolver cr=context.getApplicationContext().getContentResolver();
                ContentValues values=new ContentValues();
                Uri transactionsURI=Uri.parse("content://spiralaTM.provider.account/elements");
                values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,account.getMonthLimit());
                values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,account.getTotalLimit());
                String where= TransactionDBOpenHelper.ACCOUNT_ID+"="+account.getId();
                cr.update(transactionsURI,values,where,null);
            }
            onDone(account);
        }

    }

    public void getAccount(){
        if(ConnectivityBroadcastReceiver.isConnected()){
            new AccountInteractor(this,null,0).execute();
        }else {
            Account account=getAccountDB();
            onDone(account);
        }

    }

    @Override
    public void onDone(Account account) {
        if(getAccountDB()==null && account!=null){      //Nije još uvijek u bazi
            ContentResolver cr=context.getApplicationContext().getContentResolver();
            ContentValues values=new ContentValues();
            Uri transactionsURI=Uri.parse("content://spiralaTM.provider.account/elements");
            values.put(TransactionDBOpenHelper.ACCOUNT_ID,account.getId());
            values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET,account.getBudget());
            values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,account.getMonthLimit());
            values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,account.getTotalLimit());
            cr.insert(transactionsURI,values);
        }else{
            ContentResolver cr=context.getApplicationContext().getContentResolver();
            ContentValues values=new ContentValues();
            Uri transactionsURI=Uri.parse("content://spiralaTM.provider.account/elements");
            values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET,account.getBudget());
            values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,account.getMonthLimit());
            values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,account.getTotalLimit());
            String where= TransactionDBOpenHelper.ACCOUNT_ID+"="+account.getId();
            cr.update(transactionsURI,values,where,null);
        }
        if(method!=ADDORDELETE){
            view.setAccount(account);
        }
    }

    public Account getAccountDB() {
        Cursor cursor=accountInteractor.getAccountCursor(context);
        Account account=null;
        if(cursor.getCount()!=0){
            cursor.moveToFirst();
            int idPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_ID);
            int budgetPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_BUDGET);
            int monthPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT);
            int totalPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT);
            account=new Account(cursor.getDouble(budgetPos),cursor.getDouble(totalPos),cursor.getDouble(monthPos),cursor.getInt(idPos));
            cursor.close();
        }
       return account;

    }
}
