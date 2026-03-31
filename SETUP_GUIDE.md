# Quick Setup Guide - Food Ordering Application

## For Hackathon Judges/Reviewers

### Prerequisites Check
```bash
java -version    # Should be 11+
mysql --version  # Should be 8.x
node -v          # Should be 16+
mvn -version     # Should be 3.6+
```

### 5-Minute Setup

#### Step 1: Database (2 minutes)
```bash
# Start MySQL
mysql -u root -p

# Run schema
mysql -u root -p < database/schema.sql

# Verify
mysql -u root -p
USE food_ordering_app;
SHOW TABLES;
SELECT * FROM restaurants;
exit;
```

#### Step 2: Backend (2 minutes)
```bash
cd backend

# Update DB credentials if needed
# Edit: src/com/foodapp/util/DBConnection.java
# Change USER and PASSWORD

# Build
mvn clean package

# Deploy to Tomcat
# Copy target/food-ordering-app.war to TOMCAT_HOME/webapps/

# Start Tomcat
# On Mac/Linux: $TOMCAT_HOME/bin/catalina.sh run
# On Windows: %TOMCAT_HOME%\bin\catalina.bat run

# Verify: http://localhost:8080/food-ordering-app/api/restaurants
```

#### Step 3: Frontend (1 minute)
```bash
cd frontend

# Install & run
npm install
npm run dev

# Open: http://localhost:5173
```

### Test Flow (2 minutes)

1. Register: Create account with email/password
2. Browse: View 4 pre-seeded restaurants
3. Menu: Click restaurant, see menu items
4. Cart: Add items, view cart
5. Order: Enter address, place order
6. History: View orders, cancel if pending

### Check Logs

Backend logs in Tomcat console show:
- User actions (login, orders)
- API calls
- Email notifications (mock)
- Errors

### Common Issues

**Database Connection Error:**
- Check MySQL is running
- Verify credentials in DBConnection.java
- Ensure database exists: `SHOW DATABASES;`

**Port 8080 in use:**
- Change Tomcat port in server.xml
- Update frontend proxy in vite.config.js

**CORS Error:**
- Verify backend is running
- Check CorsFilter allows http://localhost:5173

**Frontend won't start:**
- Delete node_modules and package-lock.json
- Run `npm install` again

### Project Highlights

✅ All 11 requirements implemented
✅ Clean layered architecture
✅ Comprehensive error handling
✅ Detailed logging
✅ Session-based auth
✅ Responsive UI
✅ Mock email notifications
✅ CRUD operations
✅ Transaction management

### Architecture

```
Frontend (React) → API (Servlets) → DAO → Database (MySQL)
                ↓
            Logging & Email Utils
```

### Key Files to Review

- `backend/src/com/foodapp/servlet/` - REST APIs
- `backend/src/com/foodapp/dao/` - Database layer
- `backend/src/com/foodapp/util/Logger.java` - Logging
- `backend/src/com/foodapp/util/EmailUtil.java` - Email mock
- `frontend/src/pages/` - UI pages
- `database/schema.sql` - Database design

### Demo Credentials

After registration, use your own credentials.
Or register with:
- Email: demo@food.com
- Password: demo123
- Name: Demo User

### Time Breakdown

- Backend: 2 hours (Models, DAOs, Servlets)
- Frontend: 1.5 hours (Pages, Components, API)
- Database: 30 minutes (Schema, Seed data)
- Total: 4 hours ✅

Enjoy testing! 🍔🍕🍣🌮
