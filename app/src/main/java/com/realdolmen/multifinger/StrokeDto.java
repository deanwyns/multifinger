package com.realdolmen.multifinger;

import android.graphics.Path;

import java.io.Serializable;

public class StrokeDto implements Serializable {
    private int color;
    private Path path;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
