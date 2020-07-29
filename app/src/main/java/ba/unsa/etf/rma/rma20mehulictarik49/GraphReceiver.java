package ba.unsa.etf.rma.rma20mehulictarik49;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class GraphReceiver extends ResultReceiver {

    private Receiver receiver;
    public interface Receiver{
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver){
        this.receiver=receiver;
    }

    public GraphReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(receiver!=null){
            receiver.onReceiveResult(resultCode,resultData);
        }
    }


}
