package com.flowapp.MultiPhase.Models;

public class Api2Result {
    private final float gLR;
    private final float mixtureDensityLBCF;
    private final float w;
    private final float deltaP;

    public Api2Result(float gLR, float mixtureDensityLBCF, float w, float deltaP) {
        this.gLR = gLR;
        this.mixtureDensityLBCF = mixtureDensityLBCF;
        this.w = w;
        this.deltaP = deltaP;
    }

    public float getGLR() {
        return gLR;
    }

    public float getMixtureDensityLBCF() {
        return mixtureDensityLBCF;
    }

    public float getW() {
        return w;
    }

    public float getDeltaP() {
        return deltaP;
    }

    @Override
    public String toString() {
        return "Api2Result{" +
                "gLR=" + gLR +
                ", mixtureDensityLBCF=" + mixtureDensityLBCF +
                ", w=" + w +
                ", deltaP=" + deltaP +
                '}';
    }
}
