package ba.unsa.etf.rma.rma20mehulictarik49;

import android.database.Cursor;

import java.util.ArrayList;

public interface ITransactionListView {

    void setTransactions(ArrayList<Transaction> transactions);
    void notifyTransactionListDataSetChanged();
    void refreshBudget();
    void setCursor(Cursor cursor);
}
