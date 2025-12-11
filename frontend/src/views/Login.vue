<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-5">
        <div class="card shadow-lg border-0 rounded-4">
          <div class="card-body p-5">
            <!-- Logo o Título -->
            <div class="text-center mb-4">
              <h2 class="fw-bold text-primary">Iniciar Sesión</h2>
              <p class="text-muted">Accede a tu cuenta</p>
            </div>

            <!-- Mensaje de Error -->
            <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>
              {{ errorMsg }}
              <button type="button" class="btn-close" @click="errorMsg = ''" aria-label="Close"></button>
            </div>

            <!-- Mensaje de Éxito (si viene del registro) -->
            <div v-if="successMsg" class="alert alert-success alert-dismissible fade show" role="alert">
              <i class="bi bi-check-circle-fill me-2"></i>
              {{ successMsg }}
              <button type="button" class="btn-close" @click="successMsg = ''" aria-label="Close"></button>
            </div>

            <!-- Formulario -->
            <form @submit.prevent="handleLogin">
              <!-- Email -->
              <div class="mb-3">
                <label for="email" class="form-label fw-semibold">
                  <i class="bi bi-envelope me-2"></i>Correo Electrónico
                </label>
                <input
                  v-model="email"
                  type="email"
                  class="form-control form-control-lg"
                  id="email"
                  placeholder="tu@email.com"
                  required
                  autocomplete="email"
                >
              </div>

              <!-- Password -->
              <div class="mb-3">
                <label for="password" class="form-label fw-semibold">
                  <i class="bi bi-lock me-2"></i>Contraseña
                </label>
                <input
                  v-model="password"
                  type="password"
                  class="form-control form-control-lg"
                  id="password"
                  placeholder="••••••••"
                  required
                  autocomplete="current-password"
                >
              </div>

              <!-- Botón Submit -->
              <div class="d-grid mb-3">
                <button
                  type="submit"
                  class="btn btn-primary btn-lg"
                  :disabled="isLoading"
                >
                  <span v-if="isLoading">
                    <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                    Ingresando...
                  </span>
                  <span v-else>
                    <i class="bi bi-box-arrow-in-right me-2"></i>
                    Iniciar Sesión
                  </span>
                </button>
              </div>
            </form>

            <!-- Divider -->
            <div class="text-center my-3">
              <span class="text-muted">o</span>
            </div>

            <!-- Link a Registro -->
            <div class="text-center">
              <p class="mb-0 text-muted">
                ¿No tienes cuenta?
                <router-link to="/register" class="text-primary fw-semibold text-decoration-none">
                  Regístrate aquí
                </router-link>
              </p>
            </div>

            <!-- Usuarios de Prueba (solo para desarrollo) -->
            <div class="mt-4 p-3 bg-light rounded">
              <small class="text-muted d-block mb-2"><strong>Usuarios de prueba:</strong></small>
              <small class="d-block">
                <strong>Admin:</strong> admin@ecommerce.com / admin123
              </small>
              <small class="d-block">
                <strong>User:</strong> juan.perez@gmail.com / user123
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import axios from '../config/axios'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// Form Data
const email = ref('')
const password = ref('')

// UI State
const isLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

// Mensaje de éxito desde el registro
if (route.query.registered === 'true') {
  successMsg.value = '¡Registro exitoso! Ahora puedes iniciar sesión.'
}

const handleLogin = async () => {
  // Validaciones básicas
  if (!email.value || !password.value) {
    errorMsg.value = 'Por favor completa todos los campos'
    return
  }

  isLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''

  try {
    // Llamada directa al backend
    const response = await axios.post('/api/auth/login', {
      email: email.value,
      password: password.value
    })

    if (response.data && response.data.user) {
      // Guardar en localStorage
      localStorage.setItem('user', JSON.stringify(response.data.user))
      localStorage.setItem('token', response.data.token)

      // Actualizar store de Pinia
      authStore.user = response.data.user
      authStore.token = response.data.token

      // Mostrar mensaje de éxito
      successMsg.value = '¡Bienvenido! Redirigiendo...'

      // Redirigir después de 1 segundo
      setTimeout(() => {
        router.push('/')
      }, 1000)
    }
  } catch (error) {
    console.error('Error en login:', error)

    if (error.response) {
      // Error del servidor
      errorMsg.value = error.response.data?.error || 'Credenciales incorrectas'
    } else if (error.request) {
      // No hubo respuesta del servidor
      errorMsg.value = 'No se pudo conectar con el servidor. Verifica que esté corriendo en el puerto 8080.'
    } else {
      // Otro tipo de error
      errorMsg.value = 'Error al iniciar sesión. Intenta de nuevo.'
    }

    // Limpiar contraseña en caso de error
    password.value = ''
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.card {
  transition: transform 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
}

.form-control:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: scale(1.02);
}

.alert {
  border: none;
  border-radius: 10px;
}
</style>

