package com.zy.vplayer.tv.simple;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 扫描本地音乐文件
 */
public class ScanVideoFile {
    private static final String TAG = "ScanVideoFile";
    private static final int COUNT_CHANGE = 0X001;//扫描的数量发生了变化
    private static final int SCAN_COMPLETE = 0X000;//扫描完成

    private String[] mSupportMedia = {".mp4", ".rmvb", ".mkv", ".MP4"};//所支持的音乐格式
    private static ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    });


    private ArrayList<OnScanComplete> callBackList = new ArrayList<>();
    private volatile ArrayList<RecordEntity> mPathList = new ArrayList<>();//扫描到的音乐路径集合
    private ScanHandler mHandler = new ScanHandler(this);//线程回调的handler
    private String mInnerStoragePath;//内部存储路径
    private String mExternalStoragePath;//外部存储路径
    private Runnable mScanTask;

    private static class ScanInstance {
        private static ScanVideoFile scanVideoFile = new ScanVideoFile();
    }

    public static ScanVideoFile getInstance() {
        return ScanInstance.scanVideoFile;
    }

    private ScanVideoFile() {
    }


    public ScanVideoFile setOnScanComplete(OnScanComplete complete) {
        if (!callBackList.contains(complete)) {
            callBackList.add(complete);
        }
        return this;
    }

    public ScanVideoFile scanVideoFile(final Context c) {
        final Context context = c.getApplicationContext();
        if (mInnerStoragePath == null) {
            mInnerStoragePath = FileUtils.getStoragePath(context, false);
            mExternalStoragePath = FileUtils.getStoragePath(context, true);
        }
        synchronized (this) {
            if (mScanTask == null) {
                mScanTask = new Runnable() {
                    @Override
                    public void run() {
                        if (mPathList != null && mPathList.size() > 0) {
                            mPathList.clear();
                        }
                        Log.d(TAG, "run: 扫描开始");
                        searchFile(new File(mInnerStoragePath));
                        Log.e(TAG, "run: 扫描内部存储结束");
                        if (mExternalStoragePath != null) {
                            searchFile(new File(mExternalStoragePath));
                            Log.e(TAG, "run: 扫描外部存储结束");
                        }
                        Log.d(TAG, "run: 扫描结束");
                        mHandler.sendEmptyMessage(SCAN_COMPLETE);
                    }
                };
            }
            executor.submit(mScanTask);
        }
        return this;
    }

    private static class ScanHandler extends Handler {
        private WeakReference<ScanVideoFile> weak;

        private ScanHandler(ScanVideoFile scanMusicFile) {
            this.weak = new WeakReference<>(scanMusicFile);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weak.get() != null) {
                ScanVideoFile scanMusicFile = weak.get();
                switch (msg.what) {
                    case SCAN_COMPLETE:
                        if (scanMusicFile.callBackList.size() > 0) {
                            for (int i = scanMusicFile.callBackList.size() - 1; i >= 0; i--) {
                                OnScanComplete onScanComplete = scanMusicFile.callBackList.get(i);
                                onScanComplete.onComplete(scanMusicFile.mPathList);
                                scanMusicFile.callBackList.remove(onScanComplete);
                            }
                        }
                        break;
                    case COUNT_CHANGE:
                        if (scanMusicFile.callBackList.size() > 0) {
                            for (OnScanComplete onScanComplete : scanMusicFile.callBackList) {
                                onScanComplete.onCountChange(scanMusicFile.mPathList.size());
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 遍历文件目录下的所有文件
     *
     * @param file 需要扫描的文件目录
     */
    private void searchFile(File file) {
        if (file.isDirectory() && file.listFiles() != null) {
            File[] items = file.listFiles();
            for (File item : items) {
                searchFile(item);
            }
            return;
        }
        //过滤没有后缀名的文件
        if (!file.getName().contains(".")) {
            return;
        }
        //过滤以 '.'字符开头格式的文件（仅当只有一个‘.’符号的情况）
        if (file.getName().lastIndexOf('.') < 1) {
            return;
        }
        //过滤类似   xxx.xxxxxx || xx.x||xx.xx
        int lastIndex = file.getName().lastIndexOf('.');
        int lastCharIndex = file.getName().length() - 1;
        int delta = lastCharIndex - lastIndex;
        if (delta > 5 || delta < 2) {
            return;
        }
        //判断文件的类型是否支持
        for (String format : mSupportMedia) {
            if (file.getName().endsWith(format)) {
                long size = 1024L * 1024L;// * 1L  1M的大小
                if (size < file.length()) {
                    Log.w(TAG, file.getAbsolutePath() + ",length=" + file.lastModified());
                    mPathList.add(new RecordEntity(file.getAbsolutePath(), 0, 0,
                            DateUtil.getInstance().getDateWithTime(file.lastModified()), FileUtils.getFileSize(file)));
                    mHandler.sendEmptyMessage(COUNT_CHANGE);
                }
                return;
            }
        }
    }

    public abstract static class OnScanComplete {

        protected abstract void onComplete(ArrayList<RecordEntity> paths);

        protected void onCountChange(int size) {

        }
    }
}
