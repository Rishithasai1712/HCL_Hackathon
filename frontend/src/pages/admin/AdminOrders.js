import React, { useEffect, useState } from 'react';
import AdminLayout from '../../components/AdminLayout';
import { adminGetOrders, adminUpdateOrderStatus } from '../../api/api';

const STATUS_OPTIONS = ['PLACED', 'CONFIRMED', 'DELIVERED'];

export default function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(null);
  const [updating, setUpdating] = useState(null);
  const [msg, setMsg] = useState({ text: '', type: '' });
  const [filter, setFilter] = useState('');

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const { data } = await adminGetOrders();
      setOrders(data);
    } catch { } finally { setLoading(false); }
  };

  useEffect(() => { fetchOrders(); }, []);

  const handleStatusChange = async (orderId, status) => {
    setUpdating(orderId);
    try {
      const { data } = await adminUpdateOrderStatus(orderId, { status });
      setOrders(orders.map(o => o.id === orderId ? data : o));
      setMsg({ text: `Order #${orderId} updated to ${status}`, type: 'success' });
    } catch (err) {
      setMsg({ text: err.response?.data?.message || 'Update failed', type: 'error' });
    } finally {
      setUpdating(null);
      setTimeout(() => setMsg({ text: '', type: '' }), 3000);
    }
  };

  const filtered = filter ? orders.filter(o => o.status === filter) : orders;

  const stats = {
    total: orders.length,
    placed: orders.filter(o => o.status === 'PLACED').length,
    confirmed: orders.filter(o => o.status === 'CONFIRMED').length,
    delivered: orders.filter(o => o.status === 'DELIVERED').length,
  };

  return (
    <AdminLayout>
      <h2>Order Management</h2>

      {/* Stats */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: 16, marginBottom: 24 }}>
        {[
          { label: 'Total Orders', value: stats.total, color: '#1a1a2e' },
          { label: 'Placed', value: stats.placed, color: '#e65100' },
          { label: 'Confirmed', value: stats.confirmed, color: '#1565c0' },
          { label: 'Delivered', value: stats.delivered, color: '#2e7d32' },
        ].map(s => (
          <div key={s.label} className="card" style={{ textAlign: 'center', padding: '16px' }}>
            <div style={{ fontSize: '1.8rem', fontWeight: 700, color: s.color }}>{s.value}</div>
            <div style={{ fontSize: '.85rem', color: '#888', marginTop: 4 }}>{s.label}</div>
          </div>
        ))}
      </div>

      {msg.text && (
        <div className={`alert ${msg.type === 'error' ? 'alert-error' : 'alert-success'}`}>{msg.text}</div>
      )}

      {/* Filter */}
      <div style={{ marginBottom: 16, display: 'flex', gap: 10, alignItems: 'center' }}>
        <label style={{ fontWeight: 500, fontSize: '.9rem' }}>Filter by status:</label>
        <select
          value={filter}
          onChange={e => setFilter(e.target.value)}
          style={{ padding: '8px 12px', border: '1.5px solid #ddd', borderRadius: 6, fontSize: '.9rem' }}
        >
          <option value="">All</option>
          {STATUS_OPTIONS.map(s => <option key={s} value={s}>{s}</option>)}
        </select>
        <span style={{ color: '#888', fontSize: '.85rem' }}>{filtered.length} order(s)</span>
      </div>

      {loading ? (
        <div className="loading">Loading orders…</div>
      ) : filtered.length === 0 ? (
        <div className="empty-state"><p>No orders found.</p></div>
      ) : (
        <div className="orders-list">
          {filtered.map(order => (
            <div key={order.id} className="card order-card">
              <div className="order-header">
                <h3>Order #{order.id}</h3>
                <span className={`status-badge status-${order.status}`}>{order.status}</span>
                <span style={{ color: '#888', fontSize: '.85rem' }}>
                  {new Date(order.orderedAt).toLocaleString()}
                </span>
                <span style={{ fontWeight: 700 }}>₹{order.totalAmount.toFixed(2)}</span>

                {/* Status updater */}
                <select
                  value={order.status}
                  disabled={updating === order.id}
                  onChange={e => handleStatusChange(order.id, e.target.value)}
                  style={{
                    padding: '6px 10px', border: '1.5px solid #ddd', borderRadius: 6,
                    fontSize: '.85rem', cursor: 'pointer'
                  }}
                >
                  {STATUS_OPTIONS.map(s => <option key={s} value={s}>{s}</option>)}
                </select>

                <button className="btn btn-outline btn-sm"
                  onClick={() => setExpanded(expanded === order.id ? null : order.id)}>
                  {expanded === order.id ? 'Hide' : 'Details'}
                </button>
              </div>

              {expanded === order.id && (
                <table className="order-items-table">
                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>Qty</th>
                      <th>Unit Price</th>
                      <th>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    {order.items.map((item, i) => (
                      <tr key={i}>
                        <td>{item.productName}</td>
                        <td>{item.quantity}</td>
                        <td>₹{item.unitPrice.toFixed(2)}</td>
                        <td>₹{item.subtotal.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}

              <div className="order-footer">
                Total: ₹{order.totalAmount.toFixed(2)}
              </div>
            </div>
          ))}
        </div>
      )}
    </AdminLayout>
  );
}
