package ba.unsa.etf.rma.rma20mehulictarik49;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

@SuppressLint({"ClickableViewAccessibility","SimpleDateFormat"})

public class GraphFragment extends Fragment implements GraphReceiver.Receiver, Observer {

    private static class ButtonHolder{
        Button typeBtn;
        Button leftBtn;
        Button rightBtn;
        ScrollView scrollView;
    }

    private BarChart consumptionGraph;
    private BarChart earningsGraph;
    private BarChart totalGraph;

    private ButtonHolder holder=new ButtonHolder();

    private TransactionGraphPresenter graphPresenter;
    private ArrayList<Transaction> transactions;

    public TransactionGraphPresenter getGraphPresenter() {
//        if(graphPresenter==null){
//            graphPresenter=new TransactionGraphPresenter(getActivity(),transactions);
//        }
//        return graphPresenter;
        return new TransactionGraphPresenter(getActivity(),transactions);
    }

    private OnSwipeRight onSwipeRight;
    private OnSwipeLeft onSwipeLeft;

    private List<BarEntry> entries=new ArrayList<>();
    View fragmentView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_graph,container, false);

        Intent intent=new Intent(Intent.ACTION_SYNC,null,getActivity(), TransactionGraphServis.class);
        GraphReceiver graphReceiver = new GraphReceiver(new Handler());
        graphReceiver.setReceiver((GraphReceiver.Receiver) GraphFragment.this);
        intent.putExtra("receiver",graphReceiver);
