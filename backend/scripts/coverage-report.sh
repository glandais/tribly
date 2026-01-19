#!/bin/bash
# Transforms JaCoCo CSV report into readable text format
# Usage: ./scripts/coverage-report.sh [package-filter] [sort-by]
#   package-filter: grep pattern to filter packages (default: all)
#   sort-by: 'name', 'coverage', or 'missed' (default: coverage)

CSV_FILE="target/jacoco-report/jacoco.csv"

if [ ! -f "$CSV_FILE" ]; then
    echo "Error: $CSV_FILE not found. Run 'mvn test' first."
    exit 1
fi

FILTER="${1:-.}"
SORT_BY="${2:-coverage}"

case "$SORT_BY" in
    name)     SORT_CMD="sort -t'|' -k1" ;;
    missed)   SORT_CMD="sort -t'|' -k3 -rn" ;;
    coverage) SORT_CMD="sort -t'|' -k5 -rn" ;;
    *)        echo "Unknown sort: $SORT_BY (use: name, coverage, missed)"; exit 1 ;;
esac

echo "JaCoCo Coverage Report"
echo "Filter: $FILTER | Sort: $SORT_BY"
echo ""
printf "%-50s | %7s | %7s | %8s\n" "CLASS" "MISSED" "COVERED" "COVERAGE"
printf "%-50s-|-%7s-|-%7s-|-%8s\n" "$(printf '%0.s-' {1..50})" "-------" "-------" "--------"

tail -n +2 "$CSV_FILE" | grep "$FILTER" | \
awk -F',' '{
    class = $3
    missed = $8
    covered = $9
    total = missed + covered
    pct = (total > 0) ? (covered * 100 / total) : 0
    printf "%-50s | %7d | %7d | %7.1f%%\n", class, missed, covered, pct
}' | eval "$SORT_CMD"

echo ""
echo "Summary:"
tail -n +2 "$CSV_FILE" | grep "$FILTER" | \
awk -F',' '{
    missed += $8
    covered += $9
    classes++
}
END {
    total = missed + covered
    pct = (total > 0) ? (covered * 100 / total) : 0
    printf "  Classes: %d | Lines: %d covered, %d missed | Coverage: %.1f%%\n", classes, covered, missed, pct
}'
