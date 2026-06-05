<template>
  <main class="min-h-screen bg-surface">
    <!-- Hero Section -->
    <section class="hero-gradient text-white py-16 px-page-margin-desktop">
      <div class="max-w-7xl mx-auto text-center">
        <h1 class="font-headline-lg text-[32px] md:text-[40px] leading-tight mb-4 tracking-tight">欢迎来到大千智荟创新创意交流论坛</h1>
        <p class="font-body-lg opacity-90 max-w-2xl mx-auto">汇聚国网智慧，激发创新灵感，共同打造卓越的企业级知识共享平台。</p>
      </div>
    </section>

    <!-- Main Layout Container -->
    <div class="max-w-7xl mx-auto px-page-margin-desktop py-8">
      <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
        <!-- Left Sidebar: Categories -->
        <aside class="md:col-span-2 space-y-4">
          <div class="bg-container border border-border rounded-lg p-2 sticky top-8">
            <nav class="flex flex-col gap-1">
              <button
                v-for="cat in categories"
                :key="cat.name"
                class="flex items-center gap-3 w-full p-3 rounded-lg transition-colors font-label-md"
                :class="cat.active ? 'bg-surface-container-low text-primary-container' : 'text-on-surface-variant hover:bg-surface-container-low'"
                @click="selectCategory(cat)"
              >
                <span class="material-symbols-outlined" :class="{ 'filled-icon': cat.active }">{{ cat.icon }}</span>
                <span>{{ cat.name }}</span>
              </button>
            </nav>
          </div>
        </aside>

        <!-- Center Feed: Articles -->
        <section class="md:col-span-7 space-y-4">
          <article
            v-for="(article, index) in articles"
            :key="index"
            class="bg-container border border-border rounded-lg p-card-padding hover:shadow-sm transition-shadow cursor-pointer"
            @click="goToArticle(article)"
          >
            <div class="flex flex-col md:flex-row gap-6">
              <div class="flex-1">
                <h3 class="font-headline-sm text-on-surface mb-2 hover:text-primary-container transition-colors">{{ article.title }}</h3>
                <p class="text-body-md text-secondary mb-4 line-clamp-2">{{ article.summary }}</p>
                <div class="flex items-center justify-between text-label-md text-outline">
                  <div class="flex items-center gap-2">
                    <div class="w-6 h-6 rounded-full bg-surface-container-highest flex items-center justify-center overflow-hidden">
                      <span class="material-symbols-outlined text-[14px]">person</span>
                    </div>
                    <span class="truncate max-w-[150px]">作者：{{ article.author }}</span>
                    <span class="mx-1">•</span>
                    <span>{{ article.time }}</span>
                  </div>
                  <div class="flex items-center gap-4">
                    <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">visibility</span> {{ article.views }}</span>
                    <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">chat_bubble</span> {{ article.comments }}</span>
                    <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">thumb_up</span> {{ article.likes }}</span>
                  </div>
                </div>
              </div>
              <div v-if="article.cover" class="w-full md:w-32 h-24 rounded-lg bg-surface-container overflow-hidden shrink-0 border border-outline-variant">
                <img alt="Article Cover" class="w-full h-full object-cover" :src="article.cover">
              </div>
            </div>
          </article>

          <!-- End of feed indicator -->
          <div class="flex flex-col items-center justify-center py-12 border-2 border-dashed border-outline-variant rounded-lg bg-surface-container-low opacity-60">
            <span class="material-symbols-outlined text-outline text-[48px] mb-2">inbox</span>
            <p class="font-label-md text-outline">已经到底啦，没有更多帖子了</p>
          </div>
        </section>

        <!-- Right Sidebar: Hot Topics -->
        <aside class="md:col-span-3 space-y-4">
          <div class="bg-container border border-border rounded-lg p-card-padding">
            <div class="flex items-center justify-between mb-4">
              <h2 class="font-headline-sm flex items-center gap-2">
                <span class="material-symbols-outlined text-error filled-icon">local_fire_department</span>
                热榜
              </h2>
            </div>
            <ul class="space-y-4">
              <li
                v-for="(topic, index) in hotTopics"
                :key="index"
                class="flex items-start gap-3 group cursor-pointer"
              >
                <span
                  class="flex-shrink-0 w-6 h-6 flex items-center justify-center rounded-full font-bold text-[12px]"
                  :class="getRankClass(index + 1)"
                >{{ index + 1 }}</span>
                <span class="font-body-md text-on-surface group-hover:text-primary-container transition-colors line-clamp-1">{{ topic }}</span>
              </li>
            </ul>
            <button class="w-full mt-6 py-2 text-label-md text-primary-container font-semibold hover:bg-surface-container-low rounded-lg transition-colors" @click="$router.push('/stitch-points')">
              查看完整榜单
            </button>
          </div>

          <!-- Side Promotion Card -->
          <div class="bg-primary-container text-white rounded-lg p-card-padding relative overflow-hidden">
            <div class="relative z-10">
              <h4 class="font-headline-sm mb-1">智荟专题</h4>
              <p class="text-body-md opacity-80">探索最前沿的电力行业技术专题。</p>
            </div>
            <span class="material-symbols-outlined absolute -bottom-4 -right-4 text-white/20 text-[100px] select-none pointer-events-none">lightbulb</span>
          </div>
        </aside>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  name: 'StitchIndex',
  data() {
    return {
      categories: [
        { name: '技术交流', icon: 'thumb_up', active: true },
        { name: '求助问答', icon: 'help', active: false },
        { name: '资源共享', icon: 'folder_open', active: false },
      ],
      articles: [
        {
          title: '看下图片',
          summary: '看下图片内容描述，这里展示了关于该主题的详细内容预览...',
          author: '国网四川内江供电公司-qupeng',
          time: '9天前',
          views: 3,
          comments: 0,
          likes: 0,
          cover: null,
        },
        {
          title: '能看到图片吗',
          summary: '能看到图片吗？这是一个测试贴，用于验证平台图片渲染与布局的兼容性。',
          author: '国网四川内江供电公司-qupeng',
          time: '9天前',
          views: 2,
          comments: 0,
          likes: 0,
          cover: null,
        },
        {
          title: '看看封面图',
          summary: '没有摘要。本文主要探讨了配电网自动化系统在极端天气下的稳定性保障方案，以及新型传感器在故障定位中的应用。',
          author: '国网四川内江市东城郊供电所-罗金鑫',
          time: '16天前',
          views: 7,
          comments: 0,
          likes: 0,
          cover: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAPuyX5KB5T1A_d7TcCU5Bvq29ztDs4O7y0EWU1yJ8DUPDMJoje4LYCsTOTc2K_giaVouKJq24euFZI43gijycui8rHIsDbYZ_HrTfuw_vne9Ceox8VE9LPR8BgD3betUqrl9wgMRU_SMNqAD5e9YyIDJXv89By5_la3hq6fp0gXqqPrPL0aZMSG69yeP9scoSdBDSPKDimhBjj5xBNC4MIkgqx6SJKX0oJpvCheZhfKWRmFH5T0M6SUxIkDZBDi_r1PsGOMSAGJ34',
        },
      ],
      hotTopics: [
        '智能巡检机器人最新应用',
        '电力系统碳中和技术路线图',
        '关于数字化班组建设的思考',
        '配网不停电作业技术规范',
        '测试图片发布规范',
      ],
    }
  },
  methods: {
    selectCategory(cat) {
      this.categories.forEach(c => { c.active = false })
      cat.active = true
    },
    goToArticle(article) {
      this.$router.push({ name: 'stitchArticleDetails', params: { articleId: 1 } })
    },
    getRankClass(rank) {
      if (rank === 1) return 'rank-1'
      if (rank === 2) return 'rank-2'
      if (rank === 3) return 'rank-3'
      return 'rank-other'
    },
  },
}
</script>
