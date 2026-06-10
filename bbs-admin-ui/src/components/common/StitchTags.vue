<template>
  <div v-if="tagsList.length > 0" class="stitch-tags h-10 bg-surface-container-low border-b border-outline-variant flex items-stretch">
    <!-- Tags scroll area -->
    <div class="flex-1 flex items-stretch overflow-x-auto scrollbar-none" ref="tagsWrapper">
      <div
        v-for="(item, index) in tagsList"
        :key="item.path"
        class="tag-item flex items-center gap-1.5 px-3.5 cursor-pointer select-none border-r border-outline-variant/20 transition-all duration-150 whitespace-nowrap text-[13px] group relative"
        :class="isActive(item.path) ? 'tag-active bg-primary/5 text-primary font-semibold' : 'text-on-surface-variant hover:bg-surface-container hover:text-on-surface'"
        @click="$router.push(item.path)"
      >
        <!-- Active indicator bar -->
        <span class="absolute top-0 left-2 right-2 h-[3px] bg-primary rounded-b-full transition-all duration-150" v-if="isActive(item.path)"></span>
        <!-- Title -->
        <span class="max-w-[120px] truncate py-0.5">{{ item.title }}</span>
        <!-- Close button -->
        <span
          class="inline-flex items-center justify-center w-[18px] h-[18px] rounded-full text-[11px] opacity-0 group-hover:opacity-100 hover:bg-outline-variant/60 hover:scale-110 transition-all duration-150"
          :class="isActive(item.path) ? 'hover:bg-primary/10 text-primary' : 'text-outline'"
          @click.stop="closeTags(index)"
        >
          <span class="material-symbols-outlined text-[13px]">close</span>
        </span>
      </div>
    </div>

    <!-- Dropdown actions -->
    <div class="flex-shrink-0 flex items-center border-l border-outline-variant/20 bg-surface-container-low relative z-50">
      <button
        class="px-3.5 h-full flex items-center gap-1.5 text-on-surface-variant hover:bg-surface-container hover:text-on-surface transition-colors text-xs font-label-md"
        @click="showMenu = !showMenu"
      >
        <span class="material-symbols-outlined text-[16px]">arrow_drop_down</span>
        <span class="hidden sm:inline">标签选项</span>
      </button>

      <!-- Dropdown -->
      <transition name="fade-down">
        <div v-if="showMenu" class="absolute top-full right-0 mt-1.5 w-44 bg-container border border-outline-variant rounded-xl shadow-lg z-50 py-1.5 overflow-hidden">
          <button class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-on-surface hover:bg-surface-container-low transition-colors" @click="closeOther">
            <span class="material-symbols-outlined text-[18px] text-outline">filter_alt</span>
            关闭其他
          </button>
          <div class="mx-3 border-t border-outline-variant/15"></div>
          <button class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-on-surface hover:bg-surface-container-low transition-colors" @click="closeAll">
            <span class="material-symbols-outlined text-[18px] text-outline">close</span>
            关闭所有
          </button>
        </div>
      </transition>

      <!-- Menu backdrop -->
      <div v-if="showMenu" class="fixed inset-0 z-40" @click="showMenu = false"></div>
    </div>
  </div>
</template>

<script>
import bus from './bus'

export default {
  name: 'StitchTags',
  data() {
    return {
      tagsList: [],
      showMenu: false,
    }
  },
  computed: {
    showTags() {
      return this.tagsList.length > 0
    },
  },
  watch: {
    $route(newRoute) {
      this.setTags(newRoute)
    },
  },
  created() {
    this.setTags(this.$route)

    bus.$on('close_current_tags', () => {
      for (let i = 0; i < this.tagsList.length; i++) {
        if (this.tagsList[i].path === this.$route.fullPath) {
          const next = this.tagsList[i + 1] || this.tagsList[i - 1]
          if (next) {
            this.$router.push(next.path)
          } else {
            this.$router.push('/stitch-dashboard')
          }
          this.tagsList.splice(i, 1)
          break
        }
      }
    })
  },
  methods: {
    isActive(path) {
      return path === this.$route.fullPath
    },
    setTags(route) {
      if (!route || !route.meta || !route.meta.title) return

      const isExist = this.tagsList.some(item => item.path === route.fullPath)
      if (!isExist) {
        if (this.tagsList.length >= 8) {
          this.tagsList.shift()
        }
        this.tagsList.push({
          title: route.meta.title,
          path: route.fullPath,
          name: route.name || '',
        })
      }
      bus.$emit('tags', this.tagsList)
    },
    closeTags(index) {
      const delItem = this.tagsList.splice(index, 1)[0]
      if (this.tagsList.length === 0) {
        this.$router.push('/stitch-dashboard')
        return
      }
      const item = this.tagsList[index] || this.tagsList[index - 1]
      if (item && delItem.path === this.$route.fullPath) {
        this.$router.push(item.path)
      }
    },
    closeAll() {
      this.tagsList = []
      this.showMenu = false
      this.$router.push('/stitch-dashboard')
    },
    closeOther() {
      const curItem = this.tagsList.filter(item => item.path === this.$route.fullPath)
      this.tagsList = curItem
      this.showMenu = false
    },
  },
}
</script>

<style scoped>
.stitch-tags {
  -webkit-user-select: none;
  user-select: none;
}

.scrollbar-none {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.scrollbar-none::-webkit-scrollbar {
  display: none;
}

.tag-item {
  min-width: 0;
}
.tag-item.tag-active {
  /* Active state uses bg-primary/5 and text-primary from template */
}

.fade-down-enter-active,
.fade-down-leave-active {
  transition: all 0.15s ease;
}
.fade-down-enter,
.fade-down-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
