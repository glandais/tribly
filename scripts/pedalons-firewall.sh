#!/bin/sh
# Host firewall of a Swarm host: only Caddy may reach traefik, and the swarm's own ports and the
# monitoring stack's stay off the Internet. Install once per host, with
# scripts/pedalons-firewall.service (docs/OPERATIONS.md, "Only Caddy may reach traefik"):
#
#   install -m 755 scripts/pedalons-firewall.sh /usr/local/sbin/pedalons-firewall.sh
#   install -m 644 scripts/pedalons-firewall.service /etc/systemd/system/pedalons-firewall.service
#   echo "PUBLIC_IF=$(ip -o route get 1.1.1.1 | sed -n 's/.* dev \([^ ]*\).*/\1/p')" > /etc/default/pedalons-firewall
#   systemctl daemon-reload && systemctl enable --now pedalons-firewall.service
#
# Only these rules, not a saved ruleset: `netfilter-persistent save` would also freeze fail2ban's
# chains and Docker's, which both rebuild their own at start. Idempotent (`-C` before `-I`), and run
# before docker.service: Docker keeps an existing DOCKER-USER chain as it is, so no stack is ever up
# without the rules, not even at boot.
set -eu

: "${PUBLIC_IF:?PUBLIC_IF is not set — the public interface, in /etc/default/pedalons-firewall}"
# One per environment: HTTP_PORT of its .env. Swarm publishes them on every interface.
TRAEFIK_PORTS="${TRAEFIK_PORTS:-8089 8090}"
# The monitoring stack's Grafana (GRAFANA_PORT), published by Swarm the same way: reached through an
# SSH tunnel, never from outside.
MONITORING_PORTS="${MONITORING_PORTS:-3300}"
# Ports the host itself listens on for the monitoring stack, on every interface: Caddy's metrics
# endpoint (`:2020 { metrics }`), which Prometheus reaches through docker_gwbridge.
HOST_PORTS="${HOST_PORTS:-2020}"

add() {
  t="$1"
  shift
  "$t" -C "$@" 2>/dev/null || "$t" -I "$@"
}

for t in iptables ip6tables; do
  "$t" -N DOCKER-USER 2>/dev/null || true
  # --ctorigdstport: the port the client asked for, before the routing mesh rewrites it. Caddy comes
  # in over the loopback, which this leaves alone.
  for p in $TRAEFIK_PORTS $MONITORING_PORTS; do
    add "$t" DOCKER-USER -i "$PUBLIC_IF" -p tcp -m conntrack --ctorigdstport "$p" -j DROP
  done
  # Cluster management, gossip and the VXLAN of the overlay networks: a single-node swarm needs none
  # of them from outside, and VXLAN is unauthenticated — anyone could inject into an overlay.
  for p in 2377 7946; do add "$t" INPUT -i "$PUBLIC_IF" -p tcp --dport "$p" -j DROP; done
  for p in 7946 4789; do add "$t" INPUT -i "$PUBLIC_IF" -p udp --dport "$p" -j DROP; done
  for p in $HOST_PORTS; do add "$t" INPUT -i "$PUBLIC_IF" -p tcp --dport "$p" -j DROP; done
done
