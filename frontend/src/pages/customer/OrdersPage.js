import React, { useEffect, useState } from 'react';
import { getOrders } from '../../api/api';

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(null);

  useEffect(() => {
    getOrders()
      .then(({ data }) => setOrders(data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Loading orders…</div>;

  return (
    <div className="container">
      <div className="page-header"><h1>My Orders</h1></div>
      {orders.length === 0 ? (
        <div className="empty-state"><p>You haven't placed any orders yet.</p></div>
      ) : (
        <div className="orders-list">
          {orders.map(order => (
            <div key={order.id} className="card order-card">
              <div className="order-header">
                <h3>Order #{order.id}</h3>
                <span className={`status-badge status-${order.status}`}>{order.status}</span>
                <span style={{ color: '#888', fontSize: '.85rem' }}>
                  {new Date(order.orderedAt).toLocaleString()}
                </span>
                <span style={{ fontWeight: 700 }}>₹{order.totalAmount.toFixed(2)}</span>
                <button className="btn btn-outline btn-sm"
                  onClick={() => setExpanded(expanded === order.id ? null : order.id)}>
                  {expanded === order.id ? 'Hide Items' : 'View Items'}
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
              <div className="order-footer">Total: ₹{order.totalAmount.toFixed(2)}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
