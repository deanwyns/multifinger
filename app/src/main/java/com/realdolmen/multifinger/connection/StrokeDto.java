package com.realdolmen.multifinger.connection;

/**
 * 0x0: x
 * 0x4: y
 * 0x8: width
 * 0x9: color
 * 0x13: event
 * 0x17: finger
 *
 * size: 18 bytes
 */
public class StrokeDto {
    private float x, y;
    private byte width;
    private int color;
    private int event;
    private byte finger;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public byte getWidth() {
        return width;
    }

    public void setWidth(byte width) {
        this.width = width;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public byte getFinger() {
        return finger;
    }

    public void setFinger(byte finger) {
        this.finger = finger;
    }

    @Override
    public String toString() {
        return "StrokeDto{" +
                "finger=" + finger +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", event=" + event +
                '}';
    }
}
