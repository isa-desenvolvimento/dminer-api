package com.dminer.constantes;

import java.nio.file.Paths;

public class Constantes {
    
    public static final String ROOT_UPLOADS = Paths.get("uploads") + "";
    
    public static final String ROOT_FILES = Paths.get("files") + "";

    public static String appendInRoot(String directory) {
        return ROOT_UPLOADS + "\\" + directory;
    }

    public static String appendIn(String root, String directory) {
        return root + "\\" + directory;
    }
}