//        intent.putExtra("context",getActivity());

        getActivity().startService(intent);

        holder.leftBtn=fragmentView.findViewById(R.id.leftGraphBtn);
        holder.rightBtn=fragmentView.findViewById(R.id.rightGraphBtn);
        holder.typeBtn=fragmentView.findViewById(R.id.typeGraph);
        holder.scrollView=fragmentView.findViewById(R.id.scroll);
        holder.typeBtn.setText("MONTHLY");


        consumptionGraph=fragmentView.findViewById(R.id.consumptionChart);
        earningsGraph=fragmentView.findViewById(R.id.earningsChart);
        totalGraph=fragmentView.findViewById(R.id.totalChart);

        onSwipeLeft= (OnSwipeLeft) getActivity();
        onSwipeRight= (OnSwipeRight) getActivity();

        return fragmentView;
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case TransactionGraphServis.STATUS_RUNNING:
                Toast.makeText(getActivity(),"Setting transactions...",Toast.LENGTH_SHORT).show();
                break;
            case TransactionGraphServis.STATUS_FINISHED:
                /* Dohvatanje rezultata i update UI */
                transactions=new ArrayList<>();
                transactions=resultData.getParcelableArrayList("transactions");
                System.out.println(transactions.size());
                holder.leftBtn.setOnClickListener(leftClickListener);
                holder.rightBtn.setOnClickListener(rightClickListener);
                createMonthlyGraphs();

                fragmentView.setOnTouchListener(onTouchListener);
                holder.scrollView.setOnTouchListener(onTouchListener);
                holder.typeBtn.setOnTouchListener(onTouchListener);

                break;
            case TransactionGraphServis.STATUS_ERROR:
                //error
                break;
        }
    }


    private View.OnClickListener leftClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(holder.typeBtn.getText().toString().equals("MONTHLY")){
                holder.typeBtn.setText("DAILY");
                createDailyGraphs();
            }else if(holder.typeBtn.getText().toString().equals("DAILY")){
                holder.typeBtn.setText("WEEKLY");
                createWeeklyGraphs();
            }else{
                holder.typeBtn.setText("MONTHLY");
                createMonthlyGraphs();
            }
        }
    };

    private View.OnClickListener rightClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(holder.typeBtn.getText().toString().equals("MONTHLY")){
                holder.typeBtn.setText("WEEKLY");
                createWeeklyGraphs();
            }else if(holder.typeBtn.getText().toString().equals("DAILY")){
                holder.typeBtn.setText("MONTHLY");
                createMonthlyGraphs();
            }else{
                holder.typeBtn.setText("DAILY");
                createDailyGraphs();
            }
        }
    };


    private void createDailyGraphs() {

        Description description;
        entries = getGraphPresenter().getDayTotalEntries();
        BarDataSet set=new BarDataSet(entries,"Total");
        set.setValueFormatter(new LargeValueFormatter());
        BarData data=new BarData(set);
        setTotalGraphColors(set);
        totalGraph.setData(data);
        totalGraph.invalidate();
        description=totalGraph.getDescription();
        description.setTextColor(Color.YELLOW);
        description.setText((new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()))+" "+Calendar.getInstance().get(Calendar.YEAR));

        entries=getGraphPresenter().getDayConsumEntries();
        set=new BarDataSet(entries,"Consum");
        data=new BarData(set);
        setConsumGraphColors(set);
        consumptionGraph.setData(data);
        consumptionGraph.invalidate();
        description=consumptionGraph.getDescription();
        description.setTextColor(Color.RED);
        description.setText((new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()))+" "+Calendar.getInstance().get(Calendar.YEAR));

        entries=getGraphPresenter().getDayEarnEntries();
        set=new BarDataSet(entries,"Earnings");
        data=new BarData(set);
        setEarnGraphColors(set);
        earningsGraph.setData(data);
        earningsGraph.invalidate();
        description=earningsGraph.getDescription();
        description.setTextColor(Color.GREEN);
        description.setText((new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()))+" "+Calendar.getInstance().get(Calendar.YEAR));

    }

    private void createWeeklyGraphs() {
        Description description;
        entries=getGraphPresenter().getWeekTotalEntries();
        BarDataSet set=new BarDataSet(entries,"Total");
        set.setValueFormatter(new LargeValueFormatter());
        BarData data=new BarData(set);
        setTotalGraphColors(set);
        totalGraph.setData(data);
        totalGraph.invalidate();
        description=totalGraph.getDescription();
        description.setTextColor(Color.YELLOW);
        description.setText((new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()))+" "+Calendar.getInstance().get(Calendar.YEAR));

        entries=getGraphPresenter().getWeekConsumEntries();
        set=new BarDataSet(entries,"Consum");
        data=new BarData(set);
        setConsumGraphColors(set);
        consumptionGraph.setData(data);
        consumptionGraph.invalidate();
        description=consumptionGraph.getDescription();
        description.setTextColor(Color.RED);
        description.setText((new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()))+" "+Calendar.getInstance().get(Calendar.YEAR));

        entries=getGraphPresenter().getWeekEarnEntries();
        set=new BarDataSet(entries,"Earnings");
        data=new BarData(set);
        setEarnGraphColors(set);
        earningsGraph.setData(data);
        earningsGraph.invalidate();
        description=earningsGraph.getDescription();
        description.setTextColor(Color.GREEN);
        description.setText((new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()))+" "+Calendar.getInstance().get(Calendar.YEAR));

    }

    private void createMonthlyGraphs() {
        Description description;
        entries=getGraphPresenter().getMonthTotalEntries();
        BarDataSet set=new BarDataSet(entries,"Total");
        set.setValueFormatter(new LargeValueFormatter());
        BarData data=new BarData(set);
        setTotalGraphColors(set);
        totalGraph.setData(data);
        totalGraph.invalidate();
        description=totalGraph.getDescription();
        description.setTextColor(Color.YELLOW);
        description.setText("Year: "+Calendar.getInstance().get(Calendar.YEAR));



        entries=getGraphPresenter().getMonthConsumEntries();
        set=new BarDataSet(entries,"Spendings");
        data=new BarData(set);
        setConsumGraphColors(set);
        consumptionGraph.setData(data);
        consumptionGraph.invalidate();
        description=consumptionGraph.getDescription();
        description.setTextColor(Color.RED);
        description.setText("Year: "+Calendar.getInstance().get(Calendar.YEAR));


        entries=getGraphPresenter().getMonthEarnEntries();
        set=new BarDataSet(entries,"Earnings");
        data=new BarData(set);
        setEarnGraphColors(set);
        earningsGraph.setData(data);
        earningsGraph.invalidate();
        description=earningsGraph.getDescription();
        description.setTextColor(Color.GREEN);
        description.setText("Year: "+Calendar.getInstance().get(Calendar.YEAR));

    }

    private void setEarnGraphColors(BarDataSet set) {
        Legend legend;
        set.setColors(ColorTemplate.rgb("#3aeb34"));
        set.setValueTextColor(ColorTemplate.rgb("#3aeb34"));
        earningsGraph.getXAxis().setTextColor(Color.GREEN);
        earningsGraph.getXAxis().setGridColor(Color.GREEN);
        earningsGraph.getXAxis().setAxisLineColor(Color.GREEN);
        earningsGraph.getAxisLeft().setAxisLineColor(Color.GREEN);
        earningsGraph.getAxisRight().setAxisLineColor(Color.GREEN);
        earningsGraph.getAxisLeft().setTextColor(Color.GREEN);
        earningsGraph.getAxisLeft().setDrawGridLines(false);
        earningsGraph.getXAxis().setDrawGridLines(false);
        legend=earningsGraph.getLegend();
        legend.setTextColor(Color.GREEN);

    }

    private void setConsumGraphColors(BarDataSet set) {
        Legend legend;
        set.setColors(ColorTemplate.rgb("#eb4034"));
        set.setValueTextColor(ColorTemplate.rgb("#eb4034"));
        consumptionGraph.getXAxis().setTextColor(Color.RED);
        consumptionGraph.getXAxis().setGridColor(Color.RED);
        consumptionGraph.getXAxis().setAxisLineColor(Color.RED);
        consumptionGraph.getAxisLeft().setAxisLineColor(Color.RED);
        consumptionGraph.getAxisRight().setAxisLineColor(Color.RED);
        consumptionGraph.getAxisLeft().setTextColor(Color.RED);
        consumptionGraph.getAxisLeft().setDrawGridLines(false);
        consumptionGraph.getXAxis().setDrawGridLines(false);
        legend = consumptionGraph.getLegend();
        legend.setTextColor(Color.RED);
    }


    private void setTotalGraphColors(BarDataSet set) {
        Legend legend;
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueTextColor(Color.YELLOW);
        totalGraph.getXAxis().setTextColor(Color.YELLOW);
        totalGraph.getXAxis().setGridColor(Color.YELLOW);
        totalGraph.getAxisLeft().setTextColor(Color.YELLOW);
        totalGraph.getXAxis().setAxisLineColor(Color.YELLOW);
        totalGraph.getAxisLeft().setAxisLineColor(Color.YELLOW);
        totalGraph.getAxisRight().setAxisLineColor(Color.YELLOW);
        totalGraph.getAxisLeft().setDrawGridLines(false);
        totalGraph.getXAxis().setDrawGridLines(false);
        legend=totalGraph.getLegend();
        legend.setTextColor(Color.YELLOW);
    }



    private View.OnTouchListener onTouchListener=new OnSwipeTouchListener(getActivity()){
        @Override
        public void onSwipeRight() {
            onSwipeRight.onSwipedRight(GraphFragment.this);
        }

        @Override
        public void onSwipeLeft() {
            onSwipeLeft.onSwipedLeft(GraphFragment.this);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityBroadcastReceiver.getObservable().addObserver(this);
    }

    public void onPause(){
        super.onPause();
        ConnectivityBroadcastReceiver.getObservable().deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        Intent intent=new Intent(Intent.ACTION_SYNC,null,getActivity(), TransactionGraphServis.class);
        GraphReceiver graphReceiver = new GraphReceiver(new Handler());
        graphReceiver.setReceiver((GraphReceiver.Receiver) GraphFragment.this);
        intent.putExtra("receiver",graphReceiver);
        getActivity().stopService(intent);
        getActivity().startService(intent);
    }

}
