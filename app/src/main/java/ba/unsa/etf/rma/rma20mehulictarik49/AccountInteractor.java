package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

public class AccountInteractor extends AsyncTask<String, Integer, Void>{

    private Account account;
    private double budget;

    private OnAccountGetDone caller;
    public interface OnAccountGetDone{
        void onDone(Account account);
    }

    public final static int GET=0;
    public final static int POST=1;
    public final static int BUDGET=2;
    private int method;

    public AccountInteractor(OnAccountGetDone caller, Account account, int method){
        this.caller=caller;
        this.account=account;
        this.method=method;
    }

    public AccountInteractor(){

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
    protected Void doInBackground(String... strings) {
        String api_key="718985c0-bf29-4179-8083-6f89d651e198";
        String link="http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/"+ api_key;

        if(method==GET){
            try {
                URL url=new URL(link);
                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                String result=convertStreamToString(in);
                JSONObject acc=new JSONObject(result);
                double budget=acc.getDouble("budget");
                double totalLimit=acc.getDouble("totalLimit");
                double monthLimit=acc.getDouble("monthLimit");
                int id=acc.getInt("id");
                System.out.println(budget+" "+totalLimit+" "+monthLimit);
                account=new Account(budget,totalLimit,monthLimit,id);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(method==POST){
            try {
                URL url=new URL(link);
                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                String output="{\"monthLimit\":"+ account.getMonthLimit() +",\"totalLimit\":"+account.getTotalLimit()+",\"budget\":"+account.getBudget()+"}";
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method==BUDGET){
            try {
                Log.d(TAG, "doInBackground: method=budget");
                String query=null;
                query=URLEncoder.encode(strings[0],"utf-8");
                double newBudget=Double.parseDouble(query);
                URL url=new URL(link);
                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                String result=convertStreamToString(in);
                JSONObject acc=new JSONObject(result);
                double budget=acc.getDouble("budget")+newBudget;
                urlConnection.disconnect();
                HttpURLConnection urlConnection1= (HttpURLConnection) url.openConnection();
                urlConnection1.setRequestMethod("POST");
                urlConnection1.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection1.setRequestProperty("Accept", "application/json");
                urlConnection1.setDoOutput(true);
                String output="{\"budget\":"+budget+"}";
                try(OutputStream os = urlConnection1.getOutputStream()) {
                    byte[] input = output.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection1.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
                account.setBudget(budget);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        caller.onDone(account);
    }

    public Cursor getAccountCursor(Context context){
        ContentResolver cr=context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.ACCOUNT_ID,
                TransactionDBOpenHelper.ACCOUNT_BUDGET,
                TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,
                TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT
        };
        Uri adresa=Uri.parse("content://spiralaTM.provider.account/elements");
        String where = null;
        String[] whereArgs = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }
}
