#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# build.sh  –  Build and run the Morse Code Converter
#
# Requirements:
#   • Java 17+  JDK   (https://adoptium.net)
#   • Apache Maven 3.9+  (https://maven.apache.org/download.cgi)
#
# The first run downloads JavaFX from Maven Central; subsequent runs are fast.
# ─────────────────────────────────────────────────────────────────────────────
set -e

echo "═══════════════════════════════════════"
echo "  Morse Code Converter  –  build.sh"
echo "═══════════════════════════════════════"
echo ""

# Verify tools
command -v java  >/dev/null 2>&1 || { echo "ERROR: java not found. Install JDK 17+."; exit 1; }
command -v mvn   >/dev/null 2>&1 || { echo "ERROR: mvn not found.  Install Maven 3.9+."; exit 1; }

echo "Java:  $(java -version 2>&1 | head -1)"
echo "Maven: $(mvn -version 2>&1 | head -1)"
echo ""

case "${1:-run}" in

  clean)
    echo "Cleaning…"
    mvn -q clean
    echo "Done."
    ;;

  build)
    echo "Compiling and packaging…"
    mvn -q clean package -DskipTests
    echo ""
    echo "✓  Fat JAR built:  target/morse.jar"
    echo "   Run with:       java -jar target/morse.jar"
    ;;

  test)
    echo "Running tests…"
    mvn -q test
    echo "All tests passed."
    ;;

  run | "")
    echo "Building and launching…"
    mvn -q clean package -DskipTests
    echo "Launching GUI…"
    java -jar target/morse.jar gui
    ;;

  *)
    echo "Usage: $0 [clean|build|test|run|dev]"
    echo "  clean  – Remove build artifacts"
    echo "  build  – Compile and package fat JAR"
    echo "  test   – Run unit tests only"
    echo "  run    – Build then launch the GUI  (default)"
    exit 1
    ;;
esac
