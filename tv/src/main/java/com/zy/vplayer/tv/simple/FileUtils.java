package com.zy.vplayer.tv.simple;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 1.获取手机目录所有的音乐文件
 * 2.通过首页展示
 * 3.设计首页加载的框架
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * @param isExternalStorage false 获取内置存储路径
     *                          true  获取外置存储路径
     */
    public static String getStoragePath(Context mContext, boolean isExternalStorage) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (isExternalStorage == removable) {
                    return path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    public static List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }


    /**
     * 遍历文件目录下的所有文件
     *
     * @param file 需要扫描的文件目录
     */
    private static void searchFile(File file) {
        if (file.isDirectory()) {
            Log.e(TAG, "" + file.getAbsolutePath());
            File[] items = file.listFiles();
            for (File item : items) {
                searchFile(item);
            }
        }
    }

    /**
     * 存储对象到本地
     *
     * @param object 需要存储的数据对象
     * @param path   存储文件目录
     * @param name   存储文件名称
     */
    public static void writeDataToFile(Object object, String path, String name) {
        File dir = new File(path);
        File saveFile = null;
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                System.err.println("文件目录创建失败");
                return;
            }
        }
        saveFile = new File(dir.getAbsolutePath() + File.separator + name);
        ObjectOutputStream objectOutputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(saveFile);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeIo(objectOutputStream);
            IOUtils.closeIo(outputStream);
        }
    }

    /**
     * 从本地读取数据
     *
     * @param path 文件存放的位置
     * @return data
     */
    public static Object readDataFromFile(String path) {
        File saveFile = new File(path);
        if (!saveFile.exists()) {
            return null;
        }
        ObjectInputStream objectInputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(saveFile);
            objectInputStream = new ObjectInputStream(inputStream);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeIo(objectInputStream);
            IOUtils.closeIo(inputStream);
        }
        return null;
    }

    public static void saveObject(Object obj, String dir) {
        File file = new File(dir + "/cache.obj");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(obj);
            outputStream.flush();
            System.out.println("write object success");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeIo(outputStream);
        }
    }

    public static Object readObject(String dir) {
        File file = new File(dir + "/cache.obj");
        if (!file.exists()) {
            return null;
        }
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(file));
            System.out.println("read object success");
            return inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeIo(inputStream);
        }
        return null;
    }


    public static boolean hasFile(String path){
        return new File(path).exists();
    }

    public static String getFileSize(File file){
        if(file == null || file.isDirectory() || !file.exists()){
            return "0b";
        }
        long length = file.length();
        System.out.println("name="+file.getName()+","+length);
        String unitStr = "";
        //b范围
        if(length < 1000L){
            unitStr = "b";
            return String.format(Locale.CHINA, "%f%s", length,unitStr);
        }
        //kb范围
        if(length < 1000L * 1024L){
            unitStr = "Kb";
            return String.format(Locale.CHINA, "%f%s", length / 1024L,unitStr);
        }
        //mb范围
        if(length < (1000L * 1024L * 1024L)){
            unitStr = "Mb";
            float size = length /1024F /1024F;
            NumberFormat format = NumberFormat.getInstance(Locale.CHINA);
            if(size > 100){
                format.setMaximumFractionDigits(0);
            }else{
                format.setMaximumFractionDigits(1);
            }
            return String.format(Locale.CHINA, "%s%s", format.format(size),unitStr);
        }
        //Gb范围
        if(length < (1000L *1024L *1024L *1024L)){
            unitStr = "Gb";
            float size = length /1024F /1024F /1024F;
            NumberFormat format = NumberFormat.getInstance(Locale.CHINA);
            if(size > 100){
                format.setMaximumFractionDigits(0);
            }else{
                format.setMaximumFractionDigits(2);
            }
            System.out.println("size...."+size+","+format.format(size));
            return String.format(Locale.CHINA, "%s%s", format.format(size),unitStr);
        }
        unitStr = "too large";
        return unitStr;
    }

}
