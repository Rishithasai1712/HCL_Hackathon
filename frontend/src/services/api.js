import axios from "axios";
import { getToken } from "../utils/token";

const API = axios.create({
  baseURL: "http://localhost:8080/api",
});

 
API.interceptors.request.use((req) => {
  const token = getToken();
  if (token) {
    req.headers.Authorization = `Bearer ${token}`;
  }
  return req;
});

export default API;