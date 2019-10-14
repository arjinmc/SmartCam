package com.arjinmc.smartcam.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arjinmc.smartcam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Request permission assistant
 * Created by Eminem Lo on 6/4/17.
 * Email arjinmc@hotmail.com
 * https://github.com/arjinmc/PermissionAssistant
 */

public final class PermissionAssistant {

    private static AlertDialog mAlerDialog;

    /**
     * request one permission
     *
     * @param context
     * @param permission
     * @param isForceGrantAll
     */
    public static void requestPermissions(Context context, String permission, boolean isForceGrantAll) {
        requestPermissions(context, new String[]{permission}, isForceGrantAll);
    }

    /**
     * all request permission
     *
     * @param context
     * @param permissions
     */
    public static void requestPermissions(Context context, String[] permissions, boolean isForceGrantAll) {

        if (context == null || permissions == null || permissions.length == 0) {
            return;
        }

        if (!(context instanceof Activity)) {
            return;
        }

        if (mAlerDialog != null && mAlerDialog.isShowing()) {
            return;
        }

        boolean useDialog = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //if has granted all,dot not request permission
            if (isGrantedAllPermissions(context, permissions)) {
                return;
            }

            int permissionSize = permissions.length;
            for (int i = 0; i < permissionSize; i++) {
                int permissionResult = ContextCompat.checkSelfPermission(context, permissions[i]);
                boolean shouldRequest = ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[i]);
                if (permissionResult == PackageManager.PERMISSION_DENIED
                        && !shouldRequest && isForceGrantAll) {
                    useDialog = true;
                    break;
                }
            }

            if (useDialog) {
                showDialog(context, isForceGrantAll);
            } else {
                ActivityCompat.requestPermissions((Activity) context, permissions, 1);
            }
        }
    }

    /**
     * check if has granted permission
     *
     * @param context
     * @param permission
     * @return
     */
    public boolean hasGrantedPermission(Context context, String permission) {
        if (context == null || permission == null) {
            return false;
        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * response callback for request permission result
     *
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionResult(@NonNull String[] permissions
            , @NonNull int[] grantResults, PermissionCallback callback) {
        if (callback != null) {
            int permissionSize = permissions.length;
            List<String> grandtedList = new ArrayList<>();
            List<String> denyList = new ArrayList<>();
            for (int i = 0; i < permissionSize; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grandtedList.add(permissions[i]);
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    denyList.add(permissions[i]);
                }
            }

            if (grandtedList.size() != 0)
                callback.onAllow(grandtedList.toArray(new String[grandtedList.size()]));
            if (denyList.size() != 0) {
                callback.onDeny(denyList.toArray(new String[denyList.size()]));
            }

        }

    }

    /**
     * return for if has granted all permmission which would be requested
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean isGrantedAllPermissions(Context context, String[] permissions) {

        int permissionSize = permissions.length;
        int hasGrantedPermissionSize = 0;
        for (int i = 0; i < permissionSize; i++) {
            int permissionResult = ContextCompat.checkSelfPermission(context, permissions[i]);

            if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                hasGrantedPermissionSize++;
            }
        }
        if (permissionSize == hasGrantedPermissionSize) {
            return true;
        }
        return false;
    }

    /**
     * open this setting of current application
     *
     * @param context
     */
    public static void openSystemPermissionSetting(Context context) {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * if the system has deny request permission by clicked "never ask again"
     *
     * @param context
     */
    public static void showDialog(final Context context, final boolean isForceGrantAll) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.permission_setting))
                .setMessage(context.getString(R.string.permission_setting_tips))
                .setPositiveButton(context.getString(R.string.permission_setting_yes)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openSystemPermissionSetting(context);
                            }
                        });
        if (!isForceGrantAll) {
            builder.setNegativeButton(context.getString(R.string.permission_setting_no)
                    , null);
        }

        mAlerDialog = builder.create();

        if (isForceGrantAll) {
            mAlerDialog.setCancelable(false);
            mAlerDialog.setCanceledOnTouchOutside(false);
        }

        if (!mAlerDialog.isShowing()) {
            mAlerDialog.show();
        }
    }

    /**
     * the callback for requesting permission
     */
    public interface PermissionCallback {

        void onAllow(String[] permissions);

        void onDeny(String[] permissions);

    }

}
