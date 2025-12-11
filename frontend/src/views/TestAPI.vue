<template>
  <div class="container my-5">
    <div class="card shadow">
      <div class="card-header bg-primary text-white">
        <h3 class="mb-0">🧪 TEST API - USUARIOS Y PRODUCTOS</h3>
      </div>
      <div class="card-body">

        <!-- SECCIÓN 1: TEST DE USUARIOS -->
        <div class="mb-5">
          <h4 class="text-primary mb-3">👥 TEST USUARIOS</h4>

          <!-- Botón para cargar usuarios -->
          <button @click="testUsuarios" class="btn btn-primary mb-3" :disabled="loadingUsuarios">
            <span v-if="loadingUsuarios">
              <span class="spinner-border spinner-border-sm me-2"></span>
              Cargando...
            </span>
            <span v-else>
              <i class="bi bi-people-fill me-2"></i>
              Cargar Todos los Usuarios
            </span>
          </button>

          <!-- Resultado -->
          <div v-if="usuariosResponse" class="alert" :class="usuariosResponse.success ? 'alert-success' : 'alert-danger'">
            <h5>{{ usuariosResponse.success ? '✅ ÉXITO' : '❌ ERROR' }}</h5>
            <p><strong>URL:</strong> {{ usuariosResponse.url }}</p>
            <p><strong>Método:</strong> {{ usuariosResponse.method }}</p>
            <pre class="bg-light p-3 rounded">{{ JSON.stringify(usuariosResponse.data, null, 2) }}</pre>
          </div>
        </div>

        <hr>

        <!-- SECCIÓN 2: TEST LOGIN -->
        <div class="mb-5">
          <h4 class="text-primary mb-3">🔐 TEST LOGIN</h4>

          <div class="row">
            <div class="col-md-6">
              <div class="mb-3">
                <label class="form-label">Email:</label>
                <input v-model="loginData.email" type="email" class="form-control" placeholder="admin@ecommerce.com">
              </div>
            </div>
            <div class="col-md-6">
              <div class="mb-3">
                <label class="form-label">Contraseña:</label>
                <input v-model="loginData.password" type="password" class="form-control" placeholder="admin123">
              </div>
            </div>
          </div>

          <button @click="testLogin" class="btn btn-success mb-3" :disabled="loadingLogin">
            <span v-if="loadingLogin">
              <span class="spinner-border spinner-border-sm me-2"></span>
              Probando...
            </span>
            <span v-else>
              <i class="bi bi-box-arrow-in-right me-2"></i>
              Probar Login
            </span>
          </button>

          <!-- Resultado -->
          <div v-if="loginResponse" class="alert" :class="loginResponse.success ? 'alert-success' : 'alert-danger'">
            <h5>{{ loginResponse.success ? '✅ LOGIN EXITOSO' : '❌ LOGIN FALLIDO' }}</h5>
            <p><strong>URL:</strong> {{ loginResponse.url }}</p>
            <p><strong>Método:</strong> {{ loginResponse.method }}</p>
            <pre class="bg-light p-3 rounded">{{ JSON.stringify(loginResponse.data, null, 2) }}</pre>
          </div>
        </div>

        <hr>

        <!-- SECCIÓN 3: TEST PRODUCTOS -->
        <div class="mb-5">
          <h4 class="text-primary mb-3">📦 TEST PRODUCTOS</h4>

          <button @click="testProductos" class="btn btn-info mb-3" :disabled="loadingProductos">
            <span v-if="loadingProductos">
              <span class="spinner-border spinner-border-sm me-2"></span>
              Cargando...
            </span>
            <span v-else>
              <i class="bi bi-box-seam me-2"></i>
              Cargar Todos los Productos
            </span>
          </button>

          <!-- Resultado -->
          <div v-if="productosResponse" class="alert" :class="productosResponse.success ? 'alert-success' : 'alert-danger'">
            <h5>{{ productosResponse.success ? '✅ ÉXITO' : '❌ ERROR' }}</h5>
            <p><strong>URL:</strong> {{ productosResponse.url }}</p>
            <p><strong>Método:</strong> {{ productosResponse.method }}</p>
            <p><strong>Total productos:</strong> {{ productosResponse.data?.length || 0 }}</p>
            <pre class="bg-light p-3 rounded" style="max-height: 400px; overflow-y: auto;">{{ JSON.stringify(productosResponse.data, null, 2) }}</pre>
          </div>
        </div>

        <hr>

        <!-- SECCIÓN 4: CREDENCIALES DE PRUEBA -->
        <div class="mb-3">
          <h4 class="text-primary mb-3">🔑 CREDENCIALES DE PRUEBA</h4>
          <div class="table-responsive">
            <table class="table table-bordered table-sm">
              <thead class="table-light">
                <tr>
                  <th>Tipo</th>
                  <th>Email</th>
                  <th>Contraseña</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><span class="badge bg-danger">ADMIN</span></td>
                  <td>admin@ecommerce.com</td>
                  <td>admin123</td>
                  <td>
                    <button @click="quickLogin('admin@ecommerce.com', 'admin123')" class="btn btn-sm btn-outline-primary">
                      Probar
                    </button>
                  </td>
                </tr>
                <tr>
                  <td><span class="badge bg-info">USER</span></td>
                  <td>juan.perez@gmail.com</td>
                  <td>user123</td>
                  <td>
                    <button @click="quickLogin('juan.perez@gmail.com', 'user123')" class="btn btn-sm btn-outline-primary">
                      Probar
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '../config/axios'

