import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, isLoggedIn, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => { logout(); navigate('/'); };

  return (
    <nav className="navbar">
      <div className="container inner">
        <Link to="/" className="brand">🛒 RetailStore</Link>
        <nav>
          <Link to="/">Products</Link>
          {isLoggedIn && !isAdmin && <Link to="/orders">My Orders</Link>}
          {isAdmin && (
            <>
              <Link to="/admin/products">Products</Link>
              <Link to="/admin/inventory">Inventory</Link>
              <Link to="/admin/orders">Orders</Link>
            </>
          )}
          {isLoggedIn ? (
            <>
              {!isAdmin && (
                <Link to="/cart" className="cart-btn">🛒 Cart</Link>
              )}
              <span style={{ color: '#aaa', fontSize: '.85rem' }}>Hi, {user.name}</span>
              <button onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </nav>
      </div>
    </nav>
  );
}
