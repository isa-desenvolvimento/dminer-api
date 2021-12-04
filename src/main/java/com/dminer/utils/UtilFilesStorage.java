package com.dminer.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class UtilFilesStorage {

		
	public final static String separator = File.pathSeparator;
	
	public static boolean createDirectory(String path) {
        if (! new File(path).exists()) {
            return (new File(path)).mkdirs();
        }
        return true;
    }    
    
    public static String getNomeArquivo(String arq) {
        //arq = arq.replace("\\", "/");
        String[] explode = arq.split(separator);
        String nomeArq = explode[explode.length-1];
        return nomeArq;
    }


    public static String getProjectPath() {
        return System.getProperty("user.dir");
    }
    
    
    public static boolean fileExists(String path, String nameFile) {
        return new File(path + separator + nameFile).exists();
    }
    
    
    public static void saveFile(String path, String nameFile) {
        new File(path + separator + nameFile);
    }
    
    
    public static void saveImage(String path, String formatName, BufferedImage image) throws IOException {
    	ImageIO.write(image, formatName, new File(path));
    }
    
    
    public static void saveImage(String pathAbsolute, BufferedImage image) throws IOException {
    	try {
    		ByteArrayOutputStream os = new ByteArrayOutputStream();
    		ImageIO.write(image, "png", os);
    		InputStream is = new ByteArrayInputStream(os.toByteArray());
    		
    		Path path = Paths.get(pathAbsolute);
    		Path path2 = path.resolve(pathAbsolute);
    		if (! Files.exists(path2)) {    			
    			Files.copy(is, path2);
    		}
    	} catch (Exception e) {}
    }
}
