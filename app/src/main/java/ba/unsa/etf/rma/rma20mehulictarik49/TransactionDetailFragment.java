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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class TransactionDetailFragment extends Fragment implements AccountInfo, IDetailView, Observer {

    @Override
    public void setAccount(Account account) {
        this.account=account;
    }

    static class ViewHolder{
        EditText title;
        EditText amount;
        EditText type;
        EditText info;
        EditText date;
        EditText endDate;
        EditText interval;
        ImageView background;
        Button deleteBtn;
        Button editBtn;
        Button saveBtn;
        TextView offlineText;
    }

    private TransactionDetailPresenter presenter;
    private AccountPresenter accountPresenter;

    private Account account;
    private Transaction transaction;
    private ViewHolder viewHolder=new ViewHolder();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private static boolean confirmed;
    private boolean clickedEdit;

    private OnTransactionEdit onTransactionEdit;
    private OnTransactionAdd onTransactionAdd;
    private OnTransactionDelete onTransactionDelete;
    private Toast toast;

    public interface OnTransactionAdd{
        void onTransactionAdded();
    }

    public interface OnTransactionEdit{
        void onTransactionEdited();
    }

    public interface OnTransactionDelete{
        void onTransactionDeleted();
    }

    private boolean landMode=false;
    private boolean addMode=false;


    public TransactionDetailPresenter getPresenter(){
        if(presenter==null){
            presenter=new TransactionDetailPresenter(getActivity(),this);
        }
        return presenter;
    }

    public AccountPresenter getAccountPresenter(){
        if(accountPresenter==null){
            accountPresenter=new AccountPresenter(this,getActivity());
        }
        return accountPresenter;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView= inflater.inflate(R.layout.fragment_detail,container,false);
        account=null;

        //called to set account
        getAccountPresenter().getAccount();
        //called to get transactions (used for limits check)
        if(ConnectivityBroadcastReceiver.isConnected()){
            getPresenter().getTransactions();
        }


        dialogBuilder=new AlertDialog.Builder(getActivity());

        viewHolder.title=fragmentView.findViewById(R.id.detailTitle);
        viewHolder.amount=fragmentView.findViewById(R.id.detailAmount);
        viewHolder.info=fragmentView.findViewById(R.id.detailInfo);
        viewHolder.type=fragmentView.findViewById(R.id.detailType);
        viewHolder.date=fragmentView.findViewById(R.id.dateDetail);
        viewHolder.endDate=fragmentView.findViewById(R.id.endDateDetail);
        viewHolder.background=fragmentView.findViewById(R.id.background);
        viewHolder.interval=fragmentView.findViewById(R.id.intervalDetail);
        viewHolder.deleteBtn=fragmentView.findViewById(R.id.removeBtn);
        viewHolder.editBtn=fragmentView.findViewById(R.id.editBtn);
        viewHolder.saveBtn=fragmentView.findViewById(R.id.saveBtn);
        viewHolder.saveBtn.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.saveBtn.setTextColor(Color.TRANSPARENT);
        viewHolder.offlineText=fragmentView.findViewById(R.id.offlineTextView);
        viewHolder.offlineText.setText("");
        viewHolder.background.setColorFilter(Color.argb(200,3,218,197), PorterDuff.Mode.SRC_IN);

        if(getArguments()!=null && getArguments().containsKey("landMode")){
            landMode=getArguments().getBoolean("landMode");
        }


        if(getArguments()!=null && getArguments().containsKey("addMode")){
            addMode=getArguments().getBoolean("addMode");
        }

        //LandMode nothing selected
        if(getArguments()!=null && !getArguments().containsKey("transaction") && landMode){
            setEmpty();
        }

        //Add
        if(getArguments()!=null && !getArguments().containsKey("transaction")){
            if(account==null){
                Toast.makeText(getActivity(),"Loading budget...",Toast.LENGTH_SHORT).show();
            }
            setAddDefault();
            if(!ConnectivityBroadcastReceiver.isConnected()){
                viewHolder.offlineText.setText("Offline dodavanje");
            }
            onTransactionAdd= (OnTransactionAdd) getActivity();
        }

        if(addMode && landMode && !ConnectivityBroadcastReceiver.isConnected()){
            viewHolder.offlineText.setText("Offline dodavanje");
        }


        //Edit
        if(getArguments()!=null && getArguments().containsKey("transaction")){

            if(account==null){
                Toast.makeText(getActivity(),"Loading budget...",Toast.LENGTH_SHORT).show();
            }
            getPresenter().setTransaction((Transaction) getArguments().getParcelable("transaction"));
            transaction=getPresenter().getTransaction();
            if(transaction.getDelete()==1){
                viewHolder.deleteBtn.setText("UNDO"); // https://www.youtube.com/watch?v=L25P2xZ7VPM
            }
            if(transaction.getEdit()==1 && !ConnectivityBroadcastReceiver.isConnected()){
                viewHolder.offlineText.setText("Offline izmjena");
            }

            if(transaction.getAdd()==1 && transaction.getEdit()==0 && !ConnectivityBroadcastReceiver.isConnected()){
                viewHolder.offlineText.setText("Offline dodavanje");
            }

            if(transaction.getDelete()==1){
                viewHolder.offlineText.setText("Offline brisanje");
            }

            onTransactionEdit= (OnTransactionEdit) getActivity();
            viewHolder.title.setText(transaction.getTitle());
            viewHolder.info.setText(transaction.getItemDescription());
            StringBuilder builder=new StringBuilder();
            Formatter f=new Formatter(builder);
            f.format("%.2f",transaction.getAmount());
            viewHolder.amount.setText(builder.toString());

            setDates();
            setImageAndType();
            setInterval();
            enableEditText(false);
            viewHolder.editBtn.setOnClickListener(editOnClickListener);
            viewHolder.deleteBtn.setOnClickListener(deleteOnClickListener);
        }



        return fragmentView;
    }

    private void setAddDefault() {

        viewHolder.title.setText("Title");
        viewHolder.amount.setText("Amount");
        viewHolder.type.setText("Transaction type");
        viewHolder.info.setText("Description");
        viewHolder.date.setText("/");
        viewHolder.endDate.setText("/");
        viewHolder.interval.setText("/");
        viewHolder.background.setImageResource(R.drawable.all_transactions);
        viewHolder.deleteBtn.setOnClickListener(null);
        viewHolder.editBtn.setOnClickListener(null);
        enableEditText(true);
        setDefaultColors();
        addListeners();
    }

    private void setEmpty() {
        enableEditText(false);
        setDefaultColors();

        viewHolder.title.removeTextChangedListener(titleTextWatcher);
        viewHolder.amount.removeTextChangedListener(amountTextWather);
        viewHolder.type.removeTextChangedListener(typeTextWatcher);
        viewHolder.info.removeTextChangedListener(infoTextWatcher);
        viewHolder.date.removeTextChangedListener(dateTextWatcher);
        viewHolder.endDate.removeTextChangedListener(endDateTextWatcher);
        viewHolder.interval.removeTextChangedListener(intervalTextWatcher);
        viewHolder.deleteBtn.setOnClickListener(null);
        viewHolder.editBtn.setOnClickListener(null);
        viewHolder.title.setText("");
        viewHolder.amount.setText(null);
        viewHolder.type.setText("");
        viewHolder.info.setText("");
        viewHolder.date.setText(null);
        viewHolder.endDate.setText(null);
        viewHolder.interval.setText(null);
    }

    //Creating listeners
    private void addListeners() {
        viewHolder.saveBtn.setBackgroundResource(R.drawable.rounded);
        viewHolder.saveBtn.setTextColor(Color.parseColor("#03DAC5"));
        viewHolder.saveBtn.setOnClickListener(saveOnClickListener);
        viewHolder.title.addTextChangedListener(titleTextWatcher);
        viewHolder.amount.addTextChangedListener(amountTextWather);
        viewHolder.type.addTextChangedListener(typeTextWatcher);
        viewHolder.info.addTextChangedListener(infoTextWatcher);
        viewHolder.date.addTextChangedListener(dateTextWatcher);
        viewHolder.endDate.addTextChangedListener(endDateTextWatcher);
        viewHolder.interval.addTextChangedListener(intervalTextWatcher);
    }

    private void setInterval() {
        if(transaction.getTransactionInterval()!=0){
            viewHolder.interval.setText(String.valueOf(transaction.getTransactionInterval()));
        }
    }

    private void setImageAndType() {
        if (transaction.getType() == TransactionType.INDIVIDUALINCOME) {
            viewHolder.type.setText("Individual income");
            viewHolder.info.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.background.setImageResource(R.drawable.individual_income);
        } else if (transaction.getType() == TransactionType.INDIVIDUALPAYMENT) {
            viewHolder.type.setText("Individual payment");
            viewHolder.background.setImageResource(R.drawable.individual_payment);
        } else if (transaction.getType() == TransactionType.PURCHASE) {
            viewHolder.type.setText("Purchase");
            viewHolder.background.setImageResource(R.drawable.purchase);
        } else if (transaction.getType() == TransactionType.REGULARINCOME) {
            viewHolder.type.setText("Regular income");
            viewHolder.info.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.background.setImageResource(R.drawable.regular_income);
        } else if (transaction.getType() == TransactionType.REGULARPAYMENT) {
            viewHolder.type.setText("Regular payment");
            viewHolder.background.setImageResource(R.drawable.regular_payment);
        }
    }

    private void setDates() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault());
        String date_n=format.format(transaction.getDate().getTime());
        viewHolder.date.setText(date_n);
        if(transaction.getEndDate()!=null) {
            String date_e = format.format(transaction.getEndDate().getTime());
            viewHolder.endDate.setText(date_e);
        }
    }


    private View.OnClickListener deleteOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogBuilder.setMessage("Are you sure you want to delete this transaction?");
            dialogBuilder.setCancelable(true);
            dialogBuilder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            onTransactionDelete= (OnTransactionDelete) getActivity();
                            double amount=transaction.getAmount();
                            if(transaction.getType()==TransactionType.REGULARINCOME || transaction.getType()==TransactionType.REGULARPAYMENT){
                                amount=getRegularAmount(transaction);
                            }
