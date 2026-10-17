#!/usr/bin/env bash
# Packs the Keycloak themes into a JAR, which a Keycloak shared by several applications loads from its providers folder
# (keycloak/target/festival-keycloak-themes.jar).
set -euo pipefail
cd "$(dirname "$0")"

out="$PWD/target/festival-keycloak-themes.jar"
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

mkdir -p "$work/META-INF" "$work/theme" target
cp -R "themes/festival-page" "$work/theme/"
cp -R "themes/festival-admin" "$work/theme/"
cat > "$work/META-INF/keycloak-themes.json" <<'JSON'
{
  "themes": [
    {
      "name": "festival-page",
      "types": [
        "login",
        "email"
      ]
    },
    {
      "name": "festival-admin",
      "types": [
        "login"
      ]
    }
  ]
}
JSON

rm -f "$out"
(cd "$work" && zip -q -r -X "$out" META-INF theme)
echo "$out"
