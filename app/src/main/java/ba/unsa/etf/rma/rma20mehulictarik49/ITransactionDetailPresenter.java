package ba.unsa.etf.rma.rma20mehulictarik49;


public interface ITransactionDetailPresenter {

    void editTransaction(Transaction newTransaction);
    void deleteTransaction();
    void addTransaction(Transaction transaction);
    double getSumOfTransactionsByMonth(String date);
    double getSumOfAllTransactions();
    boolean checkIfTitleExist(String title);

}
