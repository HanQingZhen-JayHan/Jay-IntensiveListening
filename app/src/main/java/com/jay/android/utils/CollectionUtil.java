package com.jay.android.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CollectionUtil {

    public static List<String>  convertSet2ArrayList(Set<String> set) {
        List<String> list = new ArrayList<>();
        if (set == null || set.isEmpty()) {
            return list;
        }
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }
}
