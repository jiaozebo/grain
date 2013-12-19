 package com.ba.grain; 

 

import java.io.File; 

import java.io.FileInputStream; 

import java.io.FileNotFoundException; 

import java.io.IOException; 

 

import android.content.Context; 

import android.os.Environment; 

 

public class FileHelper { 

    private Context context; 
    private boolean hasSD = false; 
    private String SDPATH; 
    private String FILESPATH; 
 

    public FileHelper(Context context) { 

        this.context = context; 

        hasSD = Environment.getExternalStorageState().equals( 

                android.os.Environment.MEDIA_MOUNTED); 

        SDPATH = Environment.getExternalStorageDirectory().getPath(); 

        FILESPATH = this.context.getFilesDir().getPath(); 
        System.out.println("hasSD"+hasSD+"SDPATH"+SDPATH+"FILESPATH"+FILESPATH);

    } 

    public File createSDFile(String fileName) throws IOException { 

        File file = new File(SDPATH + "//" + fileName); 

        if (!file.exists()) { 

            file.createNewFile(); 

        } 

        return file; 

    } 

    public boolean deleteSDFile(String fileName) { 

        File file = new File(SDPATH + "//" + fileName); 

        if (file == null || !file.exists() || file.isDirectory()) 

            return false; 

        return file.delete(); 

    } 

 

    public String readSDFile(String fileName) { 

        StringBuffer sb = new StringBuffer(); 

        File file = new File(SDPATH + "//" + fileName); 

        try { 

            FileInputStream fis = new FileInputStream(file); 

            int c; 

            while ((c = fis.read()) != -1) { 

                sb.append((char) c); 

            } 

            fis.close(); 

        } catch (FileNotFoundException e) { 

            e.printStackTrace(); 

        } catch (IOException e) { 

            e.printStackTrace(); 

        } 

        return sb.toString(); 

    } 
    public String getFILESPATH() { 

        return FILESPATH; 

    } 
    public String getSDPATH() { 

        return SDPATH; 

    } 

    public boolean hasSD() { 

        return hasSD; 

    } 
} 