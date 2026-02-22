<!-- components/settings/merchant/MerchantForm.vue - 商家申请表单组件 -->
<template>
  <div>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
      <!-- 商家基本信息 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">商家类型 *</label>
        <select
          v-model="form.merchantType"
          class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
        >
          <option value="">请选择商家类型</option>
          <option value="0">个人商家</option>
          <option value="1">企业商家</option>
        </select>
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">商家名称 *</label>
        <input
          v-model="form.merchantName"
          type="text"
          placeholder="请输入商家名称"
          class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
        />
      </div>

      <!-- 联系人信息 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">联系人姓名 *</label>
        <input
          v-model="form.contactName"
          type="text"
          placeholder="请输入联系人姓名"
          class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">联系人电话 *</label>
        <input
          v-model="form.contactPhone"
          type="tel"
          placeholder="请输入联系电话"
          class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">联系人邮箱</label>
        <input
          v-model="form.contactEmail"
          type="email"
          placeholder="请输入联系邮箱"
          class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
        />
      </div>

      <!-- 资质信息 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">营业执照号 *</label>
        <input
          v-model="form.businessLicense"
          type="text"
          placeholder="请输入营业执照号"
          class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">身份证号 *</label>
        <input
          v-model="form.idCard"
          type="text"
          placeholder="请输入身份证号"
          class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
        />
      </div>

      <!-- 身份证上传 -->
      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">身份证正反面照片</label>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ImageUpload
            v-model="form.idCardFront"
            label="身份证正面"
            icon="fa-id-card"
            @upload="handleIdCardFrontUpload"
          />
          <ImageUpload
            v-model="form.idCardBack"
            label="身份证背面"
            icon="fa-id-card-alt"
            @upload="handleIdCardBackUpload"
          />
        </div>
      </div>

      <!-- 营业执照上传 -->
      <div class="md:col-span-2">
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">营业执照图片</label>
        <ImageUpload
          v-model="form.licenseImage"
          label="营业执照"
          icon="fa-file-image"
          description="支持 JPG、PNG 格式，大小不超过 5MB"
          @upload="handleLicenseImageUpload"
        />
      </div>
    </div>

    <!-- 表单验证提示 -->
    <div v-if="validationErrors.length > 0" class="mb-6 p-4 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 rounded-lg animate-shake">
      <div class="flex items-start">
        <i class="fa fa-exclamation-triangle text-red-500 mt-0.5 mr-3"></i>
        <div>
          <p class="font-medium text-red-800 dark:text-red-200 mb-1">请完善以下信息：</p>
          <ul class="text-sm text-red-700 dark:text-red-300 list-disc pl-5">
            <li v-for="error in validationErrors" :key="error">{{ error }}</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 申请协议 -->
    <div class="mb-8 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
      <div class="flex items-start">
        <input
          type="checkbox"
          v-model="form.agreed"
          id="merchantAgreement"
          class="mt-1 mr-3 accent-purple-500"
        />
        <label for="merchantAgreement" class="text-sm text-gray-700 dark:text-gray-300">
          我已阅读并同意
          <a href="#" class="text-purple-500 hover:text-purple-600 dark:text-purple-400 dark:hover:text-purple-300 transition-colors">《落叶商城商家入驻协议》</a>
          和
          <a href="#" class="text-purple-500 hover:text-purple-600 dark:text-purple-400 dark:hover:text-purple-300 transition-colors">《商家服务条款》</a>
        </label>
      </div>
    </div>

    <!-- 提交按钮 -->
    <button
      @click="handleSubmit"
      :disabled="!canSubmit || isSubmitting"
      :class="[
        'w-full py-3 rounded-lg font-medium transition-all duration-300',
        canSubmit && !isSubmitting
          ? 'bg-gradient-to-r from-purple-500 to-fuchsia-500 hover:from-purple-600 hover:to-fuchsia-600 text-white shadow-lg hover:shadow-xl transform hover:-translate-y-0.5'
          : 'bg-gray-300 dark:bg-gray-700 text-gray-500 dark:text-gray-400 cursor-not-allowed'
      ]"
    >
      <i class="fa fa-check-circle mr-2"></i>
      {{ isSubmitting ? '提交中...' : '提交商家申请' }}
    </button>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'
import ImageUpload from './ImageUpload.vue'

const props = defineProps({
  userId: {
    type: [Number, String],
    required: true
  },
  isSubmitting: {
    type: Boolean,
    default: false
  },
  validationErrors: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['submit'])

// 表单数据
const form = reactive({
  userId: props.userId,
  merchantType: '',
  merchantName: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  businessLicense: '',
  licenseImage: '',
  idCard: '',
  idCardFront: '',
  idCardBack: '',
  agreed: false
})

// 计算属性：是否可以提交
const canSubmit = computed(() => {
  return form.merchantName &&
    form.merchantType &&
    form.contactName &&
    form.contactPhone &&
    form.businessLicense &&
    form.idCard &&
    form.agreed
})

// 表单验证
const validateForm = () => {
  const errors = []

  if (!form.merchantName) errors.push('商家名称不能为空')
  if (!form.merchantType) errors.push('请选择商家类型')
  if (!form.contactName) errors.push('联系人姓名不能为空')
  if (!form.contactPhone) errors.push('联系人电话不能为空')
  else if (!/^1[3-9]\d{9}$/.test(form.contactPhone)) errors.push('请输入正确的手机号码')
  if (form.contactEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.contactEmail)) errors.push('请输入正确的邮箱格式')
  if (!form.businessLicense) errors.push('营业执照号不能为空')
  if (!form.idCard) errors.push('身份证号不能为空')
  else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(form.idCard)) errors.push('请输入正确的身份证号')
  if (!form.agreed) errors.push('请同意商家入驻协议和服务条款')

  return errors
}

// 提交处理
const handleSubmit = () => {
  const errors = validateForm()
  if (errors.length > 0) {
    emit('submit', { errors })
    return
  }
  emit('submit', { formData: { ...form } })
}

// 图片上传处理
const handleIdCardFrontUpload = (file) => {
  form.idCardFront = '上传成功：' + file.name
}

const handleIdCardBackUpload = (file) => {
  form.idCardBack = '上传成功：' + file.name
}

const handleLicenseImageUpload = (file) => {
  form.licenseImage = '上传成功：' + file.name
}
</script>
