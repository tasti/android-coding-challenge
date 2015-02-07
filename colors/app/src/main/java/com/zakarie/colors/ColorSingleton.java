package com.zakarie.colors;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class ColorSingleton {
    private static ColorSingleton instance = null;

    public static final int COLOR_DEFAULT = 127;
    private int r;
    private int g;
    private int b;

    private View colorBox;
    private TextView redLabel;
    private TextView greenLabel;
    private TextView blueLabel;

    protected ColorSingleton(View colorBox, TextView redLabel, TextView greenLabel, TextView blueLabel) {
        initialize(colorBox, redLabel, greenLabel, blueLabel);
    }

    public static void createInstance(View colorBox, TextView redLabel, TextView greenLabel, TextView blueLabel) {
        if (instance == null) {
            instance = new ColorSingleton(colorBox, redLabel, greenLabel, blueLabel);
            return;
        }

        instance.initialize(colorBox, redLabel, greenLabel, blueLabel);
    }

    // Allows for updating the updating the views when ColorsActivity is recreated
    private void initialize(View colorBox, TextView redLabel, TextView greenLabel, TextView blueLabel) {
        this.r = COLOR_DEFAULT;
        this.g = COLOR_DEFAULT;
        this.b = COLOR_DEFAULT;

        this.colorBox = colorBox;
        this.redLabel = redLabel;
        this.greenLabel = greenLabel;
        this.blueLabel= blueLabel;

        displayUpdate();
    }

    public static ColorSingleton getInstance() {
        if (instance == null) {
            throw new RuntimeException("getInstance() called before createInstance(...)");
        }

        return instance;
    }

    private void displayUpdate() {
        colorBox.setBackgroundColor(Color.rgb(this.r, this.g, this.b));

        redLabel.setText(Integer.toString(this.r));
        greenLabel.setText(Integer.toString(this.g));
        blueLabel.setText(Integer.toString(this.b));
    }

    private static int posmod(int n, int m) {
        return ((n % m) + m) % m;
    }

    public synchronized void update(RelativeCommand rc) {
        this.r = posmod(this.r + rc.getR(), 256);
        this.g = posmod(this.g + rc.getG(), 256);
        this.b = posmod(this.b + rc.getB(), 256);

        displayUpdate();
    }

    public synchronized void update(AbsoluteCommand ac) {
        this.r = ac.getR();
        this.g = ac.getG();
        this.b = ac.getB();

        displayUpdate();
    }

    public synchronized void revert(RelativeCommand rc) {
        this.r = posmod(this.r - rc.getR(), 256);
        this.g = posmod(this.g - rc.getG(), 256);
        this.b = posmod(this.b - rc.getB(), 256);

        displayUpdate();
    }

    public synchronized void revert(AbsoluteCommand ac) {
        // No changes need to be done to the color
    }
}
