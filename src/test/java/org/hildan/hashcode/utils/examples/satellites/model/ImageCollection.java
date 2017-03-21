package org.hildan.hashcode.utils.examples.satellites.model;

public class ImageCollection {

    public int value;

    public Location[] locations;

    public int[][] ranges;

    public ImageCollection(int value, int nLocations, int nRanges) {
        this.value = value;
        this.locations = new Location[nLocations];
        this.ranges = new int[nRanges][2];
    }
}
