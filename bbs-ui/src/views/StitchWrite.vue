<template>
  <div class="min-h-screen flex flex-col bg-surface">
    <!-- Main Content Area -->
    <main class="flex-grow flex flex-col pt-10 pb-28 px-4 sm:px-10 max-w-7xl mx-auto w-full">
      <!-- Title Input Section -->
      <div class="mb-8 relative flex items-center group">
        <textarea
          ref="titleInput"
          v-model="articleTitle"
          class="w-full bg-transparent border-b border-transparent hover:border-gray-200 focus:border-primary focus:ring-0 text-4xl font-bold text-gray-800 placeholder-gray-300 p-0 pb-2 resize-none overflow-hidden transition-all duration-200"
          placeholder="请输入文章标题"
          rows="1"
          @input="autoResizeTitle"
        ></textarea>
        <span class="material-symbols-outlined absolute right-0 text-gray-300 pointer-events-none group-hover:text-gray-400">edit</span>
      </div>

      <!-- Markdown Editor (using mavon-editor) -->
      <div class="bg-white border border-gray-200 rounded-lg flex flex-col flex-grow shadow-sm overflow-hidden">
        <mavon-editor
          ref="mdEditor"
          v-model="markdownContent"
          :toolbars="toolbars"
          :subfield="true"
          :default-open="'edit'"
          :placeholder="'从这里开始你的创作...'"
          :box-shadow="false"
          :ishljs="true"
          style="min-height: 500px; z-index: 1;"
          @imgAdd="handleImgAdd"
        />
      </div>

      <!-- Attachment Section -->
      <div class="mt-8">
        <div class="flex items-center justify-between mb-4">
          <h4 class="text-sm font-semibold text-gray-700">附件上传 (最多5个)</h4>
          <span class="text-xs text-gray-400">不允许上传 .exe, .bat, .sh 格式</span>
        </div>
        <button
          class="inline-flex items-center px-4 py-2 bg-primary text-white text-sm font-medium rounded-lg hover:opacity-90 transition-colors shadow-sm"
          @click="triggerAttachment"
        >
          <span class="material-symbols-outlined text-lg mr-2">add</span>
          添加附件
        </button>
        <input ref="attachmentInput" class="hidden" multiple type="file" @change="handleAttachmentAdd">

        <!-- Attachment List -->
        <div class="mt-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
          <div
            v-for="(file, index) in attachments"
            :key="index"
            class="flex items-center p-3 bg-white border border-gray-200 rounded-lg group hover:border-blue-200 transition-colors"
          >
            <div class="w-10 h-10 bg-blue-50 flex items-center justify-center rounded text-blue-500 mr-3">
              <span class="material-symbols-outlined">description</span>
            </div>
            <div class="flex-grow min-w-0">
              <p class="text-sm font-medium text-gray-700 truncate">{{ file.name }}</p>
              <p class="text-xs text-gray-400">{{ file.size }}</p>
            </div>
            <button class="p-1 text-gray-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-opacity" @click="removeAttachment(index)">
              <span class="material-symbols-outlined text-lg">delete</span>
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- Bottom Action Bar -->
    <div class="bottom-bar fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 py-4 px-10 flex justify-center space-x-4 z-40">
      <button class="px-10 py-2.5 bg-primary text-white font-medium rounded-full hover:opacity-90 transition-all shadow-md active:scale-95" @click="showPublishModal = true">
        发布文章
      </button>
      <button class="px-10 py-2.5 font-medium rounded-full hover:bg-gray-200 transition-all active:scale-95 bg-red-500 text-white" @click="$router.back()">
        取消发布
      </button>
    </div>

    <!-- Publish Modal -->
    <div v-if="showPublishModal" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="showPublishModal = false">
      <div class="modal-overlay absolute inset-0"></div>
      <div class="bg-white rounded-lg shadow-2xl w-full max-w-2xl relative z-10 overflow-hidden">
        <!-- Header -->
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
          <h3 class="text-lg font-semibold text-gray-800">发布文章</h3>
          <button class="text-gray-400 hover:text-gray-600 p-1" @click="showPublishModal = false">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <div class="p-6 space-y-6">
          <div class="flex flex-col md:flex-row gap-6">
            <!-- Left: Cover Upload -->
            <div class="w-full md:w-1/3">
              <label class="block text-sm font-medium text-gray-700 mb-2">文章封面</label>
              <div
                class="aspect-[4/3] w-full bg-gray-50 border-2 border-dashed border-gray-200 rounded-lg flex flex-col items-center justify-center text-gray-400 cursor-pointer hover:border-primary hover:bg-blue-50/50 transition-all group"
                @click="triggerCoverUpload"
              >
                <span v-if="!coverPreview" class="material-symbols-outlined text-3xl group-hover:text-primary transition-colors">add</span>
                <span v-if="!coverPreview" class="text-xs mt-2">上传封面图</span>
                <img v-if="coverPreview" :src="coverPreview" class="w-full h-full object-cover rounded-lg">
              </div>
              <input ref="coverInput" accept="image/*" class="hidden" type="file" @change="handleCoverChange">
            </div>
            <!-- Right: Summary -->
            <div class="flex-grow">
              <label class="block text-sm font-medium text-gray-700 mb-2">文章摘要</label>
              <div class="relative">
                <textarea
                  v-model="articleSummary"
                  class="w-full border-gray-200 rounded-lg focus:ring-primary/20 focus:border-primary text-sm p-3 placeholder-gray-300 resize-none"
                  maxlength="250"
                  placeholder="请输入摘要，吸引更多读者..."
                  rows="5"
                ></textarea>
                <span class="absolute bottom-2 right-2 text-xs text-gray-400">{{ articleSummary.length }}/250</span>
              </div>
            </div>
          </div>
          <!-- Tags -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-3">选择标签</label>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="tag in tags"
                :key="tag"
                class="px-4 py-1.5 rounded-full border text-sm transition-colors"
                :class="selectedTag === tag ? 'border-primary bg-blue-50 text-primary' : 'border-gray-200 text-gray-600 hover:border-primary hover:text-primary hover:bg-blue-50'"
                @click="selectedTag = tag"
              >
                {{ tag }}
              </button>
            </div>
          </div>
        </div>
        <!-- Footer -->
        <div class="px-6 py-4 bg-gray-50 flex justify-end gap-3">
          <button class="px-6 py-2 text-sm font-medium text-gray-600 hover:text-gray-800 transition-colors" @click="showPublishModal = false">取消</button>
          <button class="px-8 py-2 text-white text-sm font-bold rounded-lg hover:opacity-90 transition-all shadow-sm bg-red-500" @click="handlePublish">确认发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

