package com.dminer.utils;

import java.io.File;

public class UtilFilesStorage {

	
	public static boolean createDirectory(String path) {
        if (! new File(path).exists()) {
            return (new File(path)).mkdirs();
        }
        return true;
    }    
    
    public static String getNomeArquivo(String arq) {
        arq = arq.replace("\\", "/");
        String[] explode = arq.split("/");
        String nomeArq = explode[explode.length-1];
        return nomeArq;
    }


    public static String getProjectPath() {
        return System.getProperty("user.dir");
    }
}
