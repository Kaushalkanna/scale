package com.kaushal.scale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ScaleView extends View {
    static int screenSize = 480;
    static private float pxmm = screenSize / 67.f;
    int width, height, midScreenPoint;
    float startingPoint = 0;
    float downpoint = 0, movablePoint = 0, downPointClone = 0;
    private float mainPoint = 0, mainPointClone = 0;
    boolean isDown = false;
    boolean isUpward = false;
    private boolean isMove;
    private onViewUpdateListener mListener;
    private Paint scaleSpec, scaleTextSpec;
    private int endPoint;
    boolean isSizeChanged = false;
    float userStartingPoint = 0f;
    private int scaleLineSmall;
    private int scaleLineMedium;
    private int scaleLineLarge;
    private int textStartPoint;
    boolean isFirstTime = true;

    public ScaleView(Context context, AttributeSet foo) {
        super(context, foo);
        if (!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context) {
        setScaleSpec();
        setScaleTextSpec(context);


    }

    private void setScaleTextSpec(Context context) {
        scaleTextSpec = new TextPaint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/segoeuil.ttf");
        scaleTextSpec.setTypeface(typeface);
        scaleTextSpec.setStyle(Paint.Style.STROKE);
        scaleTextSpec.setStrokeWidth(0);
        scaleTextSpec.setAntiAlias(true);
        scaleTextSpec.setTextSize(getResources().getDimension(R.dimen.txt_size));
        scaleTextSpec.setColor(Color.BLACK);
        textStartPoint = (int) getResources().getDimension(R.dimen.text_start_point);
    }

    private void setScaleSpec() {
        scaleSpec = new Paint();
        scaleSpec.setStyle(Paint.Style.STROKE);
        scaleSpec.setStrokeWidth(1);
        scaleSpec.setAntiAlias(false);
        scaleSpec.setColor(Color.BLACK);
        scaleLineSmall = (int) getResources().getDimension(R.dimen.scale_line_small);
        scaleLineMedium = (int) getResources().getDimension(R.dimen.scale_line_medium);
        scaleLineLarge = (int) getResources().getDimension(R.dimen.scale_line_large);
    }

    public void setUpdateListener(onViewUpdateListener onViewUpdateListener) {
        mListener = onViewUpdateListener;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        width = w;
        height = h;
        screenSize = height;
        pxmm = screenSize / 67.f;
        midScreenPoint = height / 2;
        endPoint = width - 40;
        if (isSizeChanged) {
            isSizeChanged = false;
            mainPoint = midScreenPoint - (userStartingPoint * 10 * pxmm);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        startingPoint = mainPoint;
        for (int i = 1;; ++i) {
            if (startingPoint > screenSize) {
                break;
            }
            startingPoint = startingPoint + pxmm;
            int size = (i % 10 == 0) ? scaleLineLarge : (i % 5 == 0) ? scaleLineMedium : scaleLineSmall;
            canvas.drawLine(endPoint - size, startingPoint, endPoint, startingPoint, scaleSpec);
            if (i % 10 == 0) {
                System.out.println("done   " + i);
                canvas.drawText((i / 10) + " cm", endPoint - textStartPoint, startingPoint + 8, scaleTextSpec);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("touch event fire");
        mainPointClone = mainPoint;
        if (mainPoint < 0) {
            mainPointClone = -mainPoint;
        }
        float clickPoint = ((midScreenPoint + mainPointClone) / (pxmm * 10));
        if (mListener != null) {
            mListener.onViewUpdate((midScreenPoint + mainPointClone) / (pxmm * 10));
        }
        System.out.println("click point" + clickPoint + "");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = true;
                isDown = false;
                isUpward = false;
                downpoint = event.getY();
                downPointClone = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                movablePoint = event.getY();
                if (downPointClone > movablePoint) {
                    /**
                     * if user first starts moving downward and then upwards then
                     * this method makes it to move upward
                     */
                    if (isUpward) {
                        downpoint = event.getY();
                        downPointClone = downpoint;
                    }
                    isDown = true;
                    isUpward = false;
                    /**
                     * make this differnce of 1, otherwise it moves very fast and
                     * nothing shows clearly
                     */
                    if (downPointClone - movablePoint > 1) {
                        mainPoint = mainPoint + (-(downPointClone - movablePoint));
                        downPointClone = movablePoint;
                        invalidate();
                    }
                } else {
                    // downwards
                    if (isMove) {
                        /**
                         * if user first starts moving upward and then downwards,
                         * then this method makes it to move upward
                         */
                        if (isDown) {
                            downpoint = event.getY();
                            downPointClone = downpoint;
                        }
                        isDown = false;
                        isUpward = true;
                        if (movablePoint - downpoint > 1) {
                            mainPoint = mainPoint + ((movablePoint - downPointClone));
                            downPointClone = movablePoint;
                            if (mainPoint > 0) {
                                mainPoint = 0;
                                isMove = false;
                            }
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("up");
            default:
                break;
        }
        return true;
    }

    public void setStartingPoint(float point) {
        userStartingPoint = point;
        isSizeChanged = true;
        if (isFirstTime) {
            isFirstTime = false;
            if (mListener != null) {
                mListener.onViewUpdate(point);
            }
        }
    }
}