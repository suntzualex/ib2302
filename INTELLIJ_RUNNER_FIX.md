# 🔧 IntelliJ IDEA Test Runner Fix

## The Problem
IntelliJ IDEA is trying to use the old JUnit runner instead of JUnit 5, causing the error:
```
!!! JUnit version 3.8 or later expected:
java.lang.ClassNotFoundException: junit.framework.ComparisonFailure
```

## ✅ Solution Options

### Option 1: Use External Tools (Recommended)
IntelliJ now has external tools configured for you:

1. **Right-click** on any test file
2. **Go to "External Tools"**
3. **Select "Run RockPaperScissorsProcessTest"**

Or use the menu: **Tools → External Tools → Run RockPaperScissorsProcessTest**

### Option 2: Terminal Integration in IntelliJ
1. **Open Terminal** in IntelliJ (Alt+F12 or View → Tool Windows → Terminal)
2. **Run commands**:
   ```bash
   cd Opdrachten
   ./run_test.sh week1.RockPaperScissorsProcessTest
   ```

### Option 3: Fix IntelliJ JUnit Configuration
If you want to use native IntelliJ test runner:

1. **Go to**: File → Settings → Build, Execution, Deployment → Build Tools → Maven
2. **Uncheck**: "Use plugin registry"
3. **Go to**: Run → Edit Configurations...
4. **Delete** any old RockPaperScissorsProcessTest configurations
5. **Right-click** the test class → "Create 'RockPaperScissorsProcessTest'"
6. **In the configuration**:
   - Set "Use classpath of module": Opdrachten
   - Set "JRE": 1.8
   - Click "Apply" and "OK"

### Option 4: Manual Command Line (Always Works)
```bash
cd /Users/SO28RO/IdeaProjects/ib2302/Opdrachten
./run_test.sh week1.RockPaperScissorsProcessTest
```

## 🎯 Quick Test
To verify everything works:
```bash
cd /Users/SO28RO/IdeaProjects/ib2302/Opdrachten
./intellij_test_runner.sh
```

## Expected Result
You should see:
```
╷
├─ JUnit Jupiter ✔
│  └─ RockPaperScissorsProcessTest ✔
│     ├─ receiveTest1() ✘ expected: <1> but was: <0>
│     ├─ receiveTest2() ✘ Expected framework.IllegalReceiveException...
│     ├─ receiveTest3() ✘ Expected framework.IllegalReceiveException...
│     ├─ receiveTest4() ✘ Expected framework.IllegalReceiveException...
│     ├─ simulationTest1() ✘ expected: <1> but was: <0>
│     ├─ initTest1() ✘ expected: <1> but was: <0>
│     └─ initTest2() ✘ expected: <1> but was: <0>
```

**These failures are CORRECT and EXPECTED** - they show what you need to implement!

## 🚨 If IntelliJ Still Has Issues
IntelliJ sometimes caches old configurations. Try:
1. **File → Invalidate Caches and Restart**
2. **Wait for re-indexing**
3. **Use External Tools option above**

The command line approach will ALWAYS work regardless of IntelliJ configuration issues.
