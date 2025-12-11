<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-7 col-lg-6">
        <div class="card shadow-lg border-0 rounded-4">
          <div class="card-body p-5">
            <!-- Título -->
            <div class="text-center mb-4">
              <h2 class="fw-bold text-primary">Crear Cuenta</h2>
              <p class="text-muted">Únete a nuestra comunidad</p>
            </div>

            <!-- Mensaje de Error -->
            <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>
              {{ errorMsg }}
              <button type="button" class="btn-close" @click="errorMsg = ''" aria-label="Close"></button>
            </div>

            <!-- Mensaje de Éxito -->
            <div v-if="successMsg" class="alert alert-success alert-dismissible fade show" role="alert">
              <i class="bi bi-check-circle-fill me-2"></i>
              {{ successMsg }}
              <button type="button" class="btn-close" @click="successMsg = ''" aria-label="Close"></button>
            </div>

            <!-- Formulario -->
            <form @submit.prevent="handleRegister">
              <!-- Nombre Completo -->
              <div class="mb-3">
                <label for="nombre" class="form-label fw-semibold">
                  <i class="bi bi-person me-2"></i>Nombre Completo
                </label>
                <input
                  v-model="formData.nombre"
                  type="text"
                  class="form-control"
                  id="nombre"
                  placeholder="Ej: Juan Pérez"
                  required
                  autocomplete="name"
                >
              </div>

              <!-- Usuario -->
              <div class="mb-3">
                <label for="usuario" class="form-label fw-semibold">
                  <i class="bi bi-at me-2"></i>Nombre de Usuario
                </label>
                <input
                  v-model="formData.usuario"
                  type="text"
                  class="form-control"
                  id="usuario"
                  placeholder="Ej: juanperez"
                  required
                  pattern="[a-zA-Z0-9]+"
                  title="Solo letras y números, sin espacios"
                  autocomplete="username"
                >
                <small class="text-muted">Solo letras y números, sin espacios</small>
              </div>

              <!-- Email -->
              <div class="mb-3">
                <label for="correo" class="form-label fw-semibold">
                  <i class="bi bi-envelope me-2"></i>Correo Electrónico
                </label>
                <input
                  v-model="formData.correo"
                  type="email"
                  class="form-control"
                  id="correo"
                  placeholder="tu@email.com"
                  required
                  autocomplete="email"
                >
              </div>

              <!-- Contraseña -->
              <div class="mb-3">
                <label for="contrasena" class="form-label fw-semibold">
                  <i class="bi bi-lock me-2"></i>Contraseña
                </label>
                <input
                  v-model="formData.contrasena"
                  type="password"
                  class="form-control"
                  id="contrasena"
                  placeholder="Mínimo 6 caracteres"
                  required
                  minlength="6"
                  autocomplete="new-password"
                >
                <small class="text-muted">Mínimo 6 caracteres</small>
              </div>

              <!-- Confirmar Contraseña -->
              <div class="mb-3">
                <label for="confirmPassword" class="form-label fw-semibold">
                  <i class="bi bi-lock-fill me-2"></i>Confirmar Contraseña
                </label>
                <input
                  v-model="confirmPassword"
                  type="password"
                  class="form-control"
                  id="confirmPassword"
                  placeholder="Repite tu contraseña"
                  required
                  autocomplete="new-password"
                  :class="{ 'is-invalid': confirmPassword && formData.contrasena !== confirmPassword }"
                >
                <div v-if="confirmPassword && formData.contrasena !== confirmPassword" class="invalid-feedback">
                  Las contraseñas no coinciden
                </div>
              </div>

              <!-- Botón Submit -->
              <div class="d-grid mb-3">
                <button
                  type="submit"
                  class="btn btn-primary btn-lg"
                  :disabled="isLoading || (confirmPassword && formData.contrasena !== confirmPassword)"
                >
                  <span v-if="isLoading">
                    <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                    Registrando...
                  </span>
                  <span v-else>
                    <i class="bi bi-person-plus me-2"></i>
                    Crear Cuenta
                  </span>
                </button>
              </div>
            </form>

            <!-- Divider -->
            <div class="text-center my-3">
              <span class="text-muted">o</span>
            </div>

            <!-- Link a Login -->
            <div class="text-center">
              <p class="mb-0 text-muted">
                ¿Ya tienes cuenta?
                <router-link to="/login" class="text-primary fw-semibold text-decoration-none">
                  Inicia sesión aquí
                </router-link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../config/axios'

const router = useRouter()

// Form Data
const formData = ref({
  nombre: '',
  usuario: '',
  correo: '',
  contrasena: ''
})

const confirmPassword = ref('')

// UI State
const isLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const handleRegister = async () => {
  // Validaciones
  if (!formData.value.nombre || !formData.value.usuario || !formData.value.correo || !formData.value.contrasena) {
    errorMsg.value = 'Por favor completa todos los campos'
    return
  }

  if (formData.value.contrasena !== confirmPassword.value) {
    errorMsg.value = 'Las contraseñas no coinciden'
    return
  }

  if (formData.value.contrasena.length < 6) {
    errorMsg.value = 'La contraseña debe tener al menos 6 caracteres'
    return
  }

  isLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''

  try {
    // Llamada directa al backend
    const response = await axios.post('/api/auth/register', {
      nombre: formData.value.nombre.trim(),
      usuario: formData.value.usuario.trim().toLowerCase(),
      correo: formData.value.correo.trim().toLowerCase(),
      contrasena: formData.value.contrasena
    })

    if (response.data && response.data.message) {
      successMsg.value = '¡Cuenta creada exitosamente! Redirigiendo al login...'

      // Limpiar formulario
      formData.value = { nombre: '', usuario: '', correo: '', contrasena: '' }
      confirmPassword.value = ''

      // Redirigir después de 2 segundos
      setTimeout(() => {
        router.push({ path: '/login', query: { registered: 'true' } })
      }, 2000)
    }
  } catch (error) {
    console.error('Error en registro:', error)

    if (error.response) {
      // Error del servidor
      if (error.response.data?.error) {
        errorMsg.value = error.response.data.error
      } else {
        errorMsg.value = 'Error al registrar usuario. Intenta de nuevo.'
      }
    } else if (error.request) {
      // No hubo respuesta del servidor
      errorMsg.value = 'No se pudo conectar con el servidor. Verifica que esté corriendo en el puerto 8080.'
    } else {
      // Otro tipo de error
      errorMsg.value = 'Error al registrar. Intenta de nuevo.'
    }
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

.form-control.is-invalid:focus {
  border-color: #dc3545;
  box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: scale(1.02);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.alert {
  border: none;
  border-radius: 10px;
}
</style>

