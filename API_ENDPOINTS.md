# API Endpoints Documentation

Base URL: `http://localhost:8080/foodapp`

## Authentication

### POST /api/auth/signup
Register a new user
```json
Request:
{
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe",
  "phone": "1234567890"
}

Response:
{
  "success": true,
  "message": "Registration successful",
  "user": { "id": 1, "email": "user@example.com", "name": "John Doe" }
}
```

### POST /api/auth/login
Login user
```json
Request:
{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "user": { "id": 1, "email": "user@example.com", "name": "John Doe" }
}
```

### POST /api/auth/logout
Logout current user

### GET /api/auth/me
Get current authenticated user

## Restaurants

### GET /api/restaurants
Get all restaurants with location data
```json
Response:
{
  "success": true,
  "restaurants": [
    {
      "id": 1,
      "name": "Pizza Palace",
      "description": "Authentic Italian pizzas",
      "address": "123 Main Street",
      "imageUrl": "https://...",
      "rating": 4.5,
      "latitude": 12.9716,
      "longitude": 77.5946
    }
  ]
}
```

### GET /api/restaurants/{restaurantId}
Get single restaurant details

### GET /api/restaurants/{restaurantId}/menu
Get menu for specific restaurant

## Menu Items

### GET /api/menu-items/{itemId}
Get single menu item details

## Delivery

### POST /api/delivery/quote
Calculate delivery distance and fee
```json
Request:
{
  "userLat": 12.9716,
  "userLng": 77.5946,
  "restaurantId": 1
}

Response:
{
  "success": true,
  "distanceKm": 2.5,
  "deliveryFee": 0,
  "restaurantId": 1,
  "restaurantName": "Pizza Palace"
}
```

**Delivery Fee Rules:**
- Distance < 3km: ₹0 (Free delivery)
- Distance >= 3km: ₹10 per km

## Cart

### GET /api/cart
Get cart items with optional delivery calculation
```
Query params (optional):
?restaurantId=1&userLat=12.9716&userLng=77.5946

Response:
{
  "success": true,
  "cart": [...],
  "total": 299.98,
  "deliveryFee": 0,
  "distanceKm": 2.5,
  "grandTotal": 299.98
}
```

### POST /api/cart/items
Add item to cart
```json
Request:
{
  "menuItemId": 1,
  "name": "Margherita Pizza",
  "price": 12.99,
  "quantity": 1,
  "restaurantId": 1
}
```

### PUT /api/cart/items/{itemId}
Update cart item quantity
```json
Request:
{
  "quantity": 2
}
```

### DELETE /api/cart/items/{itemId}
Remove specific item from cart

### DELETE /api/cart
Clear entire cart

## Orders

### POST /api/orders
Place a new order
```json
Request:
{
  "deliveryAddress": "123 Main St, Apt 4B",
  "userLat": 12.9716,
  "userLng": 77.5946
}

Response:
{
  "success": true,
  "message": "Order placed successfully",
  "order": {
    "id": 1,
    "totalAmount": 299.98,
    "deliveryFee": 0,
    "distanceKm": 2.5,
    "grandTotal": 299.98,
    "status": "PENDING",
    ...
  }
}
```

### GET /api/orders
Get all orders for current user

### GET /api/orders/{orderId}
Get specific order details

### PATCH /api/orders/{orderId}/cancel
Cancel a pending order

## Error Responses

All endpoints return errors in this format:
```json
{
  "success": false,
  "error": "Error message here"
}
```

Common HTTP Status Codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 500: Internal Server Error
