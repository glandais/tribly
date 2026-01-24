# Pédalons Garmin Connect IQ App

A Garmin Connect IQ app for Edge cycling computers that allows users to browse and download routes from their teams.

## Features

- Browse routes from all your teams
- Routes sorted by proximity to current location
- Download routes as FIT files directly to your Garmin
- Device Code Flow authentication (no keyboard needed)
- Supports English and French

## Supported Devices

| Device | Product ID |
|--------|------------|
| Edge 530 | edge530 |
| Edge 540 | edge540 |
| Edge 550 | edge550 |
| Edge 830 | edge830 |
| Edge 840 | edge840 |
| Edge 850 | edge850 |
| Edge 1030 | edge1030 |
| Edge 1030 Plus | edge1030plus |
| Edge 1040 | edge1040 |
| Edge 1050 | edge1050 |
| Edge Explore 2 | edgeexplore2 |

Requires Connect IQ API 3.3.0 or higher.

## Quick Start

### Prerequisites

- Docker (recommended) or native Connect IQ SDK
- Garmin developer key (see [BUILD.md](BUILD.md) for generation instructions)

### Build

```bash
# Build for Edge 1040 (default)
make build

# Build for a specific device
make build DEVICE=edge530

# Build for all supported devices
make build-all
```

### Run in Simulator

#### Using Docker (Ubuntu 24.04+)

On modern Linux distributions, the simulator requires Docker due to webkit2gtk compatibility issues.

```bash
# 1. Start the simulator (runs in background)
make simulator-docker

# 2. Wait 2-3 seconds for simulator to initialize

# 3. Build and load the app
make run-docker DEVICE=edge1040
```

#### Using Native SDK (macOS or older Linux)

If webkit2gtk-4.0 is available on your system:

```bash
# 1. Start the simulator
make simulator

# 2. Build and load the app
make run DEVICE=edge1040
```

#### Manual Docker Commands

For more control over the simulator:

```bash
# Allow X11 forwarding
xhost +local:docker

# Start simulator manually
docker run --rm -d \
  --ipc=host \
  --net=host \
  -e DISPLAY=$DISPLAY \
  -e NO_AT_BRIDGE=1 \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -v ~/.Garmin/ConnectIQ:/root/.Garmin/ConnectIQ \
  --security-opt label=disable \
  garmin-sdk-manager \
  /root/.Garmin/ConnectIQ/Sdks/connectiq-sdk-lin-8.4.0-2025-12-03-5122605dc/bin/simulator

# Load app into running simulator
docker run --rm \
  --net=host \
  -v ~/.Garmin/ConnectIQ:/root/.Garmin/ConnectIQ \
  -v $(pwd):/app \
  -w /app \
  garmin-sdk-manager \
  /root/.Garmin/ConnectIQ/Sdks/connectiq-sdk-lin-8.4.0-2025-12-03-5122605dc/bin/monkeydo bin/Pedalons-edge1040.prg edge1040

# When done, revoke X11 access
xhost -local:docker
```

#### Simulator Tips

- The simulator must be running before loading an app
- Use different `DEVICE` values to test on different screen sizes
- OAuth flow requires the simulated phone browser to complete authentication

### Deploy to Device

1. Connect your Edge device via USB
2. Copy `bin/Pedalons-{device}.prg` to `GARMIN/Apps/` on the device
3. Safely eject the device

## Authentication

The app uses Device Code Flow (RFC 8628) since Edge devices don't have keyboards:

1. Open the app on your Garmin
2. Press SELECT to start login
3. Note the 6-character code displayed
4. On your phone or computer, go to `pedalons.fr/device`
5. Enter the code and log in with your Pédalons account
6. The app automatically detects authentication and shows your routes

## Project Structure

```
garmin-app/
├── manifest.xml          # App manifest (permissions, devices)
├── monkey.jungle         # Build configuration
├── Makefile              # Build automation
├── source/
│   ├── PedalonsApp.mc    # Main app entry
│   ├── AuthManager.mc    # Token management
│   ├── ApiClient.mc      # HTTP API client
│   ├── LoginView.mc      # Login/auth screen
│   ├── PedalonsView.mc   # Route list
│   └── ...
└── resources/
    └── strings/          # Localized strings (en, fr)
```

## Documentation

- [BUILD.md](BUILD.md) - Detailed build instructions, SDK setup, troubleshooting
- [Connect IQ SDK Documentation](https://developer.garmin.com/connect-iq/overview/)
- [Monkey C Programming Guide](https://developer.garmin.com/connect-iq/monkey-c/)

### Garmin Reference PDFs (in `docs/`)

- `Garmin Developer Program_Start_Guide_1.2.pdf` - Getting started with Garmin development
- `Courses_API.pdf` - Garmin Courses API for route/course management
- `OAuth2PKCE_2.pdf` - OAuth 2.0 PKCE flow documentation

## License

Proprietary - Pédalons
