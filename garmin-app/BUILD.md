# Tribly Garmin Connect IQ App - Build Instructions

## Overview

This document describes how to build the Tribly Connect IQ app for Garmin Edge cycling computers.

## Prerequisites

- Docker or Podman installed
- Garmin developer account (free): https://developer.garmin.com/
- Developer key generated from Garmin's developer portal

## SDK Installation (Docker-based)

On modern Linux distributions (Ubuntu 24.04+), the official SDK Manager has compatibility issues with webkit2gtk. The recommended approach is using Docker with Ubuntu 22.04.

### Step 1: Build the SDK Manager Container

```bash
cd garmin-app

# Build the container
docker build -t garmin-sdk-manager -f Dockerfile.sdk-manager .
```

### Step 2: Run SDK Manager to Download SDK

```bash
# Allow X11 forwarding
xhost +local:docker

# Run the SDK Manager GUI
docker run -it --rm \
  --ipc=host \
  -e DISPLAY=$DISPLAY \
  -e WEBKIT_DISABLE_DMABUF_RENDERER=1 \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -v ~/.Garmin:/root/.Garmin \
  --security-opt label=disable \
  garmin-sdk-manager

# After done, revoke X11 access
xhost -local:docker
```

In the SDK Manager:
1. Log in with your Garmin developer account
2. Download SDK 8.4.0 (or latest)
3. Download device simulators for Edge devices (530, 540, 830, 840, 1030, 1040)
4. Close the SDK Manager

The SDK will be installed to `~/.Garmin/ConnectIQ/Sdks/`.

### Step 3: Fix File Permissions

Files created by Docker are owned by root. Fix ownership:

```bash
sudo chown -R $USER:$USER ~/.Garmin/ConnectIQ/
```

### Step 4: Generate Developer Key

```bash
# Generate RSA key pair using OpenSSL (one-time setup)
openssl genrsa -out ~/.Garmin/ConnectIQ/developer_key.pem 4096

# Convert to DER format (required by Connect IQ SDK)
openssl pkcs8 -topk8 -inform PEM -outform DER \
  -in ~/.Garmin/ConnectIQ/developer_key.pem \
  -out ~/.Garmin/ConnectIQ/developer_key \
  -nocrypt
```

This creates `~/.Garmin/ConnectIQ/developer_key` (DER format, used for signing builds).

## Building the App

### Option A: Using Docker (Recommended)

```bash
cd garmin-app

# Build the app container
docker build -t tribly-garmin-builder -f Dockerfile.build .

# Build the app
docker run --rm \
  -v ~/.Garmin/ConnectIQ:/root/.Garmin/ConnectIQ:ro \
  -v $(pwd):/app \
  -w /app \
  tribly-garmin-builder \
  make build
```

The built `.prg` files will be in the `bin/` directory.

### Option B: Native Build (if SDK works on your system)

```bash
# Set SDK path
export CIQ_HOME=~/.Garmin/ConnectIQ/Sdks/connectiq-sdk-8.4.0-xxxx/

# Build
cd garmin-app
make build
```

## Build Targets

```bash
# Build for default device (edge1040)
make build

# Build for a specific device
make build DEVICE=edge530

# Build for all supported devices
make build-all

# Build debug version with symbols
make debug DEVICE=edge1040

# Clean build artifacts
make clean

# Create .iq package for Connect IQ Store
make package
```

## Supported Devices

Devices must support Connect IQ API 3.2.0 or higher.

| Device | Product ID |
|--------|------------|
| Edge 530 | edge530 |
| Edge 540 | edge540 |
| Edge 830 | edge830 |
| Edge 840 | edge840 |
| Edge 1030 | edge1030 |
| Edge 1030 Plus | edge1030plus |
| Edge 1040 | edge1040 |

## Testing in Simulator

On Ubuntu 24.04+, the simulator also requires webkit2gtk-4.0 and must run via Docker.

### Using Makefile (Recommended)

