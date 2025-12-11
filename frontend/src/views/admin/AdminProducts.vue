<template>
  <div class="container-fluid my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2><i class="bi bi-box-seam"></i> Gestión de Productos</h2>
      <div>
        <button @click="descargarReportePDF" class="btn btn-primary me-2">
          <i class="bi bi-file-pdf"></i> Descargar Reporte PDF
        </button>
        <router-link to="/admin/products/new" class="btn btn-success">
          <i class="bi bi-plus-circle"></i> Nuevo Producto
        </router-link>
      </div>
    </div>

    <!-- Filtros -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <label class="form-label">Buscar producto</label>
            <input
              v-model="searchQuery"
              type="text"
              class="form-control"
              placeholder="Nombre del producto..."
              @input="filterProducts"
            >
          </div>
          <div class="col-md-2">
            <label class="form-label">Estado</label>
            <select v-model="statusFilter" class="form-select" @change="filterProducts">
              <option value="">Todos</option>
              <option value="true">Activos</option>
              <option value="false">Inactivos</option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label">Stock</label>
            <select v-model="stockFilter" class="form-select" @change="filterProducts">
              <option value="">Todos</option>
              <option value="low">Stock bajo (&lt; 10)</option>
              <option value="out">Sin stock</option>
              <option value="high">Stock alto (&gt; 50)</option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label">Precio mínimo</label>
            <input
              v-model="minPrice"
              type="number"
              class="form-control"
              placeholder="0.00"
              @input="filterProducts"
            >
          </div>
          <div class="col-md-2">
            <label class="form-label">Precio máximo</label>
            <input
              v-model="maxPrice"
              type="number"
              class="form-control"
              placeholder="999.99"
              @input="filterProducts"
            >
          </div>
          <div class="col-md-1">
            <label class="form-label">&nbsp;</label>
            <button @click="clearFilters" class="btn btn-outline-secondary d-block w-100" title="Limpiar filtros">
              <i class="bi bi-x-circle"></i>
            </button>
          </div>
        </div>
        <div class="row mt-2">
          <div class="col-md-6">
            <label class="form-label">Ordenar por</label>
            <select v-model="sortBy" class="form-select" @change="filterProducts">
              <option value="">Sin ordenar</option>
              <option value="nombre_asc">Nombre: A-Z</option>
              <option value="nombre_desc">Nombre: Z-A</option>
              <option value="precio_asc">Precio: Menor a Mayor</option>
              <option value="precio_desc">Precio: Mayor a Menor</option>
              <option value="stock_asc">Stock: Menor a Mayor</option>
              <option value="stock_desc">Stock: Mayor a Menor</option>
              <option value="id_desc">Más recientes</option>
            </select>
          </div>
          <div class="col-md-6 d-flex align-items-end">
            <div class="text-muted">
              <i class="bi bi-info-circle"></i> 
              Mostrando {{ paginatedProducts.length }} de {{ filteredProducts.length }} productos
            </div>
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
    const minPrice = ref('')
    const maxPrice = ref('')
    const sortBy = ref('')
    const currentPage = ref(1)
    const itemsPerPage = 10

    const fetchProducts = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/admin/products')
        products.value = response.data
      } catch (error) {
        console.error('Error al cargar productos:', error)
        alert('Error al cargar productos')
      } finally {
        loading.value = false
      }
    }

    const filteredProducts = computed(() => {
      let result = [...products.value]

      // Filtro por búsqueda
      if (searchQuery.value) {
        result = result.filter(p =>
          p.nombre.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          (p.descripcion && p.descripcion.toLowerCase().includes(searchQuery.value.toLowerCase()))
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
      } else if (stockFilter.value === 'high') {
        result = result.filter(p => p.stock > 50)
      }

      // Filtro por precio mínimo
      if (minPrice.value) {
        result = result.filter(p => parseFloat(p.precio) >= parseFloat(minPrice.value))
      }

      // Filtro por precio máximo
      if (maxPrice.value) {
        result = result.filter(p => parseFloat(p.precio) <= parseFloat(maxPrice.value))
      }

      // Ordenamiento
      if (sortBy.value) {
        switch (sortBy.value) {
          case 'nombre_asc':
            result.sort((a, b) => a.nombre.localeCompare(b.nombre))
            break
          case 'nombre_desc':
            result.sort((a, b) => b.nombre.localeCompare(a.nombre))
            break
          case 'precio_asc':
            result.sort((a, b) => parseFloat(a.precio) - parseFloat(b.precio))
            break
          case 'precio_desc':
            result.sort((a, b) => parseFloat(b.precio) - parseFloat(a.precio))
            break
          case 'stock_asc':
            result.sort((a, b) => a.stock - b.stock)
            break
          case 'stock_desc':
            result.sort((a, b) => b.stock - a.stock)
            break
          case 'id_desc':
            result.sort((a, b) => b.id - a.id)
            break
        }
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
    
    const clearFilters = () => {
      searchQuery.value = ''
      statusFilter.value = ''
      stockFilter.value = ''
      minPrice.value = ''
      maxPrice.value = ''
      sortBy.value = ''
      currentPage.value = 1
    }

    const toggleStatus = async (product) => {
      if (!confirm(`¿Cambiar estado de "${product.nombre}"?`)) return

      try {
        const updated = { ...product, activo: !product.activo }
        await axios.put(`/api/admin/products/${product.id}`, updated)
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
        await axios.delete(`/api/admin/products/${product.id}`)
        products.value = products.value.filter(p => p.id !== product.id)
        alert('Producto eliminado correctamente')
      } catch (error) {
        console.error('Error al eliminar producto:', error)
        alert('Error al eliminar producto')
      }
    }

    const descargarReportePDF = async () => {
      try {
        const response = await axios.get('/api/reports/productos/pdf', {
          responseType: 'blob'
        })

        // Crear un enlace temporal para descargar el archivo
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', 'reporte_productos.pdf')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('Error al descargar reporte:', error)
        alert('Error al descargar el reporte PDF')
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
      minPrice,
      maxPrice,
      sortBy,
      currentPage,
      filteredProducts,
      paginatedProducts,
      totalPages,
      filterProducts,
      clearFilters,
      toggleStatus,
      deleteProduct,
      descargarReportePDF,
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

