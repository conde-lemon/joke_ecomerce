<template>
  <div class="container-fluid my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2><i class="bi bi-speedometer2"></i> Panel de Administración</h2>
      <button @click="refreshStats" class="btn btn-outline-primary">
        <i class="bi bi-arrow-clockwise"></i> Actualizar
      </button>
    </div>

    <!-- Tarjetas de Estadísticas -->
    <div class="row mb-4">
      <div class="col-md-3 mb-3">
        <div class="card bg-primary text-white h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="text-uppercase mb-1">Productos</h6>
                <h2 class="mb-0">{{ stats.totalProducts }}</h2>
              </div>
              <i class="bi bi-box-seam fs-1"></i>
            </div>
          </div>
          <div class="card-footer bg-primary bg-opacity-50 border-0">
            <router-link to="/admin/products" class="text-white text-decoration-none">
              Ver todos <i class="bi bi-arrow-right"></i>
            </router-link>
          </div>
        </div>
      </div>

      <div class="col-md-3 mb-3">
        <div class="card bg-success text-white h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="text-uppercase mb-1">Usuarios</h6>
                <h2 class="mb-0">{{ stats.totalUsers }}</h2>
              </div>
              <i class="bi bi-people-fill fs-1"></i>
            </div>
          </div>
          <div class="card-footer bg-success bg-opacity-50 border-0">
            <router-link to="/admin/users" class="text-white text-decoration-none">
              Ver todos <i class="bi bi-arrow-right"></i>
            </router-link>
          </div>
        </div>
      </div>

      <div class="col-md-3 mb-3">
        <div class="card bg-warning text-dark h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="text-uppercase mb-1">Pedidos</h6>
                <h2 class="mb-0">{{ stats.totalOrders }}</h2>
              </div>
              <i class="bi bi-cart-check-fill fs-1"></i>
            </div>
          </div>
          <div class="card-footer bg-warning bg-opacity-50 border-0">
            <router-link to="/admin/orders" class="text-dark text-decoration-none">
              Ver todos <i class="bi bi-arrow-right"></i>
            </router-link>
          </div>
        </div>
      </div>

      <div class="col-md-3 mb-3">
        <div class="card bg-info text-white h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="text-uppercase mb-1">Ventas Totales</h6>
                <h2 class="mb-0">S/ {{ formatPrice(stats.totalSales) }}</h2>
              </div>
              <i class="bi bi-cash-coin fs-1"></i>
            </div>
          </div>
          <div class="card-footer bg-info bg-opacity-50 border-0">
            <a href="#" @click.prevent="downloadSalesReport" class="text-white text-decoration-none">
              Descargar reporte <i class="bi bi-download"></i>
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- Productos con Bajo Stock -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header bg-danger text-white">
            <h5 class="mb-0"><i class="bi bi-exclamation-triangle"></i> Productos con Bajo Stock</h5>
          </div>
          <div class="card-body">
            <div v-if="lowStockProducts.length === 0" class="text-center text-muted py-3">
              <i class="bi bi-check-circle fs-1"></i>
              <p class="mb-0">Todos los productos tienen stock suficiente</p>
            </div>
            <div v-else class="list-group list-group-flush">
              <div v-for="product in lowStockProducts" :key="product.id"
                   class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                  <strong>{{ product.nombre }}</strong>
                  <br>
                  <small class="text-muted">ID: {{ product.id }}</small>
                </div>
                <span class="badge bg-danger rounded-pill">{{ product.stock }} unidades</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Últimos Pedidos -->
      <div class="col-md-6">
        <div class="card">
          <div class="card-header bg-primary text-white">
            <h5 class="mb-0"><i class="bi bi-clock-history"></i> Últimos Pedidos</h5>
          </div>
          <div class="card-body">
            <div v-if="recentOrders.length === 0" class="text-center text-muted py-3">
              <i class="bi bi-inbox fs-1"></i>
              <p class="mb-0">No hay pedidos recientes</p>
            </div>
            <div v-else class="list-group list-group-flush">
              <div v-for="order in recentOrders" :key="order.id"
                   class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <strong>Pedido #{{ order.id }}</strong>
                    <br>
                    <small class="text-muted">{{ formatDate(order.fechaPedido) }}</small>
                  </div>
                  <div class="text-end">
                    <div>S/ {{ formatPrice(order.total) }}</div>
                    <span class="badge" :class="getStatusBadgeClass(order.estado)">
                      {{ order.estado }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Reportes Rápidos -->
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header bg-dark text-white">
            <h5 class="mb-0"><i class="bi bi-file-earmark-bar-graph"></i> Reportes Disponibles</h5>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="col-md-4 mb-3">
                <div class="card border-primary">
                  <div class="card-body text-center">
                    <i class="bi bi-file-earmark-spreadsheet text-primary fs-1"></i>
                    <h6 class="mt-3">Reporte de Productos</h6>
                    <p class="text-muted small">Lista completa de productos con stock y precios</p>
                    <button @click="downloadProductsReport" class="btn btn-primary btn-sm">
                      <i class="bi bi-download"></i> Descargar Excel
                    </button>
                  </div>
                </div>
              </div>

              <div class="col-md-4 mb-3">
                <div class="card border-success">
                  <div class="card-body text-center">
                    <i class="bi bi-file-earmark-pdf text-success fs-1"></i>
                    <h6 class="mt-3">Reporte de Ventas</h6>
                    <p class="text-muted small">Resumen de ventas del periodo</p>
                    <button @click="downloadSalesReport" class="btn btn-success btn-sm">
                      <i class="bi bi-download"></i> Descargar PDF
                    </button>
                  </div>
                </div>
              </div>

              <div class="col-md-4 mb-3">
                <div class="card border-warning">
                  <div class="card-body text-center">
                    <i class="bi bi-file-earmark-person text-warning fs-1"></i>
                    <h6 class="mt-3">Reporte de Usuarios</h6>
                    <p class="text-muted small">Lista de usuarios registrados</p>
                    <button @click="downloadUsersReport" class="btn btn-warning btn-sm">
                      <i class="bi bi-download"></i> Descargar Excel
                    </button>
                  </div>
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
import axios from '../../config/axios'

export default {
  name: 'AdminDashboard',
  setup() {
    const stats = ref({
      totalProducts: 0,
      totalUsers: 0,
      totalOrders: 0,
      totalSales: 0
    })

    const lowStockProducts = ref([])
    const recentOrders = ref([])

    const fetchStats = async () => {
      try {
        // Obtener estadísticas de productos
        const productsRes = await axios.get('/api/products')
        stats.value.totalProducts = productsRes.data.length

        // Productos con bajo stock (menos de 10)
        lowStockProducts.value = productsRes.data
          .filter(p => p.stock < 10)
          .sort((a, b) => a.stock - b.stock)
          .slice(0, 5)

        // Obtener estadísticas de usuarios
        const usersRes = await axios.get('/api/users')
        stats.value.totalUsers = usersRes.data.length

        // Obtener pedidos
        const ordersRes = await axios.get('/api/orders/all')
        stats.value.totalOrders = ordersRes.data.length
        stats.value.totalSales = ordersRes.data.reduce((sum, order) => sum + parseFloat(order.total || 0), 0)

        // Últimos 5 pedidos
        recentOrders.value = ordersRes.data
          .sort((a, b) => new Date(b.fechaPedido) - new Date(a.fechaPedido))
          .slice(0, 5)

      } catch (error) {
        console.error('Error al cargar estadísticas:', error)
      }
    }

    const refreshStats = () => {
      fetchStats()
    }

    const downloadProductsReport = async () => {
      try {
        const response = await axios.get('/api/reports/products', {
          responseType: 'blob'
        })
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `reporte_productos_${new Date().toISOString().split('T')[0]}.xlsx`)
        document.body.appendChild(link)
        link.click()
        link.remove()
      } catch (error) {
        alert('Error al descargar el reporte: ' + error.message)
      }
    }

    const downloadSalesReport = async () => {
      try {
        const response = await axios.get('/api/reports/sales', {
          responseType: 'blob'
        })
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `reporte_ventas_${new Date().toISOString().split('T')[0]}.pdf`)
        document.body.appendChild(link)
        link.click()
        link.remove()
      } catch (error) {
        alert('Error al descargar el reporte: ' + error.message)
      }
    }

    const downloadUsersReport = async () => {
      try {
        const response = await axios.get('/api/reports/users', {
          responseType: 'blob'
        })
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `reporte_usuarios_${new Date().toISOString().split('T')[0]}.xlsx`)
        document.body.appendChild(link)
        link.click()
        link.remove()
      } catch (error) {
        alert('Error al descargar el reporte: ' + error.message)
      }
    }

    const formatPrice = (price) => {
      return parseFloat(price || 0).toFixed(2)
    }

    const formatDate = (date) => {
      return new Date(date).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const getStatusBadgeClass = (status) => {
      const classes = {
        'PENDIENTE': 'bg-warning',
        'CONFIRMADO': 'bg-info',
        'ENVIADO': 'bg-primary',
        'ENTREGADO': 'bg-success',
        'CANCELADO': 'bg-danger'
      }
      return classes[status] || 'bg-secondary'
    }

    onMounted(() => {
      fetchStats()
    })

    return {
      stats,
      lowStockProducts,
      recentOrders,
      refreshStats,
      downloadProductsReport,
      downloadSalesReport,
      downloadUsersReport,
      formatPrice,
      formatDate,
      getStatusBadgeClass
    }
  }
}
</script>

<style scoped>
.card {
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.fs-1 {
  font-size: 3rem !important;
}
</style>

