import React, { useState, useEffect } from 'react';
import { getOrders, cancelOrder as cancelOrderAPI } from '../services/api';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const response = await getOrders();
      if (response.data.success) {
        setOrders(response.data.orders);
      }
    } catch (err) {
      setError('Failed to load orders');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm('Are you sure you want to cancel this order?')) {
      return;
    }

    try {
      const response = await cancelOrderAPI(orderId);
      if (response.data.success) {
        setSuccess('Order cancelled successfully');
        fetchOrders();
        setTimeout(() => setSuccess(''), 3000);
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to cancel order');
    }
  };

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  if (loading) {
    return <div className="container">Loading orders...</div>;
  }

  if (orders.length === 0) {
    return (
      <div className="container">
        <div className="empty-state">
          <h3>No orders yet</h3>
          <p>Start ordering from our amazing restaurants!</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <h2>Your Orders</h2>
      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}
      {orders.map((order) => (
        <div key={order.id} className="order-card">
          <div className="order-header">
            <div>
              <h3>Order #{order.id}</h3>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>
                {order.restaurantName} • {formatDate(order.createdAt)}
              </p>
            </div>
            <span className={`order-status status-${order.status.toLowerCase()}`}>
              {order.status}
            </span>
          </div>
          <div className="order-items">
            <h4>Items:</h4>
            {order.items.map((item) => (
              <div key={item.id} className="order-item">
                {item.menuItemName} × {item.quantity} - ${(item.price * item.quantity).toFixed(2)}
              </div>
            ))}
          </div>
          <div style={{ marginTop: '1rem', paddingTop: '1rem', borderTop: '1px solid #eee' }}>
            <p><strong>Delivery Address:</strong> {order.deliveryAddress}</p>
            <p style={{ fontSize: '1.2rem', fontWeight: 'bold', marginTop: '0.5rem' }}>
              Total: ${order.totalAmount.toFixed(2)}
            </p>
          </div>
          {order.status === 'PENDING' && (
            <button
              className="cancel-order-btn"
              onClick={() => handleCancelOrder(order.id)}
            >
              Cancel Order
            </button>
          )}
        </div>
      ))}
    </div>
  );
};

export default Orders;
