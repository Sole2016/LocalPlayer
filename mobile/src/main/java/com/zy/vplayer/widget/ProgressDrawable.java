package com.zy.vplayer.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Locale;

public class ProgressDrawable extends Drawable {
    private static final String TAG = "ProgressDrawable";
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    private Paint mPaint;
    private int mColorUnreachable;//未完成颜色
    private int mColorReachable;//已完成颜色
    private float mCurrentProgress = 0f;//进度比例
    private Rect mUnReachRect;//未完成区域
    private Rect mReachRect;//完成区域
    private int mProgressOrientation;//绘制的方向
    private int mAlpha = 255;

    public ProgressDrawable() {
        this(Color.GRAY, Color.YELLOW, HORIZONTAL);
    }

    public ProgressDrawable(int orientation) {
        this(Color.GRAY, Color.YELLOW, orientation);
    }

    public ProgressDrawable(int mColorUnreachable, int mColorReachable, int orientation) {
        this.mColorUnreachable = mColorUnreachable;
        this.mColorReachable = mColorReachable;
        this.mProgressOrientation = orientation;

        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);

        mUnReachRect = new Rect();
        mReachRect = new Rect();
    }


    public void setColorUnreachable(int color) {
        this.mColorUnreachable = color;
        invalidateSelf();
    }

    public void setColorReachable(int color) {
        this.mColorReachable = color;
        invalidateSelf();
    }

    /**
     * @param progress 0~1
     */
    public synchronized void setProgress(float progress) {
        if (progress < 0 || progress > 1) {
            throw new IllegalArgumentException("progress must in 0~1");
        }
        this.mCurrentProgress = progress;
        invalidateSelf();
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        if (mCurrentProgress == 0) {
            mPaint.setColor(mColorUnreachable);
            canvas.drawRect(bounds, mPaint);
            return;
        }
//        int width = bounds.right - bounds.left;
//        int height = bounds.bottom - bounds.top;
//        Log.e(TAG, String.format(Locale.CHINA, "width=%d,height=%d,progress=%f", width, height, mCurrentProgress));

        if (mProgressOrientation == HORIZONTAL) {
            drawHorizontal(canvas, bounds);
        } else {
            drawVertical(canvas, bounds);
        }
    }

    private void drawHorizontal(Canvas canvas, Rect area) {
        int width = area.right - area.left;

        mReachRect.left = area.left;
        mReachRect.top = area.top;
        mReachRect.bottom = area.bottom;
        mReachRect.right = (int) (mCurrentProgress * width);

        mUnReachRect.left = mReachRect.right;
        mUnReachRect.right = area.right;
        mUnReachRect.top = area.top;
        mUnReachRect.bottom = area.bottom;

        mPaint.setColor(mColorReachable);
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(mReachRect, mPaint);

        mPaint.setColor(mColorUnreachable);
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(mUnReachRect, mPaint);
    }

    private void drawVertical(Canvas canvas, Rect area) {
        int height = area.bottom - area.top;
//        Log.e(TAG, "drawVertical: alpha="+mPaint.getAlpha() );

        mUnReachRect.top = area.top;
        mUnReachRect.right = area.right;
        mUnReachRect.left = area.left;
        mUnReachRect.bottom = (int) (height * (1 - mCurrentProgress));

        mReachRect.left = area.left;
        mReachRect.right = area.right;
        mReachRect.top = mUnReachRect.bottom;
        mReachRect.bottom = area.bottom;

        mPaint.setColor(mColorUnreachable);
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(mUnReachRect, mPaint);

        mPaint.setColor(mColorReachable);
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(mReachRect, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
//        Log.d(TAG, "setAlpha() called with: alpha = [" + alpha + "]");
        mAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        if(mPaint.getAlpha() == 255){
            return PixelFormat.OPAQUE;
        }
        return PixelFormat.TRANSLUCENT;
    }
}
