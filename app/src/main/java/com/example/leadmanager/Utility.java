package com.example.leadmanager;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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

}