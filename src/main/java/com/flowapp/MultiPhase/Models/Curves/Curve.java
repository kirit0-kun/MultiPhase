package com.flowapp.MultiPhase.Models.Curves;

public interface Curve {
    public Float getX(float y);

    public Float getY(float x);

    public boolean inRange(float x);
}
