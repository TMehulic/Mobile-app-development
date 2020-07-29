package ba.unsa.etf.rma.rma20mehulictarik49;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Transaction implements Comparable<Transaction>, Parcelable {

    private Calendar date;
    private double amount;
    private String title;
    private TransactionType type;
    private String itemDescription;
    private int transactionInterval;
    private Calendar endDate;
    private int id;
    private int internalId;
    private int add;
    private int edit;
    private int delete;

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }

    public int getEdit() {
        return edit;
    }

    public void setEdit(int edit) {
        this.edit = edit;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    public int getInternalId() {
        return internalId;
    }

    public void setInternalId(int internalId) {
        this.internalId = internalId;
    }

    public Transaction(Calendar date, double amount, String title, TransactionType type, String itemDescription, int transactionInterval, Calendar endDate, int id, int internalId, int add, int edit, int delete) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
        this.id = id;
        this.internalId = internalId;
        this.add = add;
        this.edit = edit;
        this.delete = delete;
    }

    public Transaction(Calendar date, double amount, String title, TransactionType type, String itemDescription, int transactionInterval, Calendar endDate, int id, int internalId) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
        this.id = id;
        this.internalId = internalId;
    }

    public Transaction(Calendar date, double amount, String title, TransactionType type, String itemDescription, int transactionInterval, Calendar endDate) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
    }

    //If "Add transaction" selected
    public Transaction(){
        this.id=0;
        this.date = null;
        this.amount = 0;
        this.title = null;
        this.type = null;
        this.itemDescription = null;
        this.transactionInterval = 0;
        this.endDate = null;
    }


    public static final Creator<Transaction> CREATOR =new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    protected Transaction (Parcel in){
        id=in.readInt();
        amount=in.readDouble();
        title=in.readString();
        itemDescription=in.readString();
        transactionInterval=in.readInt();
        type=TransactionType.values()[in.readInt()];

        long dateMillis=in.readLong();
        long endDateMillis=in.readLong();
        String dateTimezone_id=in.readString();
        String endDateTimezone_id=in.readString();

        date=new GregorianCalendar(TimeZone.getTimeZone(dateTimezone_id));
        date.setTimeInMillis(dateMillis);

        if(endDateTimezone_id!=null){
            endDate=new GregorianCalendar(TimeZone.getTimeZone(endDateTimezone_id));
            endDate.setTimeInMillis(endDateMillis);
        }else{
            endDate=null;
        }


    }

    public Transaction(int id, Calendar c1, String title, double amount, String itemDesc, int interval, Calendar c2, TransactionType tip) {
        this.date = c1;
        this.amount = amount;
        this.title = title;
        this.type = tip;
        this.itemDescription = itemDesc;
        this.transactionInterval = interval;
        this.endDate = c2;
        this.id=id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(amount);
        dest.writeString(title);
        dest.writeString(itemDescription);
        dest.writeInt(transactionInterval);
        dest.writeInt(type.ordinal());

        long dateMillis=date.getTimeInMillis();
        String dateTimezone_id=date.getTimeZone().getID();
        dest.writeLong(dateMillis);
        dest.writeString(dateTimezone_id);

        if(endDate!=null){
            long endDateMillis=endDate.getTimeInMillis();
            String endDateTimezone_id=endDate.getTimeZone().getID();

            dest.writeLong(endDateMillis);
            dest.writeString(endDateTimezone_id);
        }else{
            long endDateMillis=-1;
            String endDateTimezone_id=null;

            dest.writeLong(endDateMillis);
            dest.writeString(endDateTimezone_id);
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(int transactionInterval) {
        this.transactionInterval = transactionInterval;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }


    @Override
    public int compareTo(Transaction o) {
        return 0;
    }

    public static class Comparators {
        public static Comparator<Transaction> PRICEASC=new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
               return Double.compare(t1.amount,t2.amount);
            }
        };

        public static Comparator<Transaction> PRICEDESC=new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return Double.compare(t2.amount,t1.amount);
            }
        };

        public static Comparator<Transaction> TITLEASC=new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return t1.title.compareTo(t2.title);
            }
        };

        public static Comparator<Transaction> TITLEDESC=new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return t2.title.compareTo(t1.title);
            }
        };

        public static Comparator<Transaction> DATEASC= new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return t1.date.compareTo(t2.date);
            }
        };

        public static Comparator<Transaction> DATEDESC= new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return t2.date.compareTo(t1.date);
            }
        };
    }
}
