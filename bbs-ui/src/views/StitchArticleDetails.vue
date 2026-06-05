<template>
  <main class="max-w-5xl mx-auto px-page-margin-mobile md:px-page-margin-desktop py-8 bg-surface min-h-screen">
    <!-- Article Header Card -->
    <article class="bg-container rounded-lg border border-border shadow-sm overflow-hidden mb-gutter">
      <div class="p-card-padding">
        <!-- Title & Meta -->
        <header class="mb-6">
          <h1 class="font-headline-lg text-headline-lg text-on-surface mb-4 leading-tight">
            {{ article.title }}
          </h1>
          <div class="flex flex-wrap items-center justify-between gap-4 text-on-surface-variant font-label-md text-label-md">
            <div class="flex items-center space-x-6">
              <span class="flex items-center gap-1.5">
                <span class="material-symbols-outlined text-[16px]">schedule</span>
                发布时间: {{ article.publishTime }}
              </span>
              <div class="flex items-center gap-2">
                <span>文章标签:</span>
                <span class="px-2 py-0.5 bg-surface-container-low text-primary-container rounded border border-outline-variant font-medium">{{ article.tag }}</span>
              </div>
            </div>
            <div class="flex items-center gap-3 py-1 px-3 bg-surface-container-lowest border border-border rounded-lg">
              <div class="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-white overflow-hidden shadow-inner">
                <img alt="Author" class="w-full h-full object-cover" :src="article.authorAvatar">
              </div>
              <div class="flex flex-col">
                <span class="font-bold text-on-surface">{{ article.author }}</span>
                <span class="text-[10px] text-outline">{{ article.authorTitle }}</span>
              </div>
            </div>
          </div>
        </header>
        <hr class="border-border mb-8">

        <!-- Article Content (Markdown Mock) -->
        <section class="markdown-body font-body-lg text-body-lg text-on-surface">
          <p>{{ article.contentIntro }}</p>
          <img
            v-if="article.contentImage"
            class="w-full h-auto object-cover rounded-lg border border-border"
            :src="article.contentImage"
          >
          <p v-for="(para, i) in article.contentParagraphs" :key="i">{{ para }}</p>
        </section>
      </div>
    </article>

    <!-- Attachments Section -->
    <section v-if="article.attachments && article.attachments.length > 0" class="bg-container rounded-lg border border-border shadow-sm p-card-padding mb-gutter">
      <div class="flex items-center gap-2 mb-4">
        <span class="material-symbols-outlined text-primary-container">attachment</span>
        <h3 class="font-headline-sm text-headline-sm text-on-surface">附件 ({{ article.attachments.length }})</h3>
      </div>
      <div class="space-y-3">
        <div
          v-for="(att, i) in article.attachments"
          :key="i"
          class="flex items-center justify-between p-3 bg-surface rounded-lg border border-outline-variant hover:border-primary-container transition-primary group"
        >
          <div class="flex items-center gap-3">
            <span class="material-symbols-outlined text-outline group-hover:text-primary-container">description</span>
            <span class="font-body-md text-body-md text-on-surface">{{ att.name }}</span>
          </div>
          <button class="flex items-center gap-2 bg-primary-container text-white px-4 py-1.5 rounded text-label-md font-label-md hover:bg-primary transition-primary">
            <span class="material-symbols-outlined text-[18px]">download</span>
            下载
          </button>
        </div>
      </div>
    </section>

    <!-- Comment Section -->
    <section class="bg-container rounded-lg border border-border shadow-sm overflow-hidden">
      <div class="p-card-padding">
        <div class="flex items-center justify-between mb-6">
          <h3 class="font-headline-sm text-headline-sm text-on-surface flex items-center gap-2">
            评论 <span class="px-2 py-0.5 bg-surface-container-high rounded-full text-label-md">{{ totalCommentCount }}</span>
          </h3>
        </div>

        <!-- Comment Input -->
        <div class="flex gap-4 mb-10">
          <div class="w-10 h-10 rounded-full bg-surface-variant flex-shrink-0 overflow-hidden border border-outline-variant">
            <img alt="Current User" :src="currentUserAvatar">
          </div>
          <div class="flex-grow space-y-3">
            <textarea
              v-model="newComment"
              class="w-full p-3 rounded-lg border border-border focus:border-primary-container focus:ring-1 focus:ring-primary-container min-h-[100px] font-body-md text-body-md bg-surface transition-primary resize-none"
              placeholder="请输入评论内容..."
            ></textarea>
            <div class="flex justify-end">
              <button
                class="bg-primary-container text-white px-6 py-2 rounded-lg font-label-md text-label-md hover:shadow-md hover:bg-primary transition-primary active:scale-95"
                @click="submitComment"
              >
                发表评论
              </button>
            </div>
          </div>
        </div>

        <!-- Comments List -->
        <div class="space-y-8">
          <CommentItem
            v-for="comment in comments"
            :key="comment.id"
            :comment="comment"
            @delete="handleDeleteComment"
          />
        </div>
      </div>
    </section>
  </main>
</template>

<script>
import CommentItem from '@/components/CommentItem.vue'

