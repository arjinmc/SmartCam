package com.arjinmc.smartcam.core.file;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File util
 * Created by Eminem Lo on 2019-10-31.
 * email: arjinmc@hotmail.com
 */
public class SmartCamFileUtils {

    private static boolean isExternalStorageAvailerAble() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getExternalStorageDir() {
        if (isExternalStorageAvailerAble()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    public static String getExternalDir(Context context) {
        return context.getExternalFilesDir("").getAbsolutePath();
    }

    /**
     * save file
     *
     * @param bytes
     * @param file
     */
    public static boolean saveFile(byte[] bytes, File file) {
        if (bytes == null || bytes.length == 0) {
            return false;
        }
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * create dir
     *
     * @param context
     * @param rootPath
     * @param dirName
     * @return dir path
     */
    public static String createDir(Context context, String rootPath, String dirName) {

        if (context == null || TextUtils.isEmpty(rootPath) || TextUtils.isEmpty(dirName)) {
            return null;
        }
        File file = new File(rootPath);
        if (!file.exists()) {
            return null;
        }
        file = new File(rootPath + File.separator + dirName);
        if (file.exists() && file.isDirectory()) {
            return file.getAbsolutePath();
        }
        boolean result = file.mkdir();
        if (result) {
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * create file
     *
     * @param context
     * @param rootPath
     * @param fileName
     * @return
     */
    public static String createFile(Context context, String rootPath, String fileName) {
        if (context == null || TextUtils.isEmpty(rootPath) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        File file = new File(rootPath);
        if (!file.exists()) {
            return null;
        }
        file = new File(rootPath + File.separator + fileName);
        if (file.exists() && !file.isDirectory()) {
            return file.getAbsolutePath();
        }
        try {
            boolean result = file.createNewFile();
            if (result) {
                return file.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * create dir by uri
     *
     * @param context
     * @param uri
     * @param dirName
     * @return dir uri
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String createDirByUri(Context context, String uri, String dirName) {
        if (context == null || TextUtils.isEmpty(uri) || TextUtils.isEmpty(dirName)) {
            return null;
        }
        DocumentFile parentDocumentFile = DocumentFile.fromTreeUri(context, Uri.parse(uri));
        DocumentFile childDocumentFile = parentDocumentFile.findFile(dirName);
        if (childDocumentFile != null && childDocumentFile.isDirectory()) {
            return childDocumentFile.getUri().toString();
        }
        childDocumentFile = parentDocumentFile.createDirectory(dirName);
        if (childDocumentFile == null) {
            return null;
        } else {
            return childDocumentFile.getUri().toString();
        }
    }

    /**
     * create file by uri
     *
     * @param context
     * @param uri
     * @param mineType
     * @param fileName
     * @return file uri
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String createFileByUri(Context context, String uri, String mineType, String fileName) {
        if (context == null || TextUtils.isEmpty(uri) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        DocumentFile parentDocumentFile = DocumentFile.fromTreeUri(context, Uri.parse(uri));
        DocumentFile childDocumentFile = parentDocumentFile.findFile(fileName);
        if (childDocumentFile != null) {
            return childDocumentFile.getUri().toString();
        }
        childDocumentFile = parentDocumentFile.createFile(mineType, fileName);
        if (childDocumentFile == null) {
            return null;
        } else {
            return childDocumentFile.getUri().toString();
        }
    }

    /**
     * check path permission if it can writing files
     *
     * @param path
     * @return
     */
    public static boolean isAvailableDir(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File rootFile = new File(path);
        File testFile = null;
        boolean isAvailable = false;
        try {
            if (rootFile.exists() && rootFile.exists()) {
                testFile = new File(rootFile.getAbsolutePath() + File.separator + "TestisAvailableDir");
                isAvailable = testFile.createNewFile();
            } else {
                isAvailable = rootFile.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isAvailable = false;
        } finally {
            if (testFile != null && testFile.exists()) {
                testFile.delete();
            }
            return isAvailable;
        }
    }

    /**
     * apple open dir permission for read and write
     * above android Q
     *
     * @param activity
     * @param requestCode
     * @param intentType
     */
    public static void applyOpenDirPermission(Activity activity, int requestCode, String intentType) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        if (!TextUtils.isEmpty(intentType)) {
            intent.setType(intentType);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * save dir permission above android Q
     *
     * @param context
     * @param intent
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void saveDirPermission(Context context, Intent intent) {
        if (intent.getData() != null) {
            int takeFlags = intent.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.getContentResolver().takePersistableUriPermission(intent.getData(), takeFlags);
        }
    }
}
