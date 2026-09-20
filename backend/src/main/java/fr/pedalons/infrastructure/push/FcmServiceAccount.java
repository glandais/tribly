package fr.pedalons.infrastructure.push;

import java.security.PrivateKey;

/**
 * The parts of a Google service-account JSON that minting an access token needs, already decoded.
 *
 * <p>Held in memory only: the file is read once at first use and never logged.
 */
record FcmServiceAccount(
    String projectId, String clientEmail, PrivateKey privateKey, String tokenUri) {}
