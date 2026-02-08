<script setup>
import ThemeToggle from './components/ThemeToggle.vue'
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-gray-100 transition-colors">
    <header class="fixed top-0 right-0 p-4 z-50">
      <ThemeToggle />
    </header>
      <RouterView />
  </div>
</template>

<style scoped>

</style>
