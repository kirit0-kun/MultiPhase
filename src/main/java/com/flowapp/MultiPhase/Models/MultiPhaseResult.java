package com.flowapp.MultiPhase.Models;

public class MultiPhaseResult {
    private final Api1Result api1Result;
    private final Api2Result api2Result;
    private final LockhartAndMartinilliResult lockhartAndMartinilliResult;
    private final String steps;

    public MultiPhaseResult(Api1Result api1Result, Api2Result api2Result, LockhartAndMartinilliResult lockhartAndMartinilliResult, String steps) {
        this.api1Result = api1Result;
        this.api2Result = api2Result;
        this.lockhartAndMartinilliResult = lockhartAndMartinilliResult;
        this.steps = steps;
    }

    public Api1Result getApi1Result() {
        return api1Result;
    }

    public Api2Result getApi2Result() {
        return api2Result;
    }

    public LockhartAndMartinilliResult getLockhartAndMartinilliResult() {
        return lockhartAndMartinilliResult;
    }

    public String getSteps() {
        return steps;
    }
}
