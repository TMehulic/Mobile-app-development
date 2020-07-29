package ba.unsa.etf.rma.rma20mehulictarik49;

import java.util.Calendar;

public interface ITransactionListPresenter {
    void filterTransactions(Calendar calendar, String filter, String sort, boolean isConnected);
}
