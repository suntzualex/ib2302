# IntelliJ IDEA Setup Guide

## ✅ Project is Ready!

Your IB2302 Distributed Algorithms project has been fully configured for IntelliJ IDEA.

## How to Use in IntelliJ IDEA

### Option 1: IntelliJ Integration (Recommended)
1. **Open the project** in IntelliJ IDEA by opening the `/Users/SO28RO/IdeaProjects/ib2302` folder
2. **Wait for indexing** to complete
3. **Right-click** on any test class (e.g., `RockPaperScissorsProcessTest.java`)
4. **Select "Run 'RockPaperScissorsProcessTest'"**
5. The test should run with proper JUnit 5 integration

### Option 2: Command Line (Always Works)
```bash
cd /Users/SO28RO/IdeaProjects/ib2302/Opdrachten

# Compile everything
./compile.sh

# Run a specific test
./run_test.sh week1.RockPaperScissorsProcessTest

# Run all tests
./run_all_tests.sh
```

## Project Structure
- **Source**: `Opdrachten/src/` - All Java source files
- **Compiled**: `Opdrachten/bin/` - Compiled class files
- **Libraries**: `Opdrachten/lib/` - JUnit 5.10.1 JAR files
- **Tests**: Test classes are mixed with source files (Eclipse style)

## What the Test Failures Mean

The test failures you see are **EXPECTED** and **CORRECT**:

```
receiveTest1() ✘ expected: <1> but was: <0>
initTest1() ✘ expected: <1> but was: <0>
```

These failures indicate that:
- The `init()` method in `RockPaperScissorsProcess` is empty (should send messages)
- The `receive()` method is empty (should handle incoming messages and print results)

**This is your assignment** - implement these methods!

## Next Steps

1. **Open `RockPaperScissorsProcess.java`**
2. **Implement the `init()` method** to broadcast your choice to other processes
3. **Implement the `receive()` method** to collect choices and determine winners
4. **Run tests** to verify your implementation
5. **Move to next assignments** (week2, week34, etc.)

## Troubleshooting

If IntelliJ shows JUnit version conflicts:
1. Go to **File → Project Structure → Modules → Opdrachten → Dependencies** 
2. Remove any conflicting Maven JUnit entries
3. Keep only the "JUnit5" library we configured
4. **Refresh** the project

The command line approach will always work regardless of IntelliJ configuration issues.

## Ready to Code! 🚀

Your environment is fully configured. The test failures are your roadmap - they tell you exactly what to implement. Good luck with your distributed algorithms assignments!
