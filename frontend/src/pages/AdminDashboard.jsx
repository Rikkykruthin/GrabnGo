import React, { useState, useEffect } from 'react';
import axios from 'axios';

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [orders, setOrders] = useState([]);
  const [users, setUsers] = useState([]);
  const [activeTab, setActiveTab] = useState('dashboard');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const api = axios.create({
    baseURL: window.location.hostname === 'localhost' && window.location.port === '5173' 
      ? '/api'
      : '/food-ordering-app/api',
    withCredentials: true,
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const response = await api.get('/admin/dashboard');
      if (response.data.success) {
        setStats(response.data.stats);
        setOrders(response.data.recentOrders);
      }
    } catch (err) {
      setError('Failed to load dashboard data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchAllOrders = async () => {
    try {
      const response = await api.get('/admin/orders');
      if (response.data.success) {
        setOrders(response.data.orders);
      }
    } catch (err) {
      setError('Failed to load orders');
    }
  };

  const fetchAllUsers = async () => {
    try {
      const response = await api.get('/admin/users');
      if (response.data.success) {
        setUsers(response.data.users);
      }
    } catch (err) {
      setError('Failed to load users');
    }
  };

  const updateOrderStatus = async (orderId, newStatus) => {
    try {
      const response = await api.post(`/admin/orders/${orderId}/status`, { status: newStatus });
      if (response.data.success) {
        fetchAllOrders();
        alert('Order status updated successfully');
      }
    } catch (err) {
      alert('Failed to update order status');
    }
  };

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    if (tab === 'orders') fetchAllOrders();
    if (tab === 'users') fetchAllUsers();
  };

  if (loading) return <div className="container">Loading admin dashboard...</div>;

  return (
    <div className="container" style={{ maxWidth: '1200px' }}>
      <h2>Admin Dashboard</h2>
      
      <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', borderBottom: '2px solid #ddd' }}>
        <button 
          onClick={() => handleTabChange('dashboard')}
          style={{ 
            padding: '10px 20px', 
            background: activeTab === 'dashboard' ? '#ff6b6b' : 'transparent',
            color: activeTab === 'dashboard' ? 'white' : '#333',
            border: 'none',
            cursor: 'pointer'
          }}
        >
          Dashboard
        </button>
        <button 
          onClick={() => handleTabChange('orders')}
          style={{ 
            padding: '10px 20px', 
            background: activeTab === 'orders' ? '#ff6b6b' : 'transparent',
            color: activeTab === 'orders' ? 'white' : '#333',
            border: 'none',
            cursor: 'pointer'
          }}
        >
          All Orders
        </button>
        <button 
          onClick={() => handleTabChange('users')}
          style={{ 
            padding: '10px 20px', 
            background: activeTab === 'users' ? '#ff6b6b' : 'transparent',
            color: activeTab === 'users' ? 'white' : '#333',
            border: 'none',
            cursor: 'pointer'
          }}
        >
          All Users
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {activeTab === 'dashboard' && stats && (
        <div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px', marginBottom: '30px' }}>
            <div style={{ background: '#4CAF50', color: 'white', padding: '20px', borderRadius: '8px' }}>
              <h3 style={{ margin: '0 0 10px 0' }}>Total Users</h3>
              <p style={{ fontSize: '2rem', margin: 0 }}>{stats.totalUsers}</p>
            </div>
            <div style={{ background: '#2196F3', color: 'white', padding: '20px', borderRadius: '8px' }}>
              <h3 style={{ margin: '0 0 10px 0' }}>Total Orders</h3>
              <p style={{ fontSize: '2rem', margin: 0 }}>{stats.totalOrders}</p>
            </div>
            <div style={{ background: '#FF9800', color: 'white', padding: '20px', borderRadius: '8px' }}>
              <h3 style={{ margin: '0 0 10px 0' }}>Pending Orders</h3>
              <p style={{ fontSize: '2rem', margin: 0 }}>{stats.pendingOrders}</p>
            </div>
            <div style={{ background: '#9C27B0', color: 'white', padding: '20px', borderRadius: '8px' }}>
              <h3 style={{ margin: '0 0 10px 0' }}>Total Revenue</h3>
              <p style={{ fontSize: '2rem', margin: 0 }}>${stats.totalRevenue.toFixed(2)}</p>
            </div>
          </div>

          <h3>Recent Orders</h3>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ background: '#f5f5f5' }}>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Order ID</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Status</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Total</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Date</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(order => (
                  <tr key={order.id} style={{ borderBottom: '1px solid #ddd' }}>
                    <td style={{ padding: '10px' }}>#{order.id}</td>
                    <td style={{ padding: '10px' }}>
                      <span style={{ 
                        padding: '4px 8px', 
                        borderRadius: '4px',
                        background: order.status === 'DELIVERED' ? '#4CAF50' : 
                                   order.status === 'CANCELLED' ? '#f44336' : '#FF9800',
                        color: 'white',
                        fontSize: '0.85rem'
                      }}>
                        {order.status}
                      </span>
                    </td>
                    <td style={{ padding: '10px' }}>${order.grandTotal.toFixed(2)}</td>
                    <td style={{ padding: '10px' }}>{new Date(order.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'orders' && (
        <div>
          <h3>All Orders ({orders.length})</h3>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ background: '#f5f5f5' }}>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Order ID</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>User ID</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Restaurant ID</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Total</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Status</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Date</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(order => (
                  <tr key={order.id} style={{ borderBottom: '1px solid #ddd' }}>
                    <td style={{ padding: '10px' }}>#{order.id}</td>
                    <td style={{ padding: '10px' }}>{order.userId}</td>
                    <td style={{ padding: '10px' }}>{order.restaurantId}</td>
                    <td style={{ padding: '10px' }}>${order.grandTotal.toFixed(2)}</td>
                    <td style={{ padding: '10px' }}>
                      <span style={{ 
                        padding: '4px 8px', 
                        borderRadius: '4px',
                        background: order.status === 'DELIVERED' ? '#4CAF50' : 
                                   order.status === 'CANCELLED' ? '#f44336' : '#FF9800',
                        color: 'white',
                        fontSize: '0.85rem'
                      }}>
                        {order.status}
                      </span>
                    </td>
                    <td style={{ padding: '10px' }}>{new Date(order.createdAt).toLocaleString()}</td>
                    <td style={{ padding: '10px' }}>
                      {order.status === 'PENDING' && (
                        <>
                          <button 
                            onClick={() => updateOrderStatus(order.id, 'CONFIRMED')}
                            style={{ padding: '4px 8px', marginRight: '5px', background: '#4CAF50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                          >
                            Confirm
                          </button>
                          <button 
                            onClick={() => updateOrderStatus(order.id, 'CANCELLED')}
                            style={{ padding: '4px 8px', background: '#f44336', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                          >
                            Cancel
                          </button>
                        </>
                      )}
                      {order.status === 'CONFIRMED' && (
                        <button 
                          onClick={() => updateOrderStatus(order.id, 'DELIVERED')}
                          style={{ padding: '4px 8px', background: '#2196F3', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                        >
                          Mark Delivered
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'users' && (
        <div>
          <h3>All Users ({users.length})</h3>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ background: '#f5f5f5' }}>
                  <th style={{ padding: '10px', textAlign: 'left' }}>ID</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Name</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Email</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Phone</th>
                  <th style={{ padding: '10px', textAlign: 'left' }}>Role</th>
                </tr>
              </thead>
              <tbody>
                {users.map(user => (
                  <tr key={user.id} style={{ borderBottom: '1px solid #ddd' }}>
                    <td style={{ padding: '10px' }}>{user.id}</td>
                    <td style={{ padding: '10px' }}>{user.name}</td>
                    <td style={{ padding: '10px' }}>{user.email}</td>
                    <td style={{ padding: '10px' }}>{user.phone}</td>
                    <td style={{ padding: '10px' }}>
                      <span style={{ 
                        padding: '4px 8px', 
                        borderRadius: '4px',
                        background: user.role === 'ADMIN' ? '#9C27B0' : '#2196F3',
                        color: 'white',
                        fontSize: '0.85rem'
                      }}>
                        {user.role}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;
