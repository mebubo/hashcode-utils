package org.hildan.hashcode.utils.examples.satellites;

import org.hildan.hashcode.utils.examples.satellites.model.ImageCollection;
import org.hildan.hashcode.utils.examples.satellites.model.Location;
import org.hildan.hashcode.utils.examples.satellites.model.Satellite;
import org.hildan.hashcode.utils.examples.satellites.model.Simulation;
import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.hildan.hashcode.utils.parser.readers.RootReader;
import org.junit.Test;

public class Satellites {

    private static final String input = "3600\n" // Simulation lasts an hour.
            + "2\n" // Two satellites.
            + "170000 8300 300 50 500\n" // First satellite starts at [170000, 8300], heading north
            + "180000 8300 -300 50 500\n" // Second satellite starts at [180000, 8300], heading south.
            + "3\n" // Three image collections.
            + "100 1 1\n" // First image collection is worth 100 points
            + "175958 8387\n" // The only location: Google office in Paris
            + "0 3599\n" // The image can be taken at any time.
            + "100 1 2\n" // Second image collection is worth 100 points.
            + "175889 8260\n" // The only location: the Eiffel Tower
            + "0 900\n" // The image has to be taken in the first 15 minutes...
            + "2700 3599\n" // ...or in the last 15 minutes
            + "300 2 1\n" // Third image collection, worth 300 points.
            + "175958 8387\n" // Google office.
            + "175889 8260\n" // The Eiffel Tower.
            + "3300 3599\n"; // The images need to be taken in the last 5 minutes.

    private static ObjectReader<Simulation, Object> createReader() {

        RootReader<Satellite> satelliteReader = RootReader.of(Satellite::new);

        RootReader<int[]> rangeReader = RootReader.of((lat, lgt) -> new int[]{lat, lgt});

        ObjectReader<Location, ImageCollection> locationsReader =
                (ctx, parent) -> new Location(parent, ctx.readInt(), ctx.readInt());

        RootReader<ImageCollection> collectionReader = RootReader.of(ImageCollection::new)
                                                                 .fieldsAndVarsLine("@L", "@R")
                                                                 .arraySection((coll, arr) -> coll.locations = arr,
                                                                         Location[]::new, "R", locationsReader)
                                                                 .arraySection((coll, arr) -> coll.ranges = arr,
                                                                         int[][]::new, "R", rangeReader);

        return RootReader.of(Simulation::new)
                         .fieldsAndVarsLine("@S")
                         .arraySection((p, arr) -> p.satellites = arr, Satellite[]::new, "S", satelliteReader)
                         .fieldsAndVarsLine("@C")
                         .arraySection((p, arr) -> p.collections = arr, ImageCollection[]::new, "C", collectionReader);
    }

    @Test
    public void test_parser() {
        ObjectReader<Simulation, Object> rootReader = createReader();
        HCParser<Simulation> parser = new HCParser<>(rootReader);
        Simulation problem = parser.parse(input);
        // test parsed object

        //        assertEquals(5, problem.nVideos);
        //        assertEquals(2, problem.nEndpoints);
        //        assertEquals(4, problem.nRequestDescriptions);
        //        assertEquals(3, problem.nCaches);
        //        assertEquals(100, problem.cacheSize);
        //        assertArrayEquals(new int[]{50, 50, 80, 30, 110}, problem.videoSizes);
        //
        //        assertEquals(2, problem.endpoints.length);
        //
        //        assertEquals(1000, problem.endpoints[0].dcLatency);
        //        assertEquals(3, problem.endpoints[0].cacheLatencies.size());
        //        assertEquals(Integer.valueOf(100), problem.endpoints[0].cacheLatencies.get(0));
        //        assertEquals(Integer.valueOf(200), problem.endpoints[0].cacheLatencies.get(2));
        //        assertEquals(Integer.valueOf(300), problem.endpoints[0].cacheLatencies.get(1));
        //
        //        assertEquals(500, problem.endpoints[1].dcLatency);
        //        assertEquals(0, problem.endpoints[1].cacheLatencies.size());
        //
        //        assertEquals(4, problem.requestDescs.length);
        //        assertEquals(1500, problem.requestDescs[0].count);
        //        assertEquals(3, problem.requestDescs[0].videoId);
        //        assertEquals(0, problem.requestDescs[0].endpointId);
        //        assertEquals(1000, problem.requestDescs[1].count);
        //        assertEquals(0, problem.requestDescs[1].videoId);
        //        assertEquals(1, problem.requestDescs[1].endpointId);
        //        assertEquals(500, problem.requestDescs[2].count);
        //        assertEquals(4, problem.requestDescs[2].videoId);
        //        assertEquals(0, problem.requestDescs[2].endpointId);
        //        assertEquals(1000, problem.requestDescs[3].count);
        //        assertEquals(1, problem.requestDescs[3].videoId);
        //        assertEquals(0, problem.requestDescs[3].endpointId);
    }
}
