package ba.unsa.etf.rma.rma20mehulictarik49;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class TransactionListFragment extends Fragment implements ITransactionListView, AccountInfo, Observer, IDetailView {

    private TextView ammount;
    private TextView limit;
    private TextView test;
    private ListView listView;
    private Button month;
    private Button previousMonth;
    private Button nextMonth;
    private Button addButton;
    private Spinner sortSpinner;
    private Spinner filterSpinner;
    private Calendar calendar=Calendar.getInstance();

    private TransactionListAdapter transactionListAdapter;
    private TransactionListPresenter presenter;
    private AccountPresenter accPresenter;
    private Transaction selectedTransaction;

    private FilterSpinnerAdapter filterSpinnerAdapter;
    private SortSpinnerAdapter sortSpinAdapter;
    private Account account;


    private TransactionListCursorAdapter transactionListCursorAdapter;


    public TransactionListPresenter getPresenter(){
        if(presenter==null){
            presenter=new TransactionListPresenter(this,getActivity());
        }
        return presenter;
    }

    public AccountPresenter getAccountPresenter(){
        if(accPresenter==null){
            accPresenter=new AccountPresenter(this,getActivity());
        }
        return accPresenter;
    }

    private OnItemClick onItemClick;

    @Override
    public void setAccount(Account account1) {
        this.account=account1;

        ammount=fragmentView.findViewById(R.id.amount);
        limit=fragmentView.findViewById(R.id.limit);
        if(account==null){
            ammount.setText("/");
            limit.setText("/");
        }else{
            StringBuilder builder=new StringBuilder();
            Formatter f=new Formatter(builder);
            f.format("%.2f",account.getBudget());
            ammount.setText(builder.toString());

            builder.setLength(0);
            f.format("%.2f",account.getTotalLimit());
            limit.setText(builder.toString());
        }
    }


    public interface OnItemClick{
        void onItemClicked(Transaction transaction);
        void onItemClickedInDB(Boolean inDatabase,int id);
    }

    private OnAddClick onAddClick;
    public interface OnAddClick{
        void onAddClicked();
    }

    private OnSwipeRight onSwipeRight;
    private OnSwipeLeft onSwipeLeft;


    private boolean landMode=false;

    private View fragmentView;


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ConnectivityBroadcastReceiver.getObservable().addObserver(this);
        fragmentView= inflater.inflate(R.layout.fragment_list,container, false);

        if(getArguments()!=null && getArguments().containsKey("landMode")){
            landMode=getArguments().getBoolean("landMode");
        }
        selectedTransaction=null;

        month=fragmentView.findViewById(R.id.monthBtn);
        previousMonth=fragmentView.findViewById(R.id.buttonBefore);
        nextMonth=fragmentView.findViewById(R.id.buttonNext);
        calendar.add(Calendar.DATE,1);

        transactionListAdapter=new TransactionListAdapter(getActivity(),R.layout.list_element,new ArrayList<Transaction>());
        listView=fragmentView.findViewById(R.id.transactionList);
        listView.setAdapter(transactionListAdapter);
        listView.setOnItemClickListener(onClickListener);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelector(R.drawable.selector);
        onItemClick= (OnItemClick) getActivity();


        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                setMonthButton(calendar);
                addButton.setOnClickListener(addOnClickListener);
                selectedTransaction=null;
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                setMonthButton(calendar);
                addButton.setOnClickListener(addOnClickListener);
                selectedTransaction=null;
            }
        });



        //Sort spinner
        sortSpinner=fragmentView.findViewById(R.id.sortSpinner);
        sortSpinAdapter=new SortSpinnerAdapter(getActivity(),getSortTypes());
        sortSpinner.setAdapter(sortSpinAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(filterSpinner.getSelectedItem()!=null && !sortSpinner.getSelectedItem().toString().equals("Sort by")) {
                    getPresenter().filterTransactions(calendar, filterSpinner.getSelectedItem().toString(), sortSpinner.getSelectedItem().toString(),ConnectivityBroadcastReceiver.isConnected());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Auto generated method
            }
        });

        //Filter spinner
        filterSpinner=fragmentView.findViewById(R.id.filterSpinner);
        filterSpinnerAdapter=new FilterSpinnerAdapter(getActivity(),TransactionType.getTypesForFilter());
        filterSpinner.setAdapter(filterSpinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(sortSpinner.getSelectedItem()!=null && !filterSpinner.getSelectedItem().toString().equals("Filter by")) {
                    getPresenter().filterTransactions(calendar, filterSpinner.getSelectedItem().toString(), sortSpinner.getSelectedItem().toString(),ConnectivityBroadcastReceiver.isConnected());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Automatically created
            }
        });


        addButton=fragmentView.findViewById(R.id.addBtn);
        addButton.setOnClickListener(addOnClickListener);
        onAddClick= (OnAddClick) getActivity();


        //SwipeListeners
        fragmentView.setOnTouchListener(onTouchListener);
        onSwipeLeft= (OnSwipeLeft) getActivity();
        onSwipeRight= (OnSwipeRight) getActivity();
        listView.setOnTouchListener(onTouchListener);

        getAccountPresenter().getAccount();
        setMonthButton(calendar);

        return fragmentView;
    }


    private List<String> getSortTypes() {
        List<String> list=new ArrayList<>();
        list.add(0,"Sort by");
        list.add(1,"Price - Ascending");
        list.add(2,"Price - Descending");
        list.add(3,"Title - Ascending");
        list.add(4,"Title - Descending");
        list.add(5,"Date - Ascending");
        list.add(6,"Date - Descending");
        return list;
    }

    private void setMonthButton(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        String text=format.format(calendar.getTime());
        month.setText(text);
        getPresenter().filterTransactions(calendar,filterSpinner.getSelectedItem().toString(),sortSpinner.getSelectedItem().toString(),ConnectivityBroadcastReceiver.isConnected());
    }

    private AdapterView.OnItemClickListener onClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(selectedTransaction==null){
                addButton.setOnClickListener(null);
                selectedTransaction=transactionListAdapter.getTransaction(position);
                onItemClick.onItemClicked(selectedTransaction);
                view.setSelected(true);
            }else if(selectedTransaction.getTitle().equals(transactionListAdapter.getTransaction(position).getTitle())){
                addButton.setOnClickListener(addOnClickListener);
                onItemClick.onItemClicked(null);
                selectedTransaction=null;
                view.setSelected(false);
            }else{
                addButton.setOnClickListener(null);
                selectedTransaction=transactionListAdapter.getTransaction(position);
                onItemClick.onItemClicked(selectedTransaction);
                view.setSelected(true);
            }

        }
    };

    private View.OnClickListener addOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onAddClick.onAddClicked();
        }
    };


    @Override
    public void setTransactions(ArrayList<Transaction> transactions) {
        transactionListAdapter.setTransactions(transactions);
    }

    @Override
    public void notifyTransactionListDataSetChanged() {
        transactionListAdapter.notifyDataSetChanged();
    }

    //IZ MAINA SE OVO POZIVA AKO JE LAND
    public void updateIfLandscape(){
        transactionListAdapter.notifyDataSetChanged();
        getPresenter().filterTransactions(calendar,filterSpinner.getSelectedItem().toString(),sortSpinner.getSelectedItem().toString(),ConnectivityBroadcastReceiver.isConnected());
        refreshBudget();
    }


    public void refreshBudget(){
        getAccountPresenter().getAccount();
    }

    //OVO ZA PORTRET
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ConnectivityBroadcastReceiver.getObservable().addObserver(this);
        super.onViewCreated(view, savedInstanceState);
        transactionListAdapter.notifyDataSetChanged();
        getPresenter().filterTransactions(calendar,filterSpinner.getSelectedItem().toString(),sortSpinner.getSelectedItem().toString(),ConnectivityBroadcastReceiver.isConnected());
        refreshBudget();
    }

    private View.OnTouchListener onTouchListener=new OnSwipeTouchListener(getActivity()){
        @Override
        public void onSwipeRight() {
            if(!landMode){
                onSwipeRight.onSwipedRight(TransactionListFragment.this);
            }
        }

        @Override
        public void onSwipeLeft() {
            if(!landMode){
                onSwipeLeft.onSwipedLeft(TransactionListFragment.this);
            }
        }
    };

    private AdapterView.OnItemClickListener listCursorItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            if(cursor != null) {
                onItemClick.onItemClickedInDB(true, cursor.getInt(cursor.getColumnIndex(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID)));
            }
        }
    };

    public void setCursor(Cursor cursor) {
        listView.setAdapter(transactionListCursorAdapter);
        listView.setOnItemClickListener(listCursorItemClickListener);
        transactionListCursorAdapter.changeCursor(cursor);
    }

    public void onResume(){
        super.onResume();
        ConnectivityBroadcastReceiver.getObservable().addObserver(this);
    }

    public void onPause(){
        super.onPause();
        ConnectivityBroadcastReceiver.getObservable().deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(ConnectivityBroadcastReceiver.isConnected()) {
            new TransactionDetailPresenter(getActivity(), this).updateTransactions();
            if (account != null) {
                getAccountPresenter().updateAccount(account);
            } else {
                getAccountPresenter().getAccount();
            }
        }
    }

    @Override
    public void onUpdated() {
        getPresenter().filterTransactions(calendar,filterSpinner.getSelectedItem().toString(),sortSpinner.getSelectedItem().toString(),ConnectivityBroadcastReceiver.isConnected());
    }

    @Override
    public void onAdd() {
        //Zbog interfejsa naslijeđeno
    }

    @Override
    public void onEdit() {
        //Zbog interfejsa naslijeđeno
    }

    @Override
    public void onDelete() {
        //Zbog interfejsa naslijeđeno
    }


}
