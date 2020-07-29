package ba.unsa.etf.rma.rma20mehulictarik49;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.Formatter;
import java.util.Observable;
import java.util.Observer;

public class BudgetFragment extends Fragment implements AccountInfo, Observer {


    @Override
    public void update(Observable o, Object arg) {
        if(ConnectivityBroadcastReceiver.isConnected()){
            holder.offlineText.setText("");
            if(account!=null){
                accountPresenter.updateAccount(account);    //ako je off bio, pa after nek se update
            }
        }else{
            holder.offlineText.setText("Offline način rada");
        }
    }

    static class ViewHolder{
        EditText budget;
        EditText monthLimit;
        EditText totalLimit;
        Button editBtn;
        Button saveBtn;
        TextView offlineText;
    }

    private AccountPresenter accountPresenter;

    private AccountPresenter getAccountPresenter() {
        if(accountPresenter==null){
            accountPresenter=new AccountPresenter(this, getActivity());
        }
        return accountPresenter;
    }

    private Account account;

    private ViewHolder holder=new ViewHolder();
    private OnSwipeRight onSwipeRight;
    private OnSwipeLeft onSwipeLeft;

    private View fragmentView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_budget,container, false);

        holder.editBtn=fragmentView.findViewById(R.id.budgetEdit);
        holder.saveBtn=fragmentView.findViewById(R.id.budgetSave);

        holder.editBtn.setOnClickListener(editOnClickListener);
        holder.saveBtn.setBackgroundColor(Color.TRANSPARENT);
        holder.saveBtn.setTextColor(Color.TRANSPARENT);

        holder.offlineText=fragmentView.findViewById(R.id.offlineMode);

        fragmentView.setOnTouchListener(onTouchListener);
        onSwipeLeft= (OnSwipeLeft) getActivity();
        onSwipeRight= (OnSwipeRight) getActivity();

        getAccountPresenter().getAccount();

        if(!ConnectivityBroadcastReceiver.isConnected())
            holder.offlineText.setText("Offline način rada");


        return fragmentView;
    }

    @Override
    public void setAccount(Account account) {
        this.account=account;
        holder.budget=fragmentView.findViewById(R.id.budget);
        holder.totalLimit=fragmentView.findViewById(R.id.totalBudget);
        holder.monthLimit=fragmentView.findViewById(R.id.monthBudget);

        holder.budget.setFocusable(false);
        holder.budget.setEnabled(false);
        holder.budget.setFocusableInTouchMode(false);

        enableEdit(false);

        if(account!=null){
            StringBuilder builder=new StringBuilder();
            Formatter f=new Formatter(builder);
            f.format("%.2f",account.getBudget());

            holder.budget.setText(builder.toString());

            builder.setLength(0);
            f.format("%.2f",account.getTotalLimit());

            holder.totalLimit.setText(builder.toString());

            builder.setLength(0);
            f.format("%.2f",account.getMonthLimit());

            holder.monthLimit.setText(builder.toString());

            holder.editBtn.setOnClickListener(editOnClickListener);

        }else{
            holder.budget.setText("/");
            holder.totalLimit.setText("/");
            holder.monthLimit.setText("/");
            holder.editBtn.setOnClickListener(null);
            holder.saveBtn.setOnClickListener(null);
        }


    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private  View.OnClickListener editOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(account!=null)
                enableEdit(true);
        }
    };

    private View.OnClickListener saveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try{
                if(Double.parseDouble(holder.totalLimit.getText().toString())<Double.parseDouble(holder.monthLimit.getText().toString())){
                    AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(getActivity());
                    AlertDialog dialog;
                    dialogBuilder.setMessage("Total limit can't be less than month limit.");
                    dialogBuilder.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dialog = dialogBuilder.create();
                    dialog.show();
                }else{
                    account.setMonthLimit(Double.parseDouble(holder.monthLimit.getText().toString()));
                    account.setTotalLimit(Double.parseDouble(holder.totalLimit.getText().toString()));
                    getAccountPresenter().updateAccount(account);
                    enableEdit(false);
                    holder.monthLimit.getBackground().setColorFilter(Color.argb(255,3,218,197),PorterDuff.Mode.SRC_ATOP);
                    holder.totalLimit.getBackground().setColorFilter(Color.argb(255,3,218,197),PorterDuff.Mode.SRC_ATOP);

                }
            }catch (NumberFormatException e){
                AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(getActivity());
                AlertDialog dialog;
                dialogBuilder.setMessage("Please enter numeric value.");
                dialogBuilder.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dialog = dialogBuilder.create();
                dialog.show();
            }
        }
    };

    private void enableEdit(boolean enabled) {

        if(enabled){
            holder.saveBtn.setOnClickListener(saveOnClickListener);
            holder.saveBtn.setBackgroundResource(R.drawable.rounded);
            holder.saveBtn.setTextColor(Color.parseColor("#03DAC5"));
            holder.monthLimit.addTextChangedListener(monthTextWatcher);
            holder.totalLimit.addTextChangedListener(totalTextWatcher);
        }else{
            holder.saveBtn.setBackgroundColor(Color.TRANSPARENT);
            holder.saveBtn.setTextColor(Color.TRANSPARENT);
            holder.saveBtn.setOnClickListener(null);
            holder.monthLimit.removeTextChangedListener(monthTextWatcher);
            holder.totalLimit.removeTextChangedListener(totalTextWatcher);
        }


        holder.monthLimit.setFocusable(enabled);
        holder.monthLimit.setEnabled(enabled);
        holder.monthLimit.setFocusableInTouchMode(enabled);

        holder.totalLimit.setFocusable(enabled);
        holder.totalLimit.setEnabled(enabled);
        holder.totalLimit.setFocusableInTouchMode(enabled);
    }

    private TextWatcher monthTextWatcher= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try{
                if(Double.parseDouble(s.toString())>0){
                    holder.monthLimit.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                }else{
                    holder.monthLimit.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                }
            }catch (NumberFormatException e){
                holder.monthLimit.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher totalTextWatcher= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try{
                if(Double.parseDouble(s.toString())>0){
                    holder.totalLimit.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                }else{
                    holder.totalLimit.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                }
            }catch (NumberFormatException e){
                holder.totalLimit.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnTouchListener onTouchListener=new OnSwipeTouchListener(getActivity()){
        @Override
        public void onSwipeRight() {
            onSwipeRight.onSwipedRight(BudgetFragment.this);
        }

        @Override
        public void onSwipeLeft() {
            onSwipeLeft.onSwipedLeft(BudgetFragment.this);
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
}
