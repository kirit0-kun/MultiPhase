package com.flowapp.MultiPhase.Models;

public class Api1Result {
    private final float spGr;
    private final float pcPSIA;
    private final float tcR;
    private final float tr;
    private final float pr;
    private final float z;

    public Api1Result(float spGr, float pcPSIA, float tcR, float tr, float pr, float z) {
        this.spGr = spGr;
        this.pcPSIA = pcPSIA;
        this.tcR = tcR;
        this.tr = tr;
        this.pr = pr;
        this.z = z;
    }

    public float getSpGr() {
        return spGr;
    }

    public float getPcPSIA() {
        return pcPSIA;
    }

    public float getTcR() {
        return tcR;
    }

    public float getTr() {
        return tr;
    }

    public float getPr() {
        return pr;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Api1Result{" +
                "spGr=" + spGr +
                ", pcPSIA=" + pcPSIA +
                ", tcR=" + tcR +
                ", tr=" + tr +
                ", pr=" + pr +
                ", z=" + z +
                '}';
    }
}
