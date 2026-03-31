import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getMenu, addToCart as addToCartAPI } from '../services/api';
import { useCart } from '../context/CartContext';

const Menu = () => {
  const { restaurantId } = useParams();
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { updateCartCount } = useCart();

  useEffect(() => {
    fetchMenu();
  }, [restaurantId]);

  const fetchMenu = async () => {
    try {
      const response = await getMenu(restaurantId);
      if (response.data.success) {
        setMenuItems(response.data.menuItems);
      }
    } catch (err) {
      setError('Failed to load menu');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (item) => {
    try {
      const cartItem = {
        menuItemId: item.id,
        name: item.name,
        price: item.price,
        quantity: 1,
        restaurantId: parseInt(restaurantId)
      };

      const response = await addToCartAPI(cartItem);
      if (response.data.success) {
        setSuccess(`${item.name} added to cart!`);
        updateCartCount(response.data.cart.length);
        setTimeout(() => setSuccess(''), 3000);
      }
    } catch (err) {
      setError('Failed to add item to cart');
      console.error(err);
    }
  };

  if (loading) {
    return <div className="container">Loading menu...</div>;
  }

  return (
    <div className="container">
      <Link to="/restaurants" className="back-btn">← Back to Restaurants</Link>
      <h2>Menu</h2>
      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}
      <div className="menu-grid">
        {menuItems.map((item) => (
          <div key={item.id} className="menu-item-card">
            <img src={item.imageUrl} alt={item.name} />
            <div className="menu-item-info">
              <h4>{item.name}</h4>
              <p>{item.description}</p>
              <p style={{ fontSize: '0.8rem', color: '#999' }}>{item.category}</p>
              <div className="price">${item.price.toFixed(2)}</div>
              <button
                className="add-to-cart-btn"
                onClick={() => handleAddToCart(item)}
              >
                Add to Cart
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Menu;
