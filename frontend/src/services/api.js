import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Auth APIs
export const register = (userData) => api.post('/auth/register', userData);
export const login = (credentials) => api.post('/auth/login', credentials);
export const logout = () => api.post('/auth/logout');

// Restaurant APIs
export const getRestaurants = () => api.get('/restaurants');

// Menu APIs
export const getMenu = (restaurantId) => api.get(`/menu?restaurantId=${restaurantId}`);

// Cart APIs
export const getCart = () => api.get('/cart');
export const addToCart = (item) => api.post('/cart/add', item);
export const removeFromCart = (menuItemId) => api.post('/cart/remove', { menuItemId });
export const clearCart = () => api.post('/cart/clear');

// Order APIs
export const placeOrder = (orderData) => api.post('/orders/', orderData);
export const getOrders = () => api.get('/orders');
export const cancelOrder = (orderId) => api.put(`/orders/${orderId}/cancel`);

export default api;
