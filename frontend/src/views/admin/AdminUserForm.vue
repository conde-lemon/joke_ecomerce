<template>
  <div class="container my-4">
    <div class="card mx-auto" style="max-width: 720px;">
      <div class="card-header">
        <h4>{{ isEdit ? 'Editar Usuario' : 'Crear Usuario' }}</h4>
      </div>

      <!-- Spinner de carga -->
      <div v-if="loading" class="card-body text-center py-5">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="mt-2 text-muted">Cargando datos del usuario...</p>
      </div>

      <!-- Formulario -->
      <div v-else class="card-body">

          <div class="mb-3">
            <label class="form-label">Usuario</label>
            <input
              v-model="form.correo"
              type="email"
              class="form-control"
              :readonly="isEdit"
              required
              placeholder="usuario@ejemplo.com"
            >
          </div>

          <div class="mb-3 form-check">
            <label class="form-label">Nombre Completo</label>
            <input
              v-model="form.nombre"
              type="text"
              class="form-control"
              required
              placeholder="Juan Pérez"
            >
        </form>
      </div>
    </div>
            <label class="form-label">Nombre de Usuario</label>
            <input
              v-model="form.usuario"
              type="text"
              class="form-control"
              required
              placeholder="juanperez"
            >

export default {
  name: 'AdminUserForm',
  setup() {
            <input
              v-model="form.contrasena"
              type="password"
              class="form-control"
              :placeholder="isEdit ? 'Dejar en blanco para no cambiar' : 'Mínimo 6 caracteres'"
              :required="!isEdit"
            >
            <small class="text-muted d-block mt-1" v-if="isEdit">
              Si dejas este campo en blanco, se mantendrá la contraseña actual.
            </small>
      nombre: '',
      usuario: '',
      contrasena: '',
            <input
              id="isAdmin"
              class="form-check-input"
              type="checkbox"
              v-model="isAdmin"
            >
            <label for="isAdmin" class="form-check-label">
              Conceder permisos de Administrador (ADMIN)
            </label>
          </div>

          <!-- Campo oculto para roles -->
          <input
            type="hidden"
            v-model="form.roles"
          >

          <!-- Mensajes de error/éxito -->
          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
        router.push('/admin/users')
      } finally {
          <div class="d-flex justify-content-end gap-2 mt-4">
            <router-link to="/admin/users" class="btn btn-secondary">
              <i class="bi bi-x-circle"></i> Cancelar
            </router-link>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              <i class="bi bi-check-circle"></i>
              {{ isEdit ? 'Guardar cambios' : 'Crear usuario' }}
            </button>
      const hasAdminChecked = isAdmin.value

      let finalRoles = [...withoutAdmin]
      if (hasAdminChecked) finalRoles.push('ADMIN')
      if (!hasAdminChecked && finalRoles.length === 0) finalRoles = ['USER']
      form.value.roles = Array.from(new Set(finalRoles)).join(',')

      try {
        if (isEdit.value) {
          const payload = { ...form.value }
          if (!payload.contrasena) delete payload.contrasena
          await axios.put(`/api/users/${encodeURIComponent(form.value.correo)}`, payload)
          alert('Usuario actualizado')
        } else {
          await axios.post('/api/users', form.value)
          alert('Usuario creado')
        }
        router.push('/admin/users')
      } catch (err) {

        alert('Error al guardar usuario')
      }
    }

    const errorMessage = ref('')
      if (isEdit.value) loadUser()
    })

    return {
      form,
      isEdit,
      isAdmin,
      loading,
      submit
    }
  }
}
</script>

<style scoped>
      errorMessage.value = ''

