package com.xevensolutions.baseapp.utils;

public class FileUtils {

    public static String getFileNameFromPath(String path) {
        String filename = path.substring(path.lastIndexOf("/") + 1);
        return filename;
    }

}
