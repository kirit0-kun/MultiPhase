package com.flowapp.MultiPhase.Models;

import com.flowapp.MultiPhase.Utils.Constants;

public class LockhartAndMartinilliResult {

    private final float liquidNre;
    private final float liquidF;
    private final float liquidHf;
    private final double liquidPressureDropPSIA;
    private final FlowType liquidFlowType;
    private final double gasDeltaP;

    private final float bg;
    private final float gasNre;
    private final FlowType gasFlowType;

    private final float x;

    public LockhartAndMartinilliResult(float liquidNre, float liquidF, float liquidHf, double liquidPressureDropPSIA, double gasDeltaP, float bg, float gasNre, float x) {
        this.liquidNre = liquidNre;
        this.liquidF = liquidF;
        this.liquidHf = liquidHf;
        this.liquidPressureDropPSIA = liquidPressureDropPSIA;
        this.gasDeltaP = gasDeltaP;
        this.bg = bg;
        this.gasNre = gasNre;
        this.x = x;
        if (liquidNre <= Constants.LaminarFlowMaxNre) {
            liquidFlowType = FlowType.LAMINAR;
        } else {
            liquidFlowType = FlowType.TURBULENT;
        }
        if (gasNre <= Constants.LaminarFlowMaxNre) {
            gasFlowType = FlowType.LAMINAR;
        } else {
            gasFlowType = FlowType.TURBULENT;
        }

    }

    public float getLiquidNre() {
        return liquidNre;
    }

    public float getLiquidF() {
        return liquidF;
    }

    public float getLiquidHf() {
        return liquidHf;
    }

    public double getLiquidPressureDropPSIA() {
        return liquidPressureDropPSIA;
    }

    public FlowType getLiquidFlowType() {
        return liquidFlowType;
    }

    public double getGasDeltaP() {
        return gasDeltaP;
    }

    public float getBg() {
        return bg;
    }

    public float getGasNre() {
        return gasNre;
    }

    public FlowType getGasFlowType() {
        return gasFlowType;
    }

    public float getX() {
        return x;
    }

    @Override
    public String toString() {
        return "LockhartAndMartinilliResult{" +
                "liquidNre=" + liquidNre +
                ", liquidF=" + liquidF +
                ", liquidHf=" + liquidHf +
                ", liquidPressureDropPSIA=" + liquidPressureDropPSIA +
                ", liquidFlowType=" + liquidFlowType +
                ", gasDeltaP=" + gasDeltaP +
                ", bg=" + bg +
                ", gasNre=" + gasNre +
                ", gasFlowType=" + gasFlowType +
                ", x=" + x +
                '}';
    }
}
