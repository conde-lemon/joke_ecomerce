<template>
  <div class="container my-4">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card shadow">
          <div class="card-body p-5">
            <h3 class="card-title text-center mb-4">Iniciar Sesión</h3>

            <!-- Mensajes -->
            <div v-if="errorMessage" class="alert alert-danger">
              {{ errorMessage }}
            </div>

            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <label for="email" class="form-label">Correo Electrónico</label>
                <input
                  v-model="formData.email"
                  type="email"
                  class="form-control"
                  id="email"
                  required
                >
              </div>

              <div class="mb-3">
                <label for="password" class="form-label">Contraseña</label>
                <input
                  v-model="formData.password"
                  type="password"
                  class="form-control"
                  id="password"
                  required
                >
              </div>

              <div class="d-grid">
                <button
                  type="submit"
                  class="btn btn-primary"
                  :disabled="loading"
                >
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  {{ loading ? 'Ingresando...' : 'Iniciar Sesión' }}
                </button>
              </div>
            </form>

            <div class="text-center mt-3">
              <p class="mb-0">
                ¿No tienes cuenta?
                <router-link to="/register" class="text-decoration-none">Regístrate aquí</router-link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const cartStore = useCartStore()

    const formData = ref({
      email: '',
      password: ''
    })

    const loading = ref(false)
    const errorMessage = ref('')

    const handleLogin = async () => {
      loading.value = true
      errorMessage.value = ''

      try {
        // authStore.login retorna { success: boolean, error?: string }
        const result = await authStore.login(formData.value.email, formData.value.password)

        if (result && result.success) {
          // La store ya guarda user y token en localStorage
          await cartStore.fetchCart()
          router.push('/')
        } else {
          errorMessage.value = result?.error || 'Credenciales incorrectas'
          // limpiar contraseña en caso de fallo
          formData.value.password = ''
        }
      } catch (error) {
        errorMessage.value = 'Error al iniciar sesión. Intenta de nuevo.'
      } finally {
        loading.value = false
      }
    }

    return {
      formData,
      loading,
      errorMessage,
      handleLogin
    }
  }
}
</script>
