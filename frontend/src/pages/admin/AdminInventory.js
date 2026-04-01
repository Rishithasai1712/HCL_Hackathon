import React, { useEffect, useState } from 'react';
import AdminLayout from '../../components/AdminLayout';
import { adminGetInventory, adminUpdateInventory } from '../../api/api';

export default function AdminInventory() {
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [selected, setSelected] = useState(null);
  const [newQty, setNewQty] = useState('');
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState({ text: '', type: '' });

  const fetchInventory = async () => {
    setLoading(true);
    try {
      const { data } = await adminGetInventory();
      setInventory(data);
    } catch { } finally { setLoading(false); }
  };

  useEffect(() => { fetchInventory(); }, []);

  const openEdit = (item) => {
    setSelected(item);
    setNewQty(item.quantity);
    setModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await adminUpdateInventory(selected.productId, { quantity: parseInt(newQty) });
      setMsg({ text: 'Stock updated successfully!', type: 'success' });
      setModal(false);
      fetchInventory();
    } catch (err) {
      setMsg({ text: err.response?.data?.message || 'Update failed', type: 'error' });
    } finally { setSaving(false); }
    setTimeout(() => setMsg({ text: '', type: '' }), 3000);
  };

  const getStockBadge = (qty) => {
    if (qty === 0) return <span className="badge badge-red">Out of Stock</span>;
    if (qty < 20) return <span className="badge" style={{ background: '#fff3e0', color: '#e65100' }}>Low Stock</span>;
    return <span className="badge badge-green">In Stock</span>;
  };

  return (
    <AdminLayout>
      <h2>Inventory Management</h2>
      {msg.text && <div className={`alert ${msg.type === 'error' ? 'alert-error' : 'alert-success'}`}>{msg.text}</div>}

      {loading ? (
        <div className="loading">Loading inventory…</div>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Product ID</th>
              <th>Product Name</th>
              <th>Quantity</th>
              <th>Reserved</th>
              <th>Available</th>
              <th>Status</th>
              <th>Last Updated</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {inventory.map(item => (
              <tr key={item.productId}>
                <td>#{item.productId}</td>
                <td>{item.productName}</td>
                <td style={{ fontWeight: 600 }}>{item.quantity}</td>
                <td style={{ color: '#888' }}>{item.reservedQty}</td>
                <td style={{ fontWeight: 600, color: '#2e7d32' }}>{item.quantity - item.reservedQty}</td>
                <td>{getStockBadge(item.quantity - item.reservedQty)}</td>
                <td style={{ color: '#888', fontSize: '.85rem' }}>
                  {new Date(item.updatedAt).toLocaleString()}
                </td>
                <td>
                  <button className="btn btn-outline btn-sm" onClick={() => openEdit(item)}>
                    Adjust Stock
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>Adjust Stock — {selected?.productName}</h3>
            <p style={{ color: '#888', marginBottom: 20, fontSize: '.9rem' }}>
              Current stock: <strong>{selected?.quantity}</strong> &nbsp;|&nbsp;
              Reserved: <strong>{selected?.reservedQty}</strong>
            </p>
            <form onSubmit={handleSave}>
              <div className="form-group">
                <label>New Quantity</label>
                <input
                  type="number"
                  min="0"
                  value={newQty}
                  onChange={e => setNewQty(e.target.value)}
                  required
                  autoFocus
                />
              </div>
              <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-outline" onClick={() => setModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Saving…' : 'Update Stock'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AdminLayout>
  );
}
