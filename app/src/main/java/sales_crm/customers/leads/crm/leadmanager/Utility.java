package sales_crm.customers.leads.crm.leadmanager;

import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.SecretKey;

public class Utility {

    public void addToHistory(byte[] plaintext, SecretKey key) throws Exception {

    }

    public static Long getCurrentTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        //Log.v("dipak", TimeZone.getDefault() + "");
        Log.v("dipak", "" + c.getTimeInMillis());
        return c.getTimeInMillis() / 1000;
    }

    public static Long getMidNightTimeStamp() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        //Log.v("dipak", TimeZone.getDefault() + "");
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis() / 1000;
    }

    public static Long getMidNightTimeStampByMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        //Log.v("dipak", TimeZone.getDefault() + "");
        c.set(year, month-1, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        //c.set(Calendar.MONTH, month);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis() / 1000;
    }

}
