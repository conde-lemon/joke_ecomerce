<template>
  <div class="container my-4">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow">
          <div class="card-body p-5">
            <h3 class="card-title text-center mb-4">Crear Cuenta</h3>

            <!-- Mensajes -->
            <div v-if="errorMessage" class="alert alert-danger">
              {{ errorMessage }}
            </div>
            <div v-if="successMessage" class="alert alert-success">
              {{ successMessage }}
            </div>

            <form @submit.prevent="handleRegister">
              <div class="mb-3">
                <label for="nombre" class="form-label">Nombre Completo</label>
                <input
                  v-model="formData.nombre"
                  type="text"
                  class="form-control"
                  id="nombre"
                  required
                >
              </div>

              <div class="mb-3">
                <label for="usuario" class="form-label">Nombre de Usuario</label>
                <input
                  v-model="formData.usuario"
                  type="text"
                  class="form-control"
                  id="usuario"
                  required
                >
              </div>

              <div class="mb-3">
                <label for="email" class="form-label">Correo Electrónico</label>
                <input
                  v-model="formData.correo"
                  type="email"
                  class="form-control"
                  id="email"
                  required
                >
              </div>

              <div class="mb-3">
                <label for="password" class="form-label">Contraseña</label>
                <input
                  v-model="formData.contrasena"
                  type="password"
                  class="form-control"
                  id="password"
                  required
                  minlength="6"
                >
                <div class="form-text">Mínimo 6 caracteres</div>
              </div>

              <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirmar Contraseña</label>
                <input
                  v-model="confirmPassword"
                  type="password"
                  class="form-control"
                  id="confirmPassword"
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
                  {{ loading ? 'Registrando...' : 'Registrarse' }}
                </button>
              </div>
            </form>

            <div class="text-center mt-3">
              <p class="mb-0">
                ¿Ya tienes cuenta?
                <router-link to="/login" class="text-decoration-none">Inicia sesión aquí</router-link>
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

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    const formData = ref({
      nombre: '',
      usuario: '',
      correo: '',
      contrasena: ''
    })

    const confirmPassword = ref('')
    const loading = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')

    const handleRegister = async () => {
      // Validar contraseñas
      if (formData.value.contrasena !== confirmPassword.value) {
        errorMessage.value = 'Las contraseñas no coinciden'
        return
      }

      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        await authStore.register(formData.value)
        successMessage.value = 'Cuenta creada exitosamente. Redirigiendo...'

        setTimeout(() => {
          router.push('/login')
        }, 2000)
      } catch (error) {
        errorMessage.value = error.response?.data?.message || 'Error al registrar. Intenta de nuevo.'
      } finally {
        loading.value = false
      }
    }

    return {
      formData,
      confirmPassword,
      loading,
      errorMessage,
      successMessage,
      handleRegister
    }
  }
}
</script>

