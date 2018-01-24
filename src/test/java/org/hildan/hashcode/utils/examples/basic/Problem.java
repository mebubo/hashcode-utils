package org.hildan.hashcode.utils.examples.basic;

import java.util.ArrayList;
import java.util.List;

class Problem {
    public final int nClusters;

    private List<Point> points;

    Problem(int nClusters) {
        this.nClusters = nClusters;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<String> solve() {

        // solve the problem here

        // write solution into lines (this is problem-specific)
        List<String> lines = new ArrayList<>();
        lines.add("output line 0");
        lines.add("output line 1");
        return lines;
    }
}
