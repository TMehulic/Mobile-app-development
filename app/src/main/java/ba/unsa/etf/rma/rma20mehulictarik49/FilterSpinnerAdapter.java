package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FilterSpinnerAdapter extends ArrayAdapter<String> {


    static class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    public FilterSpinnerAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView==null){
            convertView=LayoutInflater.from(getContext()).inflate(R.layout.list_spinner,parent,false);
        }
        ViewHolder viewHolder=new ViewHolder();

        viewHolder.imageView=convertView.findViewById(R.id.filterImage);
        viewHolder.textView=convertView.findViewById(R.id.filterType);
        viewHolder.imageView.setColorFilter(Color.argb(200,3,218,197), PorterDuff.Mode.SRC_IN);


        String s=getItem(position);
        viewHolder.textView.setText(s);
        setIconByType(viewHolder,s);
        return convertView;
    }

    private void setIconByType(ViewHolder holder, String s) {
        if(s.equals("Individual payment")){
            holder.imageView.setImageResource(R.drawable.individual_payment);
        }else if(s.equals("Regular payment")){
            holder.imageView.setImageResource(R.drawable.regular_payment);
        }else if(s.equals("Purchase")){
            holder.imageView.setImageResource(R.drawable.purchase);
        }else if(s.equals("Individual income")){
            holder.imageView.setImageResource(R.drawable.individual_income);
        }else if(s.equals("Regular income")){
            holder.imageView.setImageResource(R.drawable.regular_income);
        }else if(s.equals("All transactions")){
            holder.imageView.setImageResource(R.drawable.all_transactions);
        }else if(s.equals("Filter by")){
            holder.imageView.setImageResource(R.drawable.filter);
        }
    }
}
