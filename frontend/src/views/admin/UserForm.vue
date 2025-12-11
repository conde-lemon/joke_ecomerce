<template>
  <div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h3 class="mb-0">
        <i class="bi bi-person-gear"></i>
        {{ isEditing ? 'Editar Usuario' : 'Nuevo Usuario' }}
      </h3>
      <router-link to="/admin/users" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Volver
      </router-link>
    </div>

    <div class="card">
      <div class="card-body">
        <div v-if="generalError" class="alert alert-danger">
          {{ generalError }}
        </div>

        <form @submit.prevent="handleSubmit" novalidate>
          <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Correo</label>
            <div class="col-sm-10">
              <input
                v-model="form.correo"
                type="email"
                class="form-control"
                :class="{ 'is-invalid': errors.correo }"
                :readonly="isEditing"
                placeholder="correo@ejemplo.com"
              >
              <div v-if="errors.correo" class="invalid-feedback">
                {{ errors.correo }}
              </div>
            </div>
          </div>

          <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Usuario</label>
            <div class="col-sm-10">
              <input
                v-model="form.usuario"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': errors.usuario }"
                placeholder="Nombre de usuario"
              >
              <div v-if="errors.usuario" class="invalid-feedback">
                {{ errors.usuario }}
              </div>
            </div>
          </div>

          <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Nombre completo</label>
            <div class="col-sm-10">
              <input
                v-model="form.nombre"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': errors.nombre }"
                placeholder="Nombre y apellidos"
              >
              <div v-if="errors.nombre" class="invalid-feedback">
                {{ errors.nombre }}
              </div>
            </div>
          </div>

          <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Contraseña</label>
            <div class="col-sm-10">
              <input
                v-model="form.contrasena"
                type="password"
                class="form-control"
                :class="{ 'is-invalid': errors.contrasena }"
                :placeholder="isEditing ? 'Deja en blanco para mantener la contraseña actual' : 'Mínimo 6 caracteres'"
              >
              <div v-if="errors.contrasena" class="invalid-feedback">
                {{ errors.contrasena }}
              </div>
              <small v-else class="form-text text-muted">
                {{ isEditing ? 'Deja este campo vacío si no deseas cambiar la contraseña.' : 'La contraseña debe tener al menos 6 caracteres.' }}
              </small>
            </div>
          </div>

          <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Roles</label>
            <div class="col-sm-10">
              <div class="form-check form-check-inline">
                <input
                  id="role-user"
                  v-model="roleUser"
                  class="form-check-input"
                  type="checkbox"
                >
                <label class="form-check-label" for="role-user">Usuario</label>
              </div>
              <div class="form-check form-check-inline">
                <input
                  id="role-admin"
                  v-model="roleAdmin"
                  class="form-check-input"
                  type="checkbox"
                >
                <label class="form-check-label" for="role-admin">Administrador</label>
              </div>
              <div v-if="errors.roles" class="text-danger small mt-1">
                {{ errors.roles }}
              </div>
              <small v-else class="form-text text-muted">
                Selecciona al menos un rol. Normalmente todos los usuarios deben tener el rol USER.
              </small>
            </div>
          </div>

          <div class="d-flex justify-content-end gap-2 mt-4">
            <router-link to="/admin/users" class="btn btn-secondary">
              Cancelar
            </router-link>
            <button type="submit" class="btn btn-primary" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              {{ saving ? 'Guardando...' : 'Guardar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '../../config/axios'

export default {
  name: 'UserForm',
  setup() {
    const route = useRoute()
    const router = useRouter()

    const isEditing = computed(() => !!route.params.id)

    const form = ref({
      correo: '',
      usuario: '',
      nombre: '',
      contrasena: '',
      roles: 'USER'
    })

    const errors = ref({})
    const generalError = ref('')
    const saving = ref(false)

    const roleUser = ref(true)
    const roleAdmin = ref(false)

    const loadUser = async () => {
      if (!isEditing.value) return

      try {
        const correo = decodeURIComponent(route.params.id)
        const response = await axios.get(`/api/users/${encodeURIComponent(correo)}`)
        const user = response.data

        form.value.correo = user.correo || ''
        form.value.usuario = user.usuario || ''
        form.value.nombre = user.nombre || ''
        form.value.contrasena = ''
        form.value.roles = user.roles || 'USER'

        const rolesArr = (user.roles || 'USER').split(',').map(r => r.trim())
        roleUser.value = rolesArr.includes('USER')
        roleAdmin.value = rolesArr.includes('ADMIN')
      } catch (error) {
        console.error('Error al cargar usuario:', error)
        generalError.value = error.response?.status === 404
          ? 'Usuario no encontrado'
          : 'Error al cargar usuario'
      }
    }

    const validate = () => {
      const newErrors = {}

      if (!form.value.correo) {
        newErrors.correo = 'El correo es obligatorio'
      }

      if (!form.value.usuario) {
        newErrors.usuario = 'El nombre de usuario es obligatorio'
      }

      if (!form.value.nombre) {
        newErrors.nombre = 'El nombre es obligatorio'
      }

      if (!isEditing.value) {
        if (!form.value.contrasena) {
          newErrors.contrasena = 'La contraseña es obligatoria para nuevos usuarios'
        } else if (form.value.contrasena.length < 6) {
          newErrors.contrasena = 'La contraseña debe tener al menos 6 caracteres'
        }
      } else if (form.value.contrasena && form.value.contrasena.length < 6) {
        newErrors.contrasena = 'Si cambias la contraseña, debe tener al menos 6 caracteres'
      }

      if (!roleUser.value && !roleAdmin.value) {
        newErrors.roles = 'Debes seleccionar al menos un rol'
      }

      errors.value = newErrors
      return Object.keys(newErrors).length === 0
    }

    const buildRolesString = () => {
      const roles = []
      if (roleUser.value) roles.push('USER')
      if (roleAdmin.value) roles.push('ADMIN')
      return roles.join(',') || 'USER'
    }

    const handleSubmit = async () => {
      generalError.value = ''
      if (!validate()) return

      saving.value = true

      try {
        const payload = {
          correo: form.value.correo,
          usuario: form.value.usuario,
          nombre: form.value.nombre,
          contrasena: form.value.contrasena,
          roles: buildRolesString()
        }

        if (isEditing.value) {
          const correo = decodeURIComponent(route.params.id)
          await axios.put(`/api/users/${encodeURIComponent(correo)}`, payload)
        } else {
          await axios.post('/api/users', payload)
        }

        alert('Usuario guardado correctamente')
        router.push('/admin/users')
      } catch (error) {
        console.error('Error al guardar usuario:', error)
        const message = error.response?.data || 'Error al guardar usuario'
        generalError.value = typeof message === 'string' ? message : (message.error || 'Error al guardar usuario')
      } finally {
        saving.value = false
      }
    }

    onMounted(() => {
      loadUser()
    })

    return {
      isEditing,
      form,
      errors,
      generalError,
      saving,
      roleUser,
      roleAdmin,
      handleSubmit
    }
  }
}
</script>
