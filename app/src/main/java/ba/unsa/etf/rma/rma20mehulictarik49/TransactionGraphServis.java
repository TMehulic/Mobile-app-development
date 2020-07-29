package ba.unsa.etf.rma.rma20mehulictarik49;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

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

public class TransactionGraphServis extends IntentService {

    public static final int STATUS_RUNNING=0;
    public static final int STATUS_FINISHED=1;
    public static final int STATUS_ERROR=2;
    private ArrayList<Transaction> transactions;
    Map<Integer,String> types;

    public TransactionGraphServis() {
        super(null);
    }

    public TransactionGraphServis(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String api_key="718985c0-bf29-4179-8083-6f89d651e198";
        String link="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/"+ api_key+"/transactions?page=";
        String typeslink="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/transactionTypes";
        transactions=new ArrayList<>();
        types=new HashMap<>();


        final ResultReceiver receiver=intent.getParcelableExtra("receiver");
        Bundle bundle=new Bundle();
        receiver.send(STATUS_RUNNING,Bundle.EMPTY);

        if(ConnectivityBroadcastReceiver.isConnected()){
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
                            transactions.add(t);
                        }catch (Exception e){
                            Transaction t=new Transaction(id,c1,title,amount,itemDesc,interval,null,tip);
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
        }else{
            Cursor cursor=new TransactionListInteractor().getTransactionCursor(getApplicationContext());
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
        }

        bundle.putParcelableArrayList("transactions",transactions);
        receiver.send(STATUS_FINISHED,bundle);

//        try {
//            URL url=new URL(typeslink);
//            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
//            InputStream in=new BufferedInputStream(urlConnection.getInputStream());
//            String result=convertStreamToString(in);
//            JSONObject jo=new JSONObject(result);
//            JSONArray results =jo.getJSONArray("rows");
//            int count=jo.getInt("count");
//            for(int i=0;i<count;i++){
//                JSONObject type=results.getJSONObject(i);
//                types.put(type.getInt("id"),type.getString("name"));
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        int i=0;
//        JSONArray results = null;
//        do{
//            try {
//                URL url=new URL(link+i);
//                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
//                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
//                String result=convertStreamToString(in);
//                JSONObject jo=new JSONObject(result);
//                results=jo.getJSONArray("transactions");
//                for(int j=0;j<results.length();j++){
//                    JSONObject transaction=results.getJSONObject(j);
//                    int id=transaction.getInt("id");
//                    String title=transaction.getString("title");
//                    double amount=transaction.getDouble("amount");
//                    String itemDesc=transaction.getString("itemDescription");
//                    int interval;
//                    try{
//                        interval=transaction.getInt("transactionInterval");
//                    }catch (Exception e){
//                        interval=0;
//                    }
//                    int typeID=transaction.getInt("TransactionTypeId");
//                    String date=transaction.getString("date");
//                    String endDate=transaction.getString("endDate");
//                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
//                    Date date1=sdf.parse(date);
//                    Calendar c1=Calendar.getInstance();
//                    c1.setTime(date1);
//                    TransactionType tip=getType(typeID);
//                    try{
//                        Date endDate1=sdf.parse(endDate);
//                        Calendar c2=Calendar.getInstance();
//                        c2.setTime(endDate1);
//                        Transaction t=new Transaction(id,c1,title,amount,itemDesc,interval,c2,tip);
//                        transactions.add(t);
//                    }catch (Exception e){
//                        Transaction t=new Transaction(id,c1,title,amount,itemDesc,interval,null,tip);
//                        transactions.add(t);
//                    }
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            i++;
//        }while (results.length()!=0);

//        bundle.putParcelableArrayList("transactions",transactions);
//        receiver.send(STATUS_FINISHED,bundle);

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
    public void onCreate() {
        super.onCreate();
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
}
