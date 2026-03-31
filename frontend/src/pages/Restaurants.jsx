import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRestaurants } from '../services/api';

const Restaurants = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const fetchRestaurants = async () => {
    try {
      const response = await getRestaurants();
      if (response.data.success) {
        setRestaurants(response.data.restaurants);
      }
    } catch (err) {
      setError('Failed to load restaurants');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRestaurantClick = (restaurantId) => {
    navigate(`/menu/${restaurantId}`);
  };

  if (loading) {
    return <div className="container">Loading restaurants...</div>;
  }

  if (error) {
    return <div className="container"><div className="error-message">{error}</div></div>;
  }

  return (
    <div className="container">
      <h2>Browse Restaurants</h2>
      <div className="restaurant-grid">
        {restaurants.map((restaurant) => (
          <div
            key={restaurant.id}
            className="restaurant-card"
            onClick={() => handleRestaurantClick(restaurant.id)}
          >
            <img src={restaurant.imageUrl} alt={restaurant.name} />
            <div className="restaurant-info">
              <h3>{restaurant.name}</h3>
              <p>{restaurant.description}</p>
              <p>{restaurant.address}</p>
              <p className="rating">⭐ {restaurant.rating}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Restaurants;
