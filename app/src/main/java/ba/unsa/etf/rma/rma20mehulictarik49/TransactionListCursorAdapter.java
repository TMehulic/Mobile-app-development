package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.Formatter;

public class TransactionListCursorAdapter extends ResourceCursorAdapter {

    static class ViewHolder{
        TextView title;
        TextView amount;
        ImageView icon;
    }


    public TransactionListCursorAdapter(Context context, int layout, Cursor c, boolean autoRequery) {
        super(context, layout, c, autoRequery);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder=new ViewHolder();
//        Transaction transaction=getItem(position);

        holder.amount=view.findViewById(R.id.listAmount);
        holder.title=view.findViewById(R.id.listTitle);
        holder.icon=view.findViewById(R.id.listIcon);

        StringBuilder builder=new StringBuilder();
        Formatter f=new Formatter(builder);
        f.format("%.2f",cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT)));

        holder.amount.setText(builder.toString());
        holder.title.setText(cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE)));
        holder.icon.setColorFilter(Color.argb(200,3,218,197), PorterDuff.Mode.SRC_IN);

        TransactionType type=getType(cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE)));


        setIconByType(holder,type);
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

    private void setIconByType(ViewHolder holder,TransactionType type) {
        if (type == TransactionType.INDIVIDUALINCOME) {
            holder.icon.setImageResource(R.drawable.individual_income);
        } else if (type == TransactionType.INDIVIDUALPAYMENT) {
            holder.icon.setImageResource(R.drawable.individual_payment);
        } else if (type == TransactionType.PURCHASE) {
            holder.icon.setImageResource(R.drawable.purchase);
        } else if (type == TransactionType.REGULARINCOME) {
            holder.icon.setImageResource(R.drawable.regular_income);
        } else if (type == TransactionType.REGULARPAYMENT) {
            holder.icon.setImageResource(R.drawable.regular_payment);
        }
    }
}
