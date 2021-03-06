package org.hildan.hashcode.utils.examples.streaming;

import org.hildan.hashcode.utils.examples.streaming.model.Endpoint;
import org.hildan.hashcode.utils.examples.streaming.model.Latency;
import org.hildan.hashcode.utils.examples.streaming.model.RequestDesc;
import org.hildan.hashcode.utils.examples.streaming.model.StreamingProblem;
import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.readers.HCReader;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StreamingExample {

    private static final String input =
            "5 2 4 3 100\n" // 5 videos, 2 endpoints, 4 request descriptions, 3 caches 100MB each
                    + "50 50 80 30 110\n" // Videos 0, 1, 2, 3, 4 have sizes 50MB, 50MB, 80MB, 30MB, 110MB.
                    + "1000 3\n" // Endpoint 0 has 1000ms datacenter latency and is connected to 3 caches:
                    + "0 100\n"  // The latency (of endpoint 0) to cache 0 is 100ms.
                    + "2 200\n"  // The latency (of endpoint 0) to cache 2 is 200ms.
                    + "1 300\n"  // The latency (of endpoint 0) to cache 1 is 300ms.
                    + "500 0\n"  // Endpoint 1 has 500ms datacenter latency and is not connected to a cache.
                    + "3 0 1500\n" // 1500 requests for video 3 coming from endpoint 0.
                    + "0 1 1000\n" // 1000 requests for video 0 coming from endpoint 1.
                    + "4 0 500\n"  // 500 requests for video 4 coming from endpoint 0.
                    + "1 0 1000";  // 1000 requests for video 1 coming from endpoint 0.

    private static ObjectReader<StreamingProblem> streamingProblemReader() {
        return HCReader.create(StreamingProblem::new)
                       .thenFieldsAndVars("nVideos", "nEndpoints@E", "nRequestDescriptions@R", "nCaches", "cacheSize")
                       .thenIntArrayLine((sp, arr) -> sp.videoSizes = arr)
                       .thenArray((sp, arr) -> sp.endpoints = arr, Endpoint[]::new, "E", endpointReader())
                       .thenArray((sp, arr) -> sp.requestDescs = arr, RequestDesc[]::new, "R", requestReader());
    }

    private static ObjectReader<Endpoint> endpointReader() {
        return HCReader.create(Endpoint::new)
                       .thenField("dcLatency")
                       .thenVar("K")
                       .thenArray(Endpoint::setLatencies, Latency[]::new, "K", latencyReader());
    }

    private static ObjectReader<Latency> latencyReader() {
        return HCReader.create(Latency::new).thenFields("cacheId", "latency");
    }

    private static ObjectReader<RequestDesc> requestReader() {
        return HCReader.create(RequestDesc::new).thenFields("videoId", "endpointId", "count");
    }

    @Test
    public void test_parser() {
        ObjectReader<StreamingProblem> rootReader = streamingProblemReader();
        HCParser<StreamingProblem> parser = new HCParser<>(rootReader);
        StreamingProblem problem = parser.parse(input);

        assertEquals(5, problem.nVideos);
        assertEquals(2, problem.nEndpoints);
        assertEquals(4, problem.nRequestDescriptions);
        assertEquals(3, problem.nCaches);
        assertEquals(100, problem.cacheSize);
        assertArrayEquals(new int[] {50, 50, 80, 30, 110}, problem.videoSizes);

        assertEquals(2, problem.endpoints.length);

        assertEquals(1000, problem.endpoints[0].dcLatency);
        assertEquals(3, problem.endpoints[0].cacheLatencies.size());
        assertEquals(Integer.valueOf(100), problem.endpoints[0].cacheLatencies.get(0));
        assertEquals(Integer.valueOf(200), problem.endpoints[0].cacheLatencies.get(2));
        assertEquals(Integer.valueOf(300), problem.endpoints[0].cacheLatencies.get(1));

        assertEquals(500, problem.endpoints[1].dcLatency);
        assertEquals(0, problem.endpoints[1].cacheLatencies.size());

        assertEquals(4, problem.requestDescs.length);

        assertEquals(1500, problem.requestDescs[0].count);
        assertEquals(3, problem.requestDescs[0].videoId);
        assertEquals(0, problem.requestDescs[0].endpointId);

        assertEquals(1000, problem.requestDescs[1].count);
        assertEquals(0, problem.requestDescs[1].videoId);
        assertEquals(1, problem.requestDescs[1].endpointId);

        assertEquals(500, problem.requestDescs[2].count);
        assertEquals(4, problem.requestDescs[2].videoId);
        assertEquals(0, problem.requestDescs[2].endpointId);

        assertEquals(1000, problem.requestDescs[3].count);
        assertEquals(1, problem.requestDescs[3].videoId);
        assertEquals(0, problem.requestDescs[3].endpointId);
    }
}
