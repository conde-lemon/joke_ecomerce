<template>
  <div class="container my-5">
    <!-- Stepper de progreso -->
    <div class="row mb-5">
      <div class="col-12">
        <div class="checkout-stepper">
          <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
            <div class="step-icon">
              <i class="bi" :class="currentStep > 1 ? 'bi-check-circle-fill' : 'bi-1-circle-fill'"></i>
            </div>
            <div class="step-label">Datos de Envío</div>
          </div>
          <div class="step-line"></div>
          <div class="step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
            <div class="step-icon">
              <i class="bi" :class="currentStep > 2 ? 'bi-check-circle-fill' : 'bi-2-circle-fill'"></i>
            </div>
            <div class="step-label">Método de Pago</div>
          </div>
          <div class="step-line"></div>
          <div class="step" :class="{ active: currentStep >= 3 }">
            <div class="step-icon">
              <i class="bi bi-3-circle-fill"></i>
            </div>
            <div class="step-label">Confirmar Pedido</div>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Formulario principal -->
      <div class="col-lg-8">
        <!-- Paso 1: Datos de Envío -->
        <div v-show="currentStep === 1" class="card card-custom mb-4">
          <div class="card-header card-header-custom-dark">
            <h4 class="mb-0"><i class="bi bi-truck"></i> Datos de Envío</h4>
          </div>
          <div class="card-body">
            <form @submit.prevent="goToStep(2)">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-bold">Nombre Completo *</label>
                  <input
                    v-model="shippingData.fullName"
                    type="text"
                    class="form-control"
                    required
                  >
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-bold">Teléfono *</label>
                  <input
                    v-model="shippingData.phone"
                    type="tel"
                    class="form-control"
                    required
                  >
                </div>
                <div class="col-12 mb-3">
                  <label class="form-label fw-bold">Dirección *</label>
                  <input
                    v-model="shippingData.address"
                    type="text"
                    class="form-control"
                    placeholder="Calle, número, departamento"
                    required
                  >
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-bold">Ciudad *</label>
                  <input
                    v-model="shippingData.city"
                    type="text"
                    class="form-control"
                    required
                  >
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-bold">Código Postal *</label>
                  <input
                    v-model="shippingData.postalCode"
                    type="text"
                    class="form-control"
                    required
                  >
                </div>
                <div class="col-12 mb-3">
                  <label class="form-label fw-bold">Notas de Entrega (Opcional)</label>
                  <textarea
                    v-model="shippingData.notes"
                    class="form-control"
                    rows="3"
                    placeholder="Referencias, instrucciones especiales..."
                  ></textarea>
                </div>
              </div>
              <div class="d-flex justify-content-between">
                <router-link to="/cart" class="btn btn-outline-custom-dark">
                  <i class="bi bi-arrow-left"></i> Volver al Carrito
                </router-link>
                <button type="submit" class="btn btn-custom-primary">
                  Continuar <i class="bi bi-arrow-right"></i>
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Paso 2: Método de Pago -->
        <div v-show="currentStep === 2" class="card card-custom mb-4">
          <div class="card-header card-header-custom-dark">
            <h4 class="mb-0"><i class="bi bi-credit-card"></i> Método de Pago</h4>
          </div>
          <div class="card-body">
            <!-- Selector de método de pago -->
            <div class="payment-methods mb-4">
              <div
                class="payment-method"
                :class="{ selected: paymentMethod === 'card' }"
                @click="paymentMethod = 'card'"
              >
                <i class="bi bi-credit-card-2-front fs-2"></i>
                <span>Tarjeta de Crédito/Débito</span>
              </div>
              <div
                class="payment-method"
                :class="{ selected: paymentMethod === 'paypal' }"
                @click="paymentMethod = 'paypal'"
              >
                <i class="bi bi-paypal fs-2"></i>
                <span>PayPal</span>
              </div>
              <div
                class="payment-method"
                :class="{ selected: paymentMethod === 'cash' }"
                @click="paymentMethod = 'cash'"
              >
                <i class="bi bi-cash-stack fs-2"></i>
                <span>Pago Contra Entrega</span>
              </div>
            </div>

            <!-- Formulario de tarjeta -->
            <div v-if="paymentMethod === 'card'" class="payment-form">
              <form @submit.prevent="goToStep(3)">
                <div class="mb-3">
                  <label class="form-label fw-bold">Número de Tarjeta *</label>
                  <input
                    v-model="cardData.number"
                    type="text"
                    class="form-control"
                    placeholder="1234 5678 9012 3456"
                    maxlength="19"
                    @input="formatCardNumber"
                    required
                  >
                </div>
                <div class="mb-3">
                  <label class="form-label fw-bold">Nombre en la Tarjeta *</label>
                  <input
                    v-model="cardData.name"
                    type="text"
                    class="form-control"
                    placeholder="NOMBRE APELLIDO"
                    required
                  >
                </div>
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label fw-bold">Fecha de Expiración *</label>
                    <input
                      v-model="cardData.expiry"
                      type="text"
                      class="form-control"
                      placeholder="MM/AA"
                      maxlength="5"
                      @input="formatExpiry"
                      required
                    >
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label fw-bold">CVV *</label>
                    <input
                      v-model="cardData.cvv"
                      type="text"
                      class="form-control"
                      placeholder="123"
                      maxlength="4"
                      required
                    >
                  </div>
                </div>
                <div class="d-flex justify-content-between mt-4">
                  <button type="button" @click="currentStep = 1" class="btn btn-outline-custom-dark">
                    <i class="bi bi-arrow-left"></i> Volver
                  </button>
                  <button type="submit" class="btn btn-custom-primary">
                    Continuar <i class="bi bi-arrow-right"></i>
                  </button>
                </div>
              </form>
            </div>

            <!-- PayPal -->
            <div v-else-if="paymentMethod === 'paypal'" class="text-center py-4">
              <i class="bi bi-paypal text-custom-secondary" style="font-size: 4rem;"></i>
              <p class="mt-3">Serás redirigido a PayPal para completar el pago</p>
              <div class="d-flex justify-content-between mt-4">
                <button type="button" @click="currentStep = 1" class="btn btn-outline-custom-dark">
                  <i class="bi bi-arrow-left"></i> Volver
                </button>
                <button @click="goToStep(3)" class="btn btn-custom-secondary">
                  Continuar con PayPal <i class="bi bi-arrow-right"></i>
                </button>
              </div>
            </div>

            <!-- Pago contra entrega -->
            <div v-else-if="paymentMethod === 'cash'" class="text-center py-4">
              <i class="bi bi-cash-stack text-custom-primary" style="font-size: 4rem;"></i>
              <p class="mt-3">Pagarás en efectivo al recibir tu pedido</p>
              <div class="alert alert-warning mt-3">
                <i class="bi bi-info-circle"></i> Asegúrate de tener el monto exacto al momento de la entrega
              </div>
              <div class="d-flex justify-content-between mt-4">
                <button type="button" @click="currentStep = 1" class="btn btn-outline-custom-dark">
                  <i class="bi bi-arrow-left"></i> Volver
                </button>
                <button @click="goToStep(3)" class="btn btn-custom-primary">
                  Continuar <i class="bi bi-arrow-right"></i>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Paso 3: Confirmar Pedido -->
        <div v-show="currentStep === 3" class="card card-custom mb-4">
          <div class="card-header card-header-custom-dark">
            <h4 class="mb-0"><i class="bi bi-check-circle"></i> Confirmar Pedido</h4>
          </div>
          <div class="card-body">
            <!-- Resumen de datos de envío -->
            <h5 class="text-custom-dark mb-3">Datos de Envío</h5>
            <div class="info-box mb-4">
              <p class="mb-1"><strong>Nombre:</strong> {{ shippingData.fullName }}</p>
              <p class="mb-1"><strong>Teléfono:</strong> {{ shippingData.phone }}</p>
              <p class="mb-1"><strong>Dirección:</strong> {{ shippingData.address }}</p>
              <p class="mb-1"><strong>Ciudad:</strong> {{ shippingData.city }} - {{ shippingData.postalCode }}</p>
              <p v-if="shippingData.notes" class="mb-0"><strong>Notas:</strong> {{ shippingData.notes }}</p>
            </div>

            <!-- Resumen de pago -->
            <h5 class="text-custom-dark mb-3">Método de Pago</h5>
            <div class="info-box mb-4">
              <p class="mb-0" v-if="paymentMethod === 'card'">
                <i class="bi bi-credit-card"></i> Tarjeta terminada en {{ cardData.number.slice(-4) }}
              </p>
              <p class="mb-0" v-else-if="paymentMethod === 'paypal'">
                <i class="bi bi-paypal"></i> PayPal
              </p>
              <p class="mb-0" v-else>
                <i class="bi bi-cash-stack"></i> Pago Contra Entrega
              </p>
            </div>

            <!-- Productos -->
            <h5 class="text-custom-dark mb-3">Productos</h5>
            <div class="table-responsive mb-4">
              <table class="table">
                <thead class="table-dark">
                  <tr>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Precio</th>
                    <th>Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in cartItems" :key="item.id">
                    <td>{{ item.product?.nombre || 'Producto' }}</td>
                    <td>{{ item.quantity }}</td>
                    <td>S/ {{ formatPrice(item.product?.precio) }}</td>
                    <td>S/ {{ formatPrice(item.product?.precio * item.quantity) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Términos y condiciones -->
            <div class="form-check mb-4">
              <input
                v-model="acceptTerms"
                class="form-check-input"
                type="checkbox"
                id="acceptTerms"
              >
              <label class="form-check-label" for="acceptTerms">
                Acepto los <a href="#" class="text-custom-secondary">términos y condiciones</a>
              </label>
            </div>

            <!-- Mensajes -->
            <div v-if="errorMessage" class="alert alert-danger">
              {{ errorMessage }}
            </div>

            <!-- Botones -->
            <div class="d-flex justify-content-between">
              <button @click="currentStep = 2" class="btn btn-outline-custom-dark" :disabled="processing">
                <i class="bi bi-arrow-left"></i> Volver
              </button>
              <button
                @click="confirmOrder"
                class="btn btn-custom-primary btn-lg"
                :disabled="!acceptTerms || processing"
              >
                <span v-if="processing" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-check-circle me-2"></i>
                {{ processing ? 'Procesando...' : 'Confirmar Pedido' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Resumen del carrito (sidebar) -->
      <div class="col-lg-4">
        <div class="card card-custom sticky-top" style="top: 20px;">
          <div class="card-header card-header-custom-primary">
            <h5 class="mb-0"><i class="bi bi-cart3"></i> Resumen del Pedido</h5>
          </div>
          <div class="card-body">
            <div class="d-flex justify-content-between mb-2">
              <span>Subtotal:</span>
              <span class="fw-bold">S/ {{ formatPrice(subtotal) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-2">
              <span>Envío:</span>
              <span class="fw-bold">S/ {{ formatPrice(shippingCost) }}</span>
            </div>
            <hr class="my-3">
            <div class="d-flex justify-content-between">
              <span class="h5 mb-0">Total:</span>
              <span class="h5 mb-0 text-custom-primary fw-bold">S/ {{ formatPrice(total) }}</span>
            </div>
          </div>
          <div class="card-footer bg-light">
            <div class="d-flex align-items-center text-muted small">
              <i class="bi bi-shield-check me-2"></i>
              <span>Pago seguro y protegido</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import axios from '../config/axios'

export default {
  name: 'Checkout',
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()

    const currentStep = ref(1)
    const paymentMethod = ref('card')
    const acceptTerms = ref(false)
    const processing = ref(false)
    const errorMessage = ref('')

    const shippingData = ref({
      fullName: '',
      phone: '',
      address: '',
      city: '',
      postalCode: '',
      notes: ''
    })

    const cardData = ref({
      number: '',
      name: '',
      expiry: '',
      cvv: ''
    })

    const cartItems = computed(() => cartStore.items)
    const subtotal = computed(() => cartStore.total)
    const shippingCost = ref(15.00)
    const total = computed(() => subtotal.value + shippingCost.value)

    const formatPrice = (price) => {
      return parseFloat(price || 0).toFixed(2)
    }

    const formatCardNumber = (event) => {
      let value = event.target.value.replace(/\s/g, '')
      let formatted = value.match(/.{1,4}/g)?.join(' ') || value
      cardData.value.number = formatted
    }

    const formatExpiry = (event) => {
      let value = event.target.value.replace(/\D/g, '')
      if (value.length >= 2) {
        value = value.slice(0, 2) + '/' + value.slice(2, 4)
      }
      cardData.value.expiry = value
    }

    const goToStep = (step) => {
      currentStep.value = step
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    const confirmOrder = async () => {
      if (!acceptTerms.value) {
        errorMessage.value = 'Debes aceptar los términos y condiciones'
        return
      }

      processing.value = true
      errorMessage.value = ''

      try {
        // Simular procesamiento de pago
        await new Promise(resolve => setTimeout(resolve, 2000))

        // Crear pedido en el backend
        const orderData = {
          shippingInfo: shippingData.value,
          paymentMethod: paymentMethod.value,
          items: cartItems.value.map(item => ({
            productId: item.productId,
            quantity: item.quantity,
            price: item.product?.precio
          })),
          total: total.value
        }

        const response = await axios.post('/api/orders', orderData)

        // Limpiar carrito
        await cartStore.clearCart()

        // Redirigir a confirmación
        router.push(`/confirmation?orderId=${response.data.id || 'SUCCESS'}`)
      } catch (error) {
        console.error('Error al procesar pedido:', error)
        errorMessage.value = error.response?.data?.error || 'Error al procesar el pedido. Intenta de nuevo.'
      } finally {
        processing.value = false
      }
    }

    onMounted(async () => {
      await cartStore.fetchCart()
      if (cartItems.value.length === 0) {
        router.push('/cart')
      }
    })

    return {
      currentStep,
      paymentMethod,
      acceptTerms,
      processing,
      errorMessage,
      shippingData,
      cardData,
      cartItems,
      subtotal,
      shippingCost,
      total,
      formatPrice,
      formatCardNumber,
      formatExpiry,
      goToStep,
      confirmOrder
    }
  }
}
</script>

<style scoped>
/* Stepper */
.checkout-stepper {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  opacity: 0.4;
  transition: all 0.3s ease;
}

.step.active {
  opacity: 1;
}

.step.completed .step-icon i {
  color: var(--color-primary);
}

.step-icon {
  font-size: 2.5rem;
  margin-bottom: 10px;
  color: var(--color-dark);
}

.step.active .step-icon {
  color: var(--color-primary);
  transform: scale(1.1);
}

.step-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--color-dark);
  text-align: center;
}

.step-line {
  width: 100px;
  height: 3px;
  background-color: #ddd;
  margin: 0 20px;
  margin-bottom: 30px;
}

/* Payment Methods */
.payment-methods {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.payment-method {
  border: 3px solid #e0e0e0;
  border-radius: 12px;
  padding: 25px 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.payment-method:hover {
  border-color: var(--color-primary);
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.payment-method.selected {
  border-color: var(--color-primary);
  background-color: #fffbea;
  box-shadow: var(--shadow-lg);
}

.payment-method i {
  color: var(--color-dark);
}

.payment-method.selected i {
  color: var(--color-primary);
}

.payment-method span {
  font-weight: 600;
  font-size: 0.9rem;
}

/* Info Box */
.info-box {
  background-color: #f8f9fa;
  border-left: 4px solid var(--color-primary);
  padding: 15px;
  border-radius: 8px;
}

/* Responsive */
@media (max-width: 768px) {
  .checkout-stepper {
    flex-direction: column;
  }

  .step-line {
    width: 3px;
    height: 50px;
    margin: 10px 0;
  }

  .payment-methods {
    grid-template-columns: 1fr;
  }
}
</style>

