package com.flowapp.MultiPhase.Models.Graphs;

import com.flowapp.MultiPhase.Models.Curves.Curve;
import com.flowapp.MultiPhase.Models.Curves.LogLogPointsCurve;
import com.flowapp.MultiPhase.Models.FlowType;
import com.flowapp.MultiPhase.Models.Point;
import com.flowapp.MultiPhase.Models.Tuple2;

import java.util.Map;

public class MultiPhaseCorrelation {

    final Map<Tuple2<FlowType, FlowType>, Curve> gasCurves = Map.of(
            Tuple2.of(FlowType.TURBULENT, FlowType.TURBULENT), new LogLogPointsCurve(new Point[]{
                    Point.of(0.01, 1.2),
                    Point.of(0.05, 1.5),
                    Point.of(0.1, 1.8),
                    Point.of(1,4.2),
                    Point.of(5,10.1),
                    Point.of(20,30),
                    Point.of(100, 110),
            }),
            Tuple2.of(FlowType.TURBULENT, FlowType.LAMINAR), new LogLogPointsCurve(new Point[]{
                    Point.of(0.01, 1),
                    Point.of(0.1, 1.5),
                    Point.of(0.5,2.5),
                    Point.of(1.5,4.4),
                    Point.of(20,30),
                    Point.of(100, 110),
            }),
            Tuple2.of(FlowType.LAMINAR, FlowType.TURBULENT), new LogLogPointsCurve(new Point[]{
                    Point.of(0.01, 1.2),
                    Point.of(0.1, 1.6),
                    Point.of(0.5,2.55),
                    Point.of(2,5),
                    Point.of(7,12),
                    Point.of(100, 110),
            }),
            Tuple2.of(FlowType.LAMINAR, FlowType.LAMINAR), new LogLogPointsCurve(new Point[]{
                    Point.of(0.01, 1),
                    Point.of(0.1, 1.3),
                    Point.of(0.2, 1.5),
                    Point.of(0.9,2.5),
                    Point.of(2,4),
                    Point.of(4,7),
                    Point.of(100, 110),
            })
    );

    final Map<Tuple2<FlowType, FlowType>, Curve> liquidCurves = Map.of(
            Tuple2.of(FlowType.TURBULENT, FlowType.TURBULENT), new LogLogPointsCurve(new Point[]{
                    Point.of(0.01, 135),
                    Point.of(0.07, 25),
                    Point.of(0.6, 6),
                    Point.of(1.5,3.5),
                    Point.of(7,2),
                    Point.of(20,1.5),
                    Point.of(100, 1.1),
            }),
            Tuple2.of(FlowType.LAMINAR, FlowType.TURBULENT), new LogLogPointsCurve(new Point[]{
                    Point.of(0.01, 130),
                    Point.of(0.07, 20),
                    Point.of(0.2,9),
                    Point.of(0.8,4),
                    Point.of(2.5,2.5),
                    Point.of(10, 1.55),
                    Point.of(100, 1.1),
            }),
            Tuple2.of(FlowType.TURBULENT, FlowType.LAMINAR), new LogLogPointsCurve(new Point[]{
                    Point.of(0.01, 110),
                    Point.of(0.07, 20),
                    Point.of(0.6,4.6),
                    Point.of(1.5,3.05),
                    Point.of(7,2),
                    Point.of(20, 1.5),
                    Point.of(100, 1.1),
            }),
            Tuple2.of(FlowType.LAMINAR, FlowType.LAMINAR), new LogLogPointsCurve(new Point[]{
                    Point.of(0.01, 100),
                    Point.of(0.1, 11.5),
                    Point.of(0.4, 4.5),
                    Point.of(1,2.7),
                    Point.of(4,1.8),
                    Point.of(100, 1.1),
            })
    );

    public Float getGasPhi(FlowType gasFlowType, FlowType liquidFlowType, float x) {
        return Math.round(gasCurves.get(Tuple2.of(gasFlowType, liquidFlowType)).getY(x) * 10.0f) / 10.0f;
    }

    public Float getLiquidPhi(FlowType liquidFlowType, FlowType gasFlowType, float x) {
        return Math.round(liquidCurves.get(Tuple2.of(liquidFlowType, gasFlowType)).getY(x) * 10.0f) / 10.0f;
    }
}
