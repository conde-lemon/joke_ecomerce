<template>
  <div
    class="product-placeholder"
    :style="{
      width: width,
      height: height,
      background: gradient
    }"
  >
    <i :class="`bi ${icon} fs-${iconSize}`"></i>
    <span v-if="showText" class="placeholder-text">{{ text }}</span>
  </div>
</template>

<script>
export default {
  name: 'ProductPlaceholder',
  props: {
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '200px'
    },
    text: {
      type: String,
      default: 'Producto'
    },
    showText: {
      type: Boolean,
      default: true
    },
    icon: {
      type: String,
      default: 'bi-image'
    },
    iconSize: {
      type: String,
      default: '1'
    },
    color: {
      type: String,
      default: '#667eea'
    }
  },
  computed: {
    gradient() {
      return `linear-gradient(135deg, ${this.color} 0%, ${this.darkenColor(this.color)} 100%)`
    }
  },
  methods: {
    darkenColor(color) {
      // Oscurecer el color en un 20%
      const hex = color.replace('#', '')
      const r = Math.max(0, parseInt(hex.substr(0, 2), 16) - 40)
      const g = Math.max(0, parseInt(hex.substr(2, 2), 16) - 40)
      const b = Math.max(0, parseInt(hex.substr(4, 2), 16) - 40)
      return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.product-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  border-radius: 8px;
  gap: 10px;
}

.placeholder-text {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
  padding: 0 10px;
}
</style>

