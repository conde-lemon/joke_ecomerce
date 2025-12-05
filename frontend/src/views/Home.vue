<template>
  <div class="container my-5">
    <!-- Banner Hero con imagen -->
    <div class="row mb-5">
      <div class="col-12">
        <div class="hero-banner position-relative">
          <img
            src="../img/banner2.jpg"
            alt="Banner Principal"
            class="w-100 rounded shadow-lg"
            style="max-height: 400px; object-fit: cover;"
          >
          <div class="banner-overlay position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center text-white rounded">
            <h1 class="display-3 fw-bold text-shadow">Bienvenido a AZ-Store</h1>
            <p class="lead mb-4 text-shadow">Los mejores productos al mejor precio</p>
            <router-link to="/catalog" class="btn btn-custom-primary btn-lg shadow">
              <i class="bi bi-grid-3x3-gap me-2"></i>Ver Catálogo
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Productos Destacados -->
    <div class="row">
      <div class="col-12 mb-4">
        <h2 class="text-center">Productos Destacados</h2>
      </div>
    </div>

    <div v-if="loading" class="text-center">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-else class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
      <div v-for="product in featuredProducts" :key="product.id" class="col">
        <div class="card h-100">
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
            <p class="card-text text-muted small">{{ truncate(product.descripcion, 80) }}</p>
            <div class="mt-auto">
              <p class="h5 text-custom-primary mb-3">S/ {{ formatPrice(product.precio) }}</p>
              <div class="d-grid gap-2">
                <router-link :to="`/product/${product.id}`" class="btn btn-outline-custom-secondary btn-sm">
                  Ver Detalles
                </router-link>
                <button @click="addToCart(product.id)" class="btn btn-custom-primary btn-sm">
                  Agregar al Carrito
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Mensaje si no hay productos -->
    <div v-if="!loading && featuredProducts.length === 0" class="alert alert-info text-center">
      No hay productos disponibles en este momento.
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from '../config/axios'
import { useCartStore } from '../stores/cart'

export default {
  name: 'Home',
  setup() {
    const cartStore = useCartStore()
    const featuredProducts = ref([])
    const loading = ref(false)

    const fetchFeaturedProducts = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/products?limit=8')
        featuredProducts.value = response.data
      } catch (error) {
        console.error('Error al cargar productos:', error)
      } finally {
        loading.value = false
      }
    }

    const addToCart = async (productId) => {
      const result = await cartStore.addToCart(productId, 1)
      if (result.success) {
        alert('Producto agregado al carrito')
      } else {
        alert(result.error)
      }
    }

    const formatPrice = (price) => {
      return parseFloat(price).toFixed(2)
    }

    const truncate = (text, length) => {
      if (!text) return ''
      return text.length > length ? text.substring(0, length) + '...' : text
    }

    const handleImageError = (event) => {
      // Si la imagen falla, ocultarla y el placeholder CSS se mostrará
      event.target.style.display = 'none'
    }

    onMounted(() => {
      fetchFeaturedProducts()
    })

    return {
      featuredProducts,
      loading,
      addToCart,
      formatPrice,
      truncate,
      handleImageError
    }
  }
}
</script>

<style scoped>
.hero-banner {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.banner-overlay {
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
}

.text-shadow {
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

.card-img-placeholder {
  height: 200px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  gap: 10px;
  border-radius: 8px 8px 0 0;
}

.card-img-placeholder i {
  font-size: 3rem;
  opacity: 0.9;
}

.card-img-placeholder span {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
  padding: 0 15px;
  max-width: 90%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card {
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}
</style>

