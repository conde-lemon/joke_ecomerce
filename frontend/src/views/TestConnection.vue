<template>
  <div class="container my-5">
    <div class="row mb-4">
      <div class="col-12">
        <div class="card border-primary">
          <div class="card-header bg-primary text-white">
            <h3 class="mb-0">
              <i class="bi bi-server"></i> Test de Conexión Backend → Frontend
            </h3>
          </div>
          <div class="card-body">
            <p class="mb-3">Esta página verifica que el backend pueda extraer y enviar productos al frontend.</p>

            <!-- Estado de la conexión -->
            <div class="alert" :class="statusClass" role="alert">
              <strong>Estado:</strong> {{ connectionStatus }}
            </div>

            <!-- Botones de acción -->
            <div class="d-flex gap-2 mb-3">
              <button @click="testConnection" class="btn btn-primary" :disabled="loading">
                <i class="bi bi-arrow-repeat"></i> Probar Conexión
              </button>
              <button @click="loadProducts" class="btn btn-success" :disabled="loading">
                <i class="bi bi-download"></i> Cargar Productos
              </button>
              <button @click="clearData" class="btn btn-warning" :disabled="loading">
                <i class="bi bi-trash"></i> Limpiar
              </button>
            </div>

            <!-- Spinner de carga -->
            <div v-if="loading" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
              </div>
              <p class="mt-2">Conectando al backend...</p>
            </div>

            <!-- Información de respuesta -->
            <div v-if="response && !loading" class="mt-3">
              <h5>📊 Respuesta del Backend:</h5>
              <div class="table-responsive">
                <table class="table table-sm table-bordered">
                  <tbody>
                    <tr>
                      <th style="width: 200px;">Endpoint:</th>
                      <td><code>{{ response.endpoint }}</code></td>
                    </tr>
                    <tr>
                      <th>Método:</th>
                      <td>{{ response.method }}</td>
                    </tr>
                    <tr>
                      <th>Código de Estado:</th>
                      <td>
                        <span class="badge" :class="response.status === 200 ? 'bg-success' : 'bg-danger'">
                          {{ response.status }}
                        </span>
                      </td>
                    </tr>
                    <tr>
                      <th>Tiempo de Respuesta:</th>
                      <td>{{ response.time }}ms</td>
                    </tr>
                    <tr>
                      <th>Total de Productos:</th>
                      <td>
                        <span class="badge bg-primary fs-6">{{ products.length }}</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Mensaje de error -->
            <div v-if="error && !loading" class="alert alert-danger mt-3">
              <h5>❌ Error de Conexión</h5>
              <p><strong>Mensaje:</strong> {{ error.message }}</p>
              <p v-if="error.details"><strong>Detalles:</strong> {{ error.details }}</p>
              <hr>
              <div>
                <p class="mb-2"><strong>Posibles causas:</strong></p>
                <ul class="mb-0">
                  <li>El backend no está corriendo (Spring Boot)</li>
                  <li>Puerto incorrecto (verifica que sea 8080)</li>
                  <li>CORS no configurado correctamente</li>
                  <li>Base de datos no conectada</li>
                  <li>Spring Security bloqueando el endpoint</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Lista de productos -->
    <div v-if="products.length > 0" class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header bg-success text-white">
            <h4 class="mb-0">
              <i class="bi bi-check-circle"></i>
              Productos Extraídos Exitosamente ({{ products.length }})
            </h4>
          </div>
          <div class="card-body">
            <!-- Tabla de productos -->
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-dark">
                  <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Descripción</th>
                    <th>Precio</th>
                    <th>Stock</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="product in products" :key="product.id">
                    <td>{{ product.id }}</td>
                    <td><strong>{{ product.nombre }}</strong></td>
                    <td>{{ truncate(product.descripcion, 50) }}</td>
                    <td class="text-primary">
                      <strong>S/ {{ formatPrice(product.precio) }}</strong>
                    </td>
                    <td>
                      <span
                        class="badge"
                        :class="getStockBadge(product.stock)"
                      >
                        {{ product.stock }} unidades
                      </span>
                    </td>
                    <td>
                      <span
                        class="badge"
                        :class="product.activo ? 'bg-success' : 'bg-secondary'"
                      >
                        {{ product.activo ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- JSON Raw -->
            <div class="mt-4">
              <button
                class="btn btn-sm btn-outline-secondary"
                @click="showJson = !showJson"
              >
                <i class="bi bi-code-square"></i>
                {{ showJson ? 'Ocultar' : 'Mostrar' }} JSON Raw
              </button>

              <div v-if="showJson" class="mt-3">
                <pre class="bg-dark text-light p-3 rounded"><code>{{ JSON.stringify(products, null, 2) }}</code></pre>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Estadísticas -->
    <div v-if="products.length > 0" class="row mt-4">
      <div class="col-md-3">
        <div class="card text-center bg-primary text-white">
          <div class="card-body">
            <h2>{{ products.length }}</h2>
            <p class="mb-0">Total Productos</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center bg-success text-white">
          <div class="card-body">
            <h2>{{ activeProducts }}</h2>
            <p class="mb-0">Productos Activos</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center bg-warning text-dark">
          <div class="card-body">
            <h2>{{ lowStockProducts }}</h2>
            <p class="mb-0">Stock Bajo (&lt; 10)</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center bg-info text-white">
          <div class="card-body">
            <h2>S/ {{ totalValue }}</h2>
            <p class="mb-0">Valor Total</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import axios from '../config/axios'

export default {
  name: 'TestConnectionView',
  setup() {
    const products = ref([])
    const loading = ref(false)
    const error = ref(null)
    const response = ref(null)
    const showJson = ref(false)

    const connectionStatus = computed(() => {
      if (loading.value) return 'Conectando...'
      if (error.value) return 'Error de Conexión'
      if (response.value && response.value.status === 200) return 'Conectado ✅'
      return 'Sin probar'
    })

    const statusClass = computed(() => {
      if (loading.value) return 'alert-info'
      if (error.value) return 'alert-danger'
      if (response.value && response.value.status === 200) return 'alert-success'
      return 'alert-secondary'
    })

    const activeProducts = computed(() => {
      return products.value.filter(p => p.activo).length
    })

    const lowStockProducts = computed(() => {
      return products.value.filter(p => p.stock < 10).length
    })

    const totalValue = computed(() => {
      const total = products.value.reduce((sum, p) => {
        return sum + (parseFloat(p.precio) * p.stock)
      }, 0)
      return formatPrice(total)
    })

    const testConnection = async () => {
      loading.value = true
      error.value = null
      response.value = null
      products.value = []

      const startTime = Date.now()

      try {
        const res = await axios.get('/api/products')
        const endTime = Date.now()

        // Verificar que la respuesta sea un array y no HTML
        if (!Array.isArray(res.data)) {
          console.error('❌ Respuesta no es un array:', typeof res.data)
          console.error('Primeros 200 caracteres:', String(res.data).substring(0, 200))

          // Detectar si es HTML
          if (typeof res.data === 'string' && res.data.includes('<!doctype html>')) {
            alert(`❌ Error: El backend devolvió HTML (página de login)\n\nSpring Security está bloqueando el endpoint.\n\nVerifica WebSecurityConfig.java`)
            throw new Error('El backend devolvió HTML (página de login). Spring Security está bloqueando el endpoint.')
          }

          alert(`❌ Error: Respuesta inválida\n\nSe esperaba un array pero se recibió: ${typeof res.data}`)
          throw new Error(`Respuesta inválida del servidor. Se esperaba un array pero se recibió: ${typeof res.data}`)
        }

        response.value = {
          endpoint: '/api/products',
          method: 'GET',
          status: res.status,
          time: endTime - startTime
        }

        console.log('✅ Conexión exitosa al backend')
        console.log('Productos recibidos:', res.data.length)
        alert(`✅ Conexión exitosa!\n\n${res.data.length} productos encontrados\nTiempo: ${endTime - startTime}ms`)
      } catch (err) {
        console.error('❌ Error de conexión:', err)

        let errorMessage = err.message
        let errorDetails = err.response?.data?.message || err.response?.statusText

        if (err.message.includes('HTML')) {
          errorMessage = 'Spring Security está bloqueando el endpoint'
          errorDetails = 'Configura WebSecurityConfig para permitir acceso público a /api/products/**'
        }

        error.value = {
          message: errorMessage,
          details: errorDetails
        }

        if (!err.message.includes('HTML')) {
          alert(`❌ Error de conexión:\n${err.message}\n\nVerifica que el backend esté corriendo en http://localhost:8080`)
        }
      } finally {
        loading.value = false
      }
    }

    const loadProducts = async () => {
      loading.value = true
      error.value = null
      response.value = null

      const startTime = Date.now()

      try {
        const res = await axios.get('/api/products')
        const endTime = Date.now()

        // Verificar que la respuesta sea un array y no HTML
        if (!Array.isArray(res.data)) {
          console.error('❌ Respuesta no es un array:', typeof res.data, res.data)

          // Detectar si es HTML (Spring Security redirección)
          if (typeof res.data === 'string' && res.data.includes('<!doctype html>')) {
            throw new Error('El backend devolvió HTML (página de login). Spring Security está bloqueando el endpoint.')
          }

          throw new Error(`Respuesta inválida del servidor. Se esperaba un array pero se recibió: ${typeof res.data}`)
        }

        products.value = res.data
        response.value = {
          endpoint: '/api/products',
          method: 'GET',
          status: res.status,
          time: endTime - startTime
        }

        console.log('✅ Productos cargados:', products.value.length, 'productos')
      } catch (err) {
        console.error('❌ Error al cargar productos:', err)

        // Mensaje de error más específico
        let errorMessage = err.message
        let errorDetails = err.response?.data?.message || err.response?.statusText

        if (err.message.includes('HTML')) {
          errorMessage = 'Spring Security está bloqueando el endpoint'
          errorDetails = 'El backend requiere autenticación. Necesitas configurar SecurityConfig para permitir acceso público a /api/products'
        }

        error.value = {
          message: errorMessage,
          details: errorDetails
        }
      } finally {
        loading.value = false
      }
    }

    const clearData = () => {
      products.value = []
      error.value = null
      response.value = null
      showJson.value = false
    }

    const formatPrice = (price) => {
      return parseFloat(price || 0).toFixed(2)
    }

    const truncate = (text, length) => {
      if (!text) return 'N/A'
      return text.length > length ? text.substring(0, length) + '...' : text
    }

    const getStockBadge = (stock) => {
      if (stock === 0) return 'bg-danger'
      if (stock < 10) return 'bg-warning text-dark'
      return 'bg-success'
    }

    return {
      products,
      loading,
      error,
      response,
      showJson,
      connectionStatus,
      statusClass,
      activeProducts,
      lowStockProducts,
      totalValue,
      testConnection,
      loadProducts,
      clearData,
      formatPrice,
      truncate,
      getStockBadge,
      JSON
    }
  }
}
</script>

<style scoped>
pre {
  max-height: 400px;
  overflow-y: auto;
}

code {
  font-size: 0.85rem;
}

.table th {
  white-space: nowrap;
}

.card {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}
</style>

