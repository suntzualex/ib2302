#!/bin/bash
# IntelliJ IDEA Test Runner for RockPaperScissorsProcessTest
# This script provides a workaround for IntelliJ JUnit configuration issues

cd /Users/SO28RO/IdeaProjects/ib2302/Opdrachten

echo "=== Running RockPaperScissorsProcessTest ==="
echo ""

# Compile if needed
./compile.sh > /dev/null 2>&1

# Run the test with proper JUnit 5 setup
./run_test.sh week1.RockPaperScissorsProcessTest
