package com.dminer.dminer.utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class UtilFilesStorage {

	
	public static boolean createDirectory(String path) {
        if (! new File(path).exists()) {
            return (new File(path)).mkdir();
        }
        return true;
    }


    public static boolean saveImageIn(String outputPath, String newName, byte[] bytesImage) {
        return saveImageIn(outputPath, newName, "jpg", bytesImage);
    }


    public static boolean saveImageIn(String outputPath, String newName, String extension, byte[] bytesImage) {
        InputStream is = new ByteArrayInputStream(bytesImage);
        try {
            BufferedImage newBi = ImageIO.read(is);
            return saveImageIn(outputPath, newName, extension, newBi);
        }
        catch(IOException e) {
            return false;
        }
    }


    public static boolean saveImageIn(String outputPath, String newName, String extension, BufferedImage bufferedImage) {
        String path = tratarExtensao(newName, extension);
        File file = new File(outputPath);
        System.out.println("Salvando imagem em: " + file.getAbsolutePath() + "\\" + path);
        String newPath = file.getAbsolutePath() + "\\" + path;
        try { ImageIO.write(bufferedImage, extension, new File(newPath)); }
        catch(IOException e) {
            return false;
        }
        return true;
    }
    
    
    public static boolean deleteImageIn(String outputPath, String name) {
        File file = new File(outputPath + "\\" + name);
        System.out.println("Deletando imagem em: " + file.getAbsolutePath());
        return file.delete();
    }


    public static byte[] loadImageIn(String outputPath, String name) throws IOException {
        File file = new File(outputPath + "\\" + name);
        System.out.println("Carregando imagem em: " + file.getAbsolutePath());
        return getBytes(ImageIO.read(file));
    }


    public static boolean checkImageIn(String outputPath, String name) {
        File file = new File(outputPath + "\\" + name);
        System.out.println("Verificando imagem em: " + file.getAbsolutePath());
        return file.exists();
    }


    public static byte[] getBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }


    public BufferedImage redimensionar(String file, int width, int height) throws IOException {    
        return redimensionar(new File(file), width, height);
    }
    
    
    public BufferedImage redimensionar(File file, int width, int height) throws IOException {
    	BufferedImage img = ImageIO.read(file);
        return redimensionar(img, width, height);
    }
    

    public BufferedImage redimensionar(byte[] file, int width, int height) throws IOException {
        InputStream is = new ByteArrayInputStream(file);
        BufferedImage img = ImageIO.read(is);
        return redimensionar(img, width, height);
    }


    private BufferedImage cropImage(BufferedImage src, int width, int height) {
        return src.getSubimage(0, 0, width, height);
    }
    
    
    public BufferedImage redimensionar(BufferedImage img, int width, int height) throws IOException {
        if (img == null) throw new IOException("Imagem nula ou incompátivel!");
        int largura = img.getWidth();
        int altura = img.getHeight();
        if (largura < width) width = largura;
        if (altura < height) height = altura;

        BufferedImage tmp = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = tmp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, width, height, null);
        g2.dispose();
        BufferedImage newImage = tmp.getSubimage(0, 0, width, height);
        return cropImage(newImage, width, height);
    }

        
    private static String tratarExtensao(String name, String extension) {
        name = name.replace("\\", "/");

        String[] explode = name.split("/");
        String nomeArq = explode[explode.length-1];
        
        String[] arNomeArq = nomeArq.split("\\.");
        
        String extensao = arNomeArq[arNomeArq.length-1];
        if (extensao.length() == 3 || extensao.length() == 4) {
            if (extensao.contains("jpg") || extensao.contains("jpeg") || extensao.contains("png")) {
                return nomeArq.replace("/", "\\");
            }
        }
        return arNomeArq[0] + "." + extension;
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
