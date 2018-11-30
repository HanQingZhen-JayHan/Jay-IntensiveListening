package com.jay.android.utils;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File o1, File o2) {
        if(o1 == null || o2 == null){
            return 0;
        }
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
