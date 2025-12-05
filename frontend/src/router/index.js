import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Importar vistas
import Home from '../views/Home.vue'
import Catalog from '../views/Catalog.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Cart from '../views/Cart.vue'
import Checkout from '../views/Checkout.vue'
import Confirmation from '../views/Confirmation.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Orders from '../views/Orders.vue'
import UserProfile from '../views/UserProfile.vue'
import TestConnection from '../views/TestConnection.vue'
import TestAuth from '../views/TestAuth.vue'

// Admin
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import AdminProducts from '../views/admin/AdminProducts.vue'
import AdminUsers from '../views/admin/AdminUsers.vue'
import ProductForm from '../views/admin/ProductForm.vue'
import UserForm from '../views/admin/UserForm.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/catalog',
    name: 'Catalog',
    component: Catalog
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: Checkout,
    meta: { requiresAuth: true }
  },
  {
    path: '/confirmation',
    name: 'Confirmation',
    component: Confirmation,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Orders,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: UserProfile,
    meta: { requiresAuth: true }
  },
  {
    path: '/test-connection',
    name: 'TestConnection',
    component: TestConnection
  },
  {
    path: '/test-auth',
    name: 'TestAuth',
    component: TestAuth
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/products',
    name: 'AdminProducts',
    component: AdminProducts,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/products/new',
    name: 'NewProduct',
    component: ProductForm,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/products/edit/:id',
    name: 'EditProduct',
    component: ProductForm,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: AdminUsers,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users/new',
    name: 'NewUser',
    component: UserForm,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users/edit/:id',
    name: 'EditUser',
    component: UserForm,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router
