<template>
  <footer class="bg-dark text-white mt-auto py-4">
    <div class="container">
      <div class="row">
        <div class="col-md-4">
          <h5>E-Commerce</h5>
          <p>Tu tienda en línea de confianza</p>
        </div>
        <div class="col-md-4">
          <h5>Enlaces</h5>
          <ul class="list-unstyled">
            <li><router-link to="/" class="text-white">Inicio</router-link></li>
            <li><router-link to="/catalog" class="text-white">Catálogo</router-link></li>
          </ul>
        </div>
        <div class="col-md-4">
          <h5>Contacto</h5>
          <p>Email: info@ecommerce.com</p>
          <p>Teléfono: (123) 456-7890</p>
        </div>
      </div>
      <hr class="bg-white">
      <div class="text-center">
        <p>&copy; 2024 E-Commerce. Todos los derechos reservados.</p>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer'
}
</script>

<style scoped>
footer {
  margin-top: auto;
}

footer a {
  text-decoration: none;
}

footer a:hover {
  text-decoration: underline;
}
</style>

