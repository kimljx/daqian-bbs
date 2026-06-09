<template>
  <div class="stitch-wrapper h-full w-full flex flex-col bg-background overflow-hidden">
    <!-- Fixed Header -->
    <StitchHeader />

    <!-- Body: Sidebar + Content -->
    <div class="flex flex-1 overflow-hidden relative">
      <!-- Sidebar -->
      <StitchSidebar />

      <!-- Main Content Area -->
      <div
        class="flex-1 flex flex-col overflow-hidden transition-all duration-300 ease-in-out"
        :class="collapse ? 'ml-16' : 'ml-64'"
      >
        <!-- Tags Bar -->
        <StitchTags />

        <!-- Page Content -->
        <div class="flex-1 overflow-y-auto bg-background" ref="contentWrapper">
          <transition name="page-fade" mode="out-in">
            <keep-alive :include="cachedTags">
              <router-view class="min-h-full" />
            </keep-alive>
          </transition>

          <!-- Back to top -->
          <transition name="fade">
            <button
              v-if="showBackTop"
              class="fixed bottom-8 right-8 w-10 h-10 bg-container border border-outline-variant rounded-xl shadow-md flex items-center justify-center text-on-surface-variant hover:bg-surface-container-low hover:text-primary transition-all z-20"
              @click="scrollToTop"
            >
              <span class="material-symbols-outlined text-[20px]">arrow_upward</span>
            </button>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import StitchHeader from './StitchHeader.vue'
import StitchSidebar from './StitchSidebar.vue'
import StitchTags from './StitchTags.vue'
import bus from './bus'

export default {
  name: 'StitchHome',
  components: { StitchHeader, StitchSidebar, StitchTags },
  data() {
    return {
      collapse: false,
      cachedTags: [],
      showBackTop: false,
    }
  },
  created() {
    bus.$on('collapse-content', (msg) => {
      this.collapse = msg
    })
    bus.$on('tags', (msg) => {
      this.cachedTags = msg.map(t => t.name).filter(Boolean)
    })
  },
  mounted() {
    // Back to top listener
    const wrapper = this.$refs.contentWrapper
    if (wrapper) {
      wrapper.addEventListener('scroll', () => {
        this.showBackTop = wrapper.scrollTop > 300
      })
    }

    // Handle initial collapse state
    if (document.body.clientWidth < 1500) {
      bus.$emit('collapse', true)
    }
  },
  methods: {
    scrollToTop() {
      const wrapper = this.$refs.contentWrapper
      if (wrapper) {
        wrapper.scrollTo({ top: 0, behavior: 'smooth' })
      }
    },
  },
}
</script>

<style scoped>
.stitch-wrapper {
  /* Full viewport layout */
}

/* Page transition */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-fade-enter {
  opacity: 0;
  transform: translateY(8px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
