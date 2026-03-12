<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElUpload } from 'element-plus'
import { get } from '@/net/index.js'
const router = useRouter()

// 当前步骤
const activeStep = ref(0)
const userId = ref('')

// 申请表单数据
const applyForm = reactive({
  // 基本信息
  shopName: '',
  shopIntro: '',
  shopNotice: '',
  shopLogo: null,
  shopBanner: null,

  // 联系方式
  contactQq: '',
  contactWechat: '',

  // 店铺分类
  shopCategory: [],

  // 资质信息
  qualifications: [],

  // 协议同意
  agreeProtocol: false
})

// 店铺分类选项（模拟数据）
const categoryOptions = ref([
  { id: 1, name: '电子产品', children: [
      { id: 11, name: '手机通讯' },
      { id: 12, name: '电脑办公' },
      { id: 13, name: '数码配件' }
    ]},
  { id: 2, name: '服装鞋包', children: [
      { id: 21, name: '女装' },
      { id: 22, name: '男装' },
      { id: 23, name: '鞋靴' }
    ]},
  { id: 3, name: '食品生鲜', children: [
      { id: 31, name: '休闲食品' },
      { id: 32, name: '生鲜水果' },
      { id: 33, name: '饮料冲调' }
    ]}
])

// 资质类型选项
const qualificationTypes = [
  { value: 1, label: '品牌授权' },
  { value: 2, label: '质检报告' },
  { value: 3, label: '专利证书' },
  { value: 4, label: '营业执照' },
  { value: 5, label: '食品经营许可证' }
]

const getUserId = () =>{
  get('api/user/selectUserById',{},(message,data)=>{
    userId.value=data.id
  })
}

// 上传图片的URL
const uploadUrl = 'http://localhost:8082/api/file/upload/image'

// 添加资质
const addQualification = () => {
  applyForm.qualifications.push({
    id: Date.now(),
    qualificationType: 1,
    title: '',
    imageUrl: '',
    description: '',
    expireDate: null
  })
}

// 删除资质
const removeQualification = (index) => {
  applyForm.qualifications.splice(index, 1)
}

