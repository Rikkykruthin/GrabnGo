# GrabnGo - Windows Setup Guide

## Prerequisites Installation

### 1. Install Java JDK 11 or higher
1. Download from: https://adoptium.net/
2. Choose "Temurin 11 (LTS)" or higher
3. Download Windows x64 installer (.msi)
4. Run installer and follow prompts
5. Verify installation:
```cmd
java -version
```

### 2. Install Maven
1. Download from: https://maven.apache.org/download.cgi
2. Download the Binary zip archive (apache-maven-3.x.x-bin.zip)
3. Extract to `C:\Program Files\Apache\maven`
4. Add to System PATH:
   - Right-click "This PC" → Properties → Advanced System Settings
   - Click "Environment Variables"
   - Under "System Variables", find "Path" and click Edit
   - Click New and add: `C:\Program Files\Apache\maven\bin`
5. Verify installation:
```cmd
mvn -version
```

### 3. Install MySQL 8.0
1. Download from: https://dev.mysql.com/downloads/installer/
2. Choose "MySQL Installer for Windows"
3. Run installer and select "Developer Default"
4. During configuration:
   - Set root password (leave empty or use "root")
   - Keep default port 3306
5. Verify installation:
```cmd
mysql --version
```

### 4. Install Apache Tomcat 9
1. Download from: https://tomcat.apache.org/download-90.cgi
2. Download "32-bit/64-bit Windows Service Installer" (.exe)
3. Run installer:
   - Choose installation directory (e.g., `C:\Program Files\Apache Tomcat 9.0`)
   - Set HTTP/1.1 Connector Port: 8080
   - Leave admin username/password blank or set as needed
4. Tomcat will install as a Windows service

### 5. Install Node.js (Optional - only for development)
1. Download from: https://nodejs.org/
2. Choose LTS version
3. Run installer with default options
4. Verify installation:
```cmd
node -v
npm -v
```

## Database Setup

### 1. Start MySQL Service
```cmd
# Open Services (Win+R, type "services.msc")
# Find "MySQL80" service and start it
# OR use command line:
net start MySQL80
```

### 2. Create Database
```cmd
# Navigate to project directory
cd path\to\GrabnGo

# Run schema
mysql -u root -p < database\schema.sql
# Press Enter if no password, or type your password

# Verify database
mysql -u root -p
USE food_ordering_app;
SHOW TABLES;
SELECT * FROM restaurants;
exit;
```

## Application Setup

### 1. Build Backend
```cmd
cd backend
mvn clean package
```

This creates `target\food-ordering-app.war`

### 2. Deploy to Tomcat

**Option A: Using Tomcat Manager (Recommended)**
1. Copy WAR file to Tomcat webapps:
```cmd
copy target\food-ordering-app.war "C:\Program Files\Apache Tomcat 9.0\webapps\"
```

**Option B: Manual Deployment**
1. Stop Tomcat service (if running)
2. Copy WAR file to `C:\Program Files\Apache Tomcat 9.0\webapps\`
3. Start Tomcat service

### 3. Start Tomcat Service
```cmd
# Using Services
# Win+R → services.msc → Find "Apache Tomcat 9.0" → Start

# OR using command line (as Administrator)
net start Tomcat9
```

### 4. Verify Installation
Open browser and go to:
- Application: http://localhost:8080/food-ordering-app/
- API Test: http://localhost:8080/food-ordering-app/api/restaurants

## Troubleshooting

### Port 8080 Already in Use
1. Check what's using port 8080:
```cmd
netstat -ano | findstr :8080
```
2. Kill the process or change Tomcat port in:
   `C:\Program Files\Apache Tomcat 9.0\conf\server.xml`

### MySQL Connection Error
1. Check MySQL is running:
```cmd
net start | findstr MySQL
```
2. Verify credentials in:
   `backend\src\main\java\com\foodapp\util\DBConnection.java`
   - Default: user=root, password=empty

### Tomcat Not Starting
1. Check logs in:
   `C:\Program Files\Apache Tomcat 9.0\logs\catalina.log`
2. Ensure Java is installed and JAVA_HOME is set

### Application Shows Blank Page
1. Clear browser cache (Ctrl+Shift+Delete)
2. Try incognito mode
3. Hard refresh (Ctrl+F5)
4. Check browser console (F12) for errors

## Quick Start Script

Create a file `start.bat` in the project root:

```batch
@echo off
echo Starting GrabnGo Application...

echo.
echo [1/3] Starting MySQL...
net start MySQL80

echo.
echo [2/3] Building application...
cd backend
call mvn clean package -q
copy target\food-ordering-app.war "C:\Program Files\Apache Tomcat 9.0\webapps\" /Y
cd ..

echo.
echo [3/3] Starting Tomcat...
net start Tomcat9

echo.
echo ========================================
echo GrabnGo is starting!
echo ========================================
echo.
echo Wait 10 seconds, then open:
echo http://localhost:8080/food-ordering-app/
echo.
timeout /t 10
start http://localhost:8080/food-ordering-app/
```

Run as Administrator:
```cmd
start.bat
```

## Stop Services

```cmd
net stop Tomcat9
net stop MySQL80
```

## Default Credentials

After setup, register a new account or use:
- No default users - register through the app
- Database: root / (empty password)

## Features

✅ User Registration & Login
✅ Browse 4 Restaurants (Pizza, Burgers, Sushi, Tacos)
✅ View Restaurant Menus (16 items)
✅ Add Items to Cart
✅ Location-based Delivery Fee Calculation
✅ Place Orders
✅ View Order History
✅ Cancel Pending Orders

## Tech Stack

- Backend: Java Servlets + MySQL
- Frontend: React (integrated in WAR)
- Server: Apache Tomcat 9
- Database: MySQL 8.0

## Support

If you encounter issues:
1. Check all services are running (MySQL, Tomcat)
2. Verify database was created successfully
3. Check Tomcat logs for errors
4. Ensure ports 3306 (MySQL) and 8080 (Tomcat) are available

## Project Structure

```
GrabnGo/
├── backend/
│   ├── src/main/java/com/foodapp/
│   │   ├── dao/          # Database access
│   │   ├── model/        # Data models
│   │   ├── servlet/      # REST APIs
│   │   ├── filter/       # CORS filter
│   │   └── util/         # Utilities
│   ├── webapp/
│   │   ├── WEB-INF/
│   │   │   └── web.xml   # Servlet config
│   │   └── assets/       # Frontend files
│   └── pom.xml           # Maven config
├── database/
│   └── schema.sql        # Database schema
└── frontend/             # React source (optional)
```

Enjoy your food ordering application! 🍕🍔🍣🌮
