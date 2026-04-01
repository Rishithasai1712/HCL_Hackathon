import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProducts, getBrands, getCategories, addToCart } from '../../api/api';
import { useAuth } from '../../context/AuthContext';

const EMOJI = { Pizza: '🍕', Drinks: '🥤', Bread: '🍞', default: '🛍️' };
const getEmoji = (name) => Object.entries(EMOJI).find(([k]) => name?.includes(k))?.[1] || EMOJI.default;

export default function ProductList() {
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [filters, setFilters] = useState({ brand: '', category: '' });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(null);
  const [msg, setMsg] = useState('');

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 8 };
      if (filters.brand) params.brand = filters.brand;
      if (filters.category) params.category = filters.category;
      const { data } = await getProducts(params);
      setProducts(data.content);
      setTotalPages(data.totalPages);
    } catch { } finally { setLoading(false); }
  }, [page, filters]);

  useEffect(() => { fetchProducts(); }, [fetchProducts]);

  useEffect(() => {
    getBrands().then(({ data }) => setBrands(data));
    getCategories().then(({ data }) => setCategories(data));
  }, []);

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
    setPage(0);
  };

  const handleAddToCart = async (product) => {
    if (!isLoggedIn) { navigate('/login'); return; }
    setAdding(product.id);
    try {
      await addToCart({ productId: product.id, quantity: 1 });
      setMsg(`${product.name} added to cart!`);
      setTimeout(() => setMsg(''), 2000);
    } catch (err) {
      setMsg(err.response?.data?.message || 'Error adding to cart');
      setTimeout(() => setMsg(''), 3000);
    } finally { setAdding(null); }
  };

  return (
    <div className="container">
      <div className="products-header">
        <h1>Our Products</h1>
        <div className="filters">
          <select name="brand" value={filters.brand} onChange={handleFilterChange}>
            <option value="">All Brands</option>
            {Array.isArray(brands) && brands.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
          </select>
          <select name="category" value={filters.category} onChange={handleFilterChange}>
            <option value="">All Categories</option>
            {Array.isArray(categories) && categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
      </div>

      {msg && <div className="alert alert-success">{msg}</div>}

      {loading ? (
        <div className="loading">Loading products…</div>
      ) : products.length === 0 ? (
        <div className="empty-state"><p>No products found.</p></div>
      ) : (
        <>
          <div className="products-grid">
            {products.map(p => (
              <div key={p.id} className="product-card">
                <div className="product-img">
                  <span>{getEmoji(p.brandName)}</span>
                </div>
                <div className="product-body">
                  <h3>{p.name}</h3>
                  <p className="meta">{p.brandName} · {p.categoryName} · {p.packagingType}</p>
                  <p className="price">₹{p.price.toFixed(2)}</p>
                  {p.stockQuantity > 0 ? (
                    <>
                      <span className="badge badge-green" style={{ marginBottom: 10, display: 'inline-block' }}>
                        In Stock ({p.stockQuantity})
                      </span>
                      <br />
                      <button className="btn btn-accent btn-sm" style={{ width: '100%' }}
                        onClick={() => handleAddToCart(p)}
                        disabled={adding === p.id}>
                        {adding === p.id ? 'Adding…' : '+ Add to Cart'}
                      </button>
                    </>
                  ) : (
                    <span className="badge badge-red">Out of Stock</span>
                  )}
                </div>
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Prev</button>
              {[...Array(totalPages)].map((_, i) => (
                <button key={i} className={i === page ? 'active' : ''} onClick={() => setPage(i)}>{i + 1}</button>
              ))}
              <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>Next →</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
