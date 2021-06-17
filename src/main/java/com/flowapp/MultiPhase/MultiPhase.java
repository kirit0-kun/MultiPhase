package com.flowapp.MultiPhase;

import com.flowapp.MultiPhase.Models.*;
import com.flowapp.MultiPhase.Models.Graphs.MultiPhaseCorrelation;
import com.flowapp.MultiPhase.Utils.Constants;
import com.flowapp.MultiPhase.Utils.FileUtils;
import com.flowapp.MultiPhase.Utils.TableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MultiPhase {

    private StringBuilder steps;

    public MultiPhaseResult multiPhase(float liquidFlowRateSCFDay,
                                       float liquidViscosityCP,
                                       Float liquidDensityLBCF,
                                       Float liquidSpGr,
                                       float iDIN,
                                       float avgTC,
                                       float roughness,
                                       float gasFlowRateSCFDay,
                                       float gasViscosityCP,
                                       Float gasDensityLBCF,
                                       Float gasSpGr,
                                       Float lineStartPressurePsia,
                                       Float lineEndPressurePsia,
                                       float lengthFt) {
        clear();
        final float avgTR = avgTC * 9/5.0f + Constants.FahrenheitZero + Constants.RankinZeroF;

        println("T,R = {} R", avgTR);
        if (liquidSpGr == null) {
            liquidSpGr = liquidDensityLBCF / Constants.WaterDensityLBCF;
            println("δl = {} / {} = {}", liquidDensityLBCF, Constants.WaterDensityLBCF, liquidSpGr);
        }
        final Api1Result api1Result;
        final Api2Result api2Result;
        if (lineStartPressurePsia != null) {
            api1Result = api1(avgTR, lineStartPressurePsia, gasDensityLBCF, gasSpGr);
            println("API RP 14 E Method");
            gasSpGr = api1Result.getSpGr();
            api2Result = api2(avgTR, api1Result.getZ(), gasSpGr, roughness, lineStartPressurePsia, gasFlowRateSCFDay, liquidFlowRateSCFDay, lengthFt, iDIN);
            final float actualAPIP2 = lineStartPressurePsia - api2Result.getDeltaP();
            println("P2 = {} - {} = {} Psia", lineStartPressurePsia, api2Result.getDeltaP(), actualAPIP2);
        } else {
            api1Result = null;
            api2Result = null;
        }
        println("Lockhart & Martinilli Method:");
        final var lockResult =  lockhartAndMartinilli(iDIN, lengthFt, lineStartPressurePsia, lineEndPressurePsia, avgTR, gasFlowRateSCFDay, gasSpGr, gasViscosityCP, liquidFlowRateSCFDay, liquidSpGr, liquidViscosityCP);
        println("From Correlation Chart:");
        final MultiPhaseCorrelation multiPhaseCorrelation = new MultiPhaseCorrelation();
        final float gasPhi = multiPhaseCorrelation.getGasPhi(lockResult.getGasFlowType(), lockResult.getLiquidFlowType(), lockResult.getX());
        println("Φg = {}", gasPhi);
        final float liquidPhi = multiPhaseCorrelation.getLiquidPhi(lockResult.getLiquidFlowType(), lockResult.getGasFlowType(), lockResult.getX());
        println("Φl = {}", liquidPhi);
        final double deltaPg = Math.pow(gasPhi, 2) * lockResult.getGasDeltaP();
        println("ΔPg = {} ^ 2 * {} = {} Psia",gasPhi,lockResult.getGasDeltaP(),  deltaPg);
        final double deltaPL = Math.pow(liquidPhi, 2) * lockResult.getLiquidPressureDropPSIA();
        println("ΔPl = {} ^ 2 * {} = {} Psia",liquidPhi, lockResult.getLiquidPressureDropPSIA(), deltaPL);
        final double actualDeltaP = Math.max(deltaPg, deltaPL);
        println("ΔP = {} Psia", actualDeltaP);
        final double actualLMP2;
        if (lineStartPressurePsia != null) {
            actualLMP2 = lineStartPressurePsia - actualDeltaP;
            println("P2 = {} - {} = {} Psia", lineStartPressurePsia, actualDeltaP, actualLMP2);
        } else {
            actualLMP2 = lineEndPressurePsia + actualDeltaP;
            println("P1 = {} + {} = {} Psia", lineEndPressurePsia, actualDeltaP, actualLMP2);
        }
        return new MultiPhaseResult(api1Result, api2Result, lockResult, steps.toString());
    }

    private LockhartAndMartinilliResult lockhartAndMartinilli(float iDIn,
                                                                     float lengthFt,
                                                                     Float operatingPressure,
                                                                     Float endPressure,
                                                                     float avgTR,
                                                                     float gasFlowRateSCFDay,
                                                                     float gasSpecificGravity,
                                                                     float gasViscosityCP,
                                                                     float liquidFlowRateSCFDay,
                                                                     float liquidSpGr,
                                                                     float liquidViscosityCP) {

        final float iDM = iDIn / Constants.InchesInMeter; //iDM
        final float lengthM = lengthFt / Constants.FeetInMeter; // lengthM
        final float liquidFlowRateM3H = liquidFlowRateSCFDay / Constants.SCFInM3 / 24; // liquidFlowRateM3H

        println("For Liquid:");
        final float velocityMS = (float) (4 * liquidFlowRateM3H / (3600 * Math.PI * Math.pow(iDM,2)));
        println("v = {} m/s", velocityMS);
        final float liquidNre = 1_000_000 * velocityMS * iDM * liquidSpGr / liquidViscosityCP; // liquidNre
        println("Nre = {}", liquidNre);
        float f;
        if (liquidNre <= Constants.LaminarFlowMaxNre) {
            f = 64 / liquidNre;
        } else if (liquidNre < Constants.TurbulentFlowMaxNre) {
            f = (float) (0.5 / Math.pow(liquidNre, 0.3));
        } else {
            f = (float) (0.316 / Math.pow(liquidNre, 0.25));
        }
        println("f = {}", f);
        final float liquidHf = (float) (f * Math.pow(velocityMS,2) * lengthM / (19.6f * iDM));
        println("Hf = {} m", liquidHf);
        final double liquidDeltaP = 14.7 * liquidHf * liquidSpGr / 10;
        println("ΔPl = {} Psia", liquidDeltaP);
        println("For Gas:");
        final PanhandlePResult gasResult;
        if (operatingPressure != null) {
            gasResult = calculateP2(operatingPressure, gasSpecificGravity, iDIn, gasFlowRateSCFDay, lengthM / 1000 / Constants.KmInMile, avgTR);
            println("P2 = {} Psia", gasResult.getP2());
        } else {
            gasResult = calculateP1(endPressure, gasSpecificGravity, iDIn, gasFlowRateSCFDay, lengthM / 1000 / Constants.KmInMile, avgTR);
            println("P1 = {} Psia", gasResult.getP1());
        }
        final double gasDeltaP = gasResult.getP1() - gasResult.getP2();
        println("ΔPg = {} Psia", gasDeltaP);
        final float x = (float) Math.sqrt(liquidDeltaP/gasDeltaP);
        println("x = {}", x);
        final float bg = (float) (0.0283f * gasResult.getZ() * avgTR/gasResult.getPAvg());
        println("bg = {}", bg);
        final float gasFlowRateM3S = gasFlowRateSCFDay / Constants.SCFInM3 / 24 / 60 / 60;
        final float gasNre = 1557.3f * gasFlowRateM3S * gasSpecificGravity * bg / (iDM * gasViscosityCP);
        println("Nre = {}", gasNre);

        return new LockhartAndMartinilliResult(liquidNre, f, liquidHf, liquidDeltaP, gasDeltaP, bg, gasNre, x);
    }

    private Api2Result api2(float avgTR,
                                   float z,
                                   float spGrGas,
                                   float roughness,
                                   float operatingPPSIA,
                                   float gasFlowRateSCFDay,
                                   float liquidFlowRateSCFDay,
                                   float lengthFt ,
                                   float iDIN) {

        float gasFlowRateMMSCFDay = gasFlowRateSCFDay / 1_000_000f;

        final float iDmm = iDIN * Constants.MmInInch;
        println("ID,mm = {} mm", iDmm);
        final float ed = roughness/iDmm;
        println("e/d = {}/{} = {}", roughness, iDmm, ed);
        final float f = (float) Math.pow(1.14f - 2 * Math.log10(ed), -2);
        println("F = {}", f);

        final float liquidDensityLBCF = 52.2f;
        float x = liquidDensityLBCF / Constants.WaterDensityLBCF;

        final float liquidFlowRateBBLDay = liquidFlowRateSCFDay / 5.615f;
        final float gLR = gasFlowRateSCFDay / liquidFlowRateBBLDay;
        println("GLR = {} SCF/BBL", gLR);
        final float mixtureDensityLBCF = (12409 * operatingPPSIA * x + 0.7f * gLR * spGrGas * operatingPPSIA ) / (198.7f * operatingPPSIA + gLR * z * avgTR); // mixtureDensityLBCF
        println("ρm = {} lb/cf", mixtureDensityLBCF);
        final float w = 3180 * gasFlowRateMMSCFDay  * spGrGas + 14.6f * liquidFlowRateBBLDay * x; // w
        println("w = {}", w);
        final float deltaP = (float) ((3.4e-6f * lengthFt * f * Math.pow(w, 2)) / (mixtureDensityLBCF * Math.pow(iDIN, 5))); // deltaP
        println("ΔP = {} Psia", deltaP);
        return new Api2Result(gLR, mixtureDensityLBCF, w, deltaP);
    }

    private Api1Result api1(float avgTR,
                            float maxPressurePSIA,
                            Float gasDensity, Float gasSpGr) {
        float assumedZ = 0.9f;
        float pcPSIA;
        float tcR;
        float tr;
        float pr;
        println("Using API 1:");
        println("ρg = 2.7Pδg / (z.T)");
        while (true) {
            if (gasDensity != null) {
                gasSpGr = gasDensity * assumedZ * avgTR / (2.7f * maxPressurePSIA);
            }
            pcPSIA = 709.605f - gasSpGr * 58.718f;
            tcR = 170.491f + 307.344f * gasSpGr;
            tr = avgTR / tcR;
            pr = maxPressurePSIA / pcPSIA;
            float zCalc = 1 + 0.257f * pr - 0.533f * pr/tr;
            final float error = Math.abs(zCalc - assumedZ);
            assumedZ = zCalc;
            if (error < Constants.ZAllowedError) {
                break;
            }
        }
        println("z = {}, Pc = {} Psia, Pr = {}", assumedZ, pcPSIA, pr);
        if (gasDensity != null) {
            println("δg = {}", gasSpGr);
        }
        println("δg = {}, Tc = {} R, Tr = {}", gasSpGr, tcR, tr);
        return new Api1Result(gasSpGr, pcPSIA, tcR, tr, pr, assumedZ);
    }


    private PanhandlePResult calculateP1(float p2,
                                         float spGr,
                                         float idIn,
                                         float qScfD,
                                         float lengthMile,
                                         float tAvg) {

        final float pc = 709.604f - 58.718f * spGr; // Pc
        final float tc = 170.492f + 307.344f * spGr; // Tc
        final float tr = tAvg / tc; // TR
        println("Pc = {} Psi, Tc = {} F, TR = {}", pc, tc, tr);
        final List<Object[]> attempts = new ArrayList<>();
        attempts.add(new Object[]{ "Pass, Psi", "Pavg, Psi", "Pr", "z", "P1(calc), Psi"});
        double p1As = p2 * 3.0; // p1As
        double pAvg; // pAvg
        float pr; // PR
        float z; //z

        // Loop
        while (true) {
            pAvg = calculateAvgPressure(p1As, p2);
            pr = (float) (pAvg / pc);
            z = 1 + 0.257f * pr - 0.533f * pr / tr;
            final double p1Calc = Math.pow(Math.pow(p2, 2) + Math.pow(qScfD / (27998.0133f * Math.pow(idIn, 2.53f)), 1.9608f) * Math.pow(spGr, 0.961f) * tAvg * z * lengthMile, 0.5f); // p1Calc
            final var temp = Math.abs(p1Calc - p1As);
            attempts.add(new Object[]{ p1As, pAvg, pr, z, p1Calc });
            p1As = p1Calc;
            if (temp <= 0.9) {
                break;
            }
        }
        renderTable(attempts);
        return new PanhandlePResult(p1As, p2, pAvg, pr, z, pc, tc, tr);
    }

    private PanhandlePResult calculateP2(float p1,
                                                float spGr,
                                                float idIN,
                                                float qScfD,
                                                float lengthMile,
                                                float tAvg) {

        final float pc = 709.604f - 58.718f * spGr; // Pc
        final float tc = 170.492f + 307.344f * spGr; // Tc
        final float tr = tAvg / tc; // TR
            println("Pc = {} Psi, Tc = {} F, TR = {}", pc, tc, tr);
        final List<Object[]> attempts = new ArrayList<>();
        attempts.add(new Object[]{ "Pass, Psi", "Pavg, Psi", "Pr", "z", "P2(calc), Psi"});
        double p2As = Math.round(p1/3.0);
        double pAvg;
        float pr;
        float z;
        while (true) {
            pAvg = calculateAvgPressure(p1, p2As);
            pr = (float) (pAvg / pc);
            z = 1 + 0.257f * pr - 0.533f * pr / tr;
            final double p2Calc = Math.pow(Math.pow(p1, 2) - Math.pow(qScfD / (27998.0134f * Math.pow(idIN, 2.53f)), 1.96078f) * Math.pow(spGr, 0.961f) * tAvg * z * lengthMile, 0.5f); // p1Calc
            final var temp = Math.abs(p2Calc - p2As);
            attempts.add(new Object[]{ p2As, pAvg, pr, z, p2Calc });
            p2As = p2Calc;
            if (temp <= 0.9) {
                break;
            }
        }
            renderTable(attempts);
        return new PanhandlePResult(p1, p2As, pAvg, pr, z, pc, tc, tr);
    }

    private double calculateAvgPressure(double p1, double p2) {
        return (Math.pow(p1, 3) - Math.pow(p2, 3)) / (Math.pow(p1, 2) - Math.pow(p2, 2)) * 2 / 3.0;
    }

    private void renderTable(List<Object[]> args) {
        renderTable(args.toArray(new Object[0][0]));
    }

    private void renderTable(Object[] ... args) {
        final var temp = args[0];
        final String[] firstRow = new String[temp.length];
        for (int i = 0; i < temp.length; i++) {
            firstRow[i] = temp[i].toString();
        }
        TableList at = new TableList(firstRow).withUnicode(true);
        final var newRows = Arrays.stream(args).skip(1).map(row -> {
            final String[] newRow = new String[row.length];
            for (int i = 0; i < row.length; i++) {
                final Object object = row[i];
                if (object instanceof Number) {
                    newRow[i] = formatNumber((Number) object);
                } else {
                    newRow[i] = object.toString();
                }
            }
            return newRow;
        }).collect(Collectors.toList());
        for (var row: newRows) {
            at.addRow(row);
        }
        String rend = at.render();
        println(rend);
    }

    private void println(@NotNull String pattern, Object... args) {
        final String message = format(pattern, args);
        steps.append(message).append('\n');
        FileUtils.printOut(message);
    }

    private void clear() {
        steps = new StringBuilder();
        FileUtils.clear();
    }

    private String formatNumber(Number number) {
        final var value = number.floatValue();
        if (number instanceof Double) {
            return number.toString();
        }
        if (value < 0) {
            return String.format("%.7f", value);
        } else if (value == 0) {
            return  "0";
        } else {
            return String.format("%.4f", value).replace(".0000", "");
        }
    }

    @NotNull
    private String format(@NotNull String pattern, Object... args) {
        Pattern rePattern = Pattern.compile("\\{([0-9+-]*)}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = rePattern.matcher(pattern);
        int counter = -1;
        while (matcher.find()) {
            counter++;
            String number = matcher.group(1);
            if (number == null) {
                number = "";
            }
            if (!number.isBlank()) {
                if (number.equals("+")) {
                    number = "\\+";
                    counter++;
                } else if (number.equals("-")) {
                    counter--;
                } else {
                    counter = Integer.parseInt(number);
                }
            }
            counter = clamp(counter, 0, args.length - 1);
            String toChange = "\\{" + number + "}";
            Object object = args[counter];
            String objectString;
            if (object instanceof Number) {
                objectString = formatNumber((Number) object);
            } else {
                objectString = object.toString();
            }
            String result = objectString;
            pattern = pattern.replaceFirst(toChange, result);
        }
        return pattern;
    }

    private <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) return min;
        else if (val.compareTo(max) > 0) return max;
        else return val;
    }
}
