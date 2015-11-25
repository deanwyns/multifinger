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

import com.realdolmen.multifinger.R;
import com.realdolmen.multifinger.views.DrawingView;

import java.util.ArrayList;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;


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
}
