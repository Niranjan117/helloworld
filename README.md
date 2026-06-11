# Route Training API

Spring Boot + embedded Tomcat. ESP32-C3 + EC200U posts cell towers (eNB ID) and GPS during route training.

## Deploy on Render (free)

1. Push this repo to GitHub
2. [render.com](https://render.com) → **New** → **Blueprint** → connect repo `Niranjan117/helloworld`
3. Render reads `render.yaml` and deploys automatically
4. Note your URL: `https://training-api-xxxx.onrender.com`

## ESP32 setup

Upload `training.txt` from the parent project. Serial monitor @ 115200:

```
api training-api-xxxx.onrender.com 443
retry
```

Port **443** enables HTTPS (required for Render).

Then open your Render URL in a browser → **Start training**.

## Local run

```bash
mvn spring-boot:run
```

Open http://localhost:8080

## API

| Method | Path |
|--------|------|
| GET | `/api/status` |
| GET | `/api/device/command` |
| POST | `/api/device/hello` |
| POST | `/api/device/ping` |
| POST | `/api/training/start` |
| POST | `/api/training/stop` |
| POST | `/api/training/point` |
| POST | `/api/training/clear` |

Points stored in RAM only (reset on redeploy).
