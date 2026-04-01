import React, { useEffect, useState } from 'react';
import AdminLayout from '../../components/AdminLayout';
import { getProducts, getBrands, getCategories, adminCreateProduct, adminUpdateProduct } from '../../api/api';
import api from '../../api/api';

const empty = { name: '', brandId: '', categoryId: '', packagingId: '', price: '', active: true };

export default function AdminProducts() {
  const [products, setProducts] = useState([]);
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [packaging, setPackaging] = useState([]);
  const [modal, setModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(empty);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const fetchProducts = async () => {
    const { data } = await getProducts({ page, size: 10 });
    setProducts(data.content);
    setTotalPages(data.totalPages);
  };

  useEffect(() => { fetchProducts(); }, [page]);
  useEffect(() => {
    getBrands().then(({ data }) => setBrands(data));
    getCategories().then(({ data }) => setCategories(data));
    api.get('/packaging').then(({ data }) => setPackaging(data));
  }, []);

  const openCreate = () => { setEditing(null); setForm(empty); setError(''); setModal(true); };
  const openEdit = (p) => {
    setEditing(p.id);
    setForm({ name: p.name, brandId: '', categoryId: '', packagingId: '', price: p.price, active: p.active });
    setError('');
    setModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true); setError('');
    try {
      const payload = { ...form, price: parseFloat(form.price) };
      if (editing) await adminUpdateProduct(editing, payload);
      else await adminCreateProduct(payload);
      setModal(false);
      fetchProducts();
    } catch (err) {
      setError(err.response?.data?.message || 'Error saving product');
    } finally { setSaving(false); }
  };

  return (
    <AdminLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2>Products</h2>
        <button className="btn btn-primary" onClick={openCreate}>+ Add Product</button>
      </div>

      <table className="data-table">
        <thead>
          <tr><th>#</th><th>Name</th><th>Brand</th><th>Category</th><th>Price</th><th>Stock</th><th>Status</th><th>Action</th></tr>
        </thead>
        <tbody>
          {products.map(p => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.brandName}</td>
              <td>{p.categoryName}</td>
              <td>₹{p.price.toFixed(2)}</td>
              <td>{p.stockQuantity}</td>
              <td><span className={`badge ${p.active ? 'badge-green' : 'badge-red'}`}>{p.active ? 'Active' : 'Inactive'}</span></td>
              <td><button className="btn btn-outline btn-sm" onClick={() => openEdit(p)}>Edit</button></td>
            </tr>
          ))}
        </tbody>
      </table>

      {totalPages > 1 && (
        <div className="pagination">
          <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Prev</button>
          {[...Array(totalPages)].map((_, i) => (
            <button key={i} className={i === page ? 'active' : ''} onClick={() => setPage(i)}>{i + 1}</button>
          ))}
          <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>Next →</button>
        </div>
      )}

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>{editing ? 'Edit Product' : 'Add Product'}</h3>
            {error && <div className="alert alert-error">{error}</div>}
            <form onSubmit={handleSave}>
              {[
                ['name', 'Product Name', 'text'],
                ['price', 'Price (₹)', 'number'],
              ].map(([field, label, type]) => (
                <div className="form-group" key={field}>
                  <label>{label}</label>
                  <input type={type} value={form[field]} min={type === 'number' ? 0.01 : undefined} step={type === 'number' ? '0.01' : undefined}
                    onChange={e => setForm({ ...form, [field]: e.target.value })} required />
                </div>
              ))}
              <div className="form-group">
                <label>Brand</label>
                <select value={form.brandId} onChange={e => setForm({ ...form, brandId: e.target.value })} required>
                  <option value="">Select brand</option>
                  {brands.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Category</label>
                <select value={form.categoryId} onChange={e => setForm({ ...form, categoryId: e.target.value })} required>
                  <option value="">Select category</option>
                  {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Packaging</label>
                <select value={form.packagingId} onChange={e => setForm({ ...form, packagingId: e.target.value })} required>
                  <option value="">Select packaging</option>
                  {packaging.map(pkg => <option key={pkg.id} value={pkg.id}>{pkg.type}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>
                  <input type="checkbox" checked={form.active}
                    onChange={e => setForm({ ...form, active: e.target.checked })} />
                  {' '}Active
                </label>
              </div>
              <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-outline" onClick={() => setModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AdminLayout>
  );
}
