<template>
  <div class="container-fluid my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2><i class="bi bi-people"></i> Gestión de Usuarios</h2>
      <router-link to="/admin/users/new" class="btn btn-success">
        <i class="bi bi-person-plus"></i> Nuevo Usuario
      </router-link>
    </div>

    <!-- Filtros -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row">
          <div class="col-md-8">
            <input
              v-model="searchQuery"
              type="text"
              class="form-control"
              placeholder="Buscar por nombre, correo o usuario..."
              @input="filterUsers"
            >
          </div>
          <div class="col-md-4">
            <select v-model="roleFilter" class="form-select" @change="filterUsers">
              <option value="">Todos los roles</option>
              <option value="USER">Usuarios</option>
              <option value="ADMIN">Administradores</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Tabla de Usuarios -->
    <div class="card">
      <div class="card-body">
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
        </div>

        <div v-else-if="filteredUsers.length === 0" class="text-center py-5 text-muted">
          <i class="bi bi-people fs-1"></i>
          <p class="mt-3">No se encontraron usuarios</p>
        </div>

        <div v-else class="table-responsive">
          <table class="table table-hover">
            <thead class="table-dark">
              <tr>
                <th>Correo</th>
                <th>Usuario</th>
                <th>Nombre</th>
                <th>Roles</th>
                <th>Fecha Registro</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in paginatedUsers" :key="user.correo">
                <td>
                  <i class="bi bi-envelope"></i>
                  {{ user.correo }}
                </td>
                <td>
                  <i class="bi bi-person-circle"></i>
                  {{ user.usuario }}
                </td>
                <td>
                  <strong>{{ user.nombre }}</strong>
                </td>
                <td>
                  <span
                    v-for="role in getRolesArray(user.roles)"
                    :key="role"
                    class="badge me-1"
                    :class="role === 'ADMIN' ? 'bg-danger' : 'bg-primary'"
                  >
                    {{ role }}
                  </span>
                </td>
                <td>
                  <small class="text-muted">
                    {{ formatDate(user.fechaCreacion) }}
                  </small>
                </td>
                <td>
                  <div class="btn-group btn-group-sm">
                    <router-link
                      :to="`/admin/users/edit/${encodeURIComponent(user.correo)}`"
                      class="btn btn-outline-primary"
                      title="Editar"
                    >
                      <i class="bi bi-pencil"></i>
                    </router-link>
                    <button
                      @click="toggleAdmin(user)"
                      class="btn btn-outline-warning"
                      :title="isAdmin(user) ? 'Quitar admin' : 'Hacer admin'"
                    >
                      <i class="bi bi-shield-check"></i>
                    </button>
                    <button
                      @click="deleteUser(user)"
                      class="btn btn-outline-danger"
                      title="Eliminar"
                      :disabled="user.correo === currentUser?.correo"
                    >
                      <i class="bi bi-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- Paginación -->
          <nav v-if="totalPages > 1">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <a class="page-link" href="#" @click.prevent="currentPage--">Anterior</a>
              </li>
              <li
                v-for="page in totalPages"
                :key="page"
                class="page-item"
                :class="{ active: currentPage === page }"
              >
                <a class="page-link" href="#" @click.prevent="currentPage = page">{{ page }}</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <a class="page-link" href="#" @click.prevent="currentPage++">Siguiente</a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import axios from '../../config/axios'
import { useAuthStore } from '../../stores/auth'

export default {
  name: 'AdminUsers',
  setup() {
    const authStore = useAuthStore()
    const users = ref([])
    const loading = ref(false)
    const searchQuery = ref('')
    const roleFilter = ref('')
    const currentPage = ref(1)
    const itemsPerPage = 10

    const currentUser = computed(() => authStore.user)

    const fetchUsers = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/users')
        users.value = response.data
      } catch (error) {
        console.error('Error al cargar usuarios:', error)
        alert('Error al cargar usuarios')
      } finally {
        loading.value = false
      }
    }

    const filteredUsers = computed(() => {
      let result = users.value

      // Filtro por búsqueda
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(u =>
          u.correo.toLowerCase().includes(query) ||
          u.usuario.toLowerCase().includes(query) ||
          u.nombre.toLowerCase().includes(query)
        )
      }

      // Filtro por rol
      if (roleFilter.value) {
        result = result.filter(u => u.roles.includes(roleFilter.value))
      }

      return result
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredUsers.value.length / itemsPerPage)
    })

    const paginatedUsers = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage
      const end = start + itemsPerPage
      return filteredUsers.value.slice(start, end)
    })

    const filterUsers = () => {
      currentPage.value = 1
    }

    const getRolesArray = (roles) => {
      return roles ? roles.split(',').map(r => r.trim()) : []
    }

    const isAdmin = (user) => {
      return user.roles.includes('ADMIN')
    }

    const toggleAdmin = async (user) => {
      const hasAdmin = isAdmin(user)
      const action = hasAdmin ? 'quitar' : 'dar'

      if (!confirm(`¿Deseas ${action} privilegios de administrador a "${user.nombre}"?`)) return

      try {
        let newRolesArr = getRolesArray(user.roles)
        if (hasAdmin) {
          newRolesArr = newRolesArr.filter(r => r !== 'ADMIN')
          if (newRolesArr.length === 0) newRolesArr = ['USER']
        } else {
          if (!newRolesArr.includes('ADMIN')) newRolesArr.push('ADMIN')
        }
        const newRoles = newRolesArr.join(',')

        const updated = { ...user, roles: newRoles }
        await axios.put(`/api/users/${user.correo}`, updated)
        user.roles = newRoles
        alert('Roles actualizados correctamente')
      } catch (error) {
        console.error('Error al actualizar roles:', error)
        alert('Error al actualizar roles')
      }
    }

    const deleteUser = async (user) => {
      if (user.correo === currentUser.value?.correo) {
        alert('No puedes eliminar tu propia cuenta')
        return
      }

      if (!confirm(`¿Eliminar el usuario "${user.nombre}"? Esta acción no se puede deshacer.`)) return

      try {
        await axios.delete(`/api/users/${user.correo}`)
        users.value = users.value.filter(u => u.correo !== user.correo)
        alert('Usuario eliminado correctamente')
      } catch (error) {
        console.error('Error al eliminar usuario:', error)
        alert('Error al eliminar usuario')
      }
    }

    const formatDate = (date) => {
      if (!date) return 'N/A'
      return new Date(date).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    }

    onMounted(() => {
      fetchUsers()
    })

    return {
      users,
      loading,
      searchQuery,
      roleFilter,
      currentPage,
      currentUser,
      filteredUsers,
      paginatedUsers,
      totalPages,
      filterUsers,
      getRolesArray,
      isAdmin,
      toggleAdmin,
      deleteUser,
      formatDate
    }
  }
}
</script>

<style scoped>
.table-hover tbody tr:hover {
  background-color: #f8f9fa;
}
</style>
