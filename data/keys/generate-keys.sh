#!/bin/bash

# Generate RSA key pair for JWT signing/verification (production)
# Keys are stored in data/keys/

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PRIVATE_KEY="$SCRIPT_DIR/privateKey.pem"
PUBLIC_KEY="$SCRIPT_DIR/publicKey.pem"

if [[ -f "$PRIVATE_KEY" && -f "$PUBLIC_KEY" ]]; then
    echo "Keys already exist in $SCRIPT_DIR"
    echo "Delete them first if you want to regenerate."
    exit 0
fi

echo "Generating RSA key pair for JWT authentication..."

# Generate 2048-bit RSA private key
openssl genrsa -out "$PRIVATE_KEY" 2048

# Extract public key from private key
openssl rsa -in "$PRIVATE_KEY" -pubout -out "$PUBLIC_KEY"

chmod 600 "$PRIVATE_KEY"
chmod 644 "$PUBLIC_KEY"

echo "Keys generated successfully:"
echo "  Private key: $PRIVATE_KEY"
echo "  Public key:  $PUBLIC_KEY"
