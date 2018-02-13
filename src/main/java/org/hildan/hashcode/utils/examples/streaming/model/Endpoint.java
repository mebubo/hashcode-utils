package org.hildan.hashcode.utils.examples.streaming.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Endpoint {

    public int dcLatency;

    public int[] cacheIds;

    public Map<Integer, Integer> cacheLatencies = new HashMap<>();

    public Map<Integer, Integer> gainPerCache = new HashMap<>();

    public Map<Video, Long> nRequestsPerVideo = new HashMap<>();

    public void setLatencies(Latency[] latencies) {
        cacheIds = Arrays.stream(latencies).mapToInt(l -> l.cacheId).toArray();
        Arrays.stream(latencies).forEach(l -> cacheLatencies.put(l.cacheId, l.latency));
        Arrays.stream(latencies).forEach(l -> gainPerCache.put(l.cacheId, dcLatency - l.latency));
    }

    public void addRequests(Video video, int nbRequests) {
        nRequestsPerVideo.putIfAbsent(video, 0L);
        nRequestsPerVideo.compute(video, (c, val) -> val + nbRequests);
    }

    public long getNbRequests(Video video) {
        return nRequestsPerVideo.getOrDefault(video, 0L);
    }
}
