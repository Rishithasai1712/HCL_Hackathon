import React from 'react';
import { NavLink } from 'react-router-dom';

export default function AdminLayout({ children }) {
  return (
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <NavLink to="/admin/products" className={({ isActive }) => isActive ? 'active' : ''}>📦 Products</NavLink>
        <NavLink to="/admin/inventory" className={({ isActive }) => isActive ? 'active' : ''}>🏭 Inventory</NavLink>
        <NavLink to="/admin/orders" className={({ isActive }) => isActive ? 'active' : ''}>📋 Orders</NavLink>
      </aside>
      <main className="admin-content">{children}</main>
    </div>
  );
}
