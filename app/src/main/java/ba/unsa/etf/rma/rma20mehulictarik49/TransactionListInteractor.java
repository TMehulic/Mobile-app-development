package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransactionListInteractor extends AsyncTask<String, Integer, Void>{

    private OnTransactionsGet caller;
    public interface OnTransactionsGet{
        void onDone(ArrayList<Transaction> transactions);
    }

    public TransactionListInteractor(OnTransactionsGet caller){
        this.caller=caller;
    }

    private ArrayList<Transaction> transactions;
    Map<Integer,String> types;

    private TransactionDBOpenHelper transactionDBOpenHelper;
    SQLiteDatabase database;
    private Transaction transaction;

    public TransactionListInteractor(){

    }

    @Override
    protected Void doInBackground(String... strings) {
        String api_key="718985c0-bf29-4179-8083-6f89d651e198";
        String link="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/"+ api_key+"/transactions?page=";
        String typeslink="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/transactionTypes";
        transactions=new ArrayList<>();
        types=new HashMap<>();

        try {
            URL url=new URL(typeslink);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            InputStream in=new BufferedInputStream(urlConnection.getInputStream());
            String result=convertStreamToString(in);
            JSONObject jo=new JSONObject(result);
            JSONArray results =jo.getJSONArray("rows");
            int count=jo.getInt("count");
            for(int i=0;i<count;i++){
                JSONObject type=results.getJSONObject(i);
                types.put(type.getInt("id"),type.getString("name"));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int i=0;
        JSONArray results = null;
        do{
            try {
                URL url=new URL(link+i);
                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                String result=convertStreamToString(in);
                JSONObject jo=new JSONObject(result);
                results=jo.getJSONArray("transactions");
                for(int j=0;j<results.length();j++){
                    JSONObject transaction=results.getJSONObject(j);
                    int id=transaction.getInt("id");
                    String title=transaction.getString("title");
                    double amount=transaction.getDouble("amount");
                    String itemDesc=transaction.getString("itemDescription");
                    if(itemDesc.equals("null")){
                        itemDesc="";
                    }
                    int interval;
                    try{
                        interval=transaction.getInt("transactionInterval");
                    }catch (Exception e){
                        interval=0;
                    }
                    int typeID=transaction.getInt("TransactionTypeId");
                    String date=transaction.getString("date");
                    String endDate=transaction.getString("endDate");
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    Date date1=sdf.parse(date);
                    Calendar c1=Calendar.getInstance();
                    c1.setTime(date1);
                    TransactionType tip=getType(typeID);
                    try{
                        Date endDate1=sdf.parse(endDate);
                        Calendar c2=Calendar.getInstance();
                        c2.setTime(endDate1);
                        Transaction t=new Transaction(id,c1,title,amount,itemDesc,interval,c2,tip);
                        t.setDelete(0);
                        t.setAdd(0);
                        t.setEdit(0);
                        transactions.add(t);
                    }catch (Exception e){
                        Transaction t=new Transaction(id,c1,title,amount,itemDesc,interval,null,tip);
                        t.setDelete(0);
                        t.setAdd(0);
                        t.setEdit(0);
                        transactions.add(t);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            i++;
        }while (results.length()!=0);

        return null;
    }


    private TransactionType getType(int typeID) {
        if(types.containsKey(typeID)){
            if(types.get(typeID).equals("Regular payment")){
                return TransactionType.REGULARPAYMENT;
            }else if(types.get(typeID).equals("Regular income")){
                return TransactionType.REGULARINCOME;
            }else if(types.get(typeID).equals("Purchase")){
                return TransactionType.PURCHASE;
            }else if(types.get(typeID).equals("Individual income")){
                return TransactionType.INDIVIDUALINCOME;
            }else{
                return TransactionType.INDIVIDUALPAYMENT;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        System.out.println("ovdje3");
        caller.onDone(transactions);
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    public Cursor getTransactionCursor(Context context){
        ContentResolver cr=context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_ID,
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID,
                TransactionDBOpenHelper.TRANSACTION_TITLE,
                TransactionDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionDBOpenHelper.TRANSACTION_DATE,
                TransactionDBOpenHelper.TRANSACTION_END_DATE,
                TransactionDBOpenHelper.TRANSACTION_DESCRIPTION,
                TransactionDBOpenHelper.TRANSACTION_INTERVAL,
                TransactionDBOpenHelper.TRANSACTION_TYPE,
                TransactionDBOpenHelper.TRANSACTION_EDIT,
                TransactionDBOpenHelper.TRANSACTION_ADD,
                TransactionDBOpenHelper.TRANSACTION_DELETE
        };
        Uri adresa=Uri.parse("content://spiralaTM.provider.transactions/elements");
        String where = null;
        String[] whereArgs = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }


}
