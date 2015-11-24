package com.realdolmen.multifinger;

import java.io.Serializable;

public class StrokeDto implements Serializable {
    private int color;
    private SerializablePath path;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public SerializablePath getPath() {
        return path;
    }

    public void setPath(SerializablePath path) {
        this.path = path;
    }
}