// 上传成功回调
const handleUploadSuccess = (response, file, fileList, target) => {
  if (response.code === 200) {
    // 检查 target 是否为 applyForm 对象
    if (target === applyForm) {
      // 根据文件类型设置对应的属性
      const type = file.uid.split('-')[0]
      if (type === 'logo') {
        applyForm.shopLogo = response.data.fileUrl
      } else if (type === 'banner') {
        applyForm.shopBanner = response.data.fileUrl
      }
    } else {
      // 资质图片上传
      target.imageUrl = response.data.fileUrl
    }
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 提交申请
const submitApply = async () => {
  // 表单验证
  if (!applyForm.agreeProtocol) {
    ElMessage.warning('请先同意入驻协议')
    return
  }

  try {
    // 构建店铺数据
    const shopData = {
      merchantId: userId.value, // 这里需要从登录信息中获取商家ID
      shopName: applyForm.shopName,
      shopIntro: applyForm.shopIntro,
      shopNotice: applyForm.shopNotice,
      shopLogo: applyForm.shopLogo,
      shopBanner: applyForm.shopBanner,
      contactQq: applyForm.contactQq,
      contactWechat: applyForm.contactWechat
    }

    console.log('提交店铺数据:', shopData)

    // 调用后端接口创建店铺
    const shopResponse = await fetch('http://localhost:8081/api/shop/applyShop', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(shopData)
    })

    const shopResult = await shopResponse.json()
    if (shopResult.code !== 200) {
      ElMessage.error(shopResult.message || '店铺申请失败')
      return
    }

    ElMessage.success('申请提交成功，请等待审核')
    // 跳转到申请成功页面
    setTimeout(() => {
      router.push('/ShopManagement')
    }, 1500)

  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败，请重试')
  }
}

// 下一步
const nextStep = () => {
  if (activeStep.value === 0) {
    // 验证基本信息
    if (!applyForm.shopName) {
      ElMessage.warning('请输入店铺名称')
      return
    }
    if (!applyForm.shopIntro) {
      ElMessage.warning('请输入店铺简介')
      return
    }
    if (!applyForm.shopLogo) {
      ElMessage.warning('请上传店铺Logo')
      return
    }
  }
  if (activeStep.value === 1) {
    // 验证联系方式
    if (applyForm.contactQq && !/^[1-9][0-9]{4,10}$/.test(applyForm.contactQq)) {
      ElMessage.warning('请输入正确的QQ号码')
      return
    }
  }
  if (activeStep.value === 2) {
    // 验证店铺分类
    if (applyForm.shopCategory.length === 0) {
      ElMessage.warning('请至少选择一个店铺分类')
      return
    }
  }
  activeStep.value++
}

// 上一步
const prevStep = () => {
  activeStep.value--
}

// 预览图片
const previewImage = (url) => {
  if (!url) return
  ElMessageBox.alert(`<img src="${url}" style="max-width: 100%; max-height: 500px;">`, '图片预览', {
    dangerouslyUseHTMLString: true,
    showConfirmButton: false,
    closeOnClickModal: true
  })
}
onMounted(()=>{
  getUserId()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-950 py-8">
    <div class="max-w-5xl mx-auto px-4">
      <!-- 页面标题 -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          申请入驻
        </h1>
        <p class="text-gray-500 dark:text-gray-400">
          填写店铺信息，开启您的电商之旅
        </p>
      </div>

      <!-- 步骤条（核心修复：改用slot自定义图标） -->
      <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 p-6 mb-6">
        <el-steps :active="activeStep" finish-status="success" simple>
          <el-step title="基本信息">
            <template #icon>
              <i class="fas fa-store text-gray-500"></i>
            </template>
          </el-step>
          <el-step title="联系方式">
            <template #icon>
              <i class="fas fa-phone text-gray-500"></i>
            </template>
          </el-step>
          <el-step title="店铺分类">
            <template #icon>
              <i class="fas fa-tags text-gray-500"></i>
            </template>
          </el-step>
          <el-step title="资质上传">
            <template #icon>
              <i class="fas fa-certificate text-gray-500"></i>
            </template>
          </el-step>
          <el-step title="提交审核">
            <template #icon>
              <i class="fas fa-check-circle text-gray-500"></i>
            </template>
          </el-step>
        </el-steps>
      </div>

      <!-- 表单内容 -->
      <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 p-8">
        <!-- 步骤1：基本信息 -->
        <div v-show="activeStep === 0" class="space-y-6">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center">
            <span class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 flex items-center justify-center mr-3">1</span>
            基本信息
          </h2>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- 店铺名称 -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                店铺名称 <span class="text-red-500">*</span>
              </label>
              <el-input
                v-model="applyForm.shopName"
                placeholder="请输入店铺名称，建议使用品牌名或行业关键词"
                maxlength="50"
                show-word-limit
              />
              <p class="text-xs text-gray-500 mt-1">好的店铺名称更容易被记住</p>
            </div>

            <!-- 店铺简介 -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                店铺简介 <span class="text-red-500">*</span>
              </label>
              <el-input
                v-model="applyForm.shopIntro"
                type="textarea"
                :rows="3"
                placeholder="简单介绍您的店铺，主要经营什么，有什么特色"
                maxlength="200"
                show-word-limit
              />
            </div>

            <!-- 店铺公告 -->
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                店铺公告
              </label>
              <el-input
                v-model="applyForm.shopNotice"
                type="textarea"
                :rows="2"
                placeholder="填写店铺公告，如发货时间、售后服务等"
                maxlength="200"
                show-word-limit
              />
            </div>

            <!-- 店铺Logo -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                店铺Logo <span class="text-red-500">*</span>
              </label>
              <div class="flex items-start space-x-4">
                <div
                  v-if="applyForm.shopLogo"
                  class="relative w-24 h-24 rounded-xl border-2 border-gray-200 dark:border-gray-700 overflow-hidden cursor-pointer group"
                  @click="previewImage(applyForm.shopLogo)"
                >
                  <img :src="applyForm.shopLogo" class="w-full h-full object-cover">
                  <div class="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                    <i class="fas fa-search-plus text-white"></i>
                  </div>
                </div>
                <el-upload
                  class="w-24 h-24"
                  :action="uploadUrl"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :on-success="(res, file) => {
                    applyForm.shopLogo = res.data.fileUrl
                    ElMessage.success('上传成功')
                  }"
                  :data="{ type: 'logo' }"
                >
                  <div v-if="!applyForm.shopLogo" class="w-24 h-24 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-xl flex flex-col items-center justify-center cursor-pointer hover:border-blue-500 transition-colors">
                    <i class="fas fa-cloud-upload-alt text-2xl text-gray-400 mb-1"></i>
                    <span class="text-xs text-gray-500">上传Logo</span>
                  </div>
                </el-upload>
              </div>
              <p class="text-xs text-gray-500 mt-2">建议尺寸：200x200px，支持jpg、png、gif</p>
            </div>

            <!-- 店铺横幅 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                店铺横幅
              </label>
              <div class="flex items-start space-x-4">
                <div
                  v-if="applyForm.shopBanner"
                  class="relative w-32 h-20 rounded-xl border-2 border-gray-200 dark:border-gray-700 overflow-hidden cursor-pointer group"
                  @click="previewImage(applyForm.shopBanner)"
                >
                  <img :src="applyForm.shopBanner" class="w-full h-full object-cover">
                  <div class="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                    <i class="fas fa-search-plus text-white"></i>
                  </div>
                </div>
                <el-upload
                  class="w-32 h-20"
                  :action="uploadUrl"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :on-success="(res, file) => {
                    applyForm.shopBanner = res.data.fileUrl
                    ElMessage.success('上传成功')
                  }"
                  :data="{ type: 'banner' }"
                >
                  <div v-if="!applyForm.shopBanner" class="w-32 h-20 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-xl flex flex-col items-center justify-center cursor-pointer hover:border-blue-500 transition-colors">
                    <i class="fas fa-cloud-upload-alt text-xl text-gray-400 mb-1"></i>
                    <span class="text-xs text-gray-500">上传横幅</span>
                  </div>
                </el-upload>
              </div>
              <p class="text-xs text-gray-500 mt-2">建议尺寸：1200x300px，展示店铺形象</p>
            </div>
          </div>
        </div>

        <!-- 步骤2：联系方式 -->
        <div v-show="activeStep === 1" class="space-y-6">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center">
            <span class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 flex items-center justify-center mr-3">2</span>
            联系方式
          </h2>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- 联系QQ -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                联系QQ
              </label>
              <el-input v-model="applyForm.contactQq" placeholder="请输入QQ号码">
                <template #prefix>
                  <i class="fab fa-qq text-blue-500"></i>
                </template>
              </el-input>
            </div>

            <!-- 联系微信 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                联系微信
              </label>
              <el-input v-model="applyForm.contactWechat" placeholder="请输入微信号">
                <template #prefix>
                  <i class="fab fa-weixin text-green-500"></i>
                </template>
              </el-input>
            </div>
          </div>

          <div class="bg-blue-50 dark:bg-blue-900/20 rounded-xl p-4">
            <div class="flex items-start space-x-3">
              <i class="fas fa-info-circle text-blue-600 dark:text-blue-400 mt-0.5"></i>
              <div>
                <p class="text-sm text-blue-800 dark:text-blue-300 font-medium">联系方式说明</p>
                <p class="text-xs text-blue-600 dark:text-blue-400 mt-1">
                  留下联系方式方便平台与您联系，仅平台可见，不会在前端展示
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- 步骤3：店铺分类 -->
        <div v-show="activeStep === 2" class="space-y-6">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center">
            <span class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 flex items-center justify-center mr-3">3</span>
            选择店铺分类
          </h2>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div v-for="category in categoryOptions" :key="category.id" class="border border-gray-200 dark:border-gray-700 rounded-xl p-4">
              <h3 class="font-semibold text-gray-900 dark:text-white mb-3">{{ category.name }}</h3>
              <div class="space-y-2">
                <el-checkbox-group v-model="applyForm.shopCategory">
                  <el-checkbox
                    v-for="child in category.children"
                    :key="child.id"
                    :label="child.id"
                    class="!ml-0 !mr-4 !mb-2"
                  >
                    {{ child.name }}
                  </el-checkbox>
                </el-checkbox-group>
              </div>
            </div>
          </div>
        </div>

        <!-- 步骤4：资质上传 -->
        <div v-show="activeStep === 3" class="space-y-6">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center">
            <span class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 flex items-center justify-center mr-3">4</span>
            上传资质证明
          </h2>

          <!-- 资质列表 -->
          <div class="space-y-4">
            <div
              v-for="(qualification, index) in applyForm.qualifications"
              :key="qualification.id"
              class="border border-gray-200 dark:border-gray-700 rounded-xl p-4 relative"
            >
              <button
                v-if="applyForm.qualifications.length > 1"
                @click="removeQualification(index)"
                class="absolute top-4 right-4 text-gray-400 hover:text-red-500 transition-colors"
              >
                <i class="fas fa-times"></i>
              </button>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <!-- 资质类型 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    资质类型
                  </label>
                  <el-select v-model="qualification.qualificationType" placeholder="请选择" class="w-full">
                    <el-option
                      v-for="item in qualificationTypes"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </div>

                <!-- 资质标题 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    资质标题
                  </label>
                  <el-input v-model="qualification.title" placeholder="如：XX品牌授权书" />
                </div>

                <!-- 资质图片 -->
                <div class="col-span-2">
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    资质图片
                  </label>
                  <div class="flex items-start space-x-4">
                    <div
                      v-if="qualification.imageUrl"
                      class="relative w-24 h-24 rounded-xl border-2 border-gray-200 dark:border-gray-700 overflow-hidden cursor-pointer group"
                      @click="previewImage(qualification.imageUrl)"
                    >
                      <img :src="qualification.imageUrl" class="w-full h-full object-cover">
                      <div class="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                        <i class="fas fa-search-plus text-white"></i>
                      </div>
                    </div>
                    <el-upload
                      class="w-24 h-24"
                      :action="uploadUrl"
                      :show-file-list="false"
                      :before-upload="beforeUpload"
                      :on-success="(res, file) => {
                        qualification.imageUrl = res.data.fileUrl
                        ElMessage.success('上传成功')
                      }"
                    >
                      <div v-if="!qualification.imageUrl" class="w-24 h-24 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-xl flex flex-col items-center justify-center cursor-pointer hover:border-blue-500 transition-colors">
                        <i class="fas fa-cloud-upload-alt text-2xl text-gray-400 mb-1"></i>
                        <span class="text-xs text-gray-500">上传图片</span>
                      </div>
                    </el-upload>
                  </div>
                </div>

                <!-- 资质描述 -->
                <div class="col-span-2">
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    资质描述
                  </label>
                  <el-input
                    v-model="qualification.description"
                    type="textarea"
                    :rows="2"
                    placeholder="对资质的简要说明"
                  />
                </div>

                <!-- 过期时间 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    过期时间
                  </label>
                  <el-date-picker
                    v-model="qualification.expireDate"
                    type="date"
                    placeholder="选择过期时间"
                    class="w-full"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 添加资质按钮 -->
          <button
            @click="addQualification"
            class="w-full py-3 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-xl text-gray-500 hover:border-blue-500 hover:text-blue-500 transition-colors flex items-center justify-center space-x-2"
          >
            <i class="fas fa-plus"></i>
            <span>添加资质</span>
          </button>
        </div>

        <!-- 步骤5：提交审核 -->
        <div v-show="activeStep === 4" class="space-y-6">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center">
            <span class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 flex items-center justify-center mr-3">5</span>
            确认信息并提交
          </h2>

          <!-- 信息预览 -->
          <div class="bg-gray-50 dark:bg-gray-800 rounded-xl p-6 space-y-4">
            <h3 class="font-medium text-gray-900 dark:text-white">店铺信息预览</h3>

            <div class="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span class="text-gray-500">店铺名称：</span>
                <span class="text-gray-900 dark:text-white font-medium">{{ applyForm.shopName }}</span>
              </div>
              <div>
                <span class="text-gray-500">联系QQ：</span>
                <span class="text-gray-900 dark:text-white">{{ applyForm.contactQq || '未填写' }}</span>
              </div>
              <div>
                <span class="text-gray-500">联系微信：</span>
                <span class="text-gray-900 dark:text-white">{{ applyForm.contactWechat || '未填写' }}</span>
              </div>
              <div>
                <span class="text-gray-500">选择分类：</span>
                <span class="text-gray-900 dark:text-white">{{ applyForm.shopCategory.length }}个</span>
              </div>
              <div>
                <span class="text-gray-500">上传资质：</span>
                <span class="text-gray-900 dark:text-white">{{ applyForm.qualifications.length }}个</span>
              </div>
            </div>
          </div>

          <!-- 入驻协议 -->
          <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-4">
            <el-checkbox v-model="applyForm.agreeProtocol">
              我已阅读并同意
              <a href="#" class="text-blue-600 hover:underline">《落叶商城商家入驻协议》</a>
            </el-checkbox>
          </div>
        </div>

        <!-- 按钮组 -->
        <div class="flex justify-between mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
          <el-button v-if="activeStep > 0" @click="prevStep">
            <i class="fas fa-arrow-left mr-2"></i>
            上一步
          </el-button>
          <div v-else></div>

          <div class="flex space-x-3">
            <el-button @click="router.back()">取消</el-button>
            <el-button
              v-if="activeStep < 4"
              type="primary"
              @click="nextStep"
            >
              下一步
              <i class="fas fa-arrow-right ml-2"></i>
            </el-button>
            <el-button
              v-if="activeStep === 4"
              type="primary"
              @click="submitApply"
              :disabled="!applyForm.agreeProtocol"
            >
              <i class="fas fa-check mr-2"></i>
              提交审核
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 自定义Element Plus样式 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  background-color: transparent;
  box-shadow: 0 0 0 1px #e5e7eb inset;
  border-radius: 0.75rem;
  transition: all 0.2s;
}

.dark :deep(.el-input__wrapper),
.dark :deep(.el-select__wrapper),
.dark :deep(.el-textarea__inner) {
  background-color: transparent;
  box-shadow: 0 0 0 1px #374151 inset;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover),
:deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #3b82f6 inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus),
:deep(.el-textarea__inner.is-focus) {
  box-shadow: 0 0 0 2px #3b82f6 inset;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #3b82f6;
  border-color: #3b82f6;
}

:deep(.el-steps--simple) {
  background: transparent;
  padding: 0;
}

:deep(.el-step.is-simple .el-step__head.is-finish) {
  color: #3b82f6;
  border-color: #3b82f6;
}

:deep(.el-step.is-simple .el-step__title.is-finish) {
  color: #3b82f6;
}

/* 修复步骤条图标样式 */
:deep(.el-step__icon) {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
