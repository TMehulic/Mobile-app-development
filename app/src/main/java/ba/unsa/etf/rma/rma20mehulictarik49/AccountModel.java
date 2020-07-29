package ba.unsa.etf.rma.rma20mehulictarik49;

public class AccountModel {

    private static Account account;

    private static void create() {
        account=new Account(9000,5000,3000);
    }

    public static Account get(){
        if(account==null){
            create();
        }
        return account;
    }

}
