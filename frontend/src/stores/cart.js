import { defineStore } from 'pinia'
import axios from '../config/axios'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    loading: false
  }),

  getters: {
    totalItems: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    subtotal: (state) => state.items.reduce((sum, item) => sum + (item.product.precio * item.quantity), 0),
    total: (state) => state.items.reduce((sum, item) => sum + (item.product.precio * item.quantity), 0)
  },

  actions: {
    async fetchCart() {
      this.loading = true
      try {
        const response = await axios.get('/api/cart')
        this.items = response.data.items || []
      } catch (error) {
        console.error('Error al cargar el carrito:', error)
      } finally {
        this.loading = false
      }
    },

    async addToCart(productId, quantity = 1) {
      try {
        await axios.post(`/api/cart/add/${productId}?qty=${quantity}`)
        await this.fetchCart()
        return { success: true }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Error al agregar al carrito'
        }
      }
    },

    async updateQuantity(productId, quantity) {
      try {
        await axios.post(`/api/cart/update/${productId}?qty=${quantity}`)
        await this.fetchCart()
        return { success: true }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Error al actualizar cantidad'
        }
      }
    },

    async removeItem(productId) {
      try {
        await axios.post(`/api/cart/remove/${productId}`)
        await this.fetchCart()
        return { success: true }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Error al eliminar producto'
        }
      }
    },

    async clearCart() {
      try {
        await axios.post('/api/cart/clear')
        this.items = []
        return { success: true }
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.error || 'Error al vaciar el carrito'
        }
      }
    }
  }
})

