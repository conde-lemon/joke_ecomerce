<template>
  <div class="container my-5">
    <div class="row mb-4">
      <div class="col-12">
        <div class="card border-success shadow-lg">
          <div class="card-header bg-success text-white">
            <h2 class="mb-0">
              <i class="bi bi-database-check"></i> Test de Conexión con Base de Datos MySQL
            </h2>
          </div>
          <div class="card-body">
            <p class="lead">
              Esta página verifica la conexión entre el Backend (Spring Boot) y la Base de Datos (MySQL).
            </p>

            <!-- Botones de acción -->
            <div class="d-flex gap-2 mb-4">
              <button @click="testDatabaseConnection" class="btn btn-success btn-lg" :disabled="loading">
                <i class="bi bi-arrow-repeat"></i>
                {{ loading ? 'Probando...' : 'Probar Conexión BD' }}
              </button>
              <button @click="getSystemInfo" class="btn btn-info btn-lg" :disabled="loading">
                <i class="bi bi-info-circle"></i>
                Info del Sistema
              </button>
              <button @click="clearResults" class="btn btn-warning btn-lg" :disabled="loading">
                <i class="bi bi-trash"></i>
                Limpiar
              </button>
            </div>

            <!-- Spinner de carga -->
            <div v-if="loading" class="text-center py-5">
              <div class="spinner-border text-success" role="status" style="width: 4rem; height: 4rem;">
                <span class="visually-hidden">Cargando...</span>
              </div>
              <p class="mt-3 fs-5">Conectando con la base de datos...</p>
            </div>

            <!-- Resultado exitoso -->
            <div v-if="result && result.status === 'SUCCESS' && !loading" class="mt-4">
              <div class="alert alert-success d-flex align-items-center" role="alert">
                <i class="bi bi-check-circle-fill fs-2 me-3"></i>
                <div>
                  <h4 class="alert-heading mb-1">✅ Conexión Exitosa</h4>
                  <p class="mb-0">{{ result.message }}</p>
                </div>
              </div>

              <!-- Información de la base de datos -->
              <div class="card border-success mb-3">
                <div class="card-header bg-light">
                  <h5 class="mb-0"><i class="bi bi-database"></i> Información de la Base de Datos</h5>
                </div>
                <div class="card-body">
                  <div class="row">
                    <div class="col-md-6">
                      <table class="table table-striped">
                        <tbody>
                          <tr>
                            <th style="width: 200px;">Base de Datos:</th>
                            <td>
                              <span class="badge bg-primary fs-6">{{ result.database }}</span>
                            </td>
                          </tr>
                          <tr>
                            <th>Total de Usuarios:</th>
                            <td>
                              <span class="badge bg-success fs-6">{{ result.totalUsers }}</span>
                            </td>
                          </tr>
                          <tr>
                            <th>Timestamp:</th>
                            <td><code>{{ result.timestamp }}</code></td>
                          </tr>
                        </tbody>
                      </table>
                    </div>

                    <!-- Usuario de ejemplo -->
                    <div v-if="result.sampleUser" class="col-md-6">
                      <div class="card bg-light">
                        <div class="card-body">
                          <h6 class="card-title">👤 Usuario de Ejemplo:</h6>
                          <ul class="list-unstyled mb-0">
                            <li><strong>Correo:</strong> {{ result.sampleUser.correo }}</li>
                            <li><strong>Nombre:</strong> {{ result.sampleUser.nombre }}</li>
                            <li><strong>Roles:</strong> <span class="badge bg-info">{{ result.sampleUser.roles }}</span></li>
                          </ul>
                        </div>
                      </div>
                    </div>

                    <!-- Warning si no hay usuarios -->
                    <div v-if="result.warning" class="col-12 mt-3">
                      <div class="alert alert-warning">
                        <i class="bi bi-exclamation-triangle"></i> {{ result.warning }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Endpoints disponibles -->
              <div class="card border-info">
                <div class="card-header bg-info text-white">
                  <h5 class="mb-0"><i class="bi bi-link-45deg"></i> Endpoints Disponibles</h5>
                </div>
                <div class="card-body">
                  <ul class="list-group">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                      <code>GET /api/test/db-connection</code>
                      <span class="badge bg-success">Activo</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                      <code>GET /api/test/system-info</code>
                      <span class="badge bg-success">Activo</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                      <code>GET /api/products</code>
                      <span class="badge bg-success">Activo</span>
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <!-- Información del sistema -->
            <div v-if="systemInfo && !loading" class="mt-4">
              <div class="card border-primary">
                <div class="card-header bg-primary text-white">
                  <h5 class="mb-0"><i class="bi bi-cpu"></i> Información del Sistema</h5>
                </div>
                <div class="card-body">
                  <table class="table table-bordered">
                    <tbody>
                      <tr>
                        <th style="width: 250px;">Aplicación:</th>
                        <td>{{ systemInfo.application }}</td>
                      </tr>
                      <tr>
                        <th>Versión:</th>
                        <td><span class="badge bg-secondary">{{ systemInfo.version }}</span></td>
                      </tr>
                      <tr>
                        <th>Estado:</th>
                        <td>
                          <span class="badge" :class="systemInfo.status === 'RUNNING' ? 'bg-success' : 'bg-danger'">
                            {{ systemInfo.status }}
                          </span>
                        </td>
                      </tr>
                      <tr>
                        <th>Timestamp:</th>
                        <td><code>{{ systemInfo.timestamp }}</code></td>
                      </tr>
                      <tr v-if="systemInfo.databaseStats">
                        <th>Usuarios en BD:</th>
                        <td><span class="badge bg-info fs-6">{{ systemInfo.databaseStats.totalUsers }}</span></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            <!-- Error -->
            <div v-if="error && !loading" class="mt-4">
              <div class="alert alert-danger" role="alert">
                <h4 class="alert-heading">
                  <i class="bi bi-x-circle-fill"></i> Error de Conexión
                </h4>
                <p><strong>Mensaje:</strong> {{ error.message }}</p>
                <p v-if="error.details"><strong>Detalles:</strong> {{ error.details }}</p>
                <hr>
                <div>
                  <p class="mb-2"><strong>Posibles soluciones:</strong></p>
                  <ol>
                    <li>Verifica que Spring Boot esté corriendo en <code>http://localhost:8080</code></li>
                    <li>Verifica que MySQL esté corriendo</li>
                    <li>Verifica las credenciales en <code>application.properties</code></li>
                    <li>Verifica que la base de datos <code>sistema_pedidos</code> exista</li>
                    <li>Ejecuta el script <code>crear_base_datos.sql</code></li>
                  </ol>
                </div>
              </div>
            </div>

            <!-- Instrucciones -->
            <div class="card border-secondary mt-4">
              <div class="card-header bg-secondary text-white">
                <h5 class="mb-0"><i class="bi bi-info-circle"></i> Instrucciones</h5>
              </div>
              <div class="card-body">
                <h6>Para que esta prueba funcione correctamente:</h6>
                <ol>
                  <li>Asegúrate de que <strong>MySQL</strong> esté corriendo</li>
                  <li>Verifica que la base de datos <code>sistema_pedidos</code> exista</li>
                  <li>Verifica que el <strong>backend Spring Boot</strong> esté corriendo en el puerto <strong>8080</strong></li>
                  <li>Haz clic en <strong>"Probar Conexión BD"</strong></li>
                </ol>

                <div class="alert alert-info mb-0 mt-3">
                  <strong><i class="bi bi-lightbulb"></i> Tip:</strong> Si ves el mensaje "Conexión Exitosa",
                  significa que el backend puede comunicarse correctamente con MySQL.
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '../config/axios'

const loading = ref(false)
const result = ref(null)
const systemInfo = ref(null)
const error = ref(null)

const testDatabaseConnection = async () => {
  loading.value = true
  error.value = null
  result.value = null
  systemInfo.value = null

  try {
    const response = await axios.get('/api/test/db-connection')
    result.value = response.data
    console.log('✅ Conexión exitosa con la base de datos:', response.data)
  } catch (err) {
    console.error('❌ Error al conectar con la base de datos:', err)
    error.value = {
      message: err.response?.data?.message || err.message || 'Error desconocido',
      details: err.response?.data?.error || err.response?.statusText || 'No se pudo conectar con el servidor'
    }
  } finally {
    loading.value = false
  }
}

const getSystemInfo = async () => {
  loading.value = true
  error.value = null
  result.value = null
  systemInfo.value = null

  try {
    const response = await axios.get('/api/test/system-info')
    systemInfo.value = response.data
    console.log('✅ Información del sistema obtenida:', response.data)
  } catch (err) {
    console.error('❌ Error al obtener información del sistema:', err)
    error.value = {
      message: err.response?.data?.message || err.message || 'Error desconocido',
      details: err.response?.data?.error || err.response?.statusText || 'No se pudo conectar con el servidor'
    }
  } finally {
    loading.value = false
  }
}

const clearResults = () => {
  result.value = null
  systemInfo.value = null
  error.value = null
}
</script>

<style scoped>
.card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  border-bottom: 3px solid rgba(0, 0, 0, 0.1);
}

.btn-lg {
  padding: 12px 24px;
  font-size: 1.1rem;
}

code {
  background-color: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
}

.badge {
  padding: 8px 12px;
}

.table th {
  background-color: #f8f9fa;
}

.shadow-lg {
  box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.175) !important;
}
</style>

