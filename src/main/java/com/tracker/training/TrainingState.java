package com.tracker.training;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TrainingState {
    private String deviceId;
    private boolean deviceConnected;
    private String deviceLastSeen;
    private boolean trainingActive;
    private volatile String pendingCommand;
    private final List<Map<String, Object>> points = Collections.synchronizedList(new ArrayList<>());
    private final Set<Long> uniqueEnodeb = ConcurrentHashMap.newKeySet();

    public String nowIso() {
        return Instant.now().toString().replace("Z", "") + "Z";
    }

    public synchronized Map<String, Object> statusSnapshot() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("device_id", deviceId);
        m.put("device_connected", deviceConnected);
        m.put("device_last_seen", deviceLastSeen);
        m.put("training_active", trainingActive);
        m.put("unique_count", uniqueEnodeb.size());
        m.put("points", new ArrayList<>(points));
        return m;
    }

    public synchronized Map<String, Object> commandSnapshot() {
        String cmd = pendingCommand;
        pendingCommand = null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("cmd", cmd);
        m.put("training_active", trainingActive);
        return m;
    }

    public synchronized void hello(String device) {
        deviceId = device;
        deviceConnected = true;
        deviceLastSeen = nowIso();
    }

    public synchronized void ping() {
        deviceLastSeen = nowIso();
        deviceConnected = true;
    }

    public synchronized boolean startTraining() {
        if (!deviceConnected) return false;
        trainingActive = true;
        pendingCommand = "start";
        return true;
    }

    public synchronized void stopTraining() {
        trainingActive = false;
        pendingCommand = "stop";
    }

    public synchronized void clearPoints() {
        points.clear();
        uniqueEnodeb.clear();
    }

    public synchronized Map<String, Object> addPoint(long enodeb, long cellId, double lat, double lon) {
        boolean isNew = uniqueEnodeb.add(enodeb);
        Map<String, Object> point = new LinkedHashMap<>();
        point.put("enodeb", enodeb);
        point.put("enodeb_hex", Long.toHexString(enodeb).toUpperCase());
        point.put("cellId", cellId);
        point.put("cell_hex", Long.toHexString(cellId).toUpperCase());
        point.put("lat", lat);
        point.put("lon", lon);
        point.put("ts", nowIso());
        point.put("new_tower", isNew);
        points.add(point);
        deviceLastSeen = nowIso();
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("ok", true);
        resp.put("new_tower", isNew);
        resp.put("unique_count", uniqueEnodeb.size());
        return resp;
    }
}