export default {
  name: 'StitchWrite',
  components: { mavonEditor },
  data() {
    return {
      articleTitle: '反馈图片3张',
      markdownContent: '![反馈详情.jpg](http://24.62.0.65:18848/bbs-server/files/User/id_33/article/2026-05-06/1778032171866_.jpg)',
      articleSummary: '',
      showPublishModal: false,
      coverPreview: null,
      selectedTag: '求助问答',
      tags: ['技术交流', '求助问答', '资源分享', '社区活动'],
      attachments: [
        { name: '反馈详情.jpg', size: '1.2 MB' },
      ],
      toolbars: {
        bold: true,
        italic: true,
        header: true,
        underline: true,
        strikethrough: true,
        mark: true,
        superscript: true,
        subscript: true,
        quote: true,
        ol: true,
        ul: true,
        link: true,
        imagelink: true,
        code: true,
        table: true,
        fullscreen: true,
        readmodel: true,
        htmlcode: false,
        help: true,
        undo: true,
        redo: true,
        trash: true,
        save: false,
        navigation: true,
        subfield: true,
        preview: true,
      },
    }
  },
  methods: {
    autoResizeTitle() {
      const el = this.$refs.titleInput
      if (el) {
        el.style.height = 'auto'
        el.style.height = el.scrollHeight + 'px'
      }
    },
    handleImgAdd(pos, file) {
      // Use existing project pattern for image upload
      const formData = new FormData()
      formData.append('file', file)
      this.postRequest('/common/upload', formData).then(res => {
        this.$refs.mdEditor.$img2Url(pos, res.url || res)
      }).catch(() => {
        // Fallback: use local blob URL
        const url = URL.createObjectURL(file)
        this.$refs.mdEditor.$img2Url(pos, url)
      })
    },
    triggerAttachment() {
      this.$refs.attachmentInput.click()
    },
    handleAttachmentAdd(e) {
      const files = e.target.files
      for (let i = 0; i < files.length; i++) {
        const file = files[i]
        const ext = file.name.split('.').pop().toLowerCase()
        if (['exe', 'bat', 'sh'].includes(ext)) continue
        if (this.attachments.length >= 5) break
        const sizeMB = (file.size / (1024 * 1024)).toFixed(1)
        this.attachments.push({ name: file.name, size: sizeMB + ' MB' })
      }
      this.$refs.attachmentInput.value = ''
    },
    removeAttachment(index) {
      this.attachments.splice(index, 1)
    },
    triggerCoverUpload() {
      this.$refs.coverInput.click()
    },
    handleCoverChange(e) {
      const file = e.target.files[0]
      if (file) {
        const reader = new FileReader()
        reader.onload = (ev) => { this.coverPreview = ev.target.result }
        reader.readAsDataURL(file)
      }
    },
    handlePublish() {
      // TODO: integrate with API
      console.log('Publishing:', {
        title: this.articleTitle,
        content: this.markdownContent,
        summary: this.articleSummary,
        tag: this.selectedTag,
        attachments: this.attachments,
      })
      this.showPublishModal = false
      this.$message?.success('发布成功！')
      this.$router.push('/stitch-stat')
    },
  },
}
</script>