//                            if(transaction.getType()==TransactionType.REGULARINCOME || transaction.getType()==TransactionType.INDIVIDUALINCOME){
//                                getAccountPresenter().updateBudget(account,amount*(-1),0);
//                            }else{
//                                getAccountPresenter().updateBudget(account,amount,0);
//                            }

                            if(ConnectivityBroadcastReceiver.isConnected() && account!=null){
                                if(transaction.getType()==TransactionType.REGULARINCOME || transaction.getType()==TransactionType.INDIVIDUALINCOME){
                                    getAccountPresenter().updateBudget(account,amount*(-1),0);
                                }else{
                                    getAccountPresenter().updateBudget(account,amount,0);
                                }
                                getPresenter().deleteTransaction();
                            }else if(account!=null){
                                if(getPresenter().getTransaction().getDelete()==0){
                                    if(transaction.getType()==TransactionType.REGULARINCOME || transaction.getType()==TransactionType.INDIVIDUALINCOME){
                                        getAccountPresenter().updateBudget(account,amount*(-1),0);
                                    }else{
                                        getAccountPresenter().updateBudget(account,amount,0);
                                    }
                                    getPresenter().getTransaction().setDelete(1);
                                }else{
                                    if(transaction.getType()==TransactionType.REGULARINCOME || transaction.getType()==TransactionType.INDIVIDUALINCOME){
                                        getAccountPresenter().updateBudget(account,amount,0);
                                    }else{
                                        getAccountPresenter().updateBudget(account,amount*(-1),0);
                                    }
                                    getPresenter().getTransaction().setDelete(0);
                                }
                                getPresenter().deleteTransactionDB();
                            }

                        }
                    });

            dialogBuilder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            dialog = dialogBuilder.create();
            dialog.show();
        }
    };



    private View.OnClickListener editOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enableEditText(true);
            addListeners();
            clickedEdit=true;
            if(!ConnectivityBroadcastReceiver.isConnected()){
                viewHolder.offlineText.setText("Offline izmjena");
            }
        }
    };


    //A lot of validations
    private View.OnClickListener saveOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean foundError=false;
            if(!checkTitle(viewHolder.title.getText().toString())){
                foundError=true;
                createDialogBuilder("Number of title characters  must be between 3 and 15 letters.");
                showAlertDialog();
            }else if(!checkType(viewHolder.type.getText().toString())){
                foundError=true;
                createDialogBuilder("You have entered wrong type.\nPlease try one of these: Regular income, Regular payment, Purchase, Individual income, Individual payment.");
                showAlertDialog();
            }else if(!checkInfo(viewHolder.info.getText().toString())){
                foundError=true;
                createDialogBuilder("You must have item description for all transactions except income transactions.");
                showAlertDialog();
            }else if(!checkDate(viewHolder.date.getText().toString())){
                foundError=true;
                createDialogBuilder("You have entered wrong date.");
                showAlertDialog();
            }else if(!checkAmount(viewHolder.amount.getText().toString())){
                foundError=true;
                createDialogBuilder("Please enter positive numeric value.");
                showAlertDialog();
            }

            if(!foundError && (viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Regular payment"))){
                if(checkDate(viewHolder.endDate.getText().toString())){
                    if(!checkIfEndDateAfter()){
                        foundError=true;
                        createDialogBuilder("End date must be after date.");
                        showAlertDialog();
                    }
                }else{
                    foundError=true;
                    createDialogBuilder("You have entered wrong end date.");
                    showAlertDialog();
                }
            }else if(!foundError){
                if(checkDate(viewHolder.endDate.getText().toString())){
                    foundError=true;
                    createDialogBuilder("You can't have end date on non-regular transactions");
                    showAlertDialog();
                }
            }

            if(!foundError && checkIfTitleExist(viewHolder.title.getText().toString())){
                foundError=true;
                createDialogBuilder("This title already exists.");
                showAlertDialog();
            }

            if(!foundError && !checkInterval(viewHolder.interval.getText().toString())){
                foundError=true;
                createDialogBuilder("You must have positive interval value, only for regular transactions.");
                showAlertDialog();
            }


            if(ConnectivityBroadcastReceiver.isConnected()){
                if(!foundError && !checkMonthLimit(viewHolder.date.getText().toString())){
                    createConfirmationDialogBuilder("This amount is beyond your month limit.\nPlease confirm that you are aware of this.");
                    showAlertDialog();
                }else if(!foundError && !checkTotalLimit()){
                    createConfirmationDialogBuilder("This amount is beyond your total limit.\nPlease confirm that you are aware of this.");
                    showAlertDialog();
                }else if(!foundError){
                    if(transaction==null){
                        addTransaction();
                    }else{
                        editTransaction();
                    }
                }
            }else if(!foundError){
                if(transaction==null){
                    addTransaction();
                }else{
                    editTransaction();
                }
            }
        }
    };


    private void setDefaultColors() {
        viewHolder.title.setBackgroundColor(Color.argb(255,11,11,11));
        viewHolder.amount.setBackgroundColor(Color.argb(255,11,11,11));
        viewHolder.type.setBackgroundColor(Color.argb(255,3,218,197));
        viewHolder.date.getBackground().setColorFilter(Color.argb(255,3,218,197),PorterDuff.Mode.SRC_ATOP);
        viewHolder.endDate.getBackground().setColorFilter(Color.argb(255,3,218,197),PorterDuff.Mode.SRC_ATOP);
        viewHolder.interval.getBackground().setColorFilter(Color.argb(255,3,218,197),PorterDuff.Mode.SRC_ATOP);
        if(viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Individual income")){
            viewHolder.info.setText("");
            viewHolder.info.setBackgroundResource(0);
        }else{
            viewHolder.info.setBackgroundResource(R.drawable.rounded);
        }
        viewHolder.saveBtn.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.saveBtn.setTextColor(Color.TRANSPARENT);
        viewHolder.saveBtn.setOnClickListener(null);
    }

    //Called on add save
    private void addTransaction(){
        transaction=new Transaction();
        updateTransaction();
        if(ConnectivityBroadcastReceiver.isConnected()){
            getPresenter().addTransaction(transaction);
        }else{
            getPresenter().addTransactionDB(transaction);
        }

    }

    private double getRegularAmount(Transaction transaction) {
        double sum=0;
        Calendar cal=Calendar.getInstance();
        cal.setTime(transaction.getDate().getTime());
        while (cal.before(transaction.getEndDate())){
            sum+=transaction.getAmount();
            cal.add(Calendar.DATE,transaction.getTransactionInterval());
        }
        return sum;
    }

    //Called on edit save
    private void editTransaction(){
        double oldSum=transaction.getAmount();
        if(transaction.getType()==TransactionType.REGULARPAYMENT || transaction.getType()==TransactionType.REGULARINCOME){
            oldSum=getRegularAmount(transaction);
        }

        //Make things like we delete the old transaction

        if(transaction.getType()==TransactionType.REGULARINCOME || transaction.getType()==TransactionType.INDIVIDUALINCOME){
            getAccountPresenter().updateBudget(account,oldSum*(-1),1);
        }else{
            getAccountPresenter().updateBudget(account,oldSum,1);
        }

        updateTransaction();

        //Now make things just like we add new transaction

        if(transaction.getType()==TransactionType.INDIVIDUALINCOME){
            getAccountPresenter().updateBudget(account,transaction.getAmount(),1);
        }else if(transaction.getType()==TransactionType.PURCHASE || transaction.getType()==TransactionType.INDIVIDUALPAYMENT){
            getAccountPresenter().updateBudget(account,transaction.getAmount()*(-1),1);
        }else if(transaction.getType()==TransactionType.REGULARINCOME){
            getAccountPresenter().updateBudget(account,getRegularAmount(transaction),1);
        }else if(transaction.getType()==TransactionType.REGULARPAYMENT){
            getAccountPresenter().updateBudget(account,getRegularAmount(transaction) * (-1),1);
        }
        getAccountPresenter().updateBudget(account,(Double.parseDouble(viewHolder.amount.getText().toString())-transaction.getAmount()),1);
        if(ConnectivityBroadcastReceiver.isConnected()){
            getPresenter().editTransaction(transaction);
        }else{
            getPresenter().editTransactionDB(transaction);
        }
        clickedEdit=false;

    }

    private void updateTransaction() {
        transaction.setTitle(viewHolder.title.getText().toString());
        transaction.setAmount(Double.parseDouble(viewHolder.amount.getText().toString()));
        Date date;
        Date endDate=null;
        Calendar date1= Calendar.getInstance();
        Calendar date2= Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/mm/yyyy",Locale.getDefault());
        try {
            date=sdf1.parse(viewHolder.date.getText().toString());
            date1.setTime(date);
        }catch (ParseException e){
            try {
                date=sdf2.parse(viewHolder.date.getText().toString());
                date1.setTime(date);
            } catch (ParseException ignored) {

            }
        }
        try {
            endDate=sdf1.parse(viewHolder.endDate.getText().toString());
            date2.setTime(endDate);
        }catch (ParseException e){
            try {
                endDate=sdf2.parse(viewHolder.endDate.getText().toString());
                date2.setTime(endDate);
            } catch (ParseException ignored) {
                endDate=null;
            }
        }

        transaction.setDate(date1);
        if(endDate!=null){
            transaction.setEndDate(date2);
        }
        if(viewHolder.type.getText().toString().equals("Regular payment")){
            transaction.setType(TransactionType.REGULARPAYMENT);
            transaction.setItemDescription(viewHolder.info.getText().toString());
            transaction.setTransactionInterval(Integer.parseInt(viewHolder.interval.getText().toString()));
        }else if(viewHolder.type.getText().toString().equals("Regular income")){
            transaction.setType(TransactionType.REGULARINCOME);
            transaction.setItemDescription(null);
            transaction.setTransactionInterval(Integer.parseInt(viewHolder.interval.getText().toString()));
        }else if(viewHolder.type.getText().toString().equals("Purchase")){
            transaction.setType(TransactionType.PURCHASE);
            transaction.setItemDescription(viewHolder.info.getText().toString());
            transaction.setTransactionInterval(0);
        }else if(viewHolder.type.getText().toString().equals("Individual income")){
            transaction.setType(TransactionType.INDIVIDUALINCOME);
            transaction.setItemDescription(null);
            transaction.setTransactionInterval(0);
        }else if(viewHolder.type.getText().toString().equals("Individual payment")){
            transaction.setType(TransactionType.INDIVIDUALPAYMENT);
            transaction.setItemDescription(viewHolder.info.getText().toString());
            transaction.setTransactionInterval(0);
        }
    }

    //Dialogs
    private void showAlertDialog() {
        dialog = dialogBuilder.create();
        dialog.show();
    }
    private void createDialogBuilder(String s) {
        dialogBuilder.setMessage(s);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
    }
    private void createConfirmationDialogBuilder(String s){
        dialogBuilder.setMessage(s);
        dialogBuilder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(transaction==null){
                            addTransaction();
                        }else{
                            editTransaction();
                        }
                        dialog.cancel();
                    }
                });

        dialogBuilder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

    }


    //Needed checks before edit/add
    private boolean checkTitle(String title) {
        if(title.length()<3 || title.length()>15){
            return false;
        }
        return true;
    }
    private boolean checkType(String type){
        if(type.equals("Purchase") || type.equals("Regular income") || type.equals("Regular payment") || type.equals("Individual income") || type.equals("Individual payment")){
            return true;
        }
        return false;
    }
    private boolean checkInfo(String info) {
        if(info.length()>0 && !info.equals("/")){
            return !viewHolder.type.getText().toString().equals("Regular income") && !viewHolder.type.getText().toString().equals("Individual income");
        }else {
            return viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Individual income");
        }
    }
    private boolean checkDate(String date) {
        DateValidator dateValidator=new DateValidator();
        if(dateValidator.isThisDateValid(date,"dd.MM.yyyy.")){
            return true;
        }else return dateValidator.isThisDateValid(date, "dd/MM/yyyy");
    }
    private boolean checkInterval(String interval) {
        try{
            if(viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Regular payment")){
                return !interval.equals("") && !interval.equals("/") && Integer.parseInt(interval) > 0;
            }else{
                return interval.equals("") || interval.equals("/") || Integer.parseInt(interval) <= 0;
            }
        }catch (NumberFormatException e){
            return false;
        }

    }
    private boolean checkAmount(String amount) {
        try {
            Double.parseDouble(amount);
            if(Double.parseDouble(amount)<=0){
                return false;
            }
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }
    private boolean checkIfTitleExist(String title){
        return getPresenter().checkIfTitleExist(viewHolder.title.getText().toString());
    }


    private boolean checkMonthLimit(String date){
        if(viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Individual income")){
            return true;
        }else {
            return !((Double.parseDouble(viewHolder.amount.getText().toString()) + getPresenter().getSumOfTransactionsByMonth(date)) > account.getMonthLimit());
        }
    }


    private boolean checkTotalLimit(){

        if(transaction==null){      //Add
            if(viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Individual income")){
                return true;
            }else if(viewHolder.type.getText().toString().equals("Purchase") || viewHolder.type.getText().toString().equals("Individual payment")){
                if(Double.parseDouble(viewHolder.amount.getText().toString())*(-1)+getPresenter().getSumOfAllTransactions()<account.getTotalLimit()*(-1)) {
                    return false;
                }
            }else if(viewHolder.type.getText().toString().equals("Regular payment")){
                if (getRegulaPaymentAmount()*(-1) + getPresenter().getSumOfAllTransactions() < account.getTotalLimit() * (-1)) {
                    return false;
                }
            }
        }else {
            //Edit
            double oldSum=transaction.getAmount();
            if(transaction.getType()==TransactionType.REGULARPAYMENT || transaction.getType()==TransactionType.REGULARINCOME){
                oldSum=getRegularAmount(transaction);
            }
            double newSum=Double.parseDouble(viewHolder.amount.getText().toString());
            if(viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Regular payment")){
                newSum=getRegulaPaymentAmount();
            }
            double difference;

            if(transaction.getType()==TransactionType.REGULARINCOME || transaction.getType()==TransactionType.INDIVIDUALINCOME){
                if(viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Individual income")){
                    difference=newSum-oldSum;
                }else{
                    difference=oldSum*(-1)-newSum;
                }
            }else{
                if(viewHolder.type.getText().toString().equals("Regular income") || viewHolder.type.getText().toString().equals("Individual income")){
                    difference=newSum+oldSum;
                }else{
                    difference=newSum*(-1)+oldSum;
                }
            }
            if(difference==0) return true;
            if(difference+getPresenter().getSumOfAllTransactions()<account.getTotalLimit()*(-1)){
                return false;
            }
        }
        return true;
    }

    private double getRegulaPaymentAmount() {
        double sum=0;
        int interval= Integer.parseInt(viewHolder.interval.getText().toString());
        double amount=Double.parseDouble(viewHolder.amount.getText().toString());
        Date date = null;
        Date endDate=null;
        Calendar date1 = Calendar.getInstance();
        Calendar date2 =Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/mm/yyyy",Locale.getDefault());
        try {
            date=sdf1.parse(viewHolder.date.getText().toString());
//            date1.setTime(date);
        }catch (ParseException e){
            try {
                date=sdf2.parse(viewHolder.date.getText().toString());
//                date1.setTime(date);
            } catch (ParseException ignored) {

            }
        }
        date1.setTime(date);
        try {
            endDate=sdf1.parse(viewHolder.endDate.getText().toString());
//            date2.setTime(endDate);
        }catch (ParseException e){
            try {
                endDate=sdf2.parse(viewHolder.endDate.getText().toString());
//                date2.setTime(endDate);
            } catch (ParseException ignored) {
                endDate=null;
            }
        }
        date2.setTime(endDate);
        while (date1.before(date2)){
            sum+=amount;
            date1.add(Calendar.DATE,interval);
        }
        return sum;
    }


    private boolean checkIfEndDateAfter()  {
        Date date;
        Date endDate;
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/mm/yyyy",Locale.getDefault());
        try {
            date=sdf1.parse(viewHolder.date.getText().toString());
        }catch (ParseException e){
            try {
                date=sdf2.parse(viewHolder.date.getText().toString());
            } catch (ParseException ex) {
                return false;
            }
        }
        try {
            endDate=sdf1.parse(viewHolder.endDate.getText().toString());
        }catch (ParseException e){
            try {
                endDate=sdf2.parse(viewHolder.endDate.getText().toString());
            } catch (ParseException ex) {
                return false;
            }
        }
        return endDate.after(date);
    }

    //To avoid duplicating code in TextWatchers
    private void setIfIncome(){
        viewHolder.type.setBackgroundColor(Color.argb(200,0,255,0));
        viewHolder.info.setFocusable(false);
        viewHolder.info.setEnabled(false);
        viewHolder.info.setText("/");
        viewHolder.info.setBackgroundResource(R.drawable.rounded_green);
        if(checkAmount(viewHolder.amount.getText().toString())) {
            viewHolder.amount.setBackgroundColor(Color.argb(100, 0, 255, 0));
        } else viewHolder.amount.setBackgroundColor(Color.argb(255,255,0,0));
    }
    private void setIfRegular(){
        viewHolder.endDate.setFocusable(true);
        viewHolder.endDate.setEnabled(true);
        viewHolder.endDate.setFocusableInTouchMode(true);
        if(viewHolder.endDate.getText().toString().equals("/")){
            viewHolder.endDate.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.SRC_ATOP);
        }
        try{
            if(viewHolder.interval.getText().toString().equals("/") || Integer.parseInt(viewHolder.interval.getText().toString())==0){
                viewHolder.interval.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.SRC_ATOP);
            }
        }catch (NumberFormatException e){
            viewHolder.interval.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.SRC_ATOP);
        }

        viewHolder.interval.setFocusable(true);
        viewHolder.interval.setEnabled(true);
        viewHolder.interval.setFocusableInTouchMode(true);

    }
    private void setIfNotRegular(){
        viewHolder.interval.setFocusable(false);
        viewHolder.interval.setEnabled(false);
        viewHolder.interval.setText("0");
        viewHolder.interval.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.SRC_ATOP);
        viewHolder.endDate.setFocusable(false);
        viewHolder.endDate.setEnabled(false);
        viewHolder.endDate.setText("/");
        viewHolder.endDate.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.SRC_ATOP);
        if(checkAmount(viewHolder.amount.getText().toString())) {
            viewHolder.amount.setBackgroundColor(Color.argb(100, 0, 255, 0));
        } else viewHolder.amount.setBackgroundColor(Color.argb(255,255,0,0));
    }

    private TextWatcher titleTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(checkTitle(s.toString()))
                viewHolder.title.setBackgroundColor(Color.argb(100,0,255,0));
            else
                viewHolder.title.setBackgroundColor(Color.argb(200,255,51,51));
        }
    };

    private TextWatcher amountTextWather=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try{
                if(checkAmount(s.toString())) {
                    viewHolder.amount.setBackgroundColor(Color.argb(100, 0, 255, 0));
                } else viewHolder.amount.setBackgroundColor(Color.argb(255,255,0,0));
            }catch (NumberFormatException e){
                viewHolder.amount.setBackgroundColor(Color.argb(255,255,0,0));
            }
        }
    };

    private TextWatcher typeTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().equals("Regular income")){
                setIfIncome();
                setIfRegular();
            }else if(s.toString().equals("Regular payment")){
                setIfRegular();
                viewHolder.type.setBackgroundColor(Color.argb(255,0,255,0));
                viewHolder.info.setFocusable(true);
                viewHolder.info.setEnabled(true);
                viewHolder.info.setFocusableInTouchMode(true);
                if(viewHolder.info.getText().toString().equals("/")){
                    viewHolder.info.setBackgroundResource(R.drawable.rounded_red);
                }
            }else if(s.toString().equals("Purchase")){
                setIfNotRegular();
                viewHolder.type.setBackgroundColor(Color.argb(255,0,255,0));
                viewHolder.info.setFocusable(true);
                viewHolder.info.setEnabled(true);
                viewHolder.info.setFocusableInTouchMode(true);
                if(viewHolder.info.getText().toString().equals("/")){
                    viewHolder.info.setBackgroundResource(R.drawable.rounded_red);
                }
            }else if(s.toString().equals("Individual income")){
                setIfIncome();
                setIfNotRegular();
            }else if(s.toString().equals("Individual payment")){
                setIfNotRegular();
                viewHolder.type.setBackgroundColor(Color.argb(200,0,255,0));
                viewHolder.info.setFocusable(true);
                viewHolder.info.setEnabled(true);
                viewHolder.info.setFocusableInTouchMode(true);
                if(viewHolder.info.getText().toString().equals("/")){
                    viewHolder.info.setBackgroundResource(R.drawable.rounded_red);
                }
            }else{
                viewHolder.type.setBackgroundColor(Color.argb(255,255,0,0));
//                viewHolder.type.setBackgroundColor(Color.RED);
            }
        }
    };

    private TextWatcher infoTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().equals("") || s.toString().equals("/")){
                viewHolder.info.setBackgroundResource(R.drawable.rounded_red);
            }else{
                viewHolder.info.setBackgroundResource(R.drawable.rounded_green);
            }
        }
    };

    private TextWatcher dateTextWatcher=new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(checkDate(s.toString())){
                viewHolder.date.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.SRC_ATOP);
            }else{
                viewHolder.date.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.SRC_ATOP);
            }
        }
    };

    private TextWatcher endDateTextWatcher=new TextWatcher() {

        DateValidator dateValidator=new DateValidator();

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(dateValidator.isThisDateValid(s.toString(),"dd.MM.yyyy.")){
                viewHolder.endDate.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.SRC_ATOP);
            }else if(dateValidator.isThisDateValid(s.toString(),"dd/MM/yyyy")){
                viewHolder.endDate.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.SRC_ATOP);
            }else{
                viewHolder.endDate.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.SRC_ATOP);
            }
        }
    };

    private TextWatcher intervalTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            try{
                Integer.parseInt(s.toString());
                if(s.toString().equals("") || Integer.parseInt(s.toString())<=0){
                    viewHolder.interval.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.SRC_ATOP);
                }else{
                    viewHolder.interval.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.SRC_ATOP);
                }
            }catch (NumberFormatException e){
                viewHolder.interval.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.SRC_ATOP);
            }
        }
    };

    //Depends of the mode selected
    private void enableEditText(boolean enabled) {

        if(transaction!=null && enabled){
            if(transaction.getType()==TransactionType.INDIVIDUALINCOME || transaction.getType()==TransactionType.REGULARINCOME){
                viewHolder.info.setBackgroundResource(R.drawable.rounded);
            }
        }

        viewHolder.title.setEnabled(enabled);
        viewHolder.title.setFocusable(enabled);
        viewHolder.title.setFocusableInTouchMode(enabled);

        viewHolder.amount.setFocusable(enabled);
        viewHolder.amount.setEnabled(enabled);
        viewHolder.amount.setFocusableInTouchMode(enabled);

        viewHolder.info.setFocusable(enabled);
        viewHolder.info.setEnabled(enabled);
        viewHolder.info.setFocusableInTouchMode(enabled);

        viewHolder.type.setFocusable(enabled);
        viewHolder.type.setEnabled(enabled);
        viewHolder.type.setFocusableInTouchMode(enabled);

        viewHolder.date.setFocusable(enabled);
        viewHolder.date.setEnabled(enabled);
        viewHolder.date.setFocusableInTouchMode(enabled);

        viewHolder.endDate.setFocusable(enabled);
        viewHolder.endDate.setEnabled(enabled);
        viewHolder.endDate.setFocusableInTouchMode(enabled);

        viewHolder.interval.setFocusable(enabled);
        viewHolder.interval.setEnabled(enabled);
        viewHolder.interval.setFocusableInTouchMode(enabled);
    }

    @Override
    public void onResume(){
        super.onResume();
        ConnectivityBroadcastReceiver.getObservable().addObserver(this);
        if(addMode){
            setAddDefault();
        }
    }

    public void onPause(){
        super.onPause();
        ConnectivityBroadcastReceiver.getObservable().deleteObserver(this);
    }

    @Override
    public void onAdd() {

        if(transaction.getType()==TransactionType.INDIVIDUALINCOME){
            getAccountPresenter().updateBudget(account,transaction.getAmount(),0);
        }else if(transaction.getType()==TransactionType.PURCHASE || transaction.getType()==TransactionType.INDIVIDUALPAYMENT){
            getAccountPresenter().updateBudget(account,transaction.getAmount()*(-1),0);
        }else if(transaction.getType()==TransactionType.REGULARINCOME){
            getAccountPresenter().updateBudget(account,getRegularAmount(transaction),0);
        }else if(transaction.getType()==TransactionType.REGULARPAYMENT){
            getAccountPresenter().updateBudget(account,getRegularAmount(transaction) * (-1),0);
        }
        onTransactionAdd.onTransactionAdded();
        if(landMode){
            setEmpty();
        }else{
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onEdit() {
        onTransactionEdit.onTransactionEdited();
        enableEditText(false);
        setDefaultColors();
    }

    @Override
    public void onDelete() {
        onTransactionDelete.onTransactionDeleted();
        if(landMode){
            setEmpty();
        }else{
            getFragmentManager().popBackStack();
        }
    }

    public void onUpdated(){
        if(viewHolder.offlineText.equals("Offline brisanje")){
            setEmpty();
        }
        viewHolder.offlineText.setText("");
    }

    @Override
    public void update(Observable o, Object arg) {
        if(ConnectivityBroadcastReceiver.isConnected()){
            getPresenter().getTransactions();
            if(account!=null){
                getAccountPresenter().updateAccount(account);
            }
//            if(ConnectivityBroadcastReceiver.alreadyChanged()){
//                getPresenter().updateTransactions();
//            }
            getPresenter().updateTransactions();

        }else{
            if(addMode || (landMode && transaction==null)){
                viewHolder.offlineText.setText("Offline dodavanje");
            }else if(transaction!=null && transaction.getDelete()==1){
                viewHolder.offlineText.setText("Offline brisanje");
            }else if(clickedEdit){
                viewHolder.offlineText.setText("Offline izmjena");
            }
        }
    }

}
