package com.flowapp.MultiPhase.Models.Curves;

import com.flowapp.MultiPhase.Models.Point;

import java.util.List;

public class LogLogPointsCurve extends PointsCurve {

    public LogLogPointsCurve(Point[] mPoints) {
        super(fixPoints(mPoints));
    }

    static Point[] fixPoints(Point[] mPoints) {
        final Point[] newPoints = new Point[mPoints.length];
        for (int i = 0; i < mPoints.length; i++) {
            newPoints[i] = Point.of(Math.log10(mPoints[i].getX()), Math.log10(mPoints[i].getY()));
        }
        return newPoints;
    }

    public LogLogPointsCurve(List<Point> points) {
        this(points.toArray(new Point[0]));
    }

    @Override
    public Float getX(float y) {
        return (float)Math.pow(10, super.getX((float) Math.log10(y)));
    }


    @Override
    public Float getY(float x) {
        return (float)Math.pow(10, super.getY((float) Math.log10(x)));
    }
}
