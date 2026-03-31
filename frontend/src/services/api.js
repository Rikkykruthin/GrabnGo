import axios from 'axios';

// Use relative path for production, works with both dev proxy and production
const API_BASE_URL = window.location.hostname === 'localhost' && window.location.port === '5173' 
  ? '/api'  // Dev server with proxy
  : '/food-ordering-app/api';  // Production on Tomcat

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Auth APIs
export const register = (userData) => api.post('/auth/signup', userData);
export const login = (credentials) => api.post('/auth/login', credentials);
export const logout = () => api.post('/auth/logout');

// Restaurant APIs
export const getRestaurants = () => api.get('/restaurants');

// Menu APIs
export const getMenu = (restaurantId) => api.get(`/restaurants/${restaurantId}/menu`);

// Cart APIs
export const getCart = () => api.get('/cart');
export const addToCart = (item) => api.post('/cart/items', item);
export const removeFromCart = (itemId) => api.delete(`/cart/items/${itemId}`);
export const clearCart = () => api.delete('/cart');

// Order APIs
export const placeOrder = (orderData) => api.post('/orders', orderData);
export const getOrders = () => api.get('/orders');
export const cancelOrder = (orderId) => api.patch(`/orders/${orderId}/cancel`);

export default api;
