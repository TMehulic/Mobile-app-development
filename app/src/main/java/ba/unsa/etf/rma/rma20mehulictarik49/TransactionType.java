package ba.unsa.etf.rma.rma20mehulictarik49;

import java.util.ArrayList;
import java.util.List;

public enum TransactionType {
    INDIVIDUALPAYMENT,
    REGULARPAYMENT,
    PURCHASE,
    INDIVIDUALINCOME,
    REGULARINCOME;

    public static List<String> getTypesForFilter(){
        ArrayList<String> types=new ArrayList<>();
        types.add(0,"Filter by");
        types.add("All transactions");
        types.add("Individual payment");
        types.add("Regular payment");
        types.add("Purchase");
        types.add("Individual income");
        types.add("Regular income");
        return types;
    }

}

