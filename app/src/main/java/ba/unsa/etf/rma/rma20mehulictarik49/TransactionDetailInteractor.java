package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class TransactionDetailInteractor extends AsyncTask<String, Integer, Void> {

    private Transaction t;
    private int method;

    private static final int ADD=0;
    private static final int EDIT=1;
    private static final int DELETE=2;
    private static final int UPDATE=3;



    private TransactionDBOpenHelper transactionDBOpenHelper;
    SQLiteDatabase database;
    private Transaction transaction;
    private Context context;
    private OnGetTransaction caller;

    public TransactionDetailInteractor() {

    }

    public interface OnGetTransaction{
        void onDone(Transaction transaction) throws InterruptedException;
    }

    public TransactionDetailInteractor(OnGetTransaction caller,Transaction t,int method){
        this.caller=caller;
        this.t=t;
        this.method=method;
        this.context=null;
        //myb method
    }
    public TransactionDetailInteractor(OnGetTransaction caller,Transaction t,int method,Context context){
        this.caller=caller;
        this.t=t;
        this.method=method;
        this.context=context;
        //myb method
    }


    Map<String,Integer> types;


    @Override
    protected Void doInBackground(String... strings) {
//        String api_key="718985c0-bf29-4179-8083-6f89d651e198";
//        String link="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/"+ api_key+"/transactions";
        String typeslink="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/transactionTypes";

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
                types.put(type.getString("name"),type.getInt("id"));
            }
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (method){
            case ADD:
                addTransaction(t);
                break;
            case EDIT:
                editTransaction(t);
                break;
            case DELETE:
                deleteTransaction(t);
                break;
            case UPDATE:
                updateTransactions();
                break;
        }

        return null;


//        if(method==ADD){
//            try {
//                Log.d(TAG, "doInBackground: METHOD ADD");
//                URL url=new URL(link);
//                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                urlConnection.setRequestProperty("Accept", "application/json");
//                urlConnection.setDoOutput(true);
//                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
//                String date=sdf.format(t.getDate().getTime());
//                String endDate=null;
//                if(t.getEndDate()!=null){
//                    endDate=sdf.format(t.getEndDate().getTime());
//                    endDate="\""+endDate+"\"";
//                }
//                String interval=null;
//                if(t.getTransactionInterval()!=0){
//                    interval=String.valueOf(t.getTransactionInterval());
//                }
//                int typeID=getTypeID(t.getType());
//                String output="{\"title\":\""+t.getTitle()+"\",\"amount\":"+t.getAmount()+",\"itemDescription\":\""+t.getItemDescription()+"\",\"date\":\""+date+
//                        "\",\"endDate\":"+endDate+",\"typeId\":"+typeID+",\"transactionInterval\":"+interval+"}";
//                try(OutputStream os = urlConnection.getOutputStream()) {
//                    byte[] input = output.getBytes("utf-8");
//                    os.write(input, 0, input.length);
//                }
//                try(BufferedReader br = new BufferedReader(
//                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
//                    StringBuilder response = new StringBuilder();
//                    String responseLine = null;
//                    while ((responseLine = br.readLine()) != null) {
//                        response.append(responseLine.trim());
//                    }
//                    System.out.println(response.toString());
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }else if(method==EDIT){
//            link+="/"+ t.getId();
//            try {
//                URL url = new URL(link);
//                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestProperty("Accept", "application/json");
//                urlConnection.setDoOutput(true);
//                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
//                String date=sdf.format(t.getDate().getTime());
//                String endDate=null;
//                if(t.getEndDate()!=null){
//                    endDate=sdf.format(t.getEndDate().getTime());
//                    endDate="\""+endDate+"\"";
//                }
//                String interval=null;
//                if(t.getTransactionInterval()!=0){
//                    interval=String.valueOf(t.getTransactionInterval());
//                }
//                int typeID=getTypeID(t.getType());
//                String output="{\"title\":\""+t.getTitle()+"\",\"amount\":"+t.getAmount()+",\"itemDescription\":\""+t.getItemDescription()+"\",\"date\":\""+date+
//                        "\",\"endDate\":"+endDate+",\"TransactionTypeId\":"+typeID+",\"transactionInterval\":"+interval+"}";
//                try(OutputStream os = urlConnection.getOutputStream()) {
//                    byte[] input = output.getBytes("utf-8");
//                    os.write(input, 0, input.length);
//                }
//                try(BufferedReader br = new BufferedReader(
//                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
//                    StringBuilder response = new StringBuilder();
//                    String responseLine = null;
//                    while ((responseLine = br.readLine()) != null) {
//                        response.append(responseLine.trim());
//                    }
//                    System.out.println(response.toString());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }else if(method==DELETE){
//            link+="/"+t.getId();
//            try {
//                URL url = new URL(link);
//                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("DELETE");
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestProperty("Accept", "application/json");
//                urlConnection.setDoOutput(true);
//                try(BufferedReader br = new BufferedReader(
//                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
//                    StringBuilder response = new StringBuilder();
//                    String responseLine = null;
//                    while ((responseLine = br.readLine()) != null) {
//                        response.append(responseLine.trim());
//                    }
//                    System.out.println(response.toString());
//                }
//            } catch (MalformedURLException | ProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
    }

    private void addTransaction(Transaction t){
        String api_key="718985c0-bf29-4179-8083-6f89d651e198";
        String link="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/"+ api_key+"/transactions";
        try {
            Log.d(TAG, "doInBackground: METHOD ADD");
            URL url=new URL(link);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            String date=sdf.format(t.getDate().getTime());
            String endDate=null;
            if(t.getEndDate()!=null){
                endDate=sdf.format(t.getEndDate().getTime());
                endDate="\""+endDate+"\"";
            }
            String interval=null;
            if(t.getTransactionInterval()!=0){
                interval=String.valueOf(t.getTransactionInterval());
            }
            int typeID=getTypeID(t.getType());
            String output="{\"title\":\""+t.getTitle()+"\",\"amount\":"+t.getAmount()+",\"itemDescription\":\""+t.getItemDescription()+"\",\"date\":\""+date+
                    "\",\"endDate\":"+endDate+",\"typeId\":"+typeID+",\"transactionInterval\":"+interval+"}";
            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = output.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editTransaction(Transaction t){
        String api_key="718985c0-bf29-4179-8083-6f89d651e198";
        String link="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/"+ api_key+"/transactions";
        link+="/"+ t.getId();
        try {
            URL url = new URL(link);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            String date=sdf.format(t.getDate().getTime());
            String endDate=null;
            if(t.getEndDate()!=null){
                endDate=sdf.format(t.getEndDate().getTime());
                endDate="\""+endDate+"\"";
            }
            String interval=null;
            if(t.getTransactionInterval()!=0){
                interval=String.valueOf(t.getTransactionInterval());
            }
            int typeID=getTypeID(t.getType());
            String output="{\"title\":\""+t.getTitle()+"\",\"amount\":"+t.getAmount()+",\"itemDescription\":\""+t.getItemDescription()+"\",\"date\":\""+date+
                    "\",\"endDate\":"+endDate+",\"TransactionTypeId\":"+typeID+",\"transactionInterval\":"+interval+"}";
            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = output.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteTransaction(Transaction t){
        String api_key="718985c0-bf29-4179-8083-6f89d651e198";
        String link="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/"+ api_key+"/transactions";
        link+="/"+t.getId();
        try {
            URL url = new URL(link);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTransactions(){
        Cursor cursor=new TransactionListInteractor().getTransactionCursor(context);
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
        while (cursor.moveToNext()){
            Calendar date=convertStringToDate(cursor.getString(datePos));
            Calendar endDate=convertStringToDate(cursor.getString(endPos));
            TransactionType type=getType(cursor.getString(typePos));
            Transaction transaction=new Transaction(date,cursor.getDouble(amountPos),cursor.getString(titlePos),
                    type,cursor.getString(descPos),cursor.getInt(intPos),endDate,cursor.getInt(idPos),
                    cursor.getInt(internalId),cursor.getInt(addPos),cursor.getInt(editPos),cursor.getInt(deletePos));
            System.out.println(transaction.getTitle());
            System.out.println(transaction.getAdd());
            System.out.println(transaction.getEdit());
            System.out.println(transaction.getDelete());
            if(transaction.getAdd()==1 && transaction.getDelete()!=1){
                addTransaction(transaction);
            }else if(transaction.getEdit()==1 && transaction.getDelete()!=1){
                editTransaction(transaction);
            }else if(transaction.getDelete()==1 && transaction.getAdd()!=1){
                deleteTransaction(transaction);
            }
        }
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri uri = Uri.parse("content://spiralaTM.provider.transactions/elements");

        cr.delete(uri, null, null);

    }

    private int getTypeID(TransactionType type) {
        if(type==TransactionType.INDIVIDUALINCOME){
            return types.get("Individual income");
        }else if(type==TransactionType.INDIVIDUALPAYMENT){
            return types.get("Individual payment");
        }else if(type==TransactionType.PURCHASE){
            return types.get("Purchase");
        }else if(type==TransactionType.REGULARPAYMENT){
            return types.get("Regular payment");
        }else if(type==TransactionType.REGULARINCOME){
            return types.get("Regular income");
        }
        return 0;
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            caller.onDone(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void add(Transaction transaction, Context context){
        ContentResolver cr=context.getApplicationContext().getContentResolver();
        Uri transactionsURI=Uri.parse("content://spiralaTM.provider.transactions/elements");
        ContentValues values=new ContentValues();
        values.put(TransactionDBOpenHelper.TRANSACTION_ID,transaction.getId());
        values.put(TransactionDBOpenHelper.TRANSACTION_DATE,convertDateToString(transaction.getDate()));
        values.put(TransactionDBOpenHelper.TRANSACTION_TITLE,transaction.getTitle());
        values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT,transaction.getAmount());
        values.put(TransactionDBOpenHelper.TRANSACTION_DESCRIPTION,transaction.getItemDescription());
        values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL,transaction.getTransactionInterval());
        values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE,convertDateToString(transaction.getEndDate()));
        values.put(TransactionDBOpenHelper.TRANSACTION_TYPE,getStringType(transaction.getType()));
        values.put(TransactionDBOpenHelper.TRANSACTION_ADD,1);
        values.put(TransactionDBOpenHelper.TRANSACTION_EDIT,0);
        values.put(TransactionDBOpenHelper.TRANSACTION_DELETE,0);
        cr.insert(transactionsURI,values);
    }

    public void edit(Transaction transaction, Context context){

        Transaction transaction1=getTransaction(context,transaction.getInternalId());


        if(transaction1==null){  //Znaci da ova ne postoji u bazi, te je potrebno dodati je sa edit oznakom
            ContentResolver cr=context.getApplicationContext().getContentResolver();
            ContentValues values=new ContentValues();
            Uri transactionsURI=Uri.parse("content://spiralaTM.provider.transactions/elements");
            values.put(TransactionDBOpenHelper.TRANSACTION_ID,transaction.getId());
            values.put(TransactionDBOpenHelper.TRANSACTION_DATE,convertDateToString(transaction.getDate()));
            values.put(TransactionDBOpenHelper.TRANSACTION_TITLE,transaction.getTitle());
            values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT,transaction.getAmount());
            values.put(TransactionDBOpenHelper.TRANSACTION_DESCRIPTION,transaction.getItemDescription());
            values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL,transaction.getTransactionInterval());
            values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE,convertDateToString(transaction.getEndDate()));
            values.put(TransactionDBOpenHelper.TRANSACTION_TYPE,getStringType(transaction.getType()));
            values.put(TransactionDBOpenHelper.TRANSACTION_ADD,0);
            values.put(TransactionDBOpenHelper.TRANSACTION_EDIT,1);
            values.put(TransactionDBOpenHelper.TRANSACTION_DELETE,0);
            cr.insert(transactionsURI,values);
        }else{
            ContentResolver cr=context.getApplicationContext().getContentResolver();
            ContentValues values=new ContentValues();
            Uri transactionsURI=Uri.parse("content://spiralaTM.provider.transactions/elements");
            values.put(TransactionDBOpenHelper.TRANSACTION_ID,transaction.getId());
            values.put(TransactionDBOpenHelper.TRANSACTION_DATE,convertDateToString(transaction.getDate()));
            values.put(TransactionDBOpenHelper.TRANSACTION_TITLE,transaction.getTitle());
            values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT,transaction.getAmount());
            values.put(TransactionDBOpenHelper.TRANSACTION_DESCRIPTION,transaction.getItemDescription());
            values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL,transaction.getTransactionInterval());
            values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE,convertDateToString(transaction.getEndDate()));
            values.put(TransactionDBOpenHelper.TRANSACTION_TYPE,getStringType(transaction.getType()));
            values.put(TransactionDBOpenHelper.TRANSACTION_EDIT,1);
            String where = TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID+"="+transaction.getInternalId();
            cr.update(transactionsURI,values,where,null);
        }

    }

    public void delete(Transaction transaction,Context context){
        Transaction transaction1=getTransaction(context,transaction.getInternalId());
        if(transaction1==null){         //Znaci da ova ne postoji u bazi, te je potrebno dodati je sa delete oznakom

            ContentResolver cr=context.getApplicationContext().getContentResolver();
            Uri transactionsURI=Uri.parse("content://spiralaTM.provider.transactions/elements");
            ContentValues values=new ContentValues();
            values.put(TransactionDBOpenHelper.TRANSACTION_ID,transaction.getId());
            values.put(TransactionDBOpenHelper.TRANSACTION_DATE,convertDateToString(transaction.getDate()));
            values.put(TransactionDBOpenHelper.TRANSACTION_TITLE,transaction.getTitle());
            values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT,transaction.getAmount());
            values.put(TransactionDBOpenHelper.TRANSACTION_DESCRIPTION,transaction.getItemDescription());
            values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL,transaction.getTransactionInterval());
            values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE,convertDateToString(transaction.getEndDate()));
            values.put(TransactionDBOpenHelper.TRANSACTION_TYPE,getStringType(transaction.getType()));
            values.put(TransactionDBOpenHelper.TRANSACTION_ADD,0);
            values.put(TransactionDBOpenHelper.TRANSACTION_EDIT,0);
            values.put(TransactionDBOpenHelper.TRANSACTION_DELETE,1);
            cr.insert(transactionsURI,values);
        }else{

            ContentResolver cr=context.getApplicationContext().getContentResolver();
            Uri transactionsURI=Uri.parse("content://spiralaTM.provider.transactions/elements");
            ContentValues values=new ContentValues();
            values.put(TransactionDBOpenHelper.TRANSACTION_DELETE,transaction.getDelete());
            String where=TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID+"="+transaction1.getInternalId();
            cr.update(transactionsURI,values,where,null);
        }
    }

    public Transaction getTransaction(Context context, Integer id){
        ContentResolver cr=context.getApplicationContext().getContentResolver();
        String [] kolone=null;
        Uri adresa= ContentUris.withAppendedId(Uri.parse("content://spiralaTM.provider.transactions/elements"),id);
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Transaction transaction=null;
        Cursor cursor = cr.query(adresa, kolone, where, whereArgs, order);
        if(cursor!=null && id!=0){
            cursor.moveToFirst();
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
            transaction=new Transaction(date,cursor.getDouble(amountPos),cursor.getString(titlePos),
                    type,cursor.getString(descPos),cursor.getInt(intPos),endDate,cursor.getInt(idPos),
                    cursor.getInt(internalId),cursor.getInt(addPos),cursor.getInt(editPos),cursor.getInt(deletePos));
        }
        cursor.close();
        return transaction;
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
