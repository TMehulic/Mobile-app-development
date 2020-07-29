package ba.unsa.etf.rma.rma20mehulictarik49;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateValidator {



        public boolean isThisDateValid(String dateToValidate, String dateFromat){

            if(dateToValidate == null){
                return false;
            }


            SimpleDateFormat sdf = new SimpleDateFormat(dateFromat, Locale.getDefault());
            sdf.setLenient(false);

            try {

                //if not valid, it will throw ParseException
                Date date = sdf.parse(dateToValidate);
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(date);
                if(calendar.get(Calendar.YEAR)<1920 ||calendar.get(Calendar.YEAR)>2120){
                    return false;
                }

            } catch (ParseException e) {

                return false;
            }


            return true;
        }

    }

