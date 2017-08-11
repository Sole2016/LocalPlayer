package com.zy.vplayer.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    public static void closeIo(Closeable... closeable){
        for (Closeable item : closeable) {
            if (item != null) {
                try {
                    item.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
