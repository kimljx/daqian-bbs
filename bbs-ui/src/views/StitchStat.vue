<template>
  <main class="max-w-7xl mx-auto px-page-margin-desktop py-12 bg-background min-h-screen">
    <!-- Page Title -->
    <header class="mb-10">
      <h1 class="font-headline-lg text-headline-lg text-on-surface">我的发布</h1>
    </header>

    <!-- Article List Container -->
    <div v-if="articles.length > 0" class="space-y-gutter">
      <div
        v-for="(article, index) in articles"
        :key="index"
        class="bg-container border border-outline-variant rounded-lg p-card-padding flex flex-col md:flex-row gap-6 relative group card-shadow hover:border-primary-container transition-colors"
        :style="{ transition: 'all 0.3s ease' }"
      >
        <div class="flex-grow flex flex-col justify-between min-w-0">
          <div>
            <h2 class="font-headline-sm text-headline-sm text-on-surface mb-2 truncate">{{ article.title }}</h2>
            <p class="font-body-md text-body-md text-on-surface-variant mb-6 line-clamp-2">{{ article.summary }}</p>
          </div>
          <!-- Metadata Bar -->
          <div class="flex items-center flex-wrap gap-4 font-label-md text-label-md text-on-surface-variant">
            <div class="flex items-center gap-2">
              <div class="w-6 h-6 rounded-full bg-primary-fixed flex items-center justify-center overflow-hidden">
                <img alt="Avatar" class="w-full h-full object-cover" :src="article.authorAvatar">
              </div>
              <span>{{ article.author }}</span>
            </div>
            <span class="text-outline">•</span>
            <span>{{ article.time }}</span>
            <div class="flex items-center gap-6 ml-auto">
              <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">visibility</span> {{ article.views }}</span>
              <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">chat_bubble</span> {{ article.comments }}</span>
              <span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">auto_awesome</span> {{ article.likes }}</span>
            </div>
          </div>
        </div>
        <!-- Optional Cover Image -->
        <div v-if="article.cover" class="w-full md:w-48 h-32 flex-shrink-0 rounded overflow-hidden">
          <img alt="Article Cover" class="w-full h-full object-cover" :src="article.cover">
        </div>
        <!-- Action Controls -->
        <div class="flex md:flex-col gap-2 md:justify-center border-l md:border-l-0 md:pl-0 pl-4 border-outline-variant">
          <button
            class="flex items-center justify-center gap-1 px-4 py-2 rounded font-label-md text-label-md border border-on-tertiary-fixed-variant text-on-tertiary-fixed-variant bg-tertiary-fixed hover:bg-tertiary-container hover:text-white transition-all active:scale-95"
            @click="handleDelete(index)"
          >
            <span class="material-symbols-outlined text-[18px]">delete</span>
            <span>删除</span>
          </button>
          <button
            class="flex items-center justify-center gap-1 px-4 py-2 rounded font-label-md text-label-md border border-outline text-secondary hover:bg-secondary-container hover:border-secondary transition-all active:scale-95"
            @click="handleEdit(article)"
          >
            <span class="material-symbols-outlined text-[18px]">edit_note</span>
            <span>修改</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="flex flex-col items-center justify-center py-24 text-on-surface-variant">
      <span class="material-symbols-outlined text-6xl mb-4 opacity-20">inventory_2</span>
      <p class="font-body-lg text-body-lg">暂无发布内容</p>
    </div>
  </main>
</template>

