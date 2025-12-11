import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
  timeout: 30000, // 30 segundos
  headers: {
    'Content-Type': 'application/json'
  }
})

// Interceptor para agregar el token
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Interceptor para manejar errores
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token inválido o expirado
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    } else if (error.code === 'ERR_NETWORK' || error.code === 'ECONNABORTED') {
      console.error('Error de conexión:', error.message)
      // Puedes mostrar un mensaje al usuario aquí
    }
    return Promise.reject(error)
  }
)

export default instance

