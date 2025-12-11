<template>
  <div class="container my-4">
    <h3 class="mb-4">Mis Pedidos</h3>

    <div v-if="loading" class="text-center">
      <div class="spinner-border" role="status"></div>
    </div>

    <div v-else>
      <div v-if="errorMessage" class="alert alert-danger d-flex justify-content-between align-items-center">
        <div>
          <strong>Error:</strong> {{ errorMessage }}
          <div class="mt-2">
            <small>
              Si el problema persiste, abre la página de diagnóstico del backend:
              <a href="/test/orders" target="_blank">/test/orders</a>
            </small>
          </div>
        </div>
        <div>
          <button class="btn btn-sm btn-outline-primary me-2" @click="fetchOrders">Reintentar</button>
        </div>
      </div>

      <div v-else-if="orders.length === 0" class="alert alert-info">
        No tienes pedidos aún.
      </div>

      <div v-else>
        <div v-for="order in orders" :key="order.id" class="card mb-3">
          <div class="card-header d-flex justify-content-between align-items-center">
            <span class="fw-bold">Pedido #{{ order.id }}</span>
            <span class="badge" :class="getStatusBadgeClass(order.estado)">{{ order.estado }}</span>
          </div>
          <div class="card-body">
            <p class="mb-2">
              <strong>Fecha:</strong> {{ formatDate(order.fechaPedido) }}
            </p>
            <p class="mb-2">
              <strong>Total:</strong> S/ {{ formatPrice(order.total) }}
            </p>
            
            <!-- Mensajes del administrador -->
            <div v-if="order.mensajes && order.mensajes.length > 0" class="mt-3">
              <h6 class="text-primary">Actualizaciones:</h6>
              <div v-for="mensaje in order.mensajes" :key="mensaje.id" class="alert alert-info alert-sm mb-2">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <strong>{{ formatDate(mensaje.fechaMensaje) }}</strong>
                    <span class="badge bg-secondary ms-2">{{ mensaje.estadoAnterior }} → {{ mensaje.estadoNuevo }}</span>
                  </div>
                </div>
                <p class="mb-0 mt-2">{{ mensaje.mensaje }}</p>
              </div>
            </div>
            
            <div class="mt-3">
              <button class="btn btn-sm btn-outline-primary me-2" @click="toggleDetails(order)">
                <i class="fas fa-eye"></i> {{ order.showDetails ? 'Ocultar' : 'Ver' }} Detalles
              </button>
              <button class="btn btn-sm btn-success" @click="downloadReceipt(order.id)">
                <i class="fas fa-download"></i> Descargar Boleta
              </button>
            </div>
            
            <!-- Detalles expandibles -->
            <div v-if="order.showDetails" class="mt-3 border-top pt-3">
              <div v-if="loadingDetails[order.id]" class="text-center">
                <div class="spinner-border spinner-border-sm" role="status"></div>
                <span class="ms-2">Cargando detalles...</span>
              </div>
              <div v-else-if="orderDetails[order.id]">
                <h6>Productos:</h6>
                <div class="table-responsive">
                  <table class="table table-sm">
                    <thead>
                      <tr>
                        <th>Producto</th>
                        <th>Cantidad</th>
                        <th>Precio Unit.</th>
                        <th>Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="detalle in orderDetails[order.id]" :key="detalle.id">
                        <td>{{ detalle.producto?.nombre || 'Producto no disponible' }}</td>
                        <td>{{ detalle.cantidad }}</td>
                        <td>S/ {{ formatPrice(detalle.precioUnitario) }}</td>
                        <td>S/ {{ formatPrice(detalle.subtotal) }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from '../config/axios'

export default {
  name: 'Orders',
  setup() {
    const orders = ref([])
    const loading = ref(false)
    const errorMessage = ref('')
    const orderDetails = ref({})
    const loadingDetails = ref({})

    const MAX_RETRIES = 3
    const AXIOS_TIMEOUT_MS = 6000
    const FETCH_TIMEOUT_MS = 6000
    const TEST_API = '/api/test/orders'
    const MAIN_API = '/api/orders'

    const sleep = ms => new Promise(res => setTimeout(res, ms))

    // Intentar obtener email del meta tag inyectado por el servidor (si existe)
    const getUserEmailMeta = () => {
      try {
        const m = document.querySelector('meta[name="user-email"]')
        return m ? (m.content || m.getAttribute('content')) : null
      } catch { return null }
    }

    const tryAxios = async (url, timeout) => {
      const res = await axios.get(url, { timeout })
      if (!res || !res.data) throw new Error('Respuesta vacía del servidor')
      return res.data
    }

    const buildUrlWithEmail = (base, email) => {
      if (!email) return base
      const sep = base.includes('?') ? '&' : '?'
      return `${base}${sep}email=${encodeURIComponent(email)}`
    }

    const fetchOrders = async () => {
      loading.value = true
      errorMessage.value = ''
      orders.value = []

      const email = getUserEmailMeta() || null
      const mainUrl = buildUrlWithEmail(MAIN_API, email)
      const testUrl = buildUrlWithEmail(TEST_API, email)

      // Intentar con reintentos en el endpoint principal
      for (let attempt = 0; attempt < MAX_RETRIES; attempt++) {
        try {
          const data = await tryAxios(mainUrl, AXIOS_TIMEOUT_MS)
          orders.value = data
          loading.value = false
          return
        } catch (err) {
          const isLast = attempt === MAX_RETRIES - 1
          console.warn(`Intento ${attempt + 1} falló:`, err && err.message ? err.message : err)

          if (!isLast) {
            await sleep(500 * Math.pow(2, attempt))
            continue
          }

          // Último intento fallido en MAIN_API -> intentar ruta de diagnóstico con axios
          try {
            console.info('Intentando endpoint de diagnóstico:', testUrl)
            const testData = await tryAxios(testUrl, AXIOS_TIMEOUT_MS)
            orders.value = testData
            loading.value = false
            return
          } catch (diagErr) {
            console.warn('Endpoint de diagnóstico también falló (axios):', diagErr && diagErr.message ? diagErr.message : diagErr)
            // Finalmente intentar fallback con fetch hacia testUrl
            try {
              const controller = new AbortController()
              const timeoutId = setTimeout(() => controller.abort(), FETCH_TIMEOUT_MS)

              const res = await fetch(testUrl, {
                method: 'GET',
                credentials: 'include',
                signal: controller.signal,
                headers: { 'Accept': 'application/json' }
              })
              clearTimeout(timeoutId)

              if (!res.ok) {
                throw new Error(`HTTP ${res.status} ${res.statusText}`)
              }
              const data = await res.json()
              orders.value = data || []
              loading.value = false
              return
            } catch (fetchErr) {
              console.error('Fallback fetch falló:', fetchErr)
              errorMessage.value = (fetchErr.name === 'AbortError')
                ? 'Tiempo de espera agotado al solicitar pedidos. Intenta nuevamente.'
                : `No se pudo cargar los pedidos: ${fetchErr.message || 'Error de red'}. Verifica que el backend esté en ejecución y abre /test/orders para diagnosticar.`
              loading.value = false
              return
            }
          }
        }
      }
    }

    const formatDate = (date) => {
      try { 
        return new Date(date).toLocaleDateString('es-PE', {
          year: 'numeric',
          month: 'short',
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        })
      } catch { return date }
    }
    const formatPrice = (price) => {
      const n = parseFloat(price)
      return isNaN(n) ? '0.00' : n.toFixed(2)
    }
    
    const getStatusBadgeClass = (estado) => {
      const classes = {
        'PENDIENTE': 'bg-warning',
        'CONFIRMADO': 'bg-info',
        'ENVIADO': 'bg-primary',
        'ENTREGADO': 'bg-success',
        'CANCELADO': 'bg-danger'
      }
      return classes[estado] || 'bg-secondary'
    }
    
    const toggleDetails = async (order) => {
      order.showDetails = !order.showDetails
      
      if (order.showDetails && !orderDetails.value[order.id]) {
        loadingDetails.value[order.id] = true
        try {
          const { data } = await axios.get(`/api/admin/orders/${order.id}/details`)
          orderDetails.value[order.id] = data
        } catch (e) {
          console.error('Error al cargar detalles:', e)
        } finally {
          loadingDetails.value[order.id] = false
        }
      }
    }
    
    const downloadReceipt = (orderId) => {
      window.open(`/api/reports/boleta/${orderId}`, '_blank')
    }

    onMounted(fetchOrders)

    return { 
      orders, loading, formatDate, formatPrice, fetchOrders, errorMessage, 
      getStatusBadgeClass, toggleDetails, downloadReceipt, orderDetails, loadingDetails 
    }
  }
}
</script>

<style scoped>
.alert-sm {
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
}

.badge {
  font-size: 0.75em;
}

.card {
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border: 1px solid #e9ecef;
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.text-primary {
  color: #0d6efd !important;
}

.table-responsive {
  border-radius: 0.375rem;
  border: 1px solid #dee2e6;
}

.table-sm th,
.table-sm td {
  padding: 0.5rem;
  font-size: 0.875rem;
}

.btn {
  transition: all 0.2s ease-in-out;
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
}
</style>
