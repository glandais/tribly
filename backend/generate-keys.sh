#!/bin/bash

# Generate RSA key pair for JWT signing/verification
# Keys are stored in the keys/ folder

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
KEYS_DIR="$SCRIPT_DIR/keys"

mkdir -p "$KEYS_DIR"

PRIVATE_KEY="$KEYS_DIR/privateKey.pem"
PUBLIC_KEY="$KEYS_DIR/publicKey.pem"

if [[ -f "$PRIVATE_KEY" && -f "$PUBLIC_KEY" ]]; then
    echo "Keys already exist in $KEYS_DIR"
    echo "Delete them first if you want to regenerate."
    exit 0
fi

echo "Generating RSA key pair for JWT authentication..."

# Generate 2048-bit RSA private key in PKCS#8 format (required by SmallRye JWT)
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out "$PRIVATE_KEY"

# Extract public key from private key
openssl rsa -in "$PRIVATE_KEY" -pubout -out "$PUBLIC_KEY"

chmod 600 "$PRIVATE_KEY"
chmod 644 "$PUBLIC_KEY"

echo "Keys generated successfully:"
echo "  Private key: $PRIVATE_KEY"
echo "  Public key:  $PUBLIC_KEY"
