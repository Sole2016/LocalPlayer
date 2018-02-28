package com.zy.vplayer.tv.simple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import java.lang.ref.WeakReference;

/**
 * @author ZhiTouPC
 */
public class FocusBorderView extends View {
    private static final String TAG = "FocusBorderView";
    private Path mLinePath;
    private Paint mLinePaint;
    private View newFocus;
    private View oldFocus;
    private long mLastUpdateTime;

    private Rect from = new Rect(0, 0, 0, 0);
    private Rect to = new Rect(0, 0, 0, 0);
    private boolean isMove;

    public FocusBorderView(Context context) {
        super(context);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setAntiAlias(true);
        mLinePath = new Path();
        mLinePath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
    }

    public void updateView(View old, View newFocus) {
        old.getGlobalVisibleRect(from);
        newFocus.getGlobalVisibleRect(to);
        this.newFocus = newFocus;
        this.oldFocus = old;
        long currentTime = System.currentTimeMillis();
//        startTranslation(currentTime - mLastUpdateTime < 180);
        Log.d(TAG, "updateView: update=" + to.toString());
        requestLayout();
        postInvalidate();
        mLastUpdateTime = System.currentTimeMillis();
    }

    public void setInit(View newFocus) {
        newFocus.getGlobalVisibleRect(to);
        this.newFocus = newFocus;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int fromWidth = from.right - from.left;
        int fromHeight = from.bottom - from.top;
        int toWidth = to.right - to.left +
                newFocus.getPaddingLeft() + newFocus.getPaddingRight();
        int toHeight = to.bottom - to.top +
                newFocus.getPaddingTop() + newFocus.getPaddingBottom();
        int width, height;
        width = fromWidth > toWidth ? fromWidth : toWidth;
        height = fromHeight > toHeight ? fromHeight : toHeight;

        if (width == 0 || height == 0) {
            setMeasuredDimension(200, 200);
        } else {
            setMeasuredDimension((int) (width + 2 * mLinePaint.getStrokeWidth()),
                    (int) (height + 2 * mLinePaint.getStrokeWidth()));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setTranslationX(to.left);
        setTranslationY(to.top);
        canvas.save();
        mLinePath.rewind();
        mLinePath.moveTo(0, 0);
        mLinePath.lineTo(getMeasuredWidth(), 0);
        mLinePath.lineTo(getMeasuredWidth(), getMeasuredHeight());
        mLinePath.lineTo(0, getMeasuredHeight());
        mLinePath.close();
        canvas.clipPath(mLinePath);
        canvas.drawPath(mLinePath, mLinePaint);
        canvas.restore();
    }

    private void startTranslation(boolean isSeek) {
        if (this.getAnimation() != null) {
            this.getAnimation().cancel();
        }
        if (isSeek) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTranslationX(to.left);
                    setTranslationY(to.top);
                }
            }, 20);
        } else {
            TranslateAnimation animation = new TranslateAnimation(from.left, to.left, from.top, to.top);
            animation.setDuration(180);
            animation.setFillAfter(true);
            this.startAnimation(animation);
        }
    }
}
