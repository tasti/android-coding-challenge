package com.zakarie.colors;

public abstract class Command {

    private final byte R;
    private final byte G;
    private final byte B;

    private boolean selected = true;

    Command(byte r, byte g, byte b) {
        this.R = r;
        this.G = g;
        this.B = b;
    }

    protected abstract int getX(byte color);

    public int getR() {
        return getX(this.R);
    }

    public int getG() {
        return getX(this.G);
    }

    public int getB() {
        return getX(this.B);
    }

    public abstract String getCommandType();

    public boolean isSelected() {
        return this.selected;
    }

    public void toggleSelected() {
        this.selected = !this.selected;
    }

}
