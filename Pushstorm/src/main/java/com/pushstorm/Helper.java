package com.pushstorm;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.IconCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Helper {
    public static Bitmap getBitmap(String path) {

        Bitmap iconBitmap = null;
        try {
            URL url = new URL(path);
            iconBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iconBitmap;
    }
    public static Drawable getDrawable(String path) {

        Drawable drawable = null;
        try {
            InputStream inputStream = new URL(path).openStream();
            drawable = Drawable.createFromStream(inputStream, "image");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  drawable;
    }
    public static IconCompat getBitmapDrawable(String path) {

        IconCompat iconCompat = IconCompat.createWithBitmap(getBitmap(path));
        return iconCompat;

    }
    public static int covertDrawableToInt(Context context, String path) {

        int drawableResourceId = 0;
        if (path != null) {
            String drawableName = "my_icon"; // نام فایل Drawable
            String packageName = context.getPackageName(); // نام پکیج فعلی
            String drawableType = "drawable"; // نوع منبع Drawable

            Resources resources = context.getResources();
            drawableResourceId = resources.getIdentifier(drawableName, drawableType, packageName);
        }
        return  drawableResourceId;

    }

}
