package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import java.util.Observable;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {

    public static boolean isConnected=false;
    private static boolean alreadyChanged=false;


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo()==null){
            isConnected=false;
            Toast.makeText(context,"Offline mode",Toast.LENGTH_LONG).show();
        }else{
            isConnected=true;
        }
        getObservable().connectionChanged();
        if(!alreadyChanged){
            alreadyChanged=true;
        }

    }

    public static class NetworkObservable extends Observable {
        private static NetworkObservable instance = null;

        private NetworkObservable() {
            // Exist to defeat instantiation.
        }

        public void connectionChanged(){
            setChanged();
            notifyObservers();
        }

        public static NetworkObservable getInstance(){
            if(instance == null){
                instance = new NetworkObservable();
            }
            return instance;
        }
    }

    public static NetworkObservable getObservable() {
        return NetworkObservable.getInstance();
    }

    public static boolean isConnected(){
        return isConnected;
    }
    public static boolean alreadyChanged(){return alreadyChanged;}

}
