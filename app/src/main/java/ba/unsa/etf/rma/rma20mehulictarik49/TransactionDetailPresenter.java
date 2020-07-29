package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class TransactionDetailPresenter  implements ITransactionDetailPresenter, TransactionDetailInteractor.OnGetTransaction, TransactionListInteractor.OnTransactionsGet {

    private Context context;
    private Transaction transaction=null;
    private ArrayList<Transaction> transactions=new ArrayList<>();
    private IDetailView view;
    private TransactionDetailInteractor transactionDetailInteractor;
    private int method;

    public TransactionDetailPresenter(Context context, IDetailView view){
        this.context=context;
        this.view=view;
        transactionDetailInteractor=new TransactionDetailInteractor();
    }



    public void setTransaction(Transaction transaction){
        this.transaction=transaction;
    }

    public Transaction getTransaction(){
        return transaction;
    }

    public Transaction getDatabaseTransaction(int id){
        transaction=new TransactionDetailInteractor().getTransaction(context,id);
        return transaction;
    }


   public void editTransactionDB(Transaction newTransaction){
        transaction=newTransaction;
        method=1;
        transactionDetailInteractor.edit(transaction,context);
        onDone(transaction);
    }

    public void addTransactionDB(Transaction transaction){
        method=0;
        transactionDetailInteractor.add(transaction,context);
        onDone(transaction);
    }

    public void deleteTransactionDB(){
        method=2;
        transactionDetailInteractor.delete(transaction,context);
        transaction=null;
        onDone(transaction);
    }

    public void updateTransactions(){
        method=3;
        new TransactionDetailInteractor(this,null,3,context).execute();
    }


    @Override
    public void editTransaction(Transaction newTransaction){
        transaction=newTransaction;
        method=1;
        new TransactionDetailInteractor(this,transaction,1).execute();
    }

    public void deleteTransaction(){
        method=2;
        new TransactionDetailInteractor(this,transaction,2).execute();
        transaction=null;
    }

    public void addTransaction(Transaction transaction){
        method=0;
        new TransactionDetailInteractor(this,transaction,0).execute();
    }





    public void getTransactions(){
        new TransactionListInteractor(this).execute();
    }

    public double getSumOfTransactionsByMonth(String date){
        double sum=0;
        Date date1;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/mm/yyyy",Locale.getDefault());
        try {
            date1=sdf1.parse(date);
        }catch (ParseException e){
            try {
                date1=sdf2.parse(date);
            } catch (ParseException ex) {
                return 0;
            }
        }
        calendar.setTime(date1);
        //Ako je ova null, Add transaction
        if(transaction==null){
            for(Transaction t:transactions){
                if(t.getType()==TransactionType.REGULARPAYMENT || t.getType()==TransactionType.REGULARINCOME){
                    if(calendar.after(t.getDate()) && calendar.before(t.getEndDate())){
                        if(t.getType()==TransactionType.REGULARINCOME){
                            sum+=t.getAmount();
                        }else{
                            sum-=t.getAmount();
                        }
                    }
                }else{
                    if(calendar.get(Calendar.MONTH)==t.getDate().get(Calendar.MONTH)){
                        if(t.getType()==TransactionType.INDIVIDUALINCOME){
                            sum+=t.getAmount();
                        }else{
                            sum-=t.getAmount();
                        }
                    }
                }
            }
        }else{
            for(Transaction t:transactions){
                if(t.getType()==TransactionType.REGULARPAYMENT || t.getType()==TransactionType.REGULARINCOME){
                    if(calendar.before(t.getEndDate()) && !t.getTitle().equals(transaction.getTitle())){
                        if(t.getType()==TransactionType.REGULARINCOME){
                            sum+=t.getAmount();
                        }else{
                            sum-=t.getAmount();
                        }
                    }
                }else{
                    if((calendar.get(Calendar.MONTH)==t.getDate().get(Calendar.MONTH)) && !t.getTitle().equals(transaction.getTitle())){
                        if(t.getType()==TransactionType.INDIVIDUALINCOME){
                            sum+=t.getAmount();
                        }else{
                            sum-=t.getAmount();
                        }
                    }
                }
            }
        }
        return sum;
    }

    public double getSumOfAllTransactions(){
        double sum=0;
        //Ako je ova null, Add transaction

        if(transaction==null){
            for(Transaction t:transactions){
                if(t.getType()==TransactionType.INDIVIDUALINCOME){
                    sum+=t.getAmount();
                }else if(t.getType()==TransactionType.PURCHASE || t.getType()==TransactionType.INDIVIDUALPAYMENT){
                    sum-=t.getAmount();
                }else{
                    addRegular(t, sum);
                }
            }
        }else{
            for(Transaction t:transactions){
                if(!t.getTitle().equals(transaction.getTitle())){
                    if(t.getType()==TransactionType.INDIVIDUALINCOME){
                        sum+=t.getAmount();
                    }else if(t.getType()==TransactionType.PURCHASE || t.getType()==TransactionType.INDIVIDUALPAYMENT){
                        sum-=t.getAmount();
                    }else{
                        addRegular(t, sum);
                    }
                }
            }
        }
        return sum;
    }

    private void addRegular(Transaction t, double sum) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(t.getDate().getTime());
        while (calendar.before(t.getEndDate())){
            if(t.getType()==TransactionType.REGULARINCOME){
                sum+=t.getAmount();
            }else{
                sum-=t.getAmount();
            }
            calendar.add(Calendar.DATE,t.getTransactionInterval());
        }
    }

    /* Ne koristi se jer se sad transakcije razliku svakako po ID */

    public boolean checkIfTitleExist(String title){

//        if(transaction==null){
//            for(Transaction t:interactor.get()){
//                if(t.getTitle().equals(title)){
//                    return true;
//                }
//            }
//        }else{
//            for(Transaction t:interactor.get()){
//                if(!t.getTitle().equals(transaction.getTitle()) && t.getTitle().equals(title)){
//                    return true;
//                }
//            }
//        }
        return false;
    }

    @Override
    public void onDone(Transaction transaction) {
        if(method==0){
            Log.d(TAG, "onDone: ide view.onAdd");
            view.onAdd();
        }else if(method==1){
            view.onEdit();
        }else if(method==2){
            view.onDelete();
        }else if(method==3){
            view.onUpdated();
        }
    }

    @Override
    public void onDone(ArrayList<Transaction> transactions) {
        this.transactions=transactions;
    }
}
