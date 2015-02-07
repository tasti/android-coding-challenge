package com.zakarie.colors;

public class RelativeCommand extends Command {

    private static final String type = "Relative";

    RelativeCommand(byte[] bytes) {
        // The first byte of each pair is irrelevant since possible offset range is [-128, 127]
        super(bytes[1], bytes[3], bytes[5]);
    }

    protected int getX(byte color) {
        return color;
    }

    public String getCommandType() {
        return type;
    }

}
