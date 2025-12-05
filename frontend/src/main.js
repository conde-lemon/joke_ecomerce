import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'

// Importar Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// Importar Bootstrap Icons
import 'bootstrap-icons/font/bootstrap-icons.css'

// Importar tema personalizado
import './assets/theme.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Esperar a que la store cargue estado (si existe) antes de montar
import { useAuthStore } from './stores/auth'

;(async () => {
  const authStore = useAuthStore(pinia)
  try {
    await authStore.checkAuth()
  } catch (e) {
    // no hacemos nada, fallback a no autenticado
    console.warn('checkAuth falló:', e)
  }

  app.mount('#app')
})()
