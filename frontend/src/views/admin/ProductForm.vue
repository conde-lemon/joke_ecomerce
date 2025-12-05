<template>
  <div class="container my-4">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <div class="card shadow">
          <div class="card-header bg-primary text-white">
            <h3 class="mb-0">
              <i class="bi" :class="isEditing ? 'bi-pencil-square' : 'bi-plus-circle'"></i>
              {{ isEditing ? 'Editar Producto' : 'Nuevo Producto' }}
            </h3>
          </div>
          <div class="card-body">
            <!-- Mensajes -->
            <div v-if="successMessage" class="alert alert-success alert-dismissible fade show">
              {{ successMessage }}
              <button type="button" class="btn-close" @click="successMessage = ''"></button>
            </div>
            <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show">
              {{ errorMessage }}
              <button type="button" class="btn-close" @click="errorMessage = ''"></button>
            </div>

            <!-- Loading -->
            <div v-if="loading" class="text-center py-5">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
              </div>
            </div>

            <!-- Formulario -->
            <form v-else @submit.prevent="handleSubmit">
              <div class="row">
                <!-- Nombre -->
                <div class="col-md-12 mb-3">
                  <label for="nombre" class="form-label">Nombre del Producto *</label>
                  <input
                    v-model="formData.nombre"
                    type="text"
                    class="form-control"
                    id="nombre"
                    required
                    maxlength="100"
                  >
                </div>

                <!-- Precio -->
                <div class="col-md-6 mb-3">
                  <label for="precio" class="form-label">Precio (S/) *</label>
                  <input
                    v-model.number="formData.precio"
                    type="number"
                    class="form-control"
                    id="precio"
                    step="0.01"
                    min="0"
                    required
                  >
                </div>

                <!-- Stock -->
                <div class="col-md-6 mb-3">
                  <label for="stock" class="form-label">Stock *</label>
                  <input
                    v-model.number="formData.stock"
                    type="number"
                    class="form-control"
                    id="stock"
                    min="0"
                    required
                  >
                </div>

                <!-- Descripción -->
                <div class="col-md-12 mb-3">
                  <label for="descripcion" class="form-label">Descripción</label>
                  <textarea
                    v-model="formData.descripcion"
                    class="form-control"
                    id="descripcion"
                    rows="4"
                  ></textarea>
                </div>

                <!-- URL de Imagen -->
                <div class="col-md-12 mb-3">
                  <label for="imageUrl" class="form-label">URL de Imagen</label>
                  <input
                    v-model="formData.imageUrl"
                    type="text"
                    class="form-control"
                    id="imageUrl"
                    placeholder="/img/p1.jpg"
                  >
                  <div class="form-text">
                    Ingresa la ruta de la imagen (ej: /img/p1.jpg, /img/p2.jpg, etc.)
                  </div>

                  <!-- Selector de imágenes predefinidas -->
                  <div class="mt-3">
                    <label class="form-label">O selecciona una imagen predefinida:</label>
                    <div class="row g-2">
                      <div
                        v-for="img in availableImages"
                        :key="img"
                        class="col-2"
                      >
                        <div
                          class="image-selector"
                          :class="{ 'selected': formData.imageUrl === img }"
                          @click="formData.imageUrl = img"
                        >
                          <img :src="img" :alt="img" class="img-fluid rounded">
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Vista previa de imagen -->
                <div v-if="formData.imageUrl" class="col-md-12 mb-3">
                  <label class="form-label">Vista Previa:</label>
                  <div class="text-center">
                    <img
                      :src="formData.imageUrl"
                      alt="Vista previa"
                      class="img-thumbnail"
                      style="max-width: 300px; max-height: 300px; object-fit: cover;"
                      @error="handleImageError"
                    >
                  </div>
                </div>

                <!-- Estado Activo -->
                <div class="col-md-12 mb-3">
                  <div class="form-check form-switch">
                    <input
                      v-model="formData.activo"
                      class="form-check-input"
                      type="checkbox"
                      id="activo"
                    >
                    <label class="form-check-label" for="activo">
                      Producto Activo
                    </label>
                  </div>
                </div>
              </div>

              <!-- Botones -->
              <div class="d-flex gap-2 justify-content-end">
                <router-link to="/admin/products" class="btn btn-secondary">
                  <i class="bi bi-x-circle"></i> Cancelar
                </router-link>
                <button type="submit" class="btn btn-primary" :disabled="saving">
                  <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="bi bi-save"></i>
                  {{ saving ? 'Guardando...' : 'Guardar' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '../../config/axios'

export default {
  name: 'ProductForm',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const isEditing = computed(() => !!route.params.id)

    const formData = ref({
      nombre: '',
      precio: 0,
      stock: 0,
      descripcion: '',
      imageUrl: '',
      activo: true
    })

    const loading = ref(false)
    const saving = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')

    // Imágenes disponibles
    const availableImages = [
      '/img/p1.jpg',
      '/img/p2.jpg',
      '/img/p3.jpg',
      '/img/p4.jpg',
      '/img/p5.jpg',
      '/img/p6.jpg',
      '/img/p7.jpg',
      '/img/p8.jpg',
      '/img/p9.jpg',
      '/img/p10.jpg',
      '/img/p11.jpg',
      '/img/p12.jpg',
      '/img/p13.jpg',
      '/img/p14.jpg',
      '/img/p15.jpg',
      '/img/p16.jpg',
      '/img/p17.jpg',
      '/img/p18.jpg',
      '/img/p19.jpg',
      '/img/p20.jpg'
    ]

    const fetchProduct = async () => {
      if (!isEditing.value) return

      loading.value = true
      try {
        const response = await axios.get(`/api/products/${route.params.id}`)
        formData.value = { ...response.data }
      } catch (error) {
        console.error('Error al cargar producto:', error)
        errorMessage.value = 'Error al cargar el producto'
      } finally {
        loading.value = false
      }
    }

    const handleSubmit = async () => {
      saving.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        if (isEditing.value) {
          await axios.put(`/api/products/${route.params.id}`, formData.value)
          successMessage.value = 'Producto actualizado correctamente'
        } else {
          await axios.post('/api/products', formData.value)
          successMessage.value = 'Producto creado correctamente'
        }

        setTimeout(() => {
          router.push('/admin/products')
        }, 1500)
      } catch (error) {
        console.error('Error al guardar producto:', error)
        errorMessage.value = error.response?.data?.error || 'Error al guardar el producto'
      } finally {
        saving.value = false
      }
    }

    const handleImageError = (event) => {
      event.target.style.display = 'none'
      errorMessage.value = 'La imagen no se puede cargar. Verifica la URL.'
    }

    onMounted(() => {
      fetchProduct()
    })

    return {
      isEditing,
      formData,
      loading,
      saving,
      successMessage,
      errorMessage,
      availableImages,
      handleSubmit,
      handleImageError
    }
  }
}
</script>

<style scoped>
.image-selector {
  cursor: pointer;
  border: 3px solid transparent;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.2s;
}

.image-selector:hover {
  border-color: #0d6efd;
  transform: scale(1.05);
}

.image-selector.selected {
  border-color: #198754;
  box-shadow: 0 0 10px rgba(25, 135, 84, 0.5);
}

.image-selector img {
  width: 100%;
  height: 80px;
  object-fit: cover;
}
</style>

