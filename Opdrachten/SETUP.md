# IB2302 Distributed Algorithms - Setup Complete

This Eclipse project for distributed algorithms has been successfully set up to run with JUnit 5.

## Project Structure

- `src/framework/` - Core framework classes for distributed algorithms
- `src/week1/` - Rock-Paper-Scissors algorithms
- `src/week2/` - Behavioral models (transitions, causal order, logical clocks)
- `src/week34/` - Snapshot algorithms (Chandy-Lamport, Lai-Yang)
- `src/week56/` - Wave algorithms (Ring, Tarry, DFS)
- `src/week78/` - Deadlock detection (Bracha-Toueg)
- `lib/` - JUnit 5 JAR files
- `bin/` - Compiled class files

## How to Use

### Compilation

To compile all source files:
```bash
./compile.sh
```

### Running Individual Tests

To run a specific test class:
```bash
./run_test.sh <test-class>
```

Examples:
```bash
./run_test.sh week1.RockPaperScissorsProcessTest
./run_test.sh week2.CausalOrderTest
./run_test.sh week34.ChandyLamportProcessTest
```

### Running All Tests

To run all tests at once:
```bash
./run_all_tests.sh
```

### Manual Commands

If you prefer to run commands manually:

**Compile:**
```bash
javac -cp ".:lib/*:bin" -d bin src/framework/*.java
javac -cp ".:lib/*:bin" -d bin src/week1/*.java
javac -cp ".:lib/*:bin" -d bin src/week2/*.java
javac -cp ".:lib/*:bin" -d bin src/week34/*.java
javac -cp ".:lib/*:bin" -d bin src/week56/*.java
javac -cp ".:lib/*:bin" -d bin src/week78/*.java
```

**Run a test:**
```bash
java -cp ".:lib/*:bin" org.junit.platform.console.ConsoleLauncher --class-path bin --select-class <test-class>
```

## Current Status

The project is ready to work with. The framework is compiled and all test classes are available. The test failures you see are expected - they indicate which methods you need to implement in each assignment.

## IntelliJ IDEA Integration

If you're using IntelliJ IDEA, you can also use the Maven configuration (pom.xml in the root directory) to import the project as a Maven project for easier IDE integration.

## Next Steps

1. Start with Week 1 assignments (Rock-Paper-Scissors)
2. Implement the required methods in the process classes
3. Run tests to verify your implementations
4. Move on to subsequent weeks

Good luck with your distributed algorithms assignments!
