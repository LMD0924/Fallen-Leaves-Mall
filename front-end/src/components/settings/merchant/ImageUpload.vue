<!-- components/settings/merchant/ImageUpload.vue - 图片上传组件 -->
<template>
  <div class="border-2 border-dashed border-gray-300 dark:border-gray-700 rounded-lg p-6 text-center hover:border-purple-400 dark:hover:border-purple-600 transition-colors group"
       @dragover.prevent="dragover = true"
       @dragleave.prevent="dragover = false"
       @drop.prevent="handleDrop">
    <!-- 上传图标 -->
    <div class="relative">
      <div class="w-16 h-16 mx-auto mb-3 rounded-full bg-gray-100 dark:bg-gray-900 flex items-center justify-center group-hover:scale-110 transition-transform">
        <i :class="['text-2xl text-gray-400', icon]"></i>
      </div>
      <!-- 上传成功标记 -->
      <div v-if="modelValue" class="absolute -top-2 -right-2 w-6 h-6 rounded-full bg-emerald-500 flex items-center justify-center text-white text-xs border-2 border-white dark:border-black">
        <i class="fa fa-check"></i>
      </div>
    </div>

    <!-- 上传提示 -->
    <p class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">{{ label }}</p>
    <p class="text-xs text-gray-500 dark:text-gray-400 mb-3">{{ description || '点击或拖拽上传' }}</p>

    <!-- 文件名显示 -->
    <div v-if="modelValue" class="mb-3 text-xs text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/30 py-1 px-2 rounded-full inline-flex items-center gap-1">
      <i class="fa fa-check-circle"></i>
      <span>{{ getFileName(modelValue) }}</span>
    </div>

    <!-- 上传按钮 -->
    <input
      type="file"
      :accept="accept"
      class="hidden"
      :id="`upload-${_uid}`"
      @change="handleFileChange"
    />
    <label :for="`upload-${_uid}`"
           class="inline-block px-4 py-2 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 cursor-pointer transition-all text-sm shadow-md hover:shadow-lg">
      <i class="fa fa-upload mr-1"></i>
      选择文件
    </label>

    <!-- 文件限制提示 -->
    <p class="mt-2 text-xs text-gray-400">支持 {{ accept }} 格式，最大 {{ maxSize }}MB</p>

    <!-- 预览区域 -->
    <div v-if="previewUrl" class="mt-4 p-2 bg-gray-50 dark:bg-gray-900 rounded-lg">
      <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">预览：</p>
      <img :src="previewUrl" class="max-h-32 mx-auto rounded-lg shadow-md" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: '上传图片'
  },
  icon: {
    type: String,
    default: 'fa fa-image'
  },
  description: {
    type: String,
    default: ''
  },
  accept: {
    type: String,
    default: 'image/*'
  },
  maxSize: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['update:modelValue', 'upload'])

const dragover = ref(false)
const previewUrl = ref('')
const _uid = ref('upload-' + Math.random().toString(36).substr(2, 9))

// 监听modelValue变化，更新预览
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.startsWith('data:image')) {
    previewUrl.value = newVal
  }
})

const getFileName = (path) => {
  if (!path) return ''
  if (path.includes('/')) {
    return path.split('/').pop()
  }
  return path.substring(0, 20) + (path.length > 20 ? '...' : '')
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    processFile(file)
  }
}

const handleDrop = (event) => {
  dragover.value = false
  const file = event.dataTransfer.files[0]
  if (file) {
    processFile(file)
  }
}

const processFile = (file) => {
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    message.error('请上传图片文件')
    return
  }

  // 检查文件大小
  if (file.size > props.maxSize * 1024 * 1024) {
    message.error(`文件大小不能超过 ${props.maxSize}MB`)
    return
  }

  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    previewUrl.value = e.target.result
    emit('update:modelValue', e.target.result)
    emit('upload', file)
  }
  reader.readAsDataURL(file)
}

// 导入message
import { message } from 'ant-design-vue'
</script>