// Estados de carga
const loadingUsuarios = ref(false)
const loadingLogin = ref(false)
const loadingProductos = ref(false)

// Respuestas
const usuariosResponse = ref(null)
const loginResponse = ref(null)
const productosResponse = ref(null)

// Datos de login
const loginData = ref({
  email: 'admin@ecommerce.com',
  password: 'admin123'
})

// TEST 1: Cargar usuarios
const testUsuarios = async () => {
  loadingUsuarios.value = true
  usuariosResponse.value = null

  try {
    const response = await axios.get('/api/usuarios')

    usuariosResponse.value = {
      success: true,
      url: 'http://localhost:8080/api/usuarios',
      method: 'GET',
      data: response.data
    }
  } catch (error) {
    console.error('Error al cargar usuarios:', error)

    usuariosResponse.value = {
      success: false,
      url: 'http://localhost:8080/api/usuarios',
      method: 'GET',
      data: {
        error: error.message,
        response: error.response?.data || 'Sin respuesta del servidor',
        status: error.response?.status || 'N/A'
      }
    }
  } finally {
    loadingUsuarios.value = false
  }
}

// TEST 2: Login
const testLogin = async () => {
  loadingLogin.value = true
  loginResponse.value = null

  try {
    const response = await axios.post('/api/auth/login', {
      email: loginData.value.email,
      password: loginData.value.password
    })

    loginResponse.value = {
      success: true,
      url: 'http://localhost:8080/api/auth/login',
      method: 'POST',
      data: response.data
    }
  } catch (error) {
    console.error('Error en login:', error)

    loginResponse.value = {
      success: false,
      url: 'http://localhost:8080/api/auth/login',
      method: 'POST',
      data: {
        error: error.message,
        response: error.response?.data || 'Sin respuesta del servidor',
        status: error.response?.status || 'N/A'
      }
    }
  } finally {
    loadingLogin.value = false
  }
}

// TEST 3: Cargar productos
const testProductos = async () => {
  loadingProductos.value = true
  productosResponse.value = null

  try {
    const response = await axios.get('/api/products')

    productosResponse.value = {
      success: true,
      url: 'http://localhost:8080/api/products',
      method: 'GET',
      data: response.data
    }
  } catch (error) {
    console.error('Error al cargar productos:', error)

    productosResponse.value = {
      success: false,
      url: 'http://localhost:8080/api/products',
      method: 'GET',
      data: {
        error: error.message,
        response: error.response?.data || 'Sin respuesta del servidor',
        status: error.response?.status || 'N/A'
      }
    }
  } finally {
    loadingProductos.value = false
  }
}

// Quick login
const quickLogin = (email, password) => {
  loginData.value.email = email
  loginData.value.password = password
  testLogin()
}
</script>

<style scoped>
.card {
  border: none;
  border-radius: 15px;
}

.card-header {
  border-radius: 15px 15px 0 0;
}

pre {
  font-size: 12px;
  max-height: 500px;
  overflow-y: auto;
}

.btn {
  transition: all 0.3s ease;
}

.btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.table {
  font-size: 14px;
}

.badge {
  font-size: 11px;
}
</style>

