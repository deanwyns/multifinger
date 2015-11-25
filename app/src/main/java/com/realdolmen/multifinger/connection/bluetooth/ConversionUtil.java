package com.realdolmen.multifinger.connection.bluetooth;

import com.realdolmen.multifinger.connection.StrokeDto;

import java.nio.ByteBuffer;

public class ConversionUtil {
    public StrokeDto fromBytes(byte[] bytes) {
        StrokeDto strokeDto = new StrokeDto();

        // x coordinate (0x4)
        ByteBuffer xbuf = ByteBuffer.wrap(bytes, 0, 4);
        strokeDto.setX(xbuf.getFloat());

        // y coordinate (0x4)
        ByteBuffer ybuf = ByteBuffer.wrap(bytes, 4, 4);
        strokeDto.setY(ybuf.getFloat());

        // width (0x1)
        strokeDto.setWidth(bytes[8]);

        // color (0x4)
        ByteBuffer colorbuf = ByteBuffer.wrap(bytes, 9, 4);
        strokeDto.setColor(colorbuf.getInt());

        // event (0x4)
        ByteBuffer eventbuf = ByteBuffer.wrap(bytes, 13, 4);
        strokeDto.setEvent(eventbuf.getInt());

        // finger (0x1)
        strokeDto.setFinger(bytes[17]);

        return strokeDto;
    }

    public byte[] toBytes(StrokeDto strokeDto) {
        ByteBuffer buf = ByteBuffer.allocate(18);
        buf.putFloat(strokeDto.getX());
        buf.putFloat(strokeDto.getY());
        buf.put(strokeDto.getWidth());
        buf.putInt(strokeDto.getColor());
        buf.putInt(strokeDto.getEvent());
        buf.put(strokeDto.getFinger());

        return buf.array();
    }
}
