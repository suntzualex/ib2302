#!/bin/bash
# Script to run all tests for the distributed algorithms project

echo "Running all tests for distributed algorithms project..."
echo "=========================================="

TESTS=(
    "week1.RockPaperScissorsProcessTest"
    "week1.RockPaperScissorsCheatingProcessTest"
    "week1.RockPaperScissorsMultiRoundsProcessTest"
    "week2.CausalOrderTest"
    "week2.GlobalTransitionSystemTest"
    "week2.LamportsClockTest"
    "week2.LogicalClockTest"
    "week2.VectorClockTest"
    "week34.ChandyLamportProcessTest"
    "week34.LaiYangProcessTest"
    "week56.DepthFirstSearchExtraControlProcessTest"
    "week56.DepthFirstSearchExtraPiggybackProcessTest"
    "week56.DepthFirstSearchProcessTest"
    "week56.RingProcessTest"
    "week56.TarryProcessTest"
    "week78.BrachaTouegProcessTest"
)

TOTAL_TESTS=${#TESTS[@]}
PASSED=0
FAILED=0

for TEST in "${TESTS[@]}"; do
    echo ""
    echo "Running $TEST..."
    echo "----------------------------"

    if java -cp ".:lib/*:bin" org.junit.platform.console.ConsoleLauncher --class-path bin --select-class $TEST --disable-banner | grep -q "tests successful"; then
        echo "✅ $TEST: PASSED"
        ((PASSED++))
    else
        echo "❌ $TEST: FAILED"
        ((FAILED++))
    fi
done

echo ""
echo "=========================================="
echo "TEST SUMMARY:"
echo "Total tests: $TOTAL_TESTS"
echo "Passed: $PASSED"
echo "Failed: $FAILED"

if [ $FAILED -eq 0 ]; then
    echo "🎉 All tests passed!"
    exit 0
else
    echo "⚠️  Some tests failed. Review the output above."
    exit 1
fi
