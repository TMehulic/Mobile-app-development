package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


public class TransactionListAdapter extends ArrayAdapter<Transaction>  {

    private int resource;

    static class ViewHolder{
        TextView title;
        TextView amount;
        ImageView icon;
    }


    public TransactionListAdapter(@NonNull Context context, int resource, List<Transaction> transactions) {
        super(context, resource, transactions);
        this.resource=resource;
    }

    public void setTransactions(ArrayList<Transaction> transactions){
        this.clear();
        this.addAll(transactions);
    }

    public Transaction getTransaction(int position){
        return this.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout newView;
        if(convertView==null){
            newView=new LinearLayout(getContext());
            String inflater=Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li= (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource,newView,true);
        }else{
            newView= (LinearLayout) convertView;
        }

        ViewHolder holder=new ViewHolder();
        Transaction transaction=getItem(position);

        holder.amount=newView.findViewById(R.id.listAmount);
        holder.title=newView.findViewById(R.id.listTitle);
        holder.icon=newView.findViewById(R.id.listIcon);

        StringBuilder builder=new StringBuilder();
        Formatter f=new Formatter(builder);
        f.format("%.2f",transaction.getAmount());

        holder.amount.setText(builder.toString());
        holder.title.setText(transaction.getTitle());
        holder.icon.setColorFilter(Color.argb(200,3,218,197), PorterDuff.Mode.SRC_IN);

        setIconByType(holder,transaction);

        return newView;
    }

    private void setIconByType(ViewHolder holder,Transaction transaction) {
        if (transaction.getType() == TransactionType.INDIVIDUALINCOME) {
            holder.icon.setImageResource(R.drawable.individual_income);
        } else if (transaction.getType() == TransactionType.INDIVIDUALPAYMENT) {
            holder.icon.setImageResource(R.drawable.individual_payment);
        } else if (transaction.getType() == TransactionType.PURCHASE) {
            holder.icon.setImageResource(R.drawable.purchase);
        } else if (transaction.getType() == TransactionType.REGULARINCOME) {
            holder.icon.setImageResource(R.drawable.regular_income);
        } else if (transaction.getType() == TransactionType.REGULARPAYMENT) {
            holder.icon.setImageResource(R.drawable.regular_payment);
        }
    }

}
