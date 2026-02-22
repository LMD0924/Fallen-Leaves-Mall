<!-- components/common/Modal.vue - 通用模态框组件 -->
<template>
  <Transition name="modal">
    <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="close">
      <!-- 遮罩层 -->
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="close"></div>

      <!-- 模态框内容 -->
      <div class="relative bg-white dark:bg-black rounded-xl shadow-2xl w-full max-w-md border border-gray-200 dark:border-gray-800 animate-scaleIn">
        <!-- 头部 -->
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-800">
          <h3 class="text-lg font-light text-gray-900 dark:text-gray-100">{{ title }}</h3>
          <button @click="close" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="fa fa-times"></i>
          </button>
        </div>

        <!-- 内容 -->
        <div class="p-6">
          <slot></slot>
        </div>

        <!-- 底部按钮 -->
        <div v-if="showFooter" class="flex justify-end gap-3 p-6 border-t border-gray-200 dark:border-gray-800">
          <button @click="close" class="px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors">
            取消
          </button>
          <button @click="handleOk" class="px-4 py-2 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 transition-colors">
            确认
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
defineProps({
  modelValue: Boolean,
  title: String,
  showFooter: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'ok'])

const close = () => {
  emit('update:modelValue', false)
}

const handleOk = () => {
  emit('ok')
  close()
}
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: scale(0.9);
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-scaleIn {
  animation: scaleIn 0.3s cubic-bezier(0.2, 0.9, 0.3, 1) forwards;
}
</style>
