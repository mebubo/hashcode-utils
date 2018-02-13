package org.hildan.hashcode.utils.examples.satellites.model;

public class ImageCollection {

    public final int value;

    public Location[] locations;

    public int[][] ranges;

    public ImageCollection(int value) {
        this.value = value;
    }

    public void setLocations(Location[] locations) {
        this.locations = locations;
    }

    public void setRanges(int[][] ranges) {
        this.ranges = ranges;
    }
}
