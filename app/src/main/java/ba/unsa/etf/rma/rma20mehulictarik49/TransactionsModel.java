package ba.unsa.etf.rma.rma20mehulictarik49;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionsModel {

    private static ArrayList<Calendar> calendarsDate=new ArrayList<>();
    private static ArrayList<Calendar> calendarsEnd=new ArrayList<>();
    private static ArrayList<Transaction> transactions=new ArrayList<>();
    private static boolean created=false;

    private static void create(){
        created=true;
        for(int i=0;i<20;i++){
            calendarsDate.add(i,Calendar.getInstance());
            calendarsEnd.add(i,Calendar.getInstance());
            calendarsDate.get(i).set(2020,i%6+1,i+1);
            calendarsEnd.get(i).set(2021+i%2,i%6+5,i+3);
        }

        transactions.add(new Transaction(calendarsDate.get(0),1000,"Power bills",TransactionType.REGULARPAYMENT,"Monthly paid power bills.",30,calendarsEnd.get(1)));
        transactions.add(new Transaction(calendarsDate.get(1),520,"Water bills",TransactionType.REGULARPAYMENT,"Monthly paid water bills.",29,calendarsEnd.get(3)));
        transactions.add(new Transaction(calendarsDate.get(2),2000,"Salary",TransactionType.REGULARINCOME,null,31,calendarsEnd.get(4)));
        transactions.add(new Transaction(calendarsDate.get(3),250,"Bonus",TransactionType.INDIVIDUALINCOME,null,0,null));
        transactions.add(new Transaction(calendarsDate.get(4),10000,"Bingo",TransactionType.INDIVIDUALINCOME,null,0,null));
        transactions.add(new Transaction(calendarsDate.get(5),2500,"Car store",TransactionType.PURCHASE,"Brand new car.",0,null));
        transactions.add(new Transaction(calendarsDate.get(6),50,"Grocery store",TransactionType.INDIVIDUALPAYMENT,"Some sweet candies.",0,null));
        transactions.add(new Transaction(calendarsDate.get(7),1540,"Gift",TransactionType.INDIVIDUALINCOME,null,0,null));
        transactions.add(new Transaction(calendarsDate.get(8),30,"Internet",TransactionType.REGULARPAYMENT,"Monthly paid internet.",29,calendarsEnd.get(5)));
        transactions.add(new Transaction(calendarsDate.get(9),500,"Store",TransactionType.PURCHASE,"Brand new vacuum cleaner.",0,null));
        transactions.add(new Transaction(calendarsDate.get(10),1010,"Birthday gift",TransactionType.INDIVIDUALINCOME,null,0,null));
        transactions.add(new Transaction(calendarsDate.get(11),120,"Cleaners",TransactionType.REGULARPAYMENT,"Service that cleans house.",30,calendarsEnd.get(8)));
        transactions.add(new Transaction(calendarsDate.get(12),130,"Wood shop",TransactionType.PURCHASE,"Wooden door.",0,null));
        transactions.add(new Transaction(calendarsDate.get(13),1100,"Charity",TransactionType.INDIVIDUALPAYMENT,"Donation to Covid-19 cure.",0,null));
        transactions.add(new Transaction(calendarsDate.get(14),200,"Party",TransactionType.INDIVIDUALPAYMENT,"Organised party.",0,null));
        transactions.add(new Transaction(calendarsDate.get(15),125,"Web sales",TransactionType.REGULARINCOME,null,29,calendarsEnd.get(9)));
        transactions.add(new Transaction(calendarsDate.get(16),100,"PS4 game",TransactionType.PURCHASE,"GTA V for PS4.",0,null));
        transactions.add(new Transaction(calendarsDate.get(17),25,"Netflix",TransactionType.REGULARPAYMENT,"Game of Thrones new episodes.",30,calendarsEnd.get(17)));
        transactions.add(new Transaction(calendarsDate.get(18),210,"Awards",TransactionType.REGULARINCOME,null,31,calendarsEnd.get(18)));
        transactions.add(new Transaction(calendarsDate.get(19),5,"Bakery",TransactionType.INDIVIDUALPAYMENT,"Cookie.",0,null));
    }
    public static ArrayList<Transaction> get(){

        if(!created){
            create();
        }
        return transactions;
    }

}
