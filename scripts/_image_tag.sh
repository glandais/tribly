# Sourced by build.sh, deploy.sh and restore.sh: which tag a build of this checkout goes under.
#
# Every build is tagged pedalons-<service>:${ENV_NAME}-<commit>, and never retagged. That is what
# lets Swarm roll the services: a deploy changes the image in the service spec, so Swarm starts the
# new task next to the old one (`order: start-first`) and still holds the previous spec for
# `docker service rollback`. A tag that moved would leave the spec untouched and nothing to roll
# back to.
#
# ENV_NAME stays in the tag because prod and staging share the host's image store.
#
# The alias pedalons-<service>:${ENV_NAME} always points at the latest build. A workstation runs
# it through compose, and so does docker-compose.restore.yml.

# The tag suffix of commit $1 (default HEAD): "-<12 hex digits>".
image_suffix() {
  local sha
  sha="$(git -C "${REPO_ROOT:?}" rev-parse --short=12 --verify --quiet "${1:-HEAD}^{commit}")" \
    || return 1
  printf -- '-%s' "$sha"
}

# Whether the checkout differs from HEAD. Then a build is not that commit, and gets the alias alone.
# Ignored files (.env, data/) do not count.
worktree_dirty() {
  [[ -n "$(git -C "${REPO_ROOT:?}" status --porcelain)" ]]
}
