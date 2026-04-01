import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          const { data } = await axios.post('/api/auth/refresh', { refreshToken });
          localStorage.setItem('accessToken', data.accessToken);
          localStorage.setItem('refreshToken', data.refreshToken);
          original.headers.Authorization = `Bearer ${data.accessToken}`;
          return api(original);
        } catch {
          localStorage.clear();
          window.location.href = '/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

// ---- Auth ----
export const register = (data) => api.post('/auth/register', data);
export const login = (data) => api.post('/auth/login', data);

// ---- Catalog ----
export const getBrands = () => api.get('/brands');
export const getCategories = () => api.get('/categories');
export const getProducts = (params) => api.get('/products', { params });
export const getProduct = (id) => api.get(`/products/${id}`);

// ---- Cart ----
export const getCart = () => api.get('/cart');
export const addToCart = (data) => api.post('/cart/items', data);
export const updateCartItem = (id, data) => api.put(`/cart/items/${id}`, data);
export const removeCartItem = (id) => api.delete(`/cart/items/${id}`);

// ---- Orders ----
export const placeOrder = () => api.post('/orders');
export const getOrders = () => api.get('/orders');
export const getOrder = (id) => api.get(`/orders/${id}`);

// ---- Admin ----
export const adminCreateProduct = (data) => api.post('/admin/products', data);
export const adminUpdateProduct = (id, data) => api.put(`/admin/products/${id}`, data);
export const adminGetInventory = () => api.get('/admin/inventory');
export const adminUpdateInventory = (productId, data) => api.put(`/admin/inventory/${productId}`, data);
export const adminGetOrders = () => api.get('/admin/orders');
export const adminUpdateOrderStatus = (id, data) => api.put(`/admin/orders/${id}/status`, data);

export default api;
