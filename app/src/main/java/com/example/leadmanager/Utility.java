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

}
