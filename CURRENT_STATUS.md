# GrabnGo - Current Status

## What's Working ✅
- Backend running on Tomcat 9 (http://localhost:8080/food-ordering-app)
- MySQL 8.0 database with all tables and seed data
- User registration and login
- Restaurant listing
- Menu display for each restaurant
- All API endpoints responding correctly

## What's Not Working ❌
- Cart persistence (session cookies not being maintained through Vite proxy)

## The Problem
The issue is with session cookie handling through the Vite development proxy:
1. When you add items to cart, the backend creates a session and returns a JSESSIONID cookie
2. The Vite proxy at localhost:5173 doesn't properly forward this cookie back to the browser
3. When you navigate to the cart page, the browser makes a new request without the session cookie
4. The backend sees this as a new session with an empty cart

## Solutions

### Option 1: Test Without Proxy (Quick Test)
Access the backend directly to verify everything works:
1. Open http://localhost:8080/food-ordering-app/api/restaurants in browser
2. Use a tool like Postman to test the full flow with cookies

### Option 2: Use Token-Based Auth (Recommended for Production)
Replace session-based cart with:
- JWT tokens for authentication
- Store cart in database instead of session
- This is more scalable and works better with modern frontends

### Option 3: Run Frontend Build (Not Dev Server)
Build the frontend and serve it from Tomcat:
```bash
cd frontend
npm run build
cp -r dist/* ../backend/webapp/
```
Then access at http://localhost:8080/food-ordering-app/

## Quick Test Commands

Test add to cart (with cookie persistence):
```bash
# Add item
curl -c /tmp/cookies.txt -X POST http://localhost:8080/food-ordering-app/api/cart/items \
  -H "Content-Type: application/json" \
  -d '{"menuItemId":1,"name":"Pizza","price":12.99,"quantity":1,"restaurantId":1}'

# Get cart (using saved cookie)
curl -b /tmp/cookies.txt http://localhost:8080/food-ordering-app/api/cart
```

## Services Running
- Tomcat 9: Port 8080
- MySQL 8.0: Port 3306 (user: root, password: empty)
- Frontend Dev: Port 5173

## Next Steps
1. Try accessing backend directly without proxy to confirm cart works
2. If confirmed, implement token-based authentication
3. Or build frontend and serve from Tomcat
