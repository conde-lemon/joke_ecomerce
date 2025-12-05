<template>
  <div class="container my-4">
    <h3 class="mb-4">Mi Perfil</h3>

    <div v-if="user" class="row">
      <div class="col-md-6">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Información Personal</h5>
            <p><strong>Nombre:</strong> {{ user.nombre }}</p>
            <p><strong>Usuario:</strong> {{ user.usuario }}</p>
            <p><strong>Correo:</strong> {{ user.correo }}</p>
            <p><strong>Miembro desde:</strong> {{ formatDate(user.fechaCreacion) }}</p>
            <button class="btn btn-primary">Editar Perfil</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'UserProfile',
  setup() {
    const authStore = useAuthStore()
    const user = ref(null)

    onMounted(() => {
      user.value = authStore.user
    })

    const formatDate = (date) => new Date(date).toLocaleDateString('es-PE')

    return { user, formatDate }
  }
}
</script>

