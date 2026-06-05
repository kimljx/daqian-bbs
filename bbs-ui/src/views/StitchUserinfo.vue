<template>
  <main class="max-w-6xl mx-auto py-12 px-page-margin-desktop bg-surface min-h-screen">
    <div class="flex flex-col md:flex-row bg-container rounded-lg shadow-sm overflow-hidden border border-border min-h-[600px]">
      <!-- Side Navigation -->
      <aside class="w-full md:w-64 border-r border-outline-variant bg-surface-container-lowest">
        <div class="p-6">
          <h2 class="font-headline-sm text-headline-sm text-primary mb-6">账户设置</h2>
          <nav class="space-y-1">
            <button
              class="w-full text-left px-4 py-3 font-label-md text-label-md rounded-l-lg transition-all duration-200"
              :class="activeTab === 'info' ? 'sidebar-tab-active' : 'text-on-surface-variant hover:bg-surface-container-low'"
              @click="activeTab = 'info'"
            >
              信息设置
            </button>
            <button
              class="w-full text-left px-4 py-3 font-label-md text-label-md rounded-l-lg transition-all duration-200"
              :class="activeTab === 'password' ? 'sidebar-tab-active' : 'text-on-surface-variant hover:bg-surface-container-low'"
              @click="activeTab = 'password'"
            >
              密码修改
            </button>
          </nav>
        </div>
      </aside>

      <!-- Content Area -->
      <section class="flex-1 p-8 md:p-12">
        <!-- Information Settings Tab -->
        <div v-show="activeTab === 'info'" class="space-y-8">
          <!-- Avatar and Nickname Section -->
          <div class="flex items-center gap-6 pb-8 border-b border-outline-variant">
            <div class="relative group tooltip cursor-pointer" data-tooltip="点击上传头像" @click="showAvatarDialog = true">
              <img
                alt="用户头像"
                class="w-24 h-24 rounded-full border-4 border-surface-container-high object-cover transition-opacity group-hover:opacity-80"
                :src="userInfo.avatar"
              >
              <div class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                <span class="material-symbols-outlined text-white text-3xl">upload</span>
              </div>
            </div>
            <div>
              <h1 class="font-headline-lg text-headline-lg text-on-surface">{{ userInfo.nickname }}</h1>
              <p class="font-body-md text-body-md text-on-surface-variant mt-1">{{ userInfo.role }} • {{ userInfo.dept }}</p>
            </div>
          </div>

          <!-- Update Notification Area -->
          <div v-if="showSaveNotification" class="flex items-center justify-between p-4 bg-primary-fixed/30 border border-primary/20 rounded-lg">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-primary">info</span>
              <span class="font-body-md text-on-surface">我的资料更新</span>
            </div>
            <div class="flex gap-2">
              <button class="px-4 py-1.5 bg-primary text-white text-label-md rounded-lg hover:bg-primary-container transition-colors" @click="saveChanges">Save</button>
              <button class="px-4 py-1.5 bg-error text-white text-label-md rounded-lg hover:bg-red-700 transition-colors" @click="cancelChanges">Cancel</button>
            </div>
          </div>

          <!-- Basic Information Section -->
          <div class="space-y-6">
            <h3 class="font-headline-sm text-headline-sm text-on-surface">基本信息</h3>
            <div class="grid grid-cols-1 gap-y-6 max-w-2xl">
              <div class="flex items-center py-4 border-b border-outline-variant">
                <label class="w-32 font-label-md text-label-md text-on-surface-variant">用户昵称</label>
                <div class="font-body-lg text-body-lg text-on-surface">{{ userInfo.nickname }}</div>
              </div>
              <div class="flex items-center py-4 border-b border-outline-variant">
                <label class="w-32 font-label-md text-label-md text-on-surface-variant">用户 ID</label>
                <div class="font-body-lg text-body-lg text-on-surface">{{ userInfo.userId }}</div>
              </div>
              <div class="flex items-center py-4 border-b border-outline-variant group cursor-default relative">
                <label class="w-32 font-label-md text-label-md text-on-surface-variant">手机号码</label>
                <div class="flex-1 flex items-center justify-between">
                  <div v-show="!editingPhone" class="font-body-lg text-body-lg text-on-surface">{{ userInfo.phone }}</div>
                  <input
                    v-show="editingPhone"
                    ref="phoneInput"
                    v-model="userInfo.phone"
                    class="flex-1 px-3 py-1 border border-outline-variant rounded focus:ring-1 focus:ring-primary focus:border-primary outline-none"
                    type="text"
                    @input="showSaveNotification = true"
                  >
                  <button class="p-1 text-on-surface-variant hover:text-primary transition-colors" @click="togglePhoneEdit">
                    <span class="material-symbols-outlined text-sm">edit</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Password Modification Tab -->
        <div v-show="activeTab === 'password'" class="space-y-8">
          <div>
            <h3 class="font-headline-sm text-headline-sm text-on-surface">安全设置</h3>
            <p class="font-body-md text-body-md text-on-surface-variant mt-2">定期更换密码可以提高您的账户安全性。</p>
          </div>
          <form class="max-w-md space-y-6" @submit.prevent="handlePasswordChange">
            <div class="space-y-2">
              <label class="font-label-md text-label-md text-on-surface-variant">用户名</label>
              <input
                class="w-full px-4 py-3 rounded-lg border border-outline-variant bg-surface-container-low text-on-surface-variant font-body-md"
                disabled
                type="text"
                :value="userInfo.phone"
              >
            </div>
            <div class="space-y-2">
              <label class="font-label-md text-label-md text-on-surface-variant">当前密码</label>
              <input
                v-model="passwordForm.currentPassword"
                class="w-full px-4 py-3 rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all font-body-md text-body-md"
                placeholder="请输入原密码"
                type="password"
              >
            </div>
            <div class="space-y-2">
              <label class="font-label-md text-label-md text-on-surface-variant">新密码</label>
              <input
                v-model="passwordForm.newPassword"
                class="w-full px-4 py-3 rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all font-body-md text-body-md"
                placeholder="6-20位字符，包含字母及数字"
                type="password"
              >
            </div>
            <div class="space-y-2">
              <label class="font-label-md text-label-md text-on-surface-variant">确认新密码</label>
              <input
                v-model="passwordForm.confirmPassword"
                class="w-full px-4 py-3 rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all font-body-md text-body-md"
                placeholder="请再次输入新密码"
                type="password"
              >
            </div>
            <div class="flex gap-4 pt-4">
              <button class="px-8 py-2.5 bg-primary text-white font-label-md text-label-md rounded-lg hover:bg-primary-container transition-colors shadow-md" type="submit">
                提交修改
              </button>
              <button class="px-8 py-2.5 bg-white text-on-surface-variant border border-outline-variant font-label-md text-label-md rounded-lg hover:bg-surface-container-low transition-colors" type="reset" @click="resetPasswordForm">
                重置
              </button>
            </div>
          </form>
        </div>
      </section>
    </div>

    <!-- Avatar Upload Dialog -->
    <div v-if="showAvatarDialog" class="fixed inset-0 z-50 flex items-center justify-center p-4 dialog-overlay" @click.self="showAvatarDialog = false">
      <div class="bg-container w-full max-w-md rounded-lg shadow-xl overflow-hidden">
        <div class="px-6 py-4 border-b border-outline-variant flex items-center justify-between">
          <h3 class="font-headline-sm text-headline-sm">修改头像</h3>
          <button class="p-1 hover:bg-surface-container-low rounded-full transition-colors" @click="showAvatarDialog = false">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <div class="p-8">
          <div class="border-2 border-dashed border-outline-variant rounded-lg p-12 text-center flex flex-col items-center justify-center gap-4 hover:border-primary transition-colors cursor-pointer bg-surface-container-lowest group" @click="triggerAvatarUpload">
            <span class="material-symbols-outlined text-4xl text-outline group-hover:text-primary">add</span>
            <p class="font-body-md text-on-surface-variant">点击或拖拽图片到此处上传</p>
            <p class="text-xs text-outline">支持 JPG、PNG 格式，大小不超过 2MB</p>
          </div>
          <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleAvatarChange">
          <div v-if="avatarPreview" class="mt-6 p-4 border border-outline-variant rounded-lg">
            <p class="text-xs text-on-surface-variant mb-3">预览：</p>
            <div class="w-20 h-20 rounded-full bg-surface-container overflow-hidden mx-auto">
              <img :src="avatarPreview" class="w-full h-full object-cover">
            </div>
          </div>
        </div>
        <div class="px-6 py-4 bg-surface-container-low flex justify-end gap-3">
          <button class="px-4 py-2 text-label-md font-medium text-on-surface-variant hover:bg-surface-container rounded-lg" @click="showAvatarDialog = false">取消</button>
          <button class="px-6 py-2 text-label-md font-medium bg-primary text-white rounded-lg hover:bg-primary-container shadow-sm" @click="showAvatarDialog = false">开始上传</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  name: 'StitchUserinfo',
  data() {
    return {
      activeTab: 'info',
      showSaveNotification: false,
      editingPhone: false,
      showAvatarDialog: false,
      avatarPreview: null,
      userInfo: {
        nickname: '国网四川内江市区域郊供电所-罗金鑫',
        userId: '33',
        phone: '18482103768',
        role: '企业高级会员',
        dept: '数字化转型部',
        avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuADs7igVFY9-RVhKz7YANqTsy3BB1Xx6nclNMBs5PdiPeOGBH6RPjnnrC9VMohHSx7YNLrvSqRVISsL_w1HmZuIE8dQxV_0m3SQMqrrXSzef0fwUVWURdz3go_NbrSuyGGmWF7wnSlD9GKKk3Oh9MCbgs0gWqAT7N6YnM8eIA_folua99RRvgxPQ6y9XSgjEzar72HxthEydASnF7QCYMnetHMkl7vOWk1rDwQDq2o9LnQFiZXSl-APdFCt1yC3faQVP275pHU0_3o',
      },
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      },
      originalPhone: '18482103768',
    }
  },
  methods: {
    togglePhoneEdit() {
      this.editingPhone = !this.editingPhone
      if (this.editingPhone) {
        this.$nextTick(() => {
          if (this.$refs.phoneInput) this.$refs.phoneInput.focus()
        })
      }
    },
    saveChanges() {
      this.editingPhone = false
      this.showSaveNotification = false
    },
    cancelChanges() {
      this.userInfo.phone = this.originalPhone
      this.editingPhone = false
      this.showSaveNotification = false
    },
    handlePasswordChange() {
      console.log('Password change:', this.passwordForm)
    },
    resetPasswordForm() {
      this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' }
    },
    triggerAvatarUpload() {
      this.$refs.avatarInput.click()
    },
    handleAvatarChange(e) {
      const file = e.target.files[0]
      if (file) {
        const reader = new FileReader()
        reader.onload = (ev) => { this.avatarPreview = ev.target.result }
        reader.readAsDataURL(file)
      }
    },
  },
}
</script>
