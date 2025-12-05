<template>
  <div class="container my-4">
    <h3 class="mb-3">Carrito de Compras</h3>

    <!-- Mensaje de éxito -->
    <div v-if="successMessage" class="alert alert-success alert-dismissible fade show">
      {{ successMessage }}
      <button type="button" class="btn-close" @click="successMessage = ''"></button>
    </div>

    <!-- Carrito vacío -->
    <div v-if="cartStore.isEmpty" class="alert alert-info">
      Tu carrito está vacío.
    </div>

    <!-- Carrito con items -->
    <div v-else>
      <table class="table align-middle">
        <thead class="table-light">
          <tr>
            <th style="width:120px">Producto</th>
            <th>Nombre</th>
            <th style="width:200px">Cantidad</th>
            <th style="width:160px" class="text-end">Precio unitario</th>
            <th style="width:160px" class="text-end">Subtotal</th>
            <th style="width:120px"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in cartStore.items" :key="item.product.id">
            <td>
              <img
                :src="item.product.imageUrl"
                class="img-thumbnail"
                style="width:100px;height:auto"
                :alt="item.product.nombre"
              >
            </td>
            <td>
              <router-link
                class="fw-semibold text-decoration-none"
                :to="`/product/${item.product.id}`"
              >
                {{ item.product.nombre }}
              </router-link>
            </td>
            <td>
              <div class="input-group" style="max-width: 200px;">
                <button
                  class="btn btn-outline-secondary"
                  @click="updateQty(item.product.id, item.quantity - 1)"
                  :disabled="item.quantity <= 1"
                >
                  −
                </button>
                <input
                  type="number"
                  class="form-control text-center"
                  :value="item.quantity"
                  @change="updateQty(item.product.id, $event.target.value)"
                  min="1"
                >
                <button
                  class="btn btn-outline-secondary"
                  @click="updateQty(item.product.id, item.quantity + 1)"
                >
                  ＋
                </button>
              </div>
            </td>
            <td class="text-end">S/ {{ formatPrice(item.unitPrice) }}</td>
            <td class="fw-semibold text-end">S/ {{ formatPrice(item.subtotal) }}</td>
            <td class="text-end">
              <button
                @click="removeItem(item.product.id)"
                class="btn btn-sm btn-outline-danger"
              >
                Quitar
              </button>
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <th colspan="4" class="text-end">
              Subtotal ({{ cartStore.totalItems }} items)
            </th>
            <th class="fw-bold text-end">
              S/ {{ formatPrice(cartStore.subtotal) }}
            </th>
            <th></th>
          </tr>
        </tfoot>
      </table>

      <!-- Botones de acción -->
      <div class="d-flex justify-content-between mt-4">
        <router-link to="/catalog" class="btn btn-outline-secondary">
          Seguir comprando
        </router-link>
        <div class="d-flex gap-2">
          <button @click="clearCart" class="btn btn-outline-danger">
            Vaciar carrito
          </button>
          <router-link to="/checkout" class="btn btn-primary">
            Ir a pagar
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useCartStore } from '../stores/cart'

export default {
  name: 'Cart',
  setup() {
    const cartStore = useCartStore()
    const successMessage = ref('')

    const updateQty = async (productId, newQty) => {
      const qty = parseInt(newQty)
      if (qty < 1) return

      const success = await cartStore.updateQuantity(productId, qty)
      if (success) {
        successMessage.value = 'Cantidad actualizada'
        setTimeout(() => successMessage.value = '', 2000)
      }
    }

    const removeItem = async (productId) => {
      if (confirm('¿Quitar este producto del carrito?')) {
        await cartStore.removeItem(productId)
      }
    }

    const clearCart = async () => {
      if (confirm('¿Vaciar todo el carrito?')) {
        await cartStore.clearCart()
      }
    }

    const formatPrice = (price) => parseFloat(price).toFixed(2)

    onMounted(() => {
      cartStore.fetchCart()
    })

    return {
      cartStore,
      successMessage,
      updateQty,
      removeItem,
      clearCart,
      formatPrice
    }
  }
}
</script>

<style scoped>
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
input[type="number"] {
  -moz-appearance: textfield;
}
</style>

