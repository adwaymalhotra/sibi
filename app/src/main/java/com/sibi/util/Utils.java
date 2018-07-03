package com.sibi.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeImageTransform;
import android.support.transition.ChangeTransform;
import android.support.transition.TransitionSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by adway on 01/12/17.
 */

public class Utils {
    public static final String TAG = "Utils";
    public static final int REQUEST_CAMERA = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;
    public static final int FILE_PICKER_CODE = 2;

    public static void openCameraForImage(Fragment fragment) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fragment.startActivityForResult(intent, REQUEST_CAMERA);
    }

    public static void openGalleryForImage(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        fragment.startActivityForResult(Intent.createChooser(intent, "Select File"),
            REQUEST_GALLERY_IMAGE);
    }

    public static File makeProfilePicFileFromBitmap(Context context, Bitmap bmp, String name) {
        OutputStream outStream;
        // String temp = null;
        File file = new File(context.getCacheDir(), name);
        if (file.exists()) {
            file.delete();
            file = new File(context.getCacheDir(), name);
        }

        try {
            outStream = new FileOutputStream(file);
            bmp = scaleDown(bmp, 300, true);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                    boolean filter) {
        float ratio = Math.min(maxImageSize / realImage.getWidth(),
            maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width,
            height, filter);
    }

    public static int hasStoragePermission(Activity activity) {
        //External Storage Permission
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//                Log.d(TAG, "hasStoragePermission: Storage denied previously");
//                return -1;
//            } else {
            ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Constants.PermissionsConstants.STORAGE_PERMISSION_CODE);
            Log.d(TAG, "hasStoragePermission: Storage requested");
            return 0;
//            }
        } else {
            Log.d(TAG, "hasStoragePermission: Storage granted previously");
            return 1;
        }
    }

    public static int hasStoragePermission(Fragment fragment) {
        //External Storage Permission
        if (ContextCompat.checkSelfPermission(fragment.getContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

//            if (fragment.shouldShowRequestPermissionRationale(Manifest.permission
//                    .WRITE_EXTERNAL_STORAGE)) {
//                Log.d(TAG, "hasStoragePermission: Storage denied previously");
//                return -1;
//            } else {
            fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Constants.PermissionsConstants.STORAGE_PERMISSION_CODE);
            Log.d(TAG, "hasStoragePermission: Storage requested");
            return 0;
//            }
        } else {
            Log.d(TAG, "hasStoragePermission: Storage granted previously");
            return 1;
        }
    }

    public static int hasCameraPermission(Activity activity) {
        //External Storage Permission
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

//            if (ActivityCompat
//                    .shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
//
//                Log.d(TAG, "hasCameraPermission: Camera permission denied previously");
//                return -1;
//            } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                Constants.PermissionsConstants.CAMERA_PERMISSION_CODE);
            Log.d(TAG, "hasCameraPermission: Camera permission requested");
            return 0;
//            }
        } else {
            Log.d(TAG, "hasCameraPermission: Camera permission granted previously");
            return 1;
        }
    }

    public static int hasCameraPermission(Fragment fragment) {
        //External Storage Permission
        if (ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

//            if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//
//                Log.d(TAG, "hasCameraPermission: Camera permission denied previously");
//                return -1;
//            } else {
            fragment.requestPermissions(new String[]{Manifest.permission.CAMERA},
                Constants.PermissionsConstants.CAMERA_PERMISSION_CODE);
            Log.d(TAG, "hasCameraPermission: Camera permission requested");
            return 0;
//            }
        } else {
            Log.d(TAG, "hasCameraPermission: Camera permission granted previously");
            return 1;
        }
    }

    public static int getColor(Context context, int resId) {
        return ContextCompat.getColor(context, resId);
    }


    public static File writeTransactionsToJson(String json) {

        File root = android.os.Environment.getExternalStorageDirectory();

        File file = new File(root.getPath() + "/sibi_backup_"
            + new SimpleDateFormat("MM_dd_yyyy", Locale.US)
            .format(System.currentTimeMillis()) + ".json");
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(json);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String loadTransactionsFromJson(URI uri) {
        Log.d(TAG, "loadTransactionsFromJson: ");
        String json = "";
        try {
            File file = new File(uri.getRawPath());
            long length = file.length();
            FileReader in = new FileReader(file);
            char[] content = new char[(int) length];

            int numRead = in.read(content);
            if (numRead != length) {
                Log.e(TAG, "Incomplete read of " + file + ". Read chars "
                    + numRead + " of " + length);
            }
            json = new String(content, 0, numRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static class FragmentTransition extends TransitionSet {
        public FragmentTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds().setDuration(300))
                .addTransition(new ChangeTransform().setDuration(300))
                .addTransition(new ChangeImageTransform().setDuration(300));
        }
    }
}
