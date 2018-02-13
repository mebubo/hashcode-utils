package org.hildan.hashcode.utils.examples.satellites;

import org.hildan.hashcode.utils.examples.satellites.model.ImageCollection;
import org.hildan.hashcode.utils.examples.satellites.model.Location;
import org.hildan.hashcode.utils.examples.satellites.model.Satellite;
import org.hildan.hashcode.utils.examples.satellites.model.Simulation;
import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Satellites {

    private static final String input = "3600\n" // Simulation lasts an hour.
            + "2\n" // Two satellites.
            + "170000 8300 300 50 500\n"  // First satellite starts at [170000, 8300], heading north
            + "180000 8300 -300 50 500\n" // Second satellite starts at [180000, 8300], heading south.
            + "3\n"           // Three image collections.
            + "100 1 1\n"     // First image collection is worth 100 points
            + "175958 8387\n" // The only location: Google office in Paris
            + "0 3599\n"      // The image can be taken at any time.
            + "100 1 2\n"     // Second image collection is worth 100 points.
            + "175889 8260\n" // The only location: the Eiffel Tower
            + "0 900\n"       // The image has to be taken in the first 15 minutes...
            + "2700 3599\n"   // ...or in the last 15 minutes
            + "300 2 1\n"     // Third image collection, worth 300 points.
            + "175958 8387\n" // Google office.
            + "175889 8260\n" // The Eiffel Tower.
            + "3300 3599\n";  // The images need to be taken in the last 5 minutes.

    @Test
    public void test_parser() {
        Parser<Simulation> rootReader = SatellitesParsers.simulation();
        HCParser<Simulation> parser = new HCParser<>(rootReader);
        Simulation problem = parser.parse(input);

        assertEquals(3600, problem.nTurns);
        assertEquals(2, problem.satellites.length);

        Satellite sat0 = problem.satellites[0];
        assertArrayEquals(new int[] {170000, 8300}, sat0.position);
        assertEquals(300, sat0.latitudeVelocity);
        assertEquals(50, sat0.maxOrientationChangePerTurn);
        assertEquals(500, sat0.maxOrientationValue);

        Satellite sat1 = problem.satellites[1];
        assertArrayEquals(new int[] {180000, 8300}, sat1.position);
        assertEquals(-300, sat1.latitudeVelocity);
        assertEquals(50, sat1.maxOrientationChangePerTurn);
        assertEquals(500, sat1.maxOrientationValue);

        assertEquals(3, problem.collections.length);

        ImageCollection coll0 = problem.collections[0];
        assertEquals(100, coll0.value);
        assertEquals(1, coll0.locations.length);
        assertEquals(1, coll0.ranges.length);

        Location loc00 = coll0.locations[0];
        assertEquals(coll0, loc00.parentCollection);
        assertArrayEquals(new int[] {175958, 8387}, loc00.coords);

        assertArrayEquals(new int[] {0, 3599}, coll0.ranges[0]);

        ImageCollection coll1 = problem.collections[1];
        assertEquals(100, coll1.value);
        assertEquals(1, coll1.locations.length);
        assertEquals(2, coll1.ranges.length);

        Location loc10 = coll1.locations[0];
        assertEquals(coll1, loc10.parentCollection);
        assertArrayEquals(new int[] {175889, 8260}, loc10.coords);

        assertArrayEquals(new int[] {0, 900}, coll1.ranges[0]);
        assertArrayEquals(new int[] {2700, 3599}, coll1.ranges[1]);

        ImageCollection coll2 = problem.collections[2];
        assertEquals(300, coll2.value);
        assertEquals(2, coll2.locations.length);
        assertEquals(1, coll2.ranges.length);

        Location loc20 = coll2.locations[0];
        assertEquals(coll2, loc20.parentCollection);
        assertArrayEquals(new int[] {175958, 8387}, loc20.coords);

        Location loc21 = coll2.locations[1];
        assertEquals(coll2, loc21.parentCollection);
        assertArrayEquals(new int[] {175889, 8260}, loc21.coords);

        assertArrayEquals(new int[] {3300, 3599}, coll2.ranges[0]);
    }
}
