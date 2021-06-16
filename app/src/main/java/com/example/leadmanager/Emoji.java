package com.example.leadmanager;

import android.app.Application;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class Emoji extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EmojiManager.install(new GoogleEmojiProvider());
    }
}
