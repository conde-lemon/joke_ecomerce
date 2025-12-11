<template>
  <div class="admin-orders">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2>Gestión de Pedidos</h2>
      <button @click="downloadProductReport" class="btn btn-success">
        <i class="fas fa-file-pdf"></i> Reporte de Productos
      </button>
    </div>
    <div v-if="loading">Cargando pedidos...</div>
    <div v-else>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Usuario</th>
            <th>Total</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id_pedido">
            <td>{{ o.id_pedido }}</td>
            <td>{{ formatDate(o.fecha_pedido) }}</td>
            <td>{{ o.usuario?.correo || o.correo_usuario }}</td>
            <td>{{ o.total }}</td>
            <td>
              <select v-model="o.estado" @change="showStatusModal(o)">
                <option value="PENDIENTE">PENDIENTE</option>
                <option value="CONFIRMADO">CONFIRMADO</option>
                <option value="ENVIADO">ENVIADO</option>
                <option value="ENTREGADO">ENTREGADO</option>
                <option value="CANCELADO">CANCELADO</option>
              </select>
            </td>
            <td>
              <button @click="viewDetails(o)" class="btn btn-sm btn-outline-info me-2">
                <i class="fas fa-eye"></i> Ver detalles
              </button>
              <button @click="downloadShippingOrder(o.id_pedido)" class="btn btn-sm btn-outline-success">
                <i class="fas fa-truck"></i> Orden Envío
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="selected">
      <h3>Detalles del pedido #{{ selected.id_pedido }}</h3>
      <ul>
        <li v-for="d in details" :key="d.id_detalle">
          Producto {{ d.id_producto }} - Cant: {{ d.cantidad }} - PU: {{ d.precio_unitario }} - Subtotal: {{ d.subtotal }}
        </li>
      </ul>

      <h3>Pagos</h3>
      <ul>
        <li v-for="p in payments" :key="p.id_pago">
          {{ p.metodo_pago }} - {{ p.monto }} - {{ formatDate(p.fecha_pago) }} - {{ p.estado }}
        </li>
      </ul>
    </div>

    <!-- Modal de confirmación -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h3>Confirmar cambio de estado</h3>
        <p>Cambiar estado del pedido #{{ selectedOrder?.id_pedido }} a: <strong>{{ selectedOrder?.estado }}</strong></p>
        <div class="form-group">
          <label>Mensaje para el cliente (opcional):</label>
          <textarea v-model="statusMessage" placeholder="Escriba un mensaje informativo para el cliente..." rows="3"></textarea>
        </div>
        <div class="modal-buttons">
          <button @click="confirmStatusUpdate" class="btn-confirm">Confirmar</button>
          <button @click="closeModal" class="btn-cancel">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from '@/config/axios'
export default {
  name: 'AdminOrders',
  data() {
    return {
      loading: true,
      orders: [],
      selected: null,
      details: [],
      payments: [],
      showModal: false,
      selectedOrder: null,
      statusMessage: '',
      originalStatus: null
    }
  },
  methods: {
    async fetchOrders(retryCount = 0) {
      this.loading = true
      try {
        console.log('Intentando cargar pedidos...')
        const { data } = await axios.get('/api/admin/orders')
        this.orders = data
        console.log('Pedidos cargados exitosamente:', data.length)
      } catch (e) {
        console.error('Error al cargar pedidos', e)
        
        // Reintentar hasta 2 veces en caso de error de red
        if ((e.code === 'ERR_NETWORK' || e.code === 'ECONNABORTED') && retryCount < 2) {
          console.log(`Reintentando... (intento ${retryCount + 1}/2)`)
          setTimeout(() => {
            this.fetchOrders(retryCount + 1)
          }, 2000) // Esperar 2 segundos antes de reintentar
          return
        }
        
        // Mostrar mensaje de error al usuario
        alert('Error al cargar los pedidos. Por favor, verifica que el servidor esté funcionando.')
      } finally {
        this.loading = false
      }
    },
    async viewDetails(order) {
      this.selected = order
      try {
        const [detailsRes, paymentsRes] = await Promise.all([
          axios.get(`/api/admin/orders/${order.id_pedido}/details`),
          axios.get(`/api/admin/orders/${order.id_pedido}/payments`)
        ])
        this.details = detailsRes.data
        this.payments = paymentsRes.data
      } catch (e) {
        console.error('Error al cargar detalles/pagos', e)
      }
    },
    showStatusModal(order) {
      this.selectedOrder = { ...order }
      this.originalStatus = this.orders.find(o => o.id_pedido === order.id_pedido).estado
      this.statusMessage = ''
      this.showModal = true
    },
    async confirmStatusUpdate() {
      try {
        const params = { estado: this.selectedOrder.estado }
        if (this.statusMessage.trim()) {
          params.mensaje = this.statusMessage.trim()
        }
        await axios.put(`/api/admin/orders/${this.selectedOrder.id_pedido}/status`, null, { params })
        this.closeModal()
      } catch (e) {
        console.error('Error actualizando estado', e)
        // Revertir el cambio en caso de error
        const order = this.orders.find(o => o.id_pedido === this.selectedOrder.id_pedido)
        if (order) order.estado = this.originalStatus
        this.closeModal()
      }
    },
    closeModal() {
      if (this.selectedOrder && this.originalStatus) {
        // Revertir el cambio visual si se cancela
        const order = this.orders.find(o => o.id_pedido === this.selectedOrder.id_pedido)
        if (order) order.estado = this.originalStatus
      }
      this.showModal = false
      this.selectedOrder = null
      this.statusMessage = ''
      this.originalStatus = null
    },
    downloadShippingOrder(orderId) {
      window.open(`/api/reports/orden-envio/${orderId}`, '_blank')
    },
    downloadProductReport() {
      window.open('/api/reports/productos/pdf', '_blank')
    },
    formatDate(d) {
      if (!d) return ''
      try { return new Date(d).toLocaleString() } catch { return d }
    }
  },
  mounted() {
    this.fetchOrders()
  }
}
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { border: 1px solid #ddd; padding: 8px; }
.table th { background: #f5f5f5; }

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
}

.form-group {
  margin: 15px 0;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.modal-buttons {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-confirm {
  background: #28a745;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-cancel {
  background: #6c757d;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.btn {
  margin: 2px;
}

.table td {
  vertical-align: middle;
}
</style>

