<template>
  <div class="container my-5">
    <h2 class="text-custom-primary mb-4">🧪 Panel de Pruebas de Autenticación</h2>

    <!-- Estado de Sesión -->
    <div class="card card-custom mb-4">
      <div class="card-header card-header-custom-dark">
        <h5 class="mb-0"><i class="bi bi-info-circle"></i> Estado de Sesión</h5>
      </div>
      <div class="card-body">
        <button @click="checkAuth" class="btn btn-custom-secondary mb-3">
          <i class="bi bi-arrow-clockwise"></i> Verificar Sesión
        </button>
        <pre v-if="authStatus" class="bg-light p-3 rounded">{{ JSON.stringify(authStatus, null, 2) }}</pre>
      </div>
    </div>

    <!-- Test Login -->
    <div class="card card-custom mb-4">
      <div class="card-header card-header-custom-primary">
        <h5 class="mb-0"><i class="bi bi-key"></i> Test Login</h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-5">
            <input v-model="loginData.email" type="email" class="form-control" placeholder="Email">
          </div>
          <div class="col-md-5">
            <input v-model="loginData.password" type="password" class="form-control" placeholder="Password">
          </div>
          <div class="col-md-2">
            <button @click="testLogin" class="btn btn-custom-primary w-100">Login</button>
          </div>
        </div>
        <div class="mt-2 small text-muted">
          <strong>Usuarios de prueba:</strong><br>
          admin@example.com / admin<br>
          user@example.com / password
        </div>
        <pre v-if="loginResult" class="bg-light p-3 rounded mt-3">{{ JSON.stringify(loginResult, null, 2) }}</pre>
      </div>
    </div>

    <!-- Verificar Contraseña -->
    <div class="card card-custom mb-4">
      <div class="card-header card-header-custom-secondary">
        <h5 class="mb-0"><i class="bi bi-shield-check"></i> Verificar Contraseña</h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-5">
            <input v-model="verifyData.email" type="email" class="form-control" placeholder="Email">
          </div>
          <div class="col-md-5">
            <input v-model="verifyData.password" type="password" class="form-control" placeholder="Password a verificar">
          </div>
          <div class="col-md-2">
            <button @click="verifyPassword" class="btn btn-custom-secondary w-100">Verificar</button>
          </div>
        </div>
        <pre v-if="verifyResult" class="bg-light p-3 rounded mt-3">{{ JSON.stringify(verifyResult, null, 2) }}</pre>
      </div>
    </div>

    <!-- Crear Usuario -->
    <div class="card card-custom mb-4">
      <div class="card-header card-header-custom-primary">
        <h5 class="mb-0"><i class="bi bi-person-plus"></i> Crear Usuario de Prueba</h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <input v-model="newUser.email" type="email" class="form-control" placeholder="Email">
          </div>
          <div class="col-md-3">
            <input v-model="newUser.username" type="text" class="form-control" placeholder="Username">
          </div>
          <div class="col-md-2">
            <input v-model="newUser.nombre" type="text" class="form-control" placeholder="Nombre">
          </div>
          <div class="col-md-2">
            <input v-model="newUser.password" type="password" class="form-control" placeholder="Password">
          </div>
          <div class="col-md-2">
            <button @click="createUser" class="btn btn-custom-primary w-100">Crear</button>
          </div>
        </div>
        <div class="mt-2">
          <label class="form-label small">Roles:</label>
          <select v-model="newUser.roles" class="form-select form-select-sm">
            <option value="USER">USER</option>
            <option value="USER,ADMIN">USER,ADMIN</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </div>
        <pre v-if="createResult" class="bg-light p-3 rounded mt-3">{{ JSON.stringify(createResult, null, 2) }}</pre>
      </div>
    </div>

    <!-- Test Checkout -->
    <div class="card card-custom mb-4">
      <div class="card-header card-header-custom-dark">
        <h5 class="mb-0"><i class="bi bi-cart-check"></i> Test Checkout</h5>
      </div>
      <div class="card-body">
        <p class="text-muted">
          <strong>Pasos para probar:</strong><br>
          1. Hacer login arriba<br>
          2. Agregar productos al carrito desde el catálogo<br>
          3. Click aquí para crear pedido
        </p>
        <button @click="testCheckout" class="btn btn-custom-dark">
          <i class="bi bi-play"></i> Crear Pedido de Prueba
        </button>
        <pre v-if="checkoutResult" class="bg-light p-3 rounded mt-3">{{ JSON.stringify(checkoutResult, null, 2) }}</pre>
      </div>
    </div>

    <!-- Info de Sesión -->
    <div class="card card-custom">
      <div class="card-header card-header-custom-secondary">
        <h5 class="mb-0"><i class="bi bi-clock-history"></i> Info de Sesión</h5>
      </div>
      <div class="card-body">
        <button @click="getSessionInfo" class="btn btn-outline-custom-secondary mb-3">
          Ver Info de Sesión
        </button>
        <pre v-if="sessionInfo" class="bg-light p-3 rounded">{{ JSON.stringify(sessionInfo, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import axios from '../config/axios'

export default {
  name: 'TestPanel',
  setup() {
    const authStatus = ref(null)
    const loginResult = ref(null)
    const verifyResult = ref(null)
    const createResult = ref(null)
    const checkoutResult = ref(null)
    const sessionInfo = ref(null)

    const loginData = ref({
      email: 'admin@example.com',
      password: 'admin'
    })

    const verifyData = ref({
      email: '',
      password: ''
    })

    const newUser = ref({
      email: '',
      username: '',
      nombre: '',
      password: '',
      roles: 'USER'
    })

    const checkAuth = async () => {
      try {
        const res = await axios.get('/api/test/auth')
        authStatus.value = res.data
      } catch (err) {
        authStatus.value = { error: err.message }
      }
    }

    const testLogin = async () => {
      try {
        const res = await axios.post('/api/test/login', loginData.value)
        loginResult.value = res.data
        if (res.data.success) {
          alert('Login exitoso!')
          checkAuth()
        }
      } catch (err) {
        loginResult.value = err.response?.data || { error: err.message }
      }
    }

    const verifyPassword = async () => {
      try {
        const res = await axios.post('/api/test/verify-password', verifyData.value)
        verifyResult.value = res.data
      } catch (err) {
        verifyResult.value = { error: err.message }
      }
    }

    const createUser = async () => {
      try {
        const res = await axios.post('/api/test/create-user', newUser.value)
        createResult.value = res.data
        if (res.data.success) {
          alert('Usuario creado exitosamente!')
          // Limpiar formulario
          newUser.value = { email: '', username: '', nombre: '', password: '', roles: 'USER' }
        }
      } catch (err) {
        createResult.value = err.response?.data || { error: err.message }
      }
    }

    const testCheckout = async () => {
      try {
        const res = await axios.post('/api/orders', {})
        checkoutResult.value = res.data
        if (res.data.id) {
          alert(`Pedido creado: #${res.data.id}`)
        }
      } catch (err) {
        checkoutResult.value = err.response?.data || { error: err.message }
      }
    }

    const getSessionInfo = async () => {
      try {
        const res = await axios.get('/api/test/session-info')
        sessionInfo.value = res.data
      } catch (err) {
        sessionInfo.value = { error: err.message }
      }
    }

    return {
      authStatus,
      loginResult,
      verifyResult,
      createResult,
      checkoutResult,
      sessionInfo,
      loginData,
      verifyData,
      newUser,
      checkAuth,
      testLogin,
      verifyPassword,
      createUser,
      testCheckout,
      getSessionInfo
    }
  }
}
</script>

<style scoped>
pre {
  font-size: 0.85rem;
  max-height: 300px;
  overflow-y: auto;
}
</style>

