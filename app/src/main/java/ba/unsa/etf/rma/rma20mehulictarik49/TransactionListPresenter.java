package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static java.util.Calendar.YEAR;

public class TransactionListPresenter implements TransactionListInteractor.OnTransactionsGet, ITransactionListPresenter {

    private ITransactionListView view;
    private Context context;
    private Calendar calendar;
    private String filter;
    private String sort;
    private TransactionListInteractor transactionListInteractor;

    public TransactionListPresenter(ITransactionListView view, Context context) {
        this.view = view;
        this.context = context;
        transactionListInteractor=new TransactionListInteractor();
    }




    public void filterTransactions(Calendar calendar, String filter, String sort, boolean isConnected) {
        this.calendar=calendar;
        this.filter=filter;
        this.sort=sort;
        if(isConnected){
            new TransactionListInteractor(this).execute();
        }else{
            //ovdje iz baze naci transakcije i pozvati onDone!!!
            Cursor cursor=transactionListInteractor.getTransactionCursor(context);
            ArrayList<Transaction> transactions=new ArrayList<>();
            while (cursor.moveToNext()){
                int idPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ID);
                int internalId=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID);
                int datePos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DATE);
                int amountPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT);
                int titlePos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE);
                int descPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DESCRIPTION);
                int intPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERVAL);
                int endPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_END_DATE);
                int typePos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE);
                int addPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ADD);
                int editPos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_EDIT);
                int deletePos=cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DELETE);
                Calendar date=convertStringToDate(cursor.getString(datePos));
                Calendar endDate=convertStringToDate(cursor.getString(endPos));
                TransactionType type=getType(cursor.getString(typePos));
                Transaction transaction=new Transaction(date,cursor.getDouble(amountPos),cursor.getString(titlePos),
                        type,cursor.getString(descPos),cursor.getInt(intPos),endDate,cursor.getInt(idPos),
                        cursor.getInt(internalId),cursor.getInt(addPos),cursor.getInt(editPos),cursor.getInt(deletePos));
                transactions.add(transaction);
            }
            cursor.close();
            onDone(transactions);
        }

    }

    private TransactionType getType(String type){
        if(type.equals("Regular payment")){
            return TransactionType.REGULARPAYMENT;
        }else if(type.equals("Regular income")){
            return TransactionType.REGULARINCOME;
        }else if(type.equals("Purchase")){
            return TransactionType.PURCHASE;
        }else if(type.equals("Individual income")){
            return TransactionType.INDIVIDUALINCOME;
        }else if(type.equals("Individual payment")){
            return TransactionType.INDIVIDUALPAYMENT;
        }
        return null;
    }

    private String getStringType(TransactionType type){
        if(type==TransactionType.REGULARPAYMENT){
            return "Regular payment";
        }else if(type==TransactionType.REGULARINCOME){
            return "Regular income";
        }else if(type==TransactionType.PURCHASE){
            return "Purchase";
        }else if(type==TransactionType.INDIVIDUALINCOME){
            return "Individual income";
        }else if(type==TransactionType.INDIVIDUALPAYMENT){
            return "Individual payment";
        }
        return null;
    }

    public Calendar convertStringToDate(String date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        Date date1= null;
        if(date==null) return null;
        try {
            date1 = sdf.parse(date);
            Calendar c1=Calendar.getInstance();
            c1.setTime(date1);
            return c1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String convertDateToString(Calendar date){
        if(date!=null){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            return sdf.format(date.getTime());
        }
        return null;
    }

    @Override
    public void onDone(ArrayList<Transaction> transactions) {
        ArrayList<Transaction> newTransactions=new ArrayList<>();
        //Filter
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        System.out.println(calendar.getTime());
        for(Transaction t:transactions){
            if(t.getType()==TransactionType.REGULARINCOME && (filter.equals("Regular income") || filter.equals("Filter by") || filter.equals("All transactions"))){
                Calendar c1=Calendar.getInstance();
                c1.setTime(t.getDate().getTime());
                if(calendar.get(YEAR)>t.getDate().get(YEAR) && calendar.get(YEAR)<=t.getEndDate().get(YEAR)){
                    while (c1.get(YEAR)<calendar.get(YEAR)){
                        c1.add(Calendar.DATE,t.getTransactionInterval());
                    }
                }
                if(!(calendar.get(Calendar.YEAR)==c1.get(Calendar.YEAR) && calendar.get(Calendar.MONTH)==c1.get(Calendar.MONTH))){
                    while (c1.before(calendar)){
                        c1.add(Calendar.DATE,t.getTransactionInterval());
                    }
                }
                while (c1.get(Calendar.MONTH)==calendar.get(Calendar.MONTH) && c1.before(t.getEndDate())){
                    newTransactions.add(t);
                    c1.add(Calendar.DATE,t.getTransactionInterval());
                }
            }else if(t.getType()==TransactionType.REGULARPAYMENT && (filter.equals("Regular payment") || filter.equals("Filter by") || filter.equals("All transactions"))){
                Calendar c1=Calendar.getInstance();
                c1.setTime(t.getDate().getTime());
                if(calendar.get(YEAR)>t.getDate().get(YEAR) && calendar.get(YEAR)<=t.getEndDate().get(YEAR)){
                    while (c1.get(YEAR)<calendar.get(YEAR)){
                        c1.add(Calendar.DATE,t.getTransactionInterval());
                    }
                }
                if(!(calendar.get(Calendar.YEAR)==c1.get(Calendar.YEAR) && calendar.get(Calendar.MONTH)==c1.get(Calendar.MONTH))){
                    while (c1.before(calendar)){
                        c1.add(Calendar.DATE,t.getTransactionInterval());
                    }
                }
                while (c1.get(Calendar.MONTH)==calendar.get(Calendar.MONTH) && c1.before(t.getEndDate())){
                    newTransactions.add(t);
                    c1.add(Calendar.DATE,t.getTransactionInterval());
                }
            }else if(t.getType()==TransactionType.PURCHASE && (filter.equals("Purchase")|| filter.equals("Filter by") || filter.equals("All transactions"))){
                if(calendar.get(Calendar.MONTH)==t.getDate().get(Calendar.MONTH) && calendar.get(YEAR)==t.getDate().get(YEAR)){
                    newTransactions.add(t);
                }
            }else if(t.getType()==TransactionType.INDIVIDUALINCOME && (filter.equals("Individual income") || filter.equals("Filter by") || filter.equals("All transactions"))){
                if(calendar.get(Calendar.MONTH)==t.getDate().get(Calendar.MONTH) && calendar.get(YEAR)==t.getDate().get(YEAR)){
                    newTransactions.add(t);
                }
            }else if(t.getType()==TransactionType.INDIVIDUALPAYMENT && (filter.equals("Individual payment") || filter.equals("Filter by") || filter.equals("All transactions"))){
                if(calendar.get(Calendar.MONTH)==t.getDate().get(Calendar.MONTH) && calendar.get(YEAR)==t.getDate().get(YEAR)){
                    newTransactions.add(t);
                }
            }
        }

        //Sort
        if(sort.equals("Price - Ascending")){
            Collections.sort(newTransactions, Transaction.Comparators.PRICEASC);
        }else if(sort.equals("Price - Descending")){
            Collections.sort(newTransactions, Transaction.Comparators.PRICEDESC);
        }else if(sort.equals("Title - Ascending")){
            Collections.sort(newTransactions, Transaction.Comparators.TITLEASC);
        }else if(sort.equals("Title - Descending")){
            Collections.sort(newTransactions, Transaction.Comparators.TITLEDESC);
        }else if(sort.equals("Date - Ascending")){
            Collections.sort(newTransactions, Transaction.Comparators.DATEASC);
        }else if(sort.equals("Date - Descending")){
            Collections.sort(newTransactions, Transaction.Comparators.DATEDESC);
        }

        view.setTransactions(newTransactions);
        view.notifyTransactionListDataSetChanged();
    }

    public void getTransactionsCursor() {
        view.setCursor(transactionListInteractor.getTransactionCursor(context.getApplicationContext()));
    }
}
