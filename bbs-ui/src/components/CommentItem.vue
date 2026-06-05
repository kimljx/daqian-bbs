<template>
  <div class="flex gap-3">
    <div class="w-8 h-8 rounded-full overflow-hidden border border-border flex-shrink-0">
      <img :src="comment.avatar" class="w-full h-full object-cover">
    </div>
    <div class="flex-grow">
      <div class="flex items-center justify-between mb-1">
        <span class="font-bold text-on-surface font-body-md">{{ comment.author }}</span>
        <div class="flex gap-3 text-on-surface-variant font-label-md">
          <button class="hover:text-primary transition-primary flex items-center gap-1" @click="showReplyInput = !showReplyInput">
            <span class="material-symbols-outlined text-[16px]">reply</span> 回复
          </button>
          <button v-if="comment.canDelete" class="hover:text-error transition-primary flex items-center gap-1" @click="$emit('delete', comment.id)">
            <span class="material-symbols-outlined text-[16px]">delete</span> 删除
          </button>
        </div>
      </div>
      <p class="text-outline text-label-md mb-2">{{ comment.time }}</p>
      <div class="bg-surface p-4 rounded-lg border border-outline-variant font-body-md text-on-surface leading-relaxed mb-4">
        <span v-if="comment.replyTo" class="text-primary-container font-medium mr-2">回复{{ comment.replyTo }}:</span>
        {{ comment.content }}
      </div>

      <!-- Nested Replies -->
      <div v-if="comment.children && comment.children.length > 0" class="ml-10 space-y-6 border-l-2 border-surface-container pl-4">
        <CommentItem
          v-for="child in comment.children"
          :key="child.id"
          :comment="child"
          @delete="$emit('delete', $event)"
        />
      </div>

      <!-- Reply Input Box -->
      <div v-if="showReplyInput" class="mt-4 ml-10 flex gap-3 items-start">
        <div class="w-8 h-8 rounded-full bg-surface-variant flex-shrink-0"></div>
        <div class="flex-grow flex gap-2">
          <input
            v-model="replyText"
            class="flex-grow p-2 rounded-lg border border-border text-label-md bg-surface focus:border-primary-container outline-none"
            placeholder="请输入评论内容..."
            type="text"
            @keyup.enter="submitReply"
          >
          <button class="bg-primary-container/10 text-primary-container px-4 py-1.5 rounded-lg font-label-md text-label-md hover:bg-primary-container hover:text-white transition-primary" @click="submitReply">
            回复
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommentItem',
  props: {
    comment: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      showReplyInput: false,
      replyText: '',
    }
  },
  methods: {
    submitReply() {
      if (!this.replyText.trim()) return
      if (!this.comment.children) {
        this.$set(this.comment, 'children', [])
      }
      this.comment.children.push({
        id: Date.now(),
        author: '当前用户',
        avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCP52mnmWZOxBsd7pKUcKH0XYcVU0EL15cm5iXqKW0w2oJgWiJMZnGlRDva84A3aPUnIm0XJVsAGvxeUDVdogPb2RB6HAvdW0vBk2D1FgVxsWjtgGPCBEDfnO_jI0yERHdbsy_8b9zL3e9LvyJTPMP9mVuhOqiwL-E9T8iV427rBqAPG2MlYQmN2f_khm74nH30oggg5H2jnk-YjDQB33lzP-NVdKM_i6YBljINKMnekK8u4Cpt-moRjcVD6_YNJ_fBpmHnKCoTb9k',
        time: '刚刚',
        content: this.replyText,
        replyTo: this.comment.author,
        canDelete: true,
        children: [],
      })
      this.replyText = ''
      this.showReplyInput = false
    },
  },
}
</script>
