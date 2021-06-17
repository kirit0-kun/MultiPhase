package com.flowapp.MultiPhase.Models;

public class PanhandlePResult {
    private final double p1;
    private final double p2;
    private final double pAvg;
    private final float pr;
    private final float z;
    private final float pc;
    private final float tc;
    private final float tr;

    public PanhandlePResult(double p1, double p2, double pAvg, float pr, float z, float pc, float tc, float tr) {
        this.p1 = p1;
        this.p2 = p2;
        this.pAvg = pAvg;
        this.pr = pr;
        this.z = z;
        this.pc = pc;
        this.tc = tc;
        this.tr = tr;
    }

    public double getP1() {
        return p1;
    }

    public double getP2() {
        return p2;
    }

    public double getPAvg() {
        return pAvg;
    }

    public float getPr() {
        return pr;
    }

    public float getZ() {
        return z;
    }

    public float getPc() {
        return pc;
    }

    public float getTc() {
        return tc;
    }

    public float getTr() {
        return tr;
    }
}