<script>
export default {
  name: 'StitchStat',
  data() {
    return {
      articles: [
        {
          title: '看看封面图',
          summary: '没有摘要',
          author: '国网四川内江市区城郊供电所-罗金鑫',
          authorAvatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBK9iWgfpcTL24GAPDgyicQFxMgvIwJGPz3fTP_tdkm-DjcZJRaQGYZURwVykt9pdG_eENnsV5uaF4hpkFUSejzwGKBirgjo13A4MuvQorvxQ36DOiQJ2hnE0DNvpODX4jHUdMYFseWLCvqlolJjIZxOL7Qun1Ed3-amz_msbJE7Uo_b9w_rdnes6g-1_yMT8d3dgWHVPjerLjSr3SC_4hS_JX0j8NAFKjxkw2CNZyYVzp280SXNhDfax_vuaYAlLpz2cVx_vDPsKA',
          time: '16天前',
          views: 7,
          comments: 0,
          likes: 0,
          cover: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDws5U0deUxe72ULVAK_bBqeVzFHI-IpLvMVWlqYrvgnBhiduttIrneMACuhguCJUPDke7mgnZvkJU9mG_djEZ7bvfcJVBFan_w-enbghMER_LcdweBp4EKmVe4pnfnC8BhPmj4D_OBfxo8l_EuveQNvYzCqeXuT6m4VQZKVjnsNF95RH91YWXGC-8hPulce9yGgPiLDez2sUsPJO3wDDsiP4aLnj8-EgW_cR-JqAeoDG9qY0JFtEdCxxX052VX1IPQbGclylMghCw',
        },
        {
          title: '反馈图片3张',
          summary: '全是图片',
          author: '国网四川内江市区城郊供电所-罗金鑫',
          authorAvatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCq-9FyhxYdjbRpbMJ3VP_w6SQddO0I9xuY-Fku9QsQLXJS0ZJulF0Z0D5ZYDRqfR-r6rA-LRW7D4UzpjFeBAWTq5wk7EOBLH9OsqNe1X6_3H4t9C0HkVPyz3yq7hwT7X0D2GVTf11OunGrBIIwIftBlnnDQitXSN_NZifuI99EDPVnQua8bhepvAGca1FTrusbXYcs4f871eASglNk_BlDev0smARHFm11NgyDs_GbGoV9J5QsjiR8eP00QCDpMCilO3yQScplpRk',
          time: '16天前',
          views: 4,
          comments: 0,
          likes: 0,
          cover: null,
        },
        {
          title: '测试图片发布',
          summary: '这是文章摘要，用于测试在卡片布局中的显示效果。通常包含核心内容的简要概括。',
          author: '国网四川内江市区城郊供电所-罗金鑫',
          authorAvatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAxyTrsiQE5O_-h79vT_G9ZLtIw8Sd_96A-_CPRWJFYT90TZyHtt73hdEEDacRaMbZ3d-RvkNqSHv8CAdSJj3oLS-8L7SF66TU43YjxqzhW-zZMLukQ1yMGgruzXx9UTV9O-6k-ODf3BvI7SsUn_o5lPpqwZT6DmahYTRWZCgNHnEnKPL7K2JFGfVEi8XyL7ieb7ZF7ZXH9jBR8rIU_5foI_MDmisCfucJdnK90qVm6K7K0racWi5N8FVjArUvepCIv7vpKIbvA9XM',
          time: '16天前',
          views: 7,
          comments: 0,
          likes: 0,
          cover: 'https://lh3.googleusercontent.com/aida-public/AB6AXuC-J0PNzVHHqbtERifF5r4HIFHYLuz_zbbr8Yki_uIK2qSSQ7PYyBXy8YxF08BClE04ijmDXffl4wIM7TvgYtrvyrIkKPVVOVeHy4WB6hXkPONfLsTwpz49tHz7NGDIjHftmgj4McStSObe-4YrtWhvuaL5BQMN5XNe05-AOgEIBzrJwxDSsHw136Q6372Jo3qAHr5nvwaqqvOH8h8ibXiJVMLe6qUP-CAZ915xw9AAuPVO7Omv3z3EJIOAH72SO2hgO9S9eNBiydk',
        },
      ],
    }
  },
  methods: {
    handleDelete(index) {
      if (confirm('确定要删除这篇文章吗？')) {
        this.articles.splice(index, 1)
      }
    },
    handleEdit(article) {
      this.$router.push({ name: 'stitchWrite', query: { id: article.title } })
    },
  },
}
</script>