```bash
cd garmin-app

# Start the simulator (runs in background)
make simulator-docker

# Build and load the app into the simulator
make run-docker DEVICE=edge1040
```

### Manual Docker Commands

If you need more control:

```bash
# Allow X11 forwarding
xhost +local:docker

# Start simulator
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

# Wait for simulator to start, then load the app
docker run --rm \
  --net=host \
  -v ~/.Garmin/ConnectIQ:/root/.Garmin/ConnectIQ \
  -v $(pwd):/app \
  -w /app \
  garmin-sdk-manager \
  /root/.Garmin/ConnectIQ/Sdks/connectiq-sdk-lin-8.4.0-2025-12-03-5122605dc/bin/monkeydo bin/Tribly-edge1040.prg edge1040
```

### Native Simulator (if webkit2gtk-4.0 is available)

```bash
# Start simulator
make simulator

# Build and load app
make run DEVICE=edge1040
```

### OAuth Testing

The OAuth flow requires the Garmin Connect Mobile simulator or a physical device. In the simulator:

1. Start the app
2. Select "Login"
3. The simulated phone browser will open to the Tribly login page
4. Complete authentication
5. The app receives the OAuth callback

## Deploying to Device

### Via Garmin Express

1. Connect your Edge device via USB
2. Copy the `.prg` file to `GARMIN/Apps/` on the device
3. Safely eject the device

### Via Connect IQ Store (Production)

1. Create app listing at https://apps.garmin.com/developer
2. Upload the signed `.iq` package
3. Submit for review

## Troubleshooting

### SDK Manager Shows Blank Window

This is a known issue on Ubuntu 24.04+. Use the Docker approach described above.

### "Unable to find manifest"

Ensure you're running the build command from the `garmin-app` directory.

### "Key not found"

Generate a developer key as described in Step 3.

### Build Fails with "Device not supported"

Check that the device ID in your command matches one in `manifest.xml`.

### "Permission denied" or "Permission non accordée"

SDK files are owned by root after Docker installation. Fix with:

```bash
sudo chown -R $USER:$USER ~/.Garmin/ConnectIQ/
```

### Simulator: "libwebkit2gtk-4.0.so.37: cannot open shared object file"

On Ubuntu 24.04+, the simulator requires webkit2gtk-4.0 which is not available. Use Docker:

```bash
make simulator-docker
make run-docker DEVICE=edge1040
```

### "Unable to connect to simulator"

The simulator must be running before loading an app. Start it first:

```bash
make simulator-docker
# Wait 2-3 seconds for it to start
make run-docker DEVICE=edge1040
```

## Project Structure

```
garmin-app/
├── manifest.xml          # App manifest (permissions, devices)
├── monkey.jungle         # Build configuration
├── Makefile              # Build automation
├── Dockerfile.sdk-manager # Docker image for SDK Manager
├── Dockerfile.build      # Docker image for building
├── source/
│   ├── TriblyApp.mc      # Main app entry
│   ├── AuthManager.mc    # OAuth token management
│   ├── ApiClient.mc      # HTTP API client
│   ├── LoginView.mc      # Login screen
│   ├── TriblyView.mc     # Route list view
│   ├── TriblyDelegate.mc # Route list input
│   ├── RouteDetailView.mc    # Route detail
│   ├── RouteDetailDelegate.mc # Route detail input
│   └── ErrorView.mc      # Error display
└── resources/
    └── strings/
        ├── strings.xml       # English strings
        └── strings-fre.xml   # French strings
```

## References

- [Connect IQ SDK Documentation](https://developer.garmin.com/connect-iq/overview/)
- [Monkey C Programming Guide](https://developer.garmin.com/connect-iq/monkey-c/)
- [Connect IQ API Reference](https://developer.garmin.com/connect-iq/api-docs/)
- [SDK Manager Linux Workaround](https://github.com/pcolby/connectiq-sdk-manager/issues/3)
