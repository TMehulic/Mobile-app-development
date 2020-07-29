package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements TransactionListFragment.OnItemClick, TransactionDetailFragment.OnTransactionEdit,
        TransactionDetailFragment.OnTransactionAdd, TransactionListFragment.OnAddClick, TransactionDetailFragment.OnTransactionDelete, OnSwipeLeft,OnSwipeRight{

    private boolean landMode=false;
    private FrameLayout details;

    private ConnectivityBroadcastReceiver receiver = new ConnectivityBroadcastReceiver();
    private IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fragmentManager=getSupportFragmentManager();
        details=findViewById(R.id.transaction_detail);



        if(details!=null){
            landMode=true;
            TransactionDetailFragment detailFragment= (TransactionDetailFragment) fragmentManager.findFragmentById(R.id.transaction_detail);
            Bundle arguments=new Bundle();
            arguments.putBoolean("landMode",landMode);
            detailFragment=new TransactionDetailFragment();
            detailFragment.setArguments(arguments);
            fragmentManager.beginTransaction().replace(R.id.transaction_detail,detailFragment,"detailFragment").commit();

            /* Moved this out of the if statement, because i wanted to refresh detailFragment on orientationChange */

//            if(detailFragment==null){
//                Bundle arguments=new Bundle();
//                arguments.putBoolean("landMode",landMode);
//                detailFragment=new TransactionDetailFragment();
//                detailFragment.setArguments(arguments);
//                fragmentManager.beginTransaction().replace(R.id.transaction_detail,detailFragment,"detailFragment").commit();
//            }
        }else{
            landMode=false;
        }


        Fragment listFragment=fragmentManager.findFragmentByTag("listFragment");
        Bundle arguments=new Bundle();
        arguments.putBoolean("landMode",landMode);
        if(listFragment==null){
            listFragment=new TransactionListFragment();
            listFragment.setArguments(arguments);
            fragmentManager.beginTransaction().replace(R.id.transactions_list,listFragment,"listFragment").commit();
        }else{
            listFragment.setArguments(arguments);
            fragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    public void onItemClicked(Transaction transaction){

        Bundle arguments=new Bundle();
        arguments.putBoolean("landMode",landMode);
        if(transaction!=null){
            arguments.putParcelable("transaction",transaction);
        }

        TransactionDetailFragment detailFragment=new TransactionDetailFragment();
        detailFragment.setArguments(arguments);

        if(landMode){
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_detail,detailFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,detailFragment,"detailFragment").addToBackStack(null).commit();
        }

    }

    @Override
    public void onItemClickedInDB(Boolean inDatabase, int id) {

    }


    @Override
    public void onAddClicked() {
        Bundle arguments=new Bundle();
        arguments.putBoolean("addMode",true);
        arguments.putBoolean("landMode",landMode);
        TransactionDetailFragment detailFragment=new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if(landMode){
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_detail,detailFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,detailFragment,"detailFragment").addToBackStack(null).commit();
        }
    }


    public void onTransactionEdited(){
        if(landMode){
            TransactionListFragment listFragment= (TransactionListFragment) getSupportFragmentManager().findFragmentById(R.id.transactions_list);
            if (listFragment != null) {
                listFragment.updateIfLandscape();
            }
        }
    }

    @Override
    public void onTransactionAdded() {
        if(landMode){
            TransactionListFragment listFragment= (TransactionListFragment) getSupportFragmentManager().findFragmentById(R.id.transactions_list);
            if (listFragment != null) {
                listFragment.updateIfLandscape();
            }
        }
    }


    @Override
    public void onTransactionDeleted() {
        if(landMode){
            TransactionListFragment listFragment= (TransactionListFragment) getSupportFragmentManager().findFragmentById(R.id.transactions_list);
            if (listFragment != null) {
                listFragment.updateIfLandscape();
            }
        }
    }


    @Override
    public void onSwipedLeft(Fragment fragment) {
        if(fragment instanceof TransactionListFragment){
            BudgetFragment budgetFragment= new BudgetFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,budgetFragment).commit();
        }else if(fragment instanceof BudgetFragment){
            GraphFragment graphFragment=new GraphFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,graphFragment).commit();
        }else if(fragment instanceof GraphFragment){
            Bundle arguments=new Bundle();
            arguments.putBoolean("landMode",false);
            TransactionListFragment listFragment=new TransactionListFragment();
            listFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,listFragment).commit();
        }
    }

    @Override
    public void onSwipedRight(Fragment fragment) {
        if(fragment instanceof TransactionListFragment){
            GraphFragment graphFragment=new GraphFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,graphFragment).commit();
        }else if(fragment instanceof BudgetFragment){
            Bundle arguments=new Bundle();
            arguments.putBoolean("landMode",false);
            TransactionListFragment listFragment=new TransactionListFragment();
            listFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,listFragment).commit();
        }else if(fragment instanceof GraphFragment){
            BudgetFragment budgetFragment= new BudgetFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,budgetFragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }


}
