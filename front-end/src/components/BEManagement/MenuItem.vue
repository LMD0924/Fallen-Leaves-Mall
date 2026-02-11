<template>
  <button
    @click="$emit('click', item.path)"
    class="w-full flex items-center px-3 py-2.5 rounded-xl transition-all duration-200 group"
    :class="[
      active
        ? 'bg-gradient-to-r from-blue-50 to-purple-50 dark:from-gray-800 dark:to-gray-800 text-blue-600 dark:text-blue-400 shadow-sm'
        : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 hover:text-gray-900 dark:hover:text-white'
    ]"
  >
    <!-- 图标容器 -->
    <div class="relative flex items-center justify-center" :class="{ 'mr-3': !collapsed }">
      <div class="w-6 h-6 flex items-center justify-center">
        <i :class="['fas', item.icon, 'text-lg', active ? 'text-blue-600 dark:text-blue-400' : 'text-gray-500 dark:text-gray-400 group-hover:text-gray-700 dark:group-hover:text-gray-300']"></i>
      </div>

      <!-- 活动指示器 -->
      <div
        v-if="active && !collapsed"
        class="absolute -left-3 w-1 h-6 bg-gradient-to-b from-blue-500 to-purple-500 rounded-full"
      ></div>
    </div>

    <!-- 菜单名称 - 折叠时隐藏 -->
    <transition name="fade">
      <span v-if="!collapsed" class="flex-1 text-sm font-medium text-left">
        {{ item.name }}
      </span>
    </transition>

    <!-- 徽章 - 折叠时显示为圆点 -->
    <template v-if="item.badge">
      <span
        v-if="!collapsed"
        class="ml-auto text-xs px-2 py-0.5 rounded-full"
        :class="[
          active
            ? 'bg-blue-100 dark:bg-blue-900 text-blue-600 dark:text-blue-400'
            : 'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400'
        ]"
      >
        {{ item.badge }}
      </span>
      <span
        v-else
        class="w-2 h-2 bg-red-500 rounded-full ml-auto"
      ></span>
    </template>
  </button>
</template>

<script setup>
defineProps({
  item: {
    type: Object,
    required: true
  },
  collapsed: {
    type: Boolean,
    default: false
  },
  active: {
    type: Boolean,
    default: false
  }
})

defineEmits(['click'])
</script>
