# Admin Feature - GrabnGo

## Overview
Added admin role system with a comprehensive admin dashboard to track all users, orders, and statistics.

## Features Added

### 1. User Roles
- **CUSTOMER**: Regular users who can order food
- **ADMIN**: Administrators who can view and manage everything

### 2. Admin Dashboard
Access at: `/admin` (only visible to admin users)

**Dashboard Tab:**
- Total Users count
- Total Orders count
- Pending Orders count
- Total Revenue
- Recent 10 orders list

**All Orders Tab:**
- View all orders from all users
- See order details (ID, user, restaurant, total, status, date)
- Update order status:
  - PENDING → CONFIRMED or CANCELLED
  - CONFIRMED → DELIVERED

**All Users Tab:**
- View all registered users
- See user details (ID, name, email, phone, role)

### 3. Default Admin Account
**Email:** admin@grabngo.com  
**Password:** admin123

## Setup Instructions

### 1. Update Database
Run the updated schema to add role column and admin user:

```bash
# On Mac/Linux:
mysql -u root -p < database/schema.sql

# On Windows:
mysql -u root -p < database\schema.sql
```

Or manually run:
```sql
ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'CUSTOMER';
INSERT INTO users (email, password, name, phone, role) VALUES
('admin@grabngo.com', 'admin123', 'Admin User', '0000000000', 'ADMIN');
```

### 2. Rebuild and Deploy

**Mac/Linux:**
```bash
cd frontend
npm run build
rm -rf ../backend/webapp/assets ../backend/webapp/index.html
cp -r dist/. ../backend/webapp/

cd ../backend
mvn clean package
cp target/food-ordering-app.war /opt/homebrew/opt/tomcat@9/libexec/webapps/
```

**Windows:**
```cmd
cd frontend
npm run build
rmdir /s /q ..\backend\webapp\assets
del ..\backend\webapp\index.html
xcopy /E /I dist\* ..\backend\webapp\

cd ..\backend
mvn clean package
copy target\food-ordering-app.war "C:\Program Files\Apache Tomcat 9.0\webapps\"
```

### 3. Restart Tomcat

**Mac/Linux:**
```bash
# Stop and start the Tomcat process
```

**Windows:**
```cmd
net stop Tomcat9
net start Tomcat9
```

### 4. Access Admin Dashboard

1. Go to: http://localhost:8080/food-ordering-app/
2. Login with admin credentials:
   - Email: admin@grabngo.com
   - Password: admin123
3. Click "Admin" link in navbar
4. View dashboard, manage orders, and see all users

## API Endpoints

### Admin APIs (Requires Admin Role)

**GET /api/admin/dashboard**
- Returns statistics and recent orders

**GET /api/admin/users**
- Returns all users

**GET /api/admin/orders**
- Returns all orders from all users

**PATCH /api/admin/orders/{orderId}/status**
- Update order status
- Body: `{"status": "CONFIRMED"}` or `"DELIVERED"` or `"CANCELLED"`

## Security

- All admin endpoints check for admin role
- Returns 403 Forbidden if non-admin tries to access
- Role is stored in session after login
- Regular customers cannot access admin features

## Customer vs Admin Experience

### Customer:
- Register/Login
- Browse restaurants
- View menus
- Add to cart
- Place orders
- View their own orders
- Cancel their pending orders

### Admin:
- All customer features PLUS:
- View dashboard with statistics
- See all users in the system
- View all orders from all customers
- Update order status (confirm, deliver, cancel)
- Track total revenue
- Monitor pending orders

## Files Modified/Created

### Backend:
- `database/schema.sql` - Added role column and admin user
- `backend/src/main/java/com/foodapp/model/User.java` - Added role field
- `backend/src/main/java/com/foodapp/dao/UserDAO.java` - Added getAllUsers()
- `backend/src/main/java/com/foodapp/dao/OrderDAO.java` - Added getAllOrders(), updateOrderStatus()
- `backend/src/main/java/com/foodapp/servlet/AdminServlet.java` - NEW: Admin API endpoints
- `backend/src/main/java/com/foodapp/servlet/AuthServlet.java` - Store role in session
- `backend/webapp/WEB-INF/web.xml` - Registered AdminServlet

### Frontend:
- `frontend/src/pages/AdminDashboard.jsx` - NEW: Admin dashboard UI
- `frontend/src/App.jsx` - Added /admin route
- `frontend/src/components/Navbar.jsx` - Show Admin link for admins

## Testing

1. **Test Admin Login:**
   - Login as admin@grabngo.com / admin123
   - Verify "Admin" link appears in navbar

2. **Test Dashboard:**
   - Click Admin link
   - Verify statistics display correctly
   - Check recent orders list

3. **Test Order Management:**
   - Go to "All Orders" tab
   - Try updating order status
   - Verify status changes

4. **Test User List:**
   - Go to "All Users" tab
   - Verify all users are listed
   - Check role badges

5. **Test Customer Access:**
   - Login as regular customer
   - Verify no "Admin" link in navbar
   - Try accessing /admin directly (should be blocked)

## Future Enhancements

Possible additions:
- Add/Edit/Delete restaurants
- Add/Edit/Delete menu items
- View order details with items
- Export reports (CSV/PDF)
- User management (ban/unban users)
- Analytics charts
- Real-time order notifications
- Email notifications to customers

Enjoy your admin dashboard! 🎉
