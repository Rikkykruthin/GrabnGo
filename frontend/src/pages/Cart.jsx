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
  const [userLat, setUserLat] = useState('');
  const [userLng, setUserLng] = useState('');
  const [placing, setPlacing] = useState(false);
  const [gettingLocation, setGettingLocation] = useState(false);
  const { updateCartCount } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      console.log('Fetching cart...');
      const response = await getCart();
      console.log('Cart response:', response.data);
      if (response.data.success) {
        setCart(response.data.cart);
        setTotal(response.data.total);
        updateCartCount(response.data.cart.length);
      }
    } catch (err) {
      console.error('Failed to load cart:', err);
      setError('Failed to load cart');
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = async (menuItemId) => {
    try {
      const response = await removeFromCartAPI(menuItemId);
      if (response.data.success) {
        fetchCart(); // Refresh the cart
      }
    } catch (err) {
      setError('Failed to remove item');
      console.error(err);
    }
  };

  const getUserLocation = () => {
    setGettingLocation(true);
    setError('');
    
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setUserLat(position.coords.latitude.toString());
          setUserLng(position.coords.longitude.toString());
          setGettingLocation(false);
          setError('');
        },
        (error) => {
          setGettingLocation(false);
          setError('Unable to get your location. Please enter coordinates manually or use default location.');
          // Set default location (Bangalore coordinates as fallback)
          setUserLat('12.9716');
          setUserLng('77.5946');
        }
      );
    } else {
      setGettingLocation(false);
      setError('Geolocation is not supported by your browser. Using default location.');
      setUserLat('12.9716');
      setUserLng('77.5946');
    }
  };

  const handlePlaceOrder = async () => {
    if (!deliveryAddress.trim()) {
      setError('Please enter delivery address');
      return;
    }

    if (!userLat || !userLng) {
      setError('Please get your location or enter coordinates');
      return;
    }

    setPlacing(true);
    setError('');

    try {
      const response = await placeOrder({ 
        deliveryAddress,
        userLat: parseFloat(userLat),
        userLng: parseFloat(userLng)
      });
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
              <p style={{ fontWeight: 'bold' }}>Subtotal: ${((item.subtotal || item.price * item.quantity)).toFixed(2)}</p>
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
        <div style={{ marginTop: '1rem' }}>
          <button
            onClick={getUserLocation}
            disabled={gettingLocation}
            style={{ 
              padding: '0.75rem 1.5rem', 
              background: '#4CAF50', 
              color: 'white', 
              border: 'none', 
              borderRadius: '4px', 
              cursor: 'pointer',
              marginBottom: '1rem'
            }}
          >
            {gettingLocation ? 'Getting Location...' : '📍 Get My Location'}
          </button>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
            <div className="form-group">
              <label>Latitude</label>
              <input
                type="number"
                step="0.0001"
                value={userLat}
                onChange={(e) => setUserLat(e.target.value)}
                placeholder="e.g., 12.9716"
                style={{ width: '100%', padding: '0.75rem', borderRadius: '4px', border: '1px solid #ddd' }}
              />
            </div>
            <div className="form-group">
              <label>Longitude</label>
              <input
                type="number"
                step="0.0001"
                value={userLng}
                onChange={(e) => setUserLng(e.target.value)}
                placeholder="e.g., 77.5946"
                style={{ width: '100%', padding: '0.75rem', borderRadius: '4px', border: '1px solid #ddd' }}
              />
            </div>
          </div>
          <p style={{ fontSize: '0.85rem', color: '#666', marginTop: '0.5rem' }}>
            Click "Get My Location" or enter coordinates manually. Delivery fee is calculated based on distance.
          </p>
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
