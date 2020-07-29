package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SortSpinnerAdapter extends ArrayAdapter<String> {

    static class ViewHolder{
        TextView textView;
    }

    public SortSpinnerAdapter(@NonNull Context context, @NonNull List<String> objects) {
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
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.sort_spinner,parent,false);
        }
        ViewHolder viewHolder=new ViewHolder();

        viewHolder.textView=convertView.findViewById(R.id.sortText);


        String s=getItem(position);
        viewHolder.textView.setText(s);
        return convertView;
    }

}
