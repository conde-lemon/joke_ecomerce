<template>
  <div class="container my-4">
    <h3>{{ isEditing ? 'Editar Usuario' : 'Nuevo Usuario' }}</h3>
    <div class="alert alert-info">Formulario de usuario - Implementar</div>
    <router-link to="/admin/users" class="btn btn-secondary">Volver</router-link>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'UserForm',
  setup() {
    const route = useRoute()
    const isEditing = computed(() => !!route.params.id)

    return { isEditing }
  }
}
</script>

