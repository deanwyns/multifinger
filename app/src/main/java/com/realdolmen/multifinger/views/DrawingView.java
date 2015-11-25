package com.realdolmen.multifinger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.google.inject.Inject;
import com.realdolmen.multifinger.connection.Connection;
import com.realdolmen.multifinger.connection.StrokeDto;
import com.realdolmen.multifinger.connection.bluetooth.ConversionUtil;

import java.util.ArrayList;

import roboguice.RoboGuice;

public class DrawingView extends View {
    @Inject
    private Connection connection;
    @Inject
    private ConversionUtil conversionUtil;

    public static int strokeWidth = 12;
    public static final int MAX_FINGERS = 5;
    private Path[] mFingerPaths = new Path[MAX_FINGERS * 2];
    private ArrayList<Pair<Path, Integer>> mCompletedPaths;
    private Paint mPaint;

    public DrawingView(Context c) {
        super(c);
        RoboGuice.getInjector(getContext()).injectMembers(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setColor(int paintColor){
        mPaint.setColor(paintColor);
    }

    public void clearScreen(){
        mCompletedPaths.clear();
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCompletedPaths = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int originalColor = mPaint.getColor();
        for (Pair completedPath : mCompletedPaths) {
            mPaint.setColor((Integer) completedPath.second);
            canvas.drawPath((Path) completedPath.first, mPaint);
        }
        mPaint.setColor(originalColor);

        for (Path fingerPath : mFingerPaths) {
            if (fingerPath != null) {
                canvas.drawPath(fingerPath, mPaint);
            }
        }
    }

    public void drawOpponentStroke(StrokeDto strokeDto) {
        int action = strokeDto.getEvent();
        int id = strokeDto.getFinger() * 2;
        float x = strokeDto.getX();
        float y = strokeDto.getY();
        // Normalize coordinates
        float density = getResources().getDisplayMetrics().density;
        x /= density;
        y /= density;

        int color = strokeDto.getColor();

        mPaint.setColor(color);
        mPaint.setStrokeWidth(strokeDto.getWidth());

        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && id < MAX_FINGERS * 2) {
            mFingerPaths[id] = new Path();
            mFingerPaths[id].moveTo(x, y);
        } else if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP) && id < MAX_FINGERS * 2) {
            if(mFingerPaths[id] != null) {
                mFingerPaths[id].setLastPoint(x, y);
                mCompletedPaths.add(new Pair<>(mFingerPaths[id], color));

                invalidate();
                mFingerPaths[id] = null;
            }
        }

        if(mFingerPaths[id] != null) {
            mFingerPaths[id].lineTo(x, y);
            invalidate();
        }
    }

    private void sendStroke(StrokeDto strokeDto) {
        connection.write(conversionUtil.toBytes(strokeDto));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int cappedPointerCount = pointerCount > MAX_FINGERS ? MAX_FINGERS : pointerCount;
        int actionIndex = event.getActionIndex();
        int action = event.getActionMasked();
        int id = event.getPointerId(actionIndex);

        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && id < MAX_FINGERS) {
            mFingerPaths[id] = new Path();
            mFingerPaths[id].moveTo(event.getX(actionIndex), event.getY(actionIndex));
        } else if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP) && id < MAX_FINGERS) {
            mFingerPaths[id].setLastPoint(event.getX(actionIndex), event.getY(actionIndex));
            mCompletedPaths.add(new Pair<>(mFingerPaths[id], mPaint.getColor()));
            invalidate();
            mFingerPaths[id] = null;
        }

        if((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) ||
                (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP)) {
            StrokeDto strokeDto = new StrokeDto();
            strokeDto.setX(event.getX(actionIndex));
            strokeDto.setY(event.getY(actionIndex));
            strokeDto.setEvent(action);
            strokeDto.setFinger((byte) id);
            strokeDto.setColor(mPaint.getColor());
            strokeDto.setWidth((byte)mPaint.getStrokeWidth());
            sendStroke(strokeDto);
        }

        for(int i = 0; i < cappedPointerCount; i++) {
            if(mFingerPaths[i] != null) {
                int index = event.findPointerIndex(i);
                mFingerPaths[i].lineTo(event.getX(index), event.getY(index));
                invalidate();

                StrokeDto strokeDto = new StrokeDto();
                strokeDto.setX(event.getX(index));
                strokeDto.setY(event.getY(index));
                strokeDto.setEvent(action);
                strokeDto.setFinger((byte) id);
                strokeDto.setColor(mPaint.getColor());
                strokeDto.setWidth((byte)mPaint.getStrokeWidth());
                sendStroke(strokeDto);
            }
        }

        return true;
    }

    public void setStrokeWidth(int strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
    }
}