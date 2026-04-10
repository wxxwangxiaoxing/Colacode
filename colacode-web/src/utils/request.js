import axios from 'axios'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:3001',
  timeout: 10000
})

service.interceptors.request.use((config) => {
  const token = localStorage.getItem('colacode_token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

service.interceptors.response.use(
  (response) => response.data,
  (error) => Promise.reject(error)
)

export default service
