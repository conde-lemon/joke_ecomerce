<template>
  <div class="container-fluid my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2><i class="bi bi-box-seam"></i> Gestión de Productos</h2>
      <router-link to="/admin/products/new" class="btn btn-success">
        <i class="bi bi-plus-circle"></i> Nuevo Producto
      </router-link>
    </div>

    <!-- Filtros -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row">
          <div class="col-md-6">
            <input
              v-model="searchQuery"
              type="text"
              class="form-control"
              placeholder="Buscar por nombre..."
              @input="filterProducts"
            >
          </div>
          <div class="col-md-3">
            <select v-model="statusFilter" class="form-select" @change="filterProducts">
              <option value="">Todos los estados</option>
              <option value="true">Activos</option>
              <option value="false">Inactivos</option>
            </select>
          </div>
          <div class="col-md-3">
            <select v-model="stockFilter" class="form-select" @change="filterProducts">
              <option value="">Todo el stock</option>
              <option value="low">Stock bajo (&lt; 10)</option>
              <option value="out">Sin stock</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Tabla de Productos -->
    <div class="card">
      <div class="card-body">
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
        </div>

        <div v-else-if="filteredProducts.length === 0" class="text-center py-5 text-muted">
          <i class="bi bi-inbox fs-1"></i>
          <p class="mt-3">No se encontraron productos</p>
        </div>

        <div v-else class="table-responsive">
          <table class="table table-hover">
            <thead class="table-dark">
              <tr>
                <th>ID</th>
                <th>Imagen</th>
                <th>Nombre</th>
                <th>Precio</th>
                <th>Stock</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="product in paginatedProducts" :key="product.id">
                <td>{{ product.id }}</td>
                <td>
                  <img
                    v-if="product.imageUrl"
                    :src="product.imageUrl"
                    :alt="product.nombre"
                    class="img-thumbnail"
                    style="width: 50px; height: 50px; object-fit: cover;"
                    @error="e => e.target.style.display = 'none'"
                  >
                  <div
                    v-if="!product.imageUrl"
                    class="img-placeholder"
                  >
                    <i class="bi bi-box"></i>
                  </div>
                </td>
                <td>
                  <strong>{{ product.nombre }}</strong>
                  <br>
                  <small class="text-muted">{{ truncate(product.descripcion, 50) }}</small>
                </td>
                <td>
                  <strong class="text-primary">S/ {{ formatPrice(product.precio) }}</strong>
                </td>
                <td>
                  <span
                    class="badge"
                    :class="getStockBadgeClass(product.stock)"
                  >
                    {{ product.stock }} unidades
                  </span>
                </td>
                <td>
                  <span class="badge" :class="product.activo ? 'bg-success' : 'bg-secondary'">
                    {{ product.activo ? 'Activo' : 'Inactivo' }}
                  </span>
                </td>
                <td>
                  <div class="btn-group btn-group-sm">
                    <router-link
                      :to="`/admin/products/edit/${product.id}`"
                      class="btn btn-outline-primary"
                      title="Editar"
                    >
                      <i class="bi bi-pencil"></i>
                    </router-link>
                    <button
                      @click="toggleStatus(product)"
                      class="btn btn-outline-warning"
                      title="Cambiar estado"
                    >
                      <i class="bi bi-toggle-on"></i>
                    </button>
                    <button
                      @click="deleteProduct(product)"
                      class="btn btn-outline-danger"
                      title="Eliminar"
                    >
                      <i class="bi bi-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- Paginación -->
          <nav v-if="totalPages > 1">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <a class="page-link" href="#" @click.prevent="currentPage--">Anterior</a>
              </li>
              <li
                v-for="page in totalPages"
                :key="page"
                class="page-item"
                :class="{ active: currentPage === page }"
              >
                <a class="page-link" href="#" @click.prevent="currentPage = page">{{ page }}</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <a class="page-link" href="#" @click.prevent="currentPage++">Siguiente</a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import axios from '../../config/axios'

export default {
  name: 'AdminProducts',
  setup() {
    const products = ref([])
    const loading = ref(false)
    const searchQuery = ref('')
    const statusFilter = ref('')
    const stockFilter = ref('')
    const currentPage = ref(1)
    const itemsPerPage = 10

    const fetchProducts = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/products')
        products.value = response.data
      } catch (error) {
        console.error('Error al cargar productos:', error)
        alert('Error al cargar productos')
      } finally {
        loading.value = false
      }
    }

    const filteredProducts = computed(() => {
      let result = products.value

      // Filtro por búsqueda
      if (searchQuery.value) {
        result = result.filter(p =>
          p.nombre.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      // Filtro por estado
      if (statusFilter.value !== '') {
        const isActive = statusFilter.value === 'true'
        result = result.filter(p => p.activo === isActive)
      }

      // Filtro por stock
      if (stockFilter.value === 'low') {
        result = result.filter(p => p.stock > 0 && p.stock < 10)
      } else if (stockFilter.value === 'out') {
        result = result.filter(p => p.stock === 0)
      }

      return result
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredProducts.value.length / itemsPerPage)
    })

    const paginatedProducts = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage
      const end = start + itemsPerPage
      return filteredProducts.value.slice(start, end)
    })

    const filterProducts = () => {
      currentPage.value = 1
    }

    const toggleStatus = async (product) => {
      if (!confirm(`¿Cambiar estado de "${product.nombre}"?`)) return

      try {
        const updated = { ...product, activo: !product.activo }
        await axios.put(`/api/products/${product.id}`, updated)
        product.activo = !product.activo
        alert('Estado actualizado correctamente')
      } catch (error) {
        console.error('Error al actualizar estado:', error)
        alert('Error al actualizar estado')
      }
    }

    const deleteProduct = async (product) => {
      if (!confirm(`¿Eliminar el producto "${product.nombre}"? Esta acción no se puede deshacer.`)) return

      try {
        await axios.delete(`/api/products/${product.id}`)
        products.value = products.value.filter(p => p.id !== product.id)
        alert('Producto eliminado correctamente')
      } catch (error) {
        console.error('Error al eliminar producto:', error)
        alert('Error al eliminar producto')
      }
    }

    const formatPrice = (price) => {
      return parseFloat(price || 0).toFixed(2)
    }

    const truncate = (text, length) => {
      if (!text) return ''
      return text.length > length ? text.substring(0, length) + '...' : text
    }

    const getStockBadgeClass = (stock) => {
      if (stock === 0) return 'bg-danger'
      if (stock < 10) return 'bg-warning text-dark'
      return 'bg-success'
    }

    onMounted(() => {
      fetchProducts()
    })

    return {
      products,
      loading,
      searchQuery,
      statusFilter,
      stockFilter,
      currentPage,
      filteredProducts,
      paginatedProducts,
      totalPages,
      filterProducts,
      toggleStatus,
      deleteProduct,
      formatPrice,
      truncate,
      getStockBadgeClass
    }
  }
}
</script>

<style scoped>
.img-thumbnail {
  border-radius: 8px;
}

.img-placeholder {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: white;
  font-size: 1.5rem;
}

.table-hover tbody tr:hover {
  background-color: #f8f9fa;
}
</style>

