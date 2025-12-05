<template>
  <div class="container my-4">
    <h3 class="mb-4">Catálogo de Productos</h3>

    <!-- Filtros y búsqueda -->
    <div class="row mb-4">
      <div class="col-md-8">
        <input
          v-model="searchQuery"
          type="text"
          class="form-control"
          placeholder="Buscar productos..."
          @input="searchProducts"
        >
      </div>
      <div class="col-md-4">
        <select v-model="sortBy" class="form-select" @change="fetchProducts">
          <option value="">Ordenar por...</option>
          <option value="precio_asc">Precio: Menor a Mayor</option>
          <option value="precio_desc">Precio: Mayor a Menor</option>
          <option value="nombre_asc">Nombre: A-Z</option>
        </select>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <!-- Grid de productos -->
    <div v-else class="row row-cols-1 row-cols-md-3 row-cols-lg-4 g-4">
      <div v-for="product in products" :key="product.id" class="col">
        <div class="card h-100 shadow-sm">
          <!-- Imagen o Placeholder -->
          <img
            v-if="product.imageUrl"
            :src="product.imageUrl"
            class="card-img-top"
            :alt="product.nombre"
            style="height: 200px; object-fit: cover;"
            @error="handleImageError"
          >
          <div
            v-else
            class="card-img-placeholder"
          >
            <i class="bi bi-box-seam"></i>
            <span>{{ product.nombre }}</span>
          </div>

          <div class="card-body d-flex flex-column">
            <h5 class="card-title">{{ product.nombre }}</h5>
            <p class="card-text text-muted small flex-grow-1">
              {{ truncate(product.descripcion, 60) }}
            </p>
            <div class="mt-auto">
              <p class="h5 text-custom-primary mb-2">S/ {{ formatPrice(product.precio) }}</p>
              <p class="text-muted small mb-3">
                Stock: {{ product.stock }}
              </p>
              <div class="d-grid gap-2">
                <router-link
                  :to="`/product/${product.id}`"
                  class="btn btn-outline-custom-secondary btn-sm"
                >
                  Ver Detalles
                </router-link>
                <button
                  @click="addToCart(product.id)"
                  class="btn btn-custom-primary btn-sm"
                  :disabled="product.stock === 0"
                >
                  {{ product.stock === 0 ? 'Sin Stock' : 'Agregar' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sin resultados -->
    <div v-if="!loading && products.length === 0" class="alert alert-info text-center">
      No se encontraron productos.
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from '../config/axios'
import { useCartStore } from '../stores/cart'

export default {
  name: 'Catalog',
  setup() {
    const cartStore = useCartStore()
    const products = ref([])
    const loading = ref(false)
    const searchQuery = ref('')
    const sortBy = ref('')

    const fetchProducts = async () => {
      loading.value = true
      try {
        const params = {}
        if (searchQuery.value) params.search = searchQuery.value
        if (sortBy.value) params.sort = sortBy.value

        const response = await axios.get('/api/products', { params })
        products.value = response.data
      } catch (error) {
        console.error('Error al cargar productos:', error)
      } finally {
        loading.value = false
      }
    }

    let searchTimeout
    const searchProducts = () => {
      clearTimeout(searchTimeout)
      searchTimeout = setTimeout(() => {
        fetchProducts()
      }, 500)
    }

    const addToCart = async (productId) => {
      const success = await cartStore.addToCart(productId, 1)
      if (success) {
        alert('Producto agregado al carrito')
      }
    }

    const formatPrice = (price) => parseFloat(price).toFixed(2)
    const truncate = (text, length) => {
      if (!text) return ''
      return text.length > length ? text.substring(0, length) + '...' : text
    }

    onMounted(fetchProducts)

    return {
      products,
      loading,
      searchQuery,
      sortBy,
      fetchProducts,
      searchProducts,
      addToCart,
      formatPrice,
      truncate
    }
  }
}
</script>

