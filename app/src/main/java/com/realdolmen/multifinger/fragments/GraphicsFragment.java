package com.realdolmen.multifinger.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import roboguice.fragment.RoboFragment;

public class GraphicsFragment extends Fragment {

    public DrawingView dv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dv = new DrawingView(getActivity());
        return dv;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void clearView(){
        dv.clearScreen();
    }

    public void setPaintColor(int paintColor) {
        dv.setColor(paintColor);
    }


    public class DrawingView extends View {
        public static final int MAX_FINGERS = 5;
        private Path[] mFingerPaths = new Path[MAX_FINGERS];
        private ArrayList<Pair<Path, Integer>> mCompletedPaths;
        private RectF mPathBounds = new RectF();
        private Paint mPaint;

        public DrawingView(Context c) {
            super(c);
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
            mPaint.setStrokeWidth(12);
            mPaint.setStrokeCap(Paint.Cap.BUTT);
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
                mFingerPaths[id].computeBounds(mPathBounds, true);
                invalidate((int) mPathBounds.left, (int) mPathBounds.top,
                        (int) mPathBounds.right, (int) mPathBounds.bottom);
                mFingerPaths[id] = null;
            }

            for(int i = 0; i < cappedPointerCount; i++) {
                if(mFingerPaths[i] != null) {
                    int index = event.findPointerIndex(i);
                    mFingerPaths[i].lineTo(event.getX(index), event.getY(index));
                    mFingerPaths[i].computeBounds(mPathBounds, true);
                    invalidate((int) mPathBounds.left, (int) mPathBounds.top,
                            (int) mPathBounds.right, (int) mPathBounds.bottom);
                }
            }

            return true;
        }
    }
}
