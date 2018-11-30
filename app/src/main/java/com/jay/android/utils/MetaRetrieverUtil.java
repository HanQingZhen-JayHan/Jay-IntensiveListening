package com.jay.android.utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.media.MediaMetadataRetriever;

public class MetaRetrieverUtil implements LifecycleObserver {
    private static MetaRetrieverUtil instance = new MetaRetrieverUtil();
    private MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    private MetaRetrieverUtil() {
    }

    public static MetaRetrieverUtil getInstance() {
        if (instance == null) {
            instance = new MetaRetrieverUtil();
        }
        if(instance.retriever == null){
            instance.retriever = new MediaMetadataRetriever();
        }
        return instance;
    }

    public boolean hasAudio(String filePath) {
        try {
            retriever.setDataSource(filePath);
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void release() {
        if (retriever != null) {
            retriever.release();
            retriever =  null;
        }
    }
}
