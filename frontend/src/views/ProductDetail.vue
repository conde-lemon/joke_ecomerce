<template>
  <div class="container my-4">
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-else-if="product" class="row">
      <!-- Imagen del producto -->
      <div class="col-md-6">
        <img
          :src="product.imageUrl"
          :alt="product.nombre"
          class="img-fluid rounded shadow"
        >
      </div>

      <!-- Información del producto -->
      <div class="col-md-6">
        <h2 class="mb-3">{{ product.nombre }}</h2>

        <div class="mb-3">
          <span class="badge bg-primary me-2">
            Stock: {{ product.stock }}
          </span>
          <span v-if="product.activo" class="badge bg-success">Disponible</span>
          <span v-else class="badge bg-danger">No Disponible</span>
        </div>

        <h3 class="text-primary mb-4">S/ {{ formatPrice(product.precio) }}</h3>

        <div class="mb-4">
          <h5>Descripción</h5>
          <p class="text-muted">{{ product.descripción }}</p>
        </div>

        <!-- Selector de cantidad -->
        <div class="mb-4">
          <label class="form-label fw-bold">Cantidad:</label>
          <div class="input-group" style="max-width: 200px;">
            <button
              class="btn btn-outline-secondary"
              @click="quantity = Math.max(1, quantity - 1)"
            >
              −
            </button>
            <input
              v-model.number="quantity"
              type="number"
              class="form-control text-center"
              min="1"
              :max="product.stock"
            >
            <button
              class="btn btn-outline-secondary"
              @click="quantity = Math.min(product.stock, quantity + 1)"
            >
              ＋
            </button>
          </div>
        </div>

        <!-- Botones de acción -->
        <div class="d-grid gap-2">
          <button
            @click="addToCart"
            class="btn btn-primary btn-lg"
            :disabled="product.stock === 0 || !product.activo"
          >
            <i class="bi bi-cart-plus"></i>
            {{ product.stock === 0 ? 'Sin Stock' : 'Agregar al Carrito' }}
          </button>
          <router-link to="/catalog" class="btn btn-outline-secondary">
            ← Volver al Catálogo
          </router-link>
        </div>
      </div>
    </div>

    <div v-else class="alert alert-warning">
      Producto no encontrado.
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../config/axios'
import { useCartStore } from '../stores/cart'

export default {
  name: 'ProductDetail',
  setup() {
    const route = useRoute()
    const cartStore = useCartStore()

    const product = ref(null)
    const loading = ref(false)
    const quantity = ref(1)

    const fetchProduct = async () => {
      loading.value = true
      try {
        const response = await axios.get(`/api/products/${route.params.id}`)
        product.value = response.data
      } catch (error) {
        console.error('Error al cargar producto:', error)
      } finally {
        loading.value = false
      }
    }

    const addToCart = async () => {
      const success = await cartStore.addToCart(product.value.id, quantity.value)
      if (success) {
        alert(`${quantity.value} ${quantity.value === 1 ? 'producto agregado' : 'productos agregados'} al carrito`)
        quantity.value = 1
      }
    }

    const formatPrice = (price) => parseFloat(price).toFixed(2)

    onMounted(fetchProduct)

    return {
      product,
      loading,
      quantity,
      addToCart,
      formatPrice
    }
  }
}
</script>