export default {
  name: 'StitchArticleDetails',
  components: { CommentItem },
  data() {
    return {
      newComment: '',
      currentUserAvatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCP52mnmWZOxBsd7pKUcKH0XYcVU0EL15cm5iXqKW0w2oJgWiJMZnGlRDva84A3aPUnIm0XJVsAGvxeUDVdogPb2RB6HAvdW0vBk2D1FgVxsWjtgGPCBEDfnO_jI0yERHdbsy_8b9zL3e9LvyJTPMP9mVuhOqiwL-E9T8iV427rBqAPG2MlYQmN2f_khm74nH30oggg5H2jnk-YjDQB33lzP-NVdKM_i6YBljINKMnekK8u4Cpt-moRjcVD6_YNJ_fBpmHnKCoTb9k',
      article: {
        title: '技术交流：大千智荟平台的前端架构演进与性能优化实践',
        publishTime: '2024-05-12 12:01:31',
        tag: '技术交流',
        author: '国网四川内江供电公司-qupeng',
        authorTitle: '高级工程师',
        authorAvatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAY_w8EMmZtJ1uTt7Om3wjhcj3gWqOf5bkE6Tcqdvj5vlqQSDDdKrEBFSO67nMOEC2jS-1bL-fngMS9sHNEKQqx49Az9s9HOF9wgQ3X_WJJ5nlEukIarjq-2Ctgy7dDFSf14fhxWl65I_zciumW0zX5cGWjm0Hp1PTm1mSNdR7To5rylPmzRyn8ujYyDC_ra72BChi0EDHttZROxnT53FydjkQZNL52rzAx_SD3nOUyoiopkq_xUEm3GGRk6wZ9-gvWC3wBgSVLKRw',
        contentIntro: '在本篇文章中，我们将深入探讨大千智荟交流论坛在近期进行的前端架构升级。作为企业级知识交流平台，其核心挑战在于如何在保持严谨、专业视觉风格的同时，提供极致的内容加载速度与交互流畅度。',
        contentImage: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBA-s1YyF6pfbdiGqhVb6Y0flR46E17B-ez9DYLENrkjuctaTv4zSUknEO7Sc2Su3cFg73LrZZXVgO9kHtjc4lThZbKFbm3IzRYpYlP3KUKW4M2T-ciNFOOVFVwFcqsa1pheQBGoW7sUyVEM_pEs7vte9JzojSbFG1fUvSiRGpVkZgKpst_Pw6YxXVOulycBWRSrKpJaX55muzUSy-ynCdSZTQQ6Khlhj1ofSwbwUpaMkeHIC9m-a1tMB7A2abwHgGFLyaACpxW9Ec',
        contentParagraphs: [
          '我们采用了基于组件化的设计系统，通过严格的 Visual Tokens 管理，确保了 UI 的一致性。此外，对于长篇文章中的大量附件与评论数据，我们实施了懒加载与虚拟滚动技术。',
          '"技术是工具，交流是灵魂。" 这句话贯穿了我们的整个开发周期。在后续的内容中，我们将展示具体的代码实现方案与性能测试结果，希望能对大家在类似平台的开发中有所启发。',
        ],
        attachments: [
          { name: '国网四川省电力公司常见漏洞检测与处置手册 (2022版) 编制...' },
        ],
      },
      comments: [
        {
          id: 1,
          author: '国网四川内江东兴双才供电所-曹煊洲',
          avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDi4uTc1WAQi2W-npQ1RZFgceqnIbyW43D-zJkvrcQC65uh21wBgaWT2IcsFmjIQImtFxKRoDUSDjDDfHxYODN82UcSaR3a4KzI4ZjL1OV6oG3emPZpWglnjk1QJ2_2wGtO3xh54JcykysXRc3ZaJ79fpiGtjjMDfb2GxJgzDLqxcc9CH8SfEwK8fMmP2CyMbb4OW9Xyz-6T0Msd6ckL8jeQawwKdp-cVX1JV5D_EnK7ufHeI8WJVs7GT2lwJ8bE2UnDVPvYSkivPI',
          time: '2026-03-19 09:04:31',
          content: '很好的一篇文章，对我们的日常工作很有启发，特别是在前端性能指标的监控方面。',
          canDelete: false,
          children: [
            {
              id: 2,
              author: '国网四川内江市区域邻供电所-罗金鑫',
              avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCpKuDSASBfD02P_85fXNjus1uaQKaUd87lm0dOX2AFA6vJpKBBl5s8N3hhx_rv7x8_Pr6r6jaBGFFQbvEft7ly1n-V2am_SfNpfMscF6GYXF0CSVlpYpXqr3NmsyQWG6pjFNQuFycTqTE7hUqx96sYNU0eC2aB8znL0bRhDVZfFUUyjo-ZHFs6IKDLrGlckKFIGtCkTRCM1NA3oVuLofHnTsrtrH1oucMAtKRap2cLskVMUNi-Dzbh7ltcSuh3FIiy_nGBzsHwsXA',
              time: '刚刚',
              content: '测试一下嵌套回复，感谢曹工的支持！',
              replyTo: '国网四川内江东兴双才供电所-曹煊洲',
              canDelete: true,
              children: [],
            },
          ],
        },
      ],
    }
  },
  computed: {
    totalCommentCount() {
      const countChildren = (list) => {
        let count = 0
        list.forEach(c => {
          count++
          if (c.children && c.children.length) count += countChildren(c.children)
        })
        return count
      }
      return countChildren(this.comments)
    },
  },
  methods: {
    submitComment() {
      if (!this.newComment.trim()) return
      this.comments.push({
        id: Date.now(),
        author: '当前用户',
        avatar: this.currentUserAvatar,
        time: '刚刚',
        content: this.newComment,
        canDelete: true,
        children: [],
      })
      this.newComment = ''
    },
    handleDeleteComment(commentId) {
      const removeFromList = (list) => {
        for (let i = 0; i < list.length; i++) {
          if (list[i].id === commentId) {
            list.splice(i, 1)
            return true
          }
          if (list[i].children && removeFromList(list[i].children)) return true
        }
        return false
      }
      removeFromList(this.comments)
    },
  },
}
</script>
