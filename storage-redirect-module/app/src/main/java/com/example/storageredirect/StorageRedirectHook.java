package com.example.storageredirect;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class StorageRedirectHook implements IXposedHookLoadPackage {
    
    private static final String TAG = "StorageRedirect";
    private static final String TARGET_PACKAGE = "com.tencent.tmgp.sgame"; // 王者荣耀包名
    private static final String VIRTUAL_DIR = "/data/data/com.example.storageredirect/virtual_storage/";
    
    // 目标文件名
    private static final String[] TARGET_FILES = {
        "mrpcs-android-l.gr_925.data",
        "mrpcs-android-1.gr_925.data"
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }
        
        Log.d(TAG, "Hook 王者荣耀包: " + lpparam.packageName);
        
        // 确保虚拟目录存在
        createVirtualDirectory();
        
        // Hook File 构造函数
        hookFileConstructor(lpparam.classLoader);
        
        // Hook FileOutputStream 构造函数
        hookFileOutputStream(lpparam.classLoader);
        
        // Hook RandomAccessFile 构造函数
        hookRandomAccessFile(lpparam.classLoader);
    }
    
    private void createVirtualDirectory() {
        try {
            File virtualDir = new File(VIRTUAL_DIR);
            if (!virtualDir.exists()) {
                virtualDir.mkdirs();
                Log.d(TAG, "创建虚拟目录: " + VIRTUAL_DIR);
            }
        } catch (Exception e) {
            Log.e(TAG, "创建虚拟目录失败", e);
        }
    }
    
    private String redirectPath(String originalPath) {
        if (originalPath == null) return originalPath;
        
        for (String targetFile : TARGET_FILES) {
            if (originalPath.contains(targetFile)) {
                String redirectedPath = VIRTUAL_DIR + targetFile;
                Log.d(TAG, "重定向文件: " + originalPath + " -> " + redirectedPath);
                return redirectedPath;
            }
        }
        return originalPath;
    }
    
    private void hookFileConstructor(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookConstructor(File.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String path = (String) param.args[0];
                    String redirected = redirectPath(path);
                    if (!redirected.equals(path)) {
                        param.args[0] = redirected;
                    }
                }
            });
            
            XposedHelpers.findAndHookConstructor(File.class, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String parent = (String) param.args[0];
                    String child = (String) param.args[1];
                    String fullPath = parent + "/" + child;
                    String redirected = redirectPath(fullPath);
                    if (!redirected.equals(fullPath)) {
                        param.args[0] = new File(redirected).getParent();
                        param.args[1] = new File(redirected).getName();
                    }
                }
            });
            
            Log.d(TAG, "File 构造函数 Hook 成功");
        } catch (Exception e) {
            Log.e(TAG, "Hook File 构造函数失败", e);
        }
    }
    
    private void hookFileOutputStream(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookConstructor(FileOutputStream.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String path = (String) param.args[0];
                    String redirected = redirectPath(path);
                    if (!redirected.equals(path)) {
                        param.args[0] = redirected;
                    }
                }
            });
            
            XposedHelpers.findAndHookConstructor(FileOutputStream.class, File.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    File file = (File) param.args[0];
                    String path = file.getAbsolutePath();
                    String redirected = redirectPath(path);
                    if (!redirected.equals(path)) {
                        param.args[0] = new File(redirected);
                    }
                }
            });
            
            Log.d(TAG, "FileOutputStream Hook 成功");
        } catch (Exception e) {
            Log.e(TAG, "Hook FileOutputStream 失败", e);
        }
    }
    
    private void hookRandomAccessFile(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookConstructor(RandomAccessFile.class, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String path = (String) param.args[0];
                    String redirected = redirectPath(path);
                    if (!redirected.equals(path)) {
                        param.args[0] = redirected;
                    }
                }
            });
            
            XposedHelpers.findAndHookConstructor(RandomAccessFile.class, File.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    File file = (File) param.args[0];
                    String path = file.getAbsolutePath();
                    String redirected = redirectPath(path);
                    if (!redirected.equals(path)) {
                        param.args[0] = new File(redirected);
                    }
                }
            });
            
            Log.d(TAG, "RandomAccessFile Hook 成功");
        } catch (Exception e) {
            Log.e(TAG, "Hook RandomAccessFile 失败", e);
        }
    }
}
