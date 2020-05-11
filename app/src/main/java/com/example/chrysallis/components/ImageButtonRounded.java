package com.example.chrysallis.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

public class ImageButtonRounded extends AppCompatImageButton {


    private int borderColor;
    private float borderWidth;

    public ImageButtonRounded(Context context) {
        super(context);
    }

    public ImageButtonRounded(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageButtonRounded(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        if(b == null || b.isRecycled())
            return;
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();
        int imgRadius = w - 2 * (int)this.borderWidth;

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(this.borderColor);
        canvas.drawCircle(this.getWidth() / 2,
                this.getHeight() / 2,
                this.getWidth() / 2, paint);
        Bitmap roundBitmap = getRoundedCroppedBitmap(bitmap, imgRadius);
        canvas.drawBitmap(roundBitmap, this.borderWidth, this.borderWidth, null);

    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
                    false);
        else
            finalBitmap = bitmap;
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(),
                finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(
                finalBitmap.getWidth() / 2,
                finalBitmap.getHeight() / 2,
                finalBitmap.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);
        return output;
    }


}
