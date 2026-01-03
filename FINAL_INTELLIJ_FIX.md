# 🎯 IntelliJ IDEA JUnit 5 Setup - Final Solution

## The Root Issue
IntelliJ IDEA is detecting your test class as JUnit 3/4 instead of JUnit 5, even though you're using `@Test` from `org.junit.jupiter.api.Test`.

## ✅ **FINAL SOLUTION** - Follow These Steps Exactly:

### Step 1: Force IntelliJ to Refresh
1. **Close IntelliJ IDEA completely**
2. **Reopen the project** `/Users/SO28RO/IdeaProjects/ib2302`
3. **Wait for indexing to complete** (bottom progress bar)

### Step 2: Check Project Structure
1. **File → Project Structure** (Cmd+;)
2. **Go to "Modules" → "Opdrachten"**
3. **In "Dependencies" tab**:
   - Verify "JUnit5" library is present
   - Verify "Module SDK" is "1.8.0_231"
   - Click "Apply"

### Step 3: Create New Test Configuration
1. **Right-click** on `RockPaperScissorsProcessTest.java` in the editor
2. **Select "Run 'RockPaperScissorsProcessTest'"** (NOT "Create Test")
3. **If it fails with the JUnit 3.8 error, continue to Step 4**

### Step 4: Fix Run Configuration Manually
1. **Run → Edit Configurations...**
2. **Find "RockPaperScissorsProcessTest" in the list**
3. **In the configuration**:
   - **Test kind**: Class
   - **Class**: `week1.RockPaperScissorsProcessTest`
   - **Module**: `Opdrachten`
   - **VM options**: `-ea`
   - **Working directory**: `$MODULE_DIR$`
4. **Click "Apply" and "OK"**

### Step 5: Force JUnit 5 Detection
1. **In the test file, add this temporary annotation above the class**:
   ```java
   @org.junit.jupiter.api.DisplayName("Test")
   class RockPaperScissorsProcessTest {
   ```
2. **Save the file**
3. **Try running again**
4. **Remove the annotation after it works**

### Step 6: If Still Not Working - Nuclear Option
1. **File → Invalidate Caches and Restart**
2. **Select "Invalidate and Restart"**
3. **Wait for full reindexing**
4. **Repeat Steps 2-4**

## 🎯 **What You Should See When It Works:**
```
Testing started at...
week1.RockPaperScissorsProcessTest > initTest1() FAILED
Expected: 1, Actual: 0
week1.RockPaperScissorsProcessTest > receiveTest1() FAILED
Expected: 1, Actual: 0
...
Process finished with exit code 255
```

**These failures are CORRECT!** They show:
- ✅ JUnit 5 is running
- ✅ Tests are executing  
- ❌ Your implementation is empty (that's the assignment!)

## 🚨 **If STILL Not Working:**
The issue is likely IntelliJ's JUnit detection. Try:
1. **Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. **Set "Build and run using" to "IntelliJ IDEA"**
3. **Set "Run tests using" to "IntelliJ IDEA"**

Or simply use the terminal in IntelliJ:
```bash
cd Opdrachten
./run_test.sh week1.RockPaperScissorsProcessTest
```

Your JUnit 5 setup is perfect - it's just an IntelliJ detection issue!
