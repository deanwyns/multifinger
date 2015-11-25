package com.realdolmen.multifinger.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.realdolmen.multifinger.connection.StrokeDto;
import com.realdolmen.multifinger.views.DrawingView;


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

    public void drawOpponentStroke(StrokeDto strokeDto) {
        dv.drawOpponentStroke(strokeDto);
    }

    public void setStrokeWidth(int strokeWidth) {
        dv.setStrokeWidth(strokeWidth);
    }
}
