package com.tracker.training;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingState state;

    public TrainingController(TrainingState state) {
        this.state = state;
    }

    @GetMapping("/api/status")
    public Map<String, Object> status() {
        return state.statusSnapshot();
    }

    @GetMapping("/api/device/command")
    public Map<String, Object> command() {
        return state.commandSnapshot();
    }

    @PostMapping("/api/device/hello")
    public Map<String, Object> hello(@RequestBody Map<String, Object> body) {
        String device = body.getOrDefault("device", "esp32").toString();
        state.hello(device);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("ok", true);
        r.put("phase", "waiting");
        return r;
    }

    @PostMapping("/api/device/ping")
    public Map<String, Object> ping() {
        state.ping();
        return Map.of("ok", true);
    }

    @PostMapping("/api/training/start")
    public ResponseEntity<Map<String, Object>> start() {
        if (!state.startTraining()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("ok", false, "error", "device not connected"));
        }
        return ResponseEntity.ok(Map.of("ok", true, "training_active", true));
    }

    @PostMapping("/api/training/stop")
    public Map<String, Object> stop() {
        state.stopTraining();
        return Map.of("ok", true, "training_active", false);
    }

    @PostMapping("/api/training/clear")
    public Map<String, Object> clear() {
        state.clearPoints();
        return Map.of("ok", true);
    }

    @PostMapping("/api/training/point")
    public ResponseEntity<Map<String, Object>> point(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("enodeb") || !body.containsKey("cellId")
                || !body.containsKey("lat") || !body.containsKey("lon")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "error", "need enodeb, cellId, lat, lon"));
        }
        long enodeb = ((Number) body.get("enodeb")).longValue();
        long cellId = ((Number) body.get("cellId")).longValue();
        double lat = ((Number) body.get("lat")).doubleValue();
        double lon = ((Number) body.get("lon")).doubleValue();
        return ResponseEntity.ok(state.addPoint(enodeb, cellId, lat, lon));
    }
}
