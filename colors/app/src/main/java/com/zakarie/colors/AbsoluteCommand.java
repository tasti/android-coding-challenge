package com.zakarie.colors;

public class AbsoluteCommand extends Command {

    private static final String type = "Absolute";

    AbsoluteCommand(byte[] bytes) {
        super(bytes[0], bytes[1], bytes[2]);
    }

    public int getX(byte color) {
        // The color should be in the range of [0, 255]
        if (color < 0) {
            return color + 256;
        }

        return color;
    }

    public String getCommandType() {
        return type;
    }

}
