<template>
  <nav class="navbar navbar-expand-lg bg-custom-dark shadow">
    <div class="container-fluid">
      <router-link class="navbar-brand text-custom-primary fw-bold" to="/">
        <i class="bi bi-shop fs-4 me-2"></i>
        AZ-Store
      </router-link>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/">
              <i class="bi bi-house-door"></i> Inicio
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/catalog">
              <i class="bi bi-grid"></i> Catálogo
            </router-link>
          </li>

          <!-- Menú Admin Expandido -->
          <li v-if="isAdmin" class="nav-item dropdown">
            <a class="nav-link dropdown-toggle text-custom-primary" href="#" role="button" data-bs-toggle="dropdown">
              <i class="bi bi-gear-fill"></i> Administración
            </a>
            <ul class="dropdown-menu">
              <li>
                <router-link class="dropdown-item" to="/admin">
                  <i class="bi bi-speedometer2 me-2"></i>Dashboard
                </router-link>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <router-link class="dropdown-item" to="/admin/products">
                  <i class="bi bi-box-seam me-2"></i>Gestión de Productos
                </router-link>
              </li>
              <li>
                <router-link class="dropdown-item" to="/admin/products/new">
                  <i class="bi bi-plus-circle me-2"></i>Nuevo Producto
                </router-link>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <router-link class="dropdown-item" to="/admin/users">
                  <i class="bi bi-people me-2"></i>Gestión de Usuarios
                </router-link>
              </li>
              <li>
                <router-link class="dropdown-item" to="/admin/users/new">
                  <i class="bi bi-person-plus me-2"></i>Nuevo Usuario
                </router-link>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <router-link class="dropdown-item" to="/admin/orders">
                  <i class="bi bi-receipt me-2"></i>Gestión de Pedidos
                </router-link>
              </li>
            </ul>
          </li>
        </ul>

        <ul class="navbar-nav">
          <!-- Carrito -->
          <li class="nav-item">
            <router-link class="nav-link position-relative" to="/cart">
              <i class="bi bi-cart3 fs-5"></i>
              <span v-if="cartItemsCount > 0" class="badge badge-custom-primary position-absolute top-0 start-100 translate-middle">
                {{ cartItemsCount }}
              </span>
            </router-link>
          </li>

          <!-- Usuario autenticado -->
          <li v-if="isAuthenticated" class="nav-item dropdown">
            <a class="nav-link dropdown-toggle text-custom-primary" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
              <i class="bi bi-person-circle me-1"></i>
              {{ user.nombre }}
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li>
                <router-link class="dropdown-item" to="/profile">
                  <i class="bi bi-person me-2"></i>Mi Perfil
                </router-link>
              </li>
              <li>
                <router-link class="dropdown-item" to="/orders">
                  <i class="bi bi-bag-check me-2"></i>Mis Pedidos
                </router-link>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <a class="dropdown-item text-danger" href="#" @click.prevent="logout">
                  <i class="bi bi-box-arrow-right me-2"></i>Cerrar Sesión
                </a>
              </li>
            </ul>
          </li>

          <!-- Usuario no autenticado -->
          <li v-else class="nav-item">
            <router-link class="btn btn-custom-primary btn-sm" to="/login">
              <i class="bi bi-box-arrow-in-right me-1"></i>
              Iniciar Sesión
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'

export default {
  name: 'Navbar',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const cartStore = useCartStore()

    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const user = computed(() => authStore.user)
    const isAdmin = computed(() => authStore.isAdmin)
    const cartItemsCount = computed(() => cartStore.totalItems)

    const logout = async () => {
      await authStore.logout()
      router.push('/login')
    }

    return {
      isAuthenticated,
      user,
      isAdmin,
      cartItemsCount,
      logout
    }
  }
}
</script>

<style scoped>
.navbar {
  border-bottom: 3px solid var(--color-primary);
}

.navbar-brand {
  font-size: 1.5rem;
  transition: transform 0.3s ease;
}

.navbar-brand:hover {
  transform: scale(1.05);
}

.nav-link {
  color: rgba(255, 255, 255, 0.85) !important;
  transition: all 0.3s ease;
  padding: 0.5rem 1rem !important;
}

.nav-link:hover {
  color: var(--color-primary) !important;
  background-color: rgba(255, 215, 0, 0.1);
  border-radius: 8px;
}

.nav-link.router-link-active {
  color: var(--color-primary) !important;
  font-weight: 600;
}

.dropdown-menu {
  border: 2px solid var(--color-primary);
  box-shadow: var(--shadow-lg);
}

.dropdown-item {
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background-color: var(--color-primary);
  color: var(--text-on-primary);
  padding-left: 1.5rem;
}

.badge {
  font-size: 0.65rem;
  padding: 0.35em 0.5em;
  border-radius: 50%;
  font-weight: bold;
}
</style>
