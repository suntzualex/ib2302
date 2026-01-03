#!/bin/bash
# Verification script to confirm JUnit 5 setup is complete

echo "🔍 Verifying JUnit 5 Setup..."
echo ""

cd /Users/SO28RO/IdeaProjects/ib2302/Opdrachten

# Check if all JAR files are present
echo "📦 Checking JAR files:"
REQUIRED_JARS=(
    "junit-jupiter-api-5.10.1.jar"
    "junit-jupiter-engine-5.10.1.jar"
    "junit-platform-engine-1.10.1.jar"
    "junit-platform-launcher-1.10.1.jar"
    "junit-platform-commons-1.10.1.jar"
    "apiguardian-api-1.1.2.jar"
    "opentest4j-1.3.0.jar"
)

ALL_PRESENT=true
for jar in "${REQUIRED_JARS[@]}"; do
    if [ -f "lib/$jar" ]; then
        echo "   ✅ $jar"
    else
        echo "   ❌ $jar (MISSING)"
        ALL_PRESENT=false
    fi
done

echo ""

if [ "$ALL_PRESENT" = true ]; then
    echo "🎉 All JUnit 5 JAR files are present!"
    echo ""

    # Test compilation
    echo "🔨 Testing compilation..."
    if javac -cp "lib/*" -d bin src/framework/*.java src/week1/*.java 2>/dev/null; then
        echo "   ✅ Compilation successful"

        # Test execution
        echo ""
        echo "🧪 Testing JUnit 5 execution..."
        if java -cp "lib/*:bin" org.junit.platform.console.ConsoleLauncher --class-path bin --select-class week1.RockPaperScissorsProcessTest --disable-banner --details summary >/dev/null 2>&1; then
            echo "   ✅ JUnit 5 execution successful"
            echo ""
            echo "🎉 SUCCESS! IntelliJ IDEA JUnit 5 setup is complete and working!"
            echo ""
            echo "🚀 You can now run tests in IntelliJ IDEA by:"
            echo "   1. Right-clicking on any test class"
            echo "   2. Selecting 'Run TestClassName'"
            echo ""
        else
            echo "   ❌ JUnit 5 execution failed"
        fi
    else
        echo "   ❌ Compilation failed"
    fi
else
    echo "❌ Some JAR files are missing. Please run the setup again."
fi
