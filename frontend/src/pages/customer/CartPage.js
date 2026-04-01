import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCart, updateCartItem, removeCartItem, placeOrder } from '../../api/api';

export default function CartPage() {
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [placing, setPlacing] = useState(false);
  const [msg, setMsg] = useState({ text: '', type: '' });

  const fetchCart = async () => {
    setLoading(true);
    try { const { data } = await getCart(); setCart(data); }
    catch { } finally { setLoading(false); }
  };

  useEffect(() => { fetchCart(); }, []);

  const handleQty = async (item, delta) => {
    const newQty = item.quantity + delta;
    if (newQty < 1) return handleRemove(item.itemId);
    try {
      const { data } = await updateCartItem(item.itemId, { productId: item.productId, quantity: newQty });
      setCart(data);
    } catch (err) { setMsg({ text: err.response?.data?.message || 'Error', type: 'error' }); }
  };

  const handleRemove = async (itemId) => {
    try {
      await removeCartItem(itemId);
      await fetchCart();
    } catch { }
  };

  const handlePlaceOrder = async () => {
    setPlacing(true);
    try {
      await placeOrder();
      setMsg({ text: 'Order placed successfully! 🎉', type: 'success' });
      await fetchCart();
      setTimeout(() => navigate('/orders'), 2000);
    } catch (err) {
      setMsg({ text: err.response?.data?.message || 'Failed to place order', type: 'error' });
    } finally { setPlacing(false); }
  };

  if (loading) return <div className="loading">Loading cart…</div>;

  const items = cart?.items || [];

  return (
    <div className="container">
      <div className="page-header"><h1>Your Cart</h1></div>
      {msg.text && (
        <div className={`alert ${msg.type === 'error' ? 'alert-error' : 'alert-success'}`}>{msg.text}</div>
      )}

      {items.length === 0 ? (
        <div className="empty-state">
          <p>Your cart is empty.</p>
          <button className="btn btn-primary" onClick={() => navigate('/')}>Browse Products</button>
        </div>
      ) : (
        <div className="cart-layout">
          <div className="card">
            {items.map(item => (
              <div key={item.itemId} className="cart-item-row">
                <div className="cart-item-icon">🛍️</div>
                <div className="cart-item-info">
                  <h4>{item.productName}</h4>
                  <span>₹{item.unitPrice.toFixed(2)} each</span>
                </div>
                <div className="qty-controls">
                  <button onClick={() => handleQty(item, -1)}>−</button>
                  <span>{item.quantity}</span>
                  <button onClick={() => handleQty(item, +1)}>+</button>
                </div>
                <span style={{ minWidth: 70, textAlign: 'right', fontWeight: 600 }}>
                  ₹{item.subtotal.toFixed(2)}
                </span>
                <button className="btn btn-danger btn-sm" onClick={() => handleRemove(item.itemId)}>✕</button>
              </div>
            ))}
          </div>

          <div className="card cart-total-card">
            <h3>Order Summary</h3>
            {items.map(item => (
              <div key={item.itemId} className="total-row">
                <span>{item.productName} × {item.quantity}</span>
                <span>₹{item.subtotal.toFixed(2)}</span>
              </div>
            ))}
            <div className="total-row grand">
              <span>Total</span>
              <span>₹{cart.grandTotal.toFixed(2)}</span>
            </div>
            <button className="btn btn-accent" style={{ width: '100%', marginTop: 16 }}
              onClick={handlePlaceOrder} disabled={placing}>
              {placing ? 'Placing Order…' : 'Place Order'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
