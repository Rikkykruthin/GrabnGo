# Fix MySQL Access Denied Error on Windows

## Problem
Getting error: "Access denied for user 'root'@'localhost' (using password: NO)"

This means the application is trying to connect to MySQL with no password, but your MySQL requires a password.

## Solution - Update Database Password in Code

### Step 1: Find Your MySQL Password
The password you set during MySQL installation.

### Step 2: Update DBConnection.java

1. Open file: `backend\src\main\java\com\foodapp\util\DBConnection.java`

2. Find these lines (around line 8-10):
```java
private static final String URL = "jdbc:mysql://localhost:3306/food_ordering_app";
private static final String USER = "root";
private static final String PASSWORD = "";
```

3. Change the PASSWORD line to your MySQL password:
```java
private static final String PASSWORD = "your_mysql_password_here";
```

For example, if your password is "admin123":
```java
private static final String PASSWORD = "admin123";
```

### Step 3: Rebuild and Redeploy

Open Command Prompt as Administrator:

```cmd
cd path\to\GrabnGo\backend
mvn clean package
copy target\food-ordering-app.war "C:\Program Files\Apache Tomcat 9.0\webapps\" /Y
```

### Step 4: Restart Tomcat

```cmd
net stop Tomcat9
net start Tomcat9
```

Wait 10 seconds, then try again at:
http://localhost:8080/food-ordering-app/

## Alternative: Reset MySQL Root Password to Empty

If you want to use no password (easier for development):

### Option A: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your database
3. Go to Server → Users and Privileges
4. Select 'root' user
5. Click "Change Password"
6. Leave password fields empty
7. Click Apply

### Option B: Using Command Line

```cmd
# Stop MySQL
net stop MySQL80

# Start MySQL without password check
mysqld --skip-grant-tables

# In a NEW command prompt:
mysql -u root

# Run these commands:
USE mysql;
ALTER USER 'root'@'localhost' IDENTIFIED BY '';
FLUSH PRIVILEGES;
exit;

# Stop the mysqld process (Ctrl+C in first window)
# Start MySQL normally
net start MySQL80
```

Then rebuild and redeploy the application (Step 3 above).

## Quick Fix Script

Create `fix-database.bat`:

```batch
@echo off
echo Enter your MySQL root password:
set /p MYSQL_PASS=

echo Updating database connection...
powershell -Command "(gc backend\src\main\java\com\foodapp\util\DBConnection.java) -replace 'PASSWORD = \"\"', 'PASSWORD = \"%MYSQL_PASS%\"' | Out-File -encoding ASCII backend\src\main\java\com\foodapp\util\DBConnection.java"

echo Rebuilding application...
cd backend
call mvn clean package -q
copy target\food-ordering-app.war "C:\Program Files\Apache Tomcat 9.0\webapps\" /Y
cd ..

echo Restarting Tomcat...
net stop Tomcat9
timeout /t 2
net start Tomcat9

echo.
echo Done! Wait 10 seconds then open:
echo http://localhost:8080/food-ordering-app/
timeout /t 10
start http://localhost:8080/food-ordering-app/
```

Run as Administrator and enter your MySQL password when prompted.

## Verify Database Connection

Test if you can connect to MySQL:

```cmd
mysql -u root -p
# Enter your password when prompted

# If successful, you'll see:
mysql>

# Test the database:
USE food_ordering_app;
SHOW TABLES;
exit;
```

If this works, then the issue is just the password in the code.
