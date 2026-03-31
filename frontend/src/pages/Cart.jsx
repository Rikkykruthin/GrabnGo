import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCart, removeFromCart as removeFromCartAPI, placeOrder } from '../services/api';
import { useCart } from '../context/CartContext';

const Cart = () => {
  const [cart, setCart] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [placing, setPlacing] = useState(false);
  const { updateCartCount } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const response = await getCart();
      if (response.data.success) {
        setCart(response.data.cart);
        setTotal(response.data.total);
        updateCartCount(response.data.cart.length);
      }
    } catch (err) {
      setError('Failed to load cart');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = async (menuItemId) => {
    try {
      const response = await removeFromCartAPI(menuItemId);
      if (response.data.success) {
        setCart(response.data.cart);
        const newTotal = response.data.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        setTotal(newTotal);
        updateCartCount(response.data.cart.length);
      }
    } catch (err) {
      setError('Failed to remove item');
      console.error(err);
    }
  };

  const handlePlaceOrder = async () => {
    if (!deliveryAddress.trim()) {
      setError('Please enter delivery address');
      return;
    }

    setPlacing(true);
    setError('');

    try {
      const response = await placeOrder({ deliveryAddress });
      if (response.data.success) {
        updateCartCount(0);
        navigate('/orders');
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to place order');
    } finally {
      setPlacing(false);
    }
  };

  if (loading) {
    return <div className="container">Loading cart...</div>;
  }

  if (cart.length === 0) {
    return (
      <div className="container">
        <div className="empty-state">
          <h3>Your cart is empty</h3>
          <p>Add some delicious items from our restaurants!</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <h2>Your Cart</h2>
      {error && <div className="error-message">{error}</div>}
      <div className="cart-container">
        {cart.map((item) => (
          <div key={item.menuItemId} className="cart-item">
            <div className="cart-item-info">
              <h4>{item.name}</h4>
              <p>Quantity: {item.quantity} × ${item.price.toFixed(2)}</p>
              <p style={{ fontWeight: 'bold' }}>Subtotal: ${item.subtotal.toFixed(2)}</p>
            </div>
            <button
              className="remove-btn"
              onClick={() => handleRemove(item.menuItemId)}
            >
              Remove
            </button>
          </div>
        ))}
        <div className="cart-total">
          Total: ${total.toFixed(2)}
        </div>
        <div className="form-group" style={{ marginTop: '1.5rem' }}>
          <label>Delivery Address</label>
          <input
            type="text"
            value={deliveryAddress}
            onChange={(e) => setDeliveryAddress(e.target.value)}
            placeholder="Enter your delivery address"
            style={{ width: '100%', padding: '0.75rem', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <button
          className="btn"
          onClick={handlePlaceOrder}
          disabled={placing}
          style={{ marginTop: '1rem' }}
        >
          {placing ? 'Placing Order...' : 'Place Order'}
        </button>
      </div>
    </div>
  );
};

export default Cart;
