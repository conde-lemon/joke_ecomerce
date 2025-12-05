import { defineStore } from 'pinia'
import axios from '../config/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.user,
    isAdmin: (state) => state.user?.roles?.includes('ADMIN') || false
  },

  actions: {
    async login(email, password) {
      try {
        const response = await axios.post('/api/auth/login', { email, password })
        this.user = response.data.user
        this.token = response.data.token

        // Guardar en localStorage
        localStorage.setItem('user', JSON.stringify(this.user))
        localStorage.setItem('token', this.token)

        return { success: true }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Error al iniciar sesión'
        }
      }
    },

    async register(userData) {
      try {
        const response = await axios.post('/api/auth/register', userData)
        return { success: true, data: response.data }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Error al registrarse'
        }
      }
    },

    async logout() {
      try {
        await axios.post('/api/auth/logout')
      } catch (error) {
        console.error('Error al cerrar sesión:', error)
      } finally {
        this.user = null
        this.token = null
        localStorage.removeItem('user')
        localStorage.removeItem('token')
      }
    },

    async checkAuth() {
      const user = localStorage.getItem('user')
      const token = localStorage.getItem('token')

      if (user && token) {
        this.user = JSON.parse(user)
        this.token = token

        // Verificar si el token es válido
        try {
          const response = await axios.get('/api/auth/me')
          this.user = response.data
        } catch (error) {
          this.logout()
        }
      }
    }
  }
})

