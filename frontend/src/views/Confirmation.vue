<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <div class="card card-custom border-0 shadow-lg">
          <div class="card-body text-center py-5">
            <!-- Icono de éxito -->
            <div class="success-icon mb-4">
              <i class="bi bi-check-circle-fill"></i>
            </div>

            <!-- Título -->
            <h1 class="display-5 fw-bold text-custom-dark mb-3">
              ¡Pedido Confirmado!
            </h1>

            <!-- Número de pedido -->
            <div class="order-number-box mb-4">
              <p class="mb-1 text-muted">Número de Pedido</p>
              <h3 class="text-custom-primary fw-bold mb-0">{{ orderId }}</h3>
            </div>

            <!-- Mensaje -->
            <p class="lead mb-4">
              Tu pedido ha sido procesado exitosamente y está en camino.
            </p>

            <!-- Información adicional -->
            <div class="info-cards row g-3 mb-5">
              <div class="col-md-4">
                <div class="info-card">
                  <i class="bi bi-envelope-check"></i>
                  <p class="mb-0">Recibirás un correo de confirmación</p>
                </div>
              </div>
              <div class="col-md-4">
                <div class="info-card">
                  <i class="bi bi-truck"></i>
                  <p class="mb-0">Entrega en 2-5 días hábiles</p>
                </div>
              </div>
              <div class="col-md-4">
                <div class="info-card">
                  <i class="bi bi-headset"></i>
                  <p class="mb-0">Soporte disponible 24/7</p>
                </div>
              </div>
            </div>

            <!-- Botones de acción -->
            <div class="d-flex flex-column flex-md-row gap-3 justify-content-center mb-4">
              <button @click="descargarBoleta" class="btn btn-success btn-lg">
                <i class="bi bi-file-earmark-pdf me-2"></i>
                Descargar Boleta
              </button>
              <button @click="descargarOrdenEnvio" class="btn btn-info btn-lg">
                <i class="bi bi-file-earmark-text me-2"></i>
                Descargar Orden de Envío
              </button>
            </div>

            <div class="d-flex flex-column flex-md-row gap-3 justify-content-center">
              <router-link to="/orders" class="btn btn-custom-primary btn-lg">
                <i class="bi bi-list-ul me-2"></i>
                Ver Mis Pedidos
              </router-link>
              <router-link to="/catalog" class="btn btn-outline-custom-secondary btn-lg">
                <i class="bi bi-arrow-left me-2"></i>
                Seguir Comprando
              </router-link>
            </div>
          </div>
        </div>

        <!-- Sección de ayuda -->
        <div class="text-center mt-4">
          <p class="text-muted">
            ¿Tienes alguna pregunta?
            <a href="#" class="text-custom-secondary fw-bold">Contáctanos</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../config/axios'

export default {
  name: 'Confirmation',
  setup() {
    const route = useRoute()
    const orderId = ref('')

    onMounted(() => {
      orderId.value = route.query.orderId || 'XXXXXXXX'
    })

    const descargarBoleta = async () => {
      if (!orderId.value || orderId.value === 'XXXXXXXX') {
        alert('ID de pedido no válido')
        return
      }

      try {
        const response = await axios.get(`/api/reports/boleta/${orderId.value}`, {
          responseType: 'blob'
        })

        // Crear enlace temporal para descargar
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `boleta_${orderId.value}.pdf`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('Error al descargar boleta:', error)
        alert('Error al descargar la boleta. Por favor, intenta más tarde.')
      }
    }

    const descargarOrdenEnvio = async () => {
      if (!orderId.value || orderId.value === 'XXXXXXXX') {
        alert('ID de pedido no válido')
        return
      }

      try {
        const response = await axios.get(`/api/reports/orden-envio/${orderId.value}`, {
          responseType: 'blob'
        })

        // Crear enlace temporal para descargar
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `orden_envio_${orderId.value}.pdf`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('Error al descargar orden de envío:', error)
        alert('Error al descargar la orden de envío. Por favor, intenta más tarde.')
      }
    }

    return {
      orderId,
      descargarBoleta,
      descargarOrdenEnvio
    }
  }
}
</script>

<style scoped>
.success-icon {
  font-size: 6rem;
  color: var(--color-primary);
  animation: scaleIn 0.5s ease-out;
}

@keyframes scaleIn {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}

.order-number-box {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  padding: 20px;
  border: 2px solid var(--color-primary);
}

.info-cards {
  margin: 0 auto;
  max-width: 800px;
}

.info-card {
  background-color: #f8f9fa;
  border-radius: 12px;
  padding: 25px 15px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.info-card:hover {
  border-color: var(--color-primary);
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.info-card i {
  font-size: 2rem;
  color: var(--color-secondary);
  display: block;
  margin-bottom: 10px;
}

.info-card p {
  font-size: 0.9rem;
  color: var(--color-dark);
  font-weight: 500;
}
</style>

