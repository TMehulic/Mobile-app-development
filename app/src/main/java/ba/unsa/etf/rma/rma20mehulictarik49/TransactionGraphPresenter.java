package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.Context;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TransactionGraphPresenter implements IGraphPresenter{

    private ITransactionListInteractor interactor;
    private Context context;

    private float[] months;
    private float[] days;
    private float[] weeks;
    private ArrayList<Transaction> transactions;


    public TransactionGraphPresenter(Context context,ArrayList<Transaction> transactions){
        this.context=context;
        months=new float[12];
        days=new float[Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)];
        weeks=new float[Calendar.getInstance().getActualMaximum(Calendar.WEEK_OF_MONTH)];
        this.transactions=transactions;
    }


                                        // M O N T H L Y   G R A P H S


    public List<BarEntry> getMonthConsumEntries() {
        Arrays.fill(months,0);
        List<BarEntry> entries=new ArrayList<>();
        for(Transaction t:transactions){
            if((t.getType()==TransactionType.PURCHASE || t.getType()==TransactionType.INDIVIDUALPAYMENT) && t.getDate().get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)){
                months[t.getDate().get(Calendar.MONTH)]+=t.getAmount();
            }else if(t.getType()==TransactionType.REGULARPAYMENT){
                addMonthRegularTransaction(t);
            }
        }
        for(int i=0;i<12;i++){
            entries.add(new BarEntry(i+1,months[i]));
        }
        return entries;
    }

    public List<BarEntry> getMonthEarnEntries() {
        List<BarEntry> entries=new ArrayList<>();
        Arrays.fill(months,0);
        for(Transaction t:transactions){
            if(t.getType()==TransactionType.INDIVIDUALINCOME && t.getDate().get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)){
                months[t.getDate().get(Calendar.MONTH)]+=t.getAmount();
            }else if(t.getType()==TransactionType.REGULARINCOME){
                addMonthRegularTransaction(t);
            }
        }
        for(int i=0;i<12;i++){
            entries.add(new BarEntry(i+1,months[i]));
        }
        return entries;
    }


    public List<BarEntry> getMonthTotalEntries() {
        List<BarEntry> entries=new ArrayList<>();
        Arrays.fill(months,0);
        for(Transaction t:transactions){
            if(t.getType()==TransactionType.REGULARINCOME || t.getType()==TransactionType.REGULARPAYMENT){
                addMonthRegular(t);
            }else if(t.getDate().get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)){
                if(t.getType()==TransactionType.INDIVIDUALINCOME){
                    months[t.getDate().get(Calendar.MONTH)]+=t.getAmount();
                }else{
                    months[t.getDate().get(Calendar.MONTH)]-=t.getAmount();
                }
            }
        }
        for(int i=0;i<12;i++){
            if(i!=0){
                months[i]+=months[i-1];
            }
            entries.add(new BarEntry(i+1,months[i]));
        }
        return entries;
    }


    private void addMonthRegular(Transaction t) {
        if(t.getEndDate().get(Calendar.YEAR)>=Calendar.getInstance().get(Calendar.YEAR)){
            Calendar cal=Calendar.getInstance();
            cal.setTime(t.getDate().getTime());
            while(cal.get(Calendar.YEAR)<Calendar.getInstance().get(Calendar.YEAR)){
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
            while (cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && cal.before(t.getEndDate())){
                if(t.getType()==TransactionType.REGULARINCOME){
                    months[cal.get(Calendar.MONTH)]+=t.getAmount();
                }else{
                    months[cal.get(Calendar.MONTH)]-=t.getAmount();
                }
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
        }
    }

    private void addMonthRegularTransaction(Transaction t) {
        if(t.getEndDate().get(Calendar.YEAR)>=Calendar.getInstance().get(Calendar.YEAR)){
            Calendar cal=Calendar.getInstance();
            cal.setTime(t.getDate().getTime());
            while(cal.get(Calendar.YEAR)<Calendar.getInstance().get(Calendar.YEAR)){
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
            while(cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && cal.before(t.getEndDate())){
                months[cal.get(Calendar.MONTH)]+=t.getAmount();
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
        }
    }


                                        //D A I L Y   G R A P H S



    public List<BarEntry> getDayConsumEntries(){
        List<BarEntry> entries=new ArrayList<>();
        Arrays.fill(days,0);
        for(Transaction t:transactions){
            if((t.getType()==TransactionType.PURCHASE || t.getType()==TransactionType.INDIVIDUALPAYMENT) && t.getDate().get(Calendar.YEAR)==2020 && t.getDate().get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)){
                days[t.getDate().get(Calendar.DAY_OF_MONTH)-1]+=t.getAmount();
            }else if(t.getType()==TransactionType.REGULARPAYMENT){
                addDayRegularTransaction(t);
            }
        }
        for(int i=0;i<days.length;i++){
            entries.add(new BarEntry(i+1,days[i]));
        }
        return entries;
    }


    public List<BarEntry> getDayEarnEntries() {
        List<BarEntry> entries=new ArrayList<>();
        Arrays.fill(days,0);
        for(Transaction t:transactions){
            if(t.getType()==TransactionType.INDIVIDUALINCOME && t.getDate().get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH) && t.getDate().get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)){
                days[t.getDate().get(Calendar.DAY_OF_MONTH)-1]+=t.getAmount();
            }else if(t.getType()==TransactionType.REGULARINCOME){
                addDayRegularTransaction(t);
            }
        }
        for(int i=0;i<days.length;i++){
            entries.add(new BarEntry(i+1,days[i]));
        }
        return entries;
    }

    public List<BarEntry> getDayTotalEntries() {
        List<BarEntry> entries=new ArrayList<>();
        Arrays.fill(days,0);
        for(Transaction t:transactions){
            if((t.getType()==TransactionType.REGULARINCOME || t.getType()==TransactionType.REGULARPAYMENT)){
                addDayRegular(t);
            }else if(t.getDate().get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH) && t.getDate().get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)){
                if(t.getType()==TransactionType.INDIVIDUALINCOME){
                    days[t.getDate().get(Calendar.DAY_OF_MONTH)-1]+=t.getAmount();
                }else{
                    days[t.getDate().get(Calendar.DAY_OF_MONTH)-1]-=t.getAmount();
                }
            }
        }
        for(int i=0;i<days.length;i++){
            if(i!=0){
                days[i]+=days[i-1];
            }

            entries.add(new BarEntry(i+1,days[i]));
        }
        return entries;
    }


    private void addDayRegularTransaction(Transaction t) {
        if(t.getEndDate().get(Calendar.YEAR)>=Calendar.getInstance().get(Calendar.YEAR)){
            Calendar cal=Calendar.getInstance();
            cal.setTime(t.getDate().getTime());
            while(cal.get(Calendar.YEAR)<Calendar.getInstance().get(Calendar.YEAR)){
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
            while (cal.get(Calendar.MONTH)<=Calendar.getInstance().get(Calendar.MONTH) && cal.before(t.getEndDate()) && cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)){
                if(cal.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)){
                    days[cal.get(Calendar.DAY_OF_MONTH)-1]+=t.getAmount();
                }
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
        }
    }


    private void addDayRegular(Transaction t){
        if(t.getEndDate().get(Calendar.YEAR)>=Calendar.getInstance().get(Calendar.YEAR)){
            Calendar cal=Calendar.getInstance();
            cal.setTime(t.getDate().getTime());
            while (cal.get(Calendar.YEAR)<Calendar.getInstance().get(Calendar.YEAR)){
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
            while (cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && cal.get(Calendar.MONTH)<=Calendar.getInstance().get(Calendar.MONTH) && cal.before(t.getEndDate())){
                if(cal.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)){
                    if(t.getType()==TransactionType.REGULARINCOME){
                        days[cal.get(Calendar.DAY_OF_MONTH)-1]+=t.getAmount();
                    }else{
                        days[cal.get(Calendar.DAY_OF_MONTH)-1]-=t.getAmount();
                    }
                }
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
        }
    }


                                                        //W E E K L Y   G R A P H S


    public List<BarEntry> getWeekConsumEntries() {
        List<BarEntry> entries=new ArrayList<>();
        Arrays.fill(weeks,0);
        for(Transaction t:transactions){
            if(t.getType()==TransactionType.REGULARPAYMENT){
                addWeekRegularTransaction(t);
            }else if(t.getDate().get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && t.getDate().get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)){
                if(t.getType()==TransactionType.PURCHASE || t.getType()==TransactionType.INDIVIDUALPAYMENT){
                    weeks[t.getDate().get(Calendar.WEEK_OF_MONTH)-1]+=t.getAmount();
                }
            }
        }
        for(int i=0;i<weeks.length;i++){
            entries.add(new BarEntry(i+1,weeks[i]));
        }
        return entries;
    }



    public List<BarEntry> getWeekEarnEntries() {
        List<BarEntry> entries=new ArrayList<>();
        Arrays.fill(weeks,0);
        for(Transaction t:transactions){
            if(t.getType()==TransactionType.INDIVIDUALINCOME && t.getDate().get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && t.getDate().get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)){
                weeks[t.getDate().get(Calendar.WEEK_OF_MONTH)-1]+=t.getAmount();
            }else if(t.getType()==TransactionType.REGULARINCOME){
                addWeekRegularTransaction(t);
            }
        }
        for(int i=0;i<weeks.length;i++){
            entries.add(new BarEntry(i+1,weeks[i]));
        }
        return entries;
    };


    public List<BarEntry> getWeekTotalEntries() {
        List<BarEntry> entries=new ArrayList<>();
        Arrays.fill(weeks,0);
        for(Transaction t:transactions){
            if(t.getType()==TransactionType.REGULARINCOME || t.getType()==TransactionType.REGULARPAYMENT){
                addWeekRegular(t);
            }else if(t.getDate().get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && t.getDate().get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)){
                if(t.getType()==TransactionType.INDIVIDUALINCOME){
                    weeks[t.getDate().get(Calendar.WEEK_OF_MONTH)-1]+=t.getAmount();
                }else{
                    weeks[t.getDate().get(Calendar.WEEK_OF_MONTH)-1]-=t.getAmount();
                }
            }
        }
        for(int i=0;i<weeks.length;i++){
            if(i!=0){
                weeks[i]+=weeks[i-1];
            }
            entries.add(new BarEntry(i+1,weeks[i]));
        }
        return entries;
    }

    private void addWeekRegularTransaction(Transaction t) {
        if(t.getEndDate().get(Calendar.YEAR)>=Calendar.getInstance().get(Calendar.YEAR)){
            Calendar cal=Calendar.getInstance();
            cal.setTime(t.getDate().getTime());
            while(cal.get(Calendar.YEAR)<Calendar.getInstance().get(Calendar.YEAR)){
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
            while (cal.get(Calendar.MONTH)<=Calendar.getInstance().get(Calendar.MONTH) && cal.before(t.getEndDate()) && cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)){
                if(cal.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH) && cal.get(Calendar.WEEK_OF_MONTH)==Calendar.getInstance().get(Calendar.WEEK_OF_MONTH)){
                    weeks[cal.get(Calendar.WEEK_OF_MONTH)-1]+=t.getAmount();
                }
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
        }
    }

    private void addWeekRegular(Transaction t) {
        if(t.getEndDate().get(Calendar.YEAR)>=Calendar.getInstance().get(Calendar.YEAR)){
            Calendar cal=Calendar.getInstance();
            cal.setTime(t.getDate().getTime());
            while (cal.get(Calendar.YEAR)<Calendar.getInstance().get(Calendar.YEAR)){
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
            while (cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && cal.get(Calendar.MONTH)<=Calendar.getInstance().get(Calendar.MONTH) && cal.before(t.getEndDate())){
                if(cal.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH) && cal.get(Calendar.WEEK_OF_MONTH)==Calendar.getInstance().get(Calendar.WEEK_OF_MONTH)){
                    if(t.getType()==TransactionType.REGULARINCOME){
                        weeks[cal.get(Calendar.WEEK_OF_MONTH)-1]+=t.getAmount();
                    }else{
                        weeks[cal.get(Calendar.WEEK_OF_MONTH)-1]-=t.getAmount();
                    }
                }
                cal.add(Calendar.DATE,t.getTransactionInterval());
            }
        }
    }

}
