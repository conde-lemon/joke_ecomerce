<template>
  <div class="container my-4">
    <h2>Prueba de Auth</h2>

    <div class="card p-3 mb-3">
      <h5>Login</h5>
      <div class="row g-2">
        <div class="col-md-5">
          <input v-model="email" type="email" class="form-control" placeholder="email" />
        </div>
        <div class="col-md-5">
          <input v-model="password" type="password" class="form-control" placeholder="password" />
        </div>
        <div class="col-md-2">
          <button class="btn btn-primary w-100" @click="doLogin">Login</button>
        </div>
      </div>
      <div v-if="loginResult" class="mt-2">
        <pre>{{ loginResult }}</pre>
      </div>
    </div>

    <div class="card p-3 mb-3">
      <h5>Endpoints</h5>
      <div class="d-flex gap-2">
        <button class="btn btn-outline-secondary" @click="getMe">GET /api/auth/me</button>
        <button class="btn btn-outline-secondary" @click="getUsers">GET /api/users (admin)</button>
        <button class="btn btn-outline-danger" @click="doLogout">Logout</button>
      </div>

      <div class="mt-2">
        <h6>Última respuesta</h6>
        <pre>{{ lastResponse }}</pre>
      </div>
    </div>

    <div class="card p-3">
      <h5>Instrucciones</h5>
      <ul>
        <li>Usa el login con credenciales que existan en la DB.</li>
        <li>Si tras hacer <strong>Login</strong> <em>GET /api/auth/me</em> devuelve 200 y datos del usuario, la cookie de sesión fue creada y enviada correctamente.</li>
        <li>Si <em>GET /api/users</em> devuelve 401, necesitas un usuario con rol ADMIN o el login no creó sesión.</li>
      </ul>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import axios from '../config/axios'

export default {
  name: 'TestAuth',
  setup() {
    const email = ref('')
    const password = ref('')
    const loginResult = ref('')
    const lastResponse = ref('')

    const doLogin = async () => {
      loginResult.value = ''
      lastResponse.value = ''
      try {
        const res = await axios.post('/api/auth/login', { email: email.value, password: password.value })
        loginResult.value = JSON.stringify(res.data, null, 2)
        lastResponse.value = `Status: ${res.status}\nBody: ${JSON.stringify(res.data, null, 2)}`
      } catch (err) {
        const msg = err.response ? (`Status: ${err.response.status}\nBody: ${JSON.stringify(err.response.data, null, 2)}`) : err.message
        loginResult.value = msg
        lastResponse.value = msg
      }
    }

    const getMe = async () => {
      lastResponse.value = ''
      try {
        const res = await axios.get('/api/auth/me')
        lastResponse.value = `Status: ${res.status}\nBody: ${JSON.stringify(res.data, null, 2)}`
      } catch (err) {
        lastResponse.value = err.response ? (`Status: ${err.response.status}\nBody: ${JSON.stringify(err.response.data, null, 2)}`) : err.message
      }
    }

    const getUsers = async () => {
      lastResponse.value = ''
      try {
        const res = await axios.get('/api/users')
        lastResponse.value = `Status: ${res.status}\nBody: ${JSON.stringify(res.data, null, 2)}`
      } catch (err) {
        lastResponse.value = err.response ? (`Status: ${err.response.status}\nBody: ${JSON.stringify(err.response.data, null, 2)}`) : err.message
      }
    }

    const doLogout = async () => {
      try {
        const res = await axios.post('/api/auth/logout')
        lastResponse.value = `Status: ${res.status}\nBody: ${JSON.stringify(res.data, null, 2)}`
        // limpiar UI
        loginResult.value = ''
        email.value = ''
        password.value = ''
      } catch (err) {
        lastResponse.value = err.response ? (`Status: ${err.response.status}\nBody: ${JSON.stringify(err.response.data, null, 2)}`) : err.message
      }
    }

    return {
      email,
      password,
      doLogin,
      loginResult,
      getMe,
      getUsers,
      lastResponse,
      doLogout
    }
  }
}
</script>

<style scoped>
pre {
  background: #f8f9fa;
  padding: 10px;
}
</style>

