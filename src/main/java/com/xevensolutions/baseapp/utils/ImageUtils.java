package com.xevensolutions.baseapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xevensolutions.baseapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.sql.DataSource;

import static com.xevensolutions.baseapp.utils.TextUtils.isStringEmpty;


public class ImageUtils {

    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;

    public static String compressImage(String imagePath, Bitmap bitmap) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp;
        if (bitmap == null)
            bmp = BitmapFactory.decodeFile(imagePath, options);
        else {
            byte[] bitmapByteArray = getByteArrayFromBitmap(bitmap);
            bmp = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length, options);
        }


        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = ImageUtils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            if (bitmap == null)
                bmp = BitmapFactory.decodeFile(imagePath, options);
            else {
                byte[] bitmapByteArray = getByteArrayFromBitmap(bitmap);
                bmp = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length, options);
            }
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);

        String directoryPath = Environment.getExternalStorageDirectory() + Constants.EXTERNAL_STORAGE_DIRECTORY_SENT_FILES;
        File directory = new File(directoryPath);
        directory.mkdirs();
        String newImagePath = directory + File.separator + FileUtils.getFileNameFromPath(imagePath);


        File f = new File(newImagePath);
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(out.toByteArray());

            return newImagePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
//write the bytes in file
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static void setImage(Context activity, String username, String path, ImageView imageView, boolean isCircular,
                                boolean loadThumbnail, boolean isLocal, int imagePlaceholder,
                                RequestListener<Drawable> requestListener) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.setColorSchemeColors(GenericUtils.getAttributedColor(activity, com.google.android.material.R.attr.colorSecondary),
                GenericUtils.getAttributedColor(activity, com.google.android.material.R.attr.colorSecondary));
        circularProgressDrawable.start();

        if (isStringEmpty(path))
            return;
        else {

            if (!isLocal) {
                if (path != null && !path.startsWith("http"))
                    path = Constants.BLOB_PATH + path;


            }
            Glide.with(activity).load(path).apply(
                    DataProvider.getGlideRequestOptions(isCircular, loadThumbnail).error(imagePlaceholder)
            ).placeholder(circularProgressDrawable).thumbnail(loadThumbnail ? 0.2f : 1.0f).addListener(requestListener).
                    error(imagePlaceholder).
                    into(imageView);
        }
    }


    public static void setImage(Context activity, String username, String path, ImageView imageView, boolean isCircular,
                                boolean loadThumbnail, boolean isLocal, int imagePlaceholder) {

        setImage(activity, username, path, imageView, isCircular,
                loadThumbnail, isLocal, imagePlaceholder, new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }

                });

    }

    public static String createImageFromBitmap(Context context, Bitmap bitmap) {
        String fileName = "SignatureImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;

    }


    public static Bitmap getBitmapFromImageName(Context context, String imageName) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context
                    .openFileInput(imageName));
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            return mutableBitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTempPathForBitmap(Context inContext, Bitmap inImage) {
    /*    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        String realPath = FileUtils.getImageRealPathFromURI(inContext, Uri.parse(path));
        return realPath;
*/

        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(inImage, 200, 200,
                    true);
            File file = new File(inContext.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = inContext.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            return realPath;
            /* File f = new File(realPath);
            Uri uri = Uri.fromFile(f);

            return FileUtils.getImageRealPathFromURI(inContext, uri);*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public static void setLocalImage(Context activity, ImageView imageView, int imageRes) {
        setLocalImage(activity, imageView, imageRes, true);
    }

    public static void setLocalImage(Context activity, ImageView imageView, int imageRes, boolean applyTint, boolean isTintRed) {
        if (applyTint)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageTintList(ColorStateList.valueOf(GenericUtils.getAttributedColor(activity,
                        isTintRed ? com.google.android.material.R.attr.colorSecondaryVariant :
                                com.google.android.material.R.attr.colorSecondary)));
            }

        Glide.with(activity).load(imageRes).into(imageView);
    }


    public static void setLocalImage(Context activity, ImageView imageView, int imageRes, boolean applyTint) {
        setLocalImage(activity, imageView, imageRes, applyTint, true);
    }


    public static void setLocalImage(Activity activity, ImageView imageView, String path) {
        setImage(activity, "", path, imageView, false, false, true, R.drawable.ic_profile_placeholder);
    }

    public static void setRemoteImage(Context context, String propertyImage, ImageView ivProfile, int imagePlaceholder) {
        setImage(context, "", propertyImage, ivProfile, false, true, false, imagePlaceholder);
    }

    public static void setRemoteImage(Context context, String propertyImage, ImageView ivProfile, int imagePlaceholder,
                                      boolean loadThumbnail) {
        setImage(context, "", propertyImage, ivProfile, false, loadThumbnail, false, imagePlaceholder);
    }
}
