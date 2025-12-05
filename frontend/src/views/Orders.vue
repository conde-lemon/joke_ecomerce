<template>
  <div class="container my-4">
    <h3 class="mb-4">Mis Pedidos</h3>

    <div v-if="loading" class="text-center">
      <div class="spinner-border" role="status"></div>
    </div>

    <div v-else-if="orders.length === 0" class="alert alert-info">
      No tienes pedidos aún.
    </div>

    <div v-else>
      <div v-for="order in orders" :key="order.id" class="card mb-3">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span class="fw-bold">Pedido #{{ order.id }}</span>
          <span class="badge bg-primary">{{ order.estado }}</span>
        </div>
        <div class="card-body">
          <p class="mb-2">
            <strong>Fecha:</strong> {{ formatDate(order.fechaPedido) }}
          </p>
          <p class="mb-2">
            <strong>Total:</strong> S/ {{ formatPrice(order.total) }}
          </p>
          <button class="btn btn-sm btn-outline-primary">Ver Detalles</button>
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

    const fetchOrders = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/orders')
        orders.value = response.data
      } catch (error) {
        console.error('Error al cargar pedidos:', error)
      } finally {
        loading.value = false
      }
    }

    const formatDate = (date) => new Date(date).toLocaleDateString('es-PE')
    const formatPrice = (price) => parseFloat(price).toFixed(2)

    onMounted(fetchOrders)

    return { orders, loading, formatDate, formatPrice }
  }
}
</script>

