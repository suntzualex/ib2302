#!/bin/bash
# Test runner script for the distributed algorithms project

if [ $# -eq 0 ]; then
    echo "Usage: $0 <test-class>"
    echo "Examples:"
    echo "  $0 week1.RockPaperScissorsProcessTest"
    echo "  $0 week2.CausalOrderTest"
    echo "  $0 week34.ChandyLamportProcessTest"
    exit 1
fi

TEST_CLASS=$1

echo "Running test: $TEST_CLASS"
java -cp ".:lib/*:bin" org.junit.platform.console.ConsoleLauncher --class-path bin --select-class $TEST_CLASS
