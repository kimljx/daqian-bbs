<template>
  <aside
    class="stitch-sidebar bg-surface-container-lowest border-r border-outline-variant overflow-y-auto overflow-x-hidden transition-all duration-300 ease-in-out flex flex-col"
    :class="collapse ? 'w-16' : 'w-64'"
  >
    <nav class="flex-1 py-3 px-2 space-y-0.5">
      <template v-for="item in menuItems">
        <!-- Item with submenu -->
        <div v-if="item.children && item.children.length" :key="item.label">
          <button
            class="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 font-body-md text-body-md"
            :class="isParentActive(item) ? 'text-primary bg-primary-fixed/40 font-semibold' : 'text-on-surface-variant hover:bg-surface-container-low hover:text-on-surface'"
            @click="toggleSubmenu(item)"
          >
            <span class="material-symbols-outlined text-[22px] flex-shrink-0">{{ item.icon }}</span>
            <span v-show="!collapse" class="truncate flex-1 text-left">{{ item.label }}</span>
            <span v-show="!collapse" class="material-symbols-outlined text-[16px] transition-transform duration-200" :class="{ 'rotate-180': item._open }">expand_more</span>
          </button>
          <transition name="submenu">
            <div v-show="item._open && !collapse" class="ml-8 mt-0.5 space-y-0.5 border-l-2 border-outline-variant/30 pl-2">
              <router-link
                v-for="child in item.children"
                :key="child.label"
                :to="child.path"
                class="block px-3 py-2 rounded-lg transition-all duration-200 font-body-md text-body-md"
                :class="isActive(child.path) ? 'text-primary bg-primary-fixed/30 font-semibold' : 'text-on-surface-variant hover:bg-surface-container-low hover:text-on-surface'"
              >
                {{ child.label }}
              </router-link>
            </div>
          </transition>
        </div>

        <!-- Regular item -->
        <router-link
          v-else
          :key="item.label"
          :to="item.path"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 font-body-md text-body-md group"
          :class="isActive(item.path) ? 'text-primary bg-primary-fixed/40 font-semibold shadow-sm' : 'text-on-surface-variant hover:bg-surface-container-low hover:text-on-surface'"
        >
          <span class="material-symbols-outlined text-[22px] flex-shrink-0 transition-colors" :class="isActive(item.path) ? 'text-primary' : 'text-outline group-hover:text-on-surface-variant'">{{ item.icon }}</span>
          <span v-show="!collapse" class="truncate">{{ item.label }}</span>
          <!-- Active indicator dot -->
          <span v-if="isActive(item.path) && collapse" class="absolute right-1 w-1.5 h-1.5 rounded-full bg-primary"></span>
        </router-link>
      </template>
    </nav>

    <!-- Bottom collapse toggle -->
    <div class="p-2 border-t border-outline-variant/50">
      <button
        class="w-full flex items-center justify-center gap-2 px-3 py-2.5 rounded-lg text-on-surface-variant hover:bg-surface-container-low transition-colors font-label-md text-label-md"
        @click="toggleSidebar"
      >
        <span class="material-symbols-outlined text-[20px] transition-transform duration-300" :class="{ 'rotate-180': collapse }">chevron_left</span>
        <span v-show="!collapse">收起侧栏</span>
      </button>
    </div>
  </aside>
</template>

<script>
import bus from './bus'

export default {
  name: 'StitchSidebar',
  data() {
    return {
      collapse: false,
      menuItems: [
        { icon: 'dashboard', label: '系统首页', path: '/stitch-dashboard' },
        { icon: 'group', label: '用户管理', path: '/stitch-user' },
        { icon: 'article', label: '帖子管理', path: '/stitch-article' },
        {
          icon: 'content_paste',
          label: '内容管理',
          path: '',
          children: [
            { label: '标签管理', path: '/stitch-article-label' },
            { label: '配置管理', path: '/stitch-dict' },
            { label: '敏感词管理', path: '/stitch-sensitive-word' },
            { label: '轮播图管理', path: '/stitch-slideshow' },
          ],
        },
        { icon: 'emoji_events', label: '积分排名', path: '/stitch-points' },
        { icon: 'business', label: '单位管理', path: '/stitch-unit-manage' },
        { icon: 'bar_chart', label: '文章统计', path: '/stitch-statistic' },
        { icon: 'edit_note', label: '写文章', path: '/stitch-write' },
      ].map(item => ({ ...item, _open: false })),
    }
  },
  watch: {
    $route() {
      // Auto-expand parent of active child
      this.menuItems.forEach(item => {
        if (item.children) {
          item._open = item.children.some(child => this.isActive(child.path))
        }
      })
    },
  },
  created() {
    bus.$on('collapse', (msg) => {
      this.collapse = msg
    })
    // Auto-expand on mount
    this.$nextTick(() => {
      this.menuItems.forEach(item => {
        if (item.children) {
          item._open = item.children.some(child => this.isActive(child.path))
        }
      })
    })
  },
  methods: {
    isActive(path) {
      if (!path) return false
      return this.$route.path === path || this.$route.path.startsWith(path + '/')
    },
    isParentActive(item) {
      if (!item.children) return false
      return item.children.some(child => this.isActive(child.path))
    },
    toggleSubmenu(item) {
      item._open = !item._open
    },
    toggleSidebar() {
      this.collapse = !this.collapse
      bus.$emit('collapse', this.collapse)
    },
  },
}
</script>

<style scoped>
.stitch-sidebar {
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
}
.stitch-sidebar:hover {
  scrollbar-color: #c3c6d7 transparent;
}
.stitch-sidebar::-webkit-scrollbar {
  width: 4px;
}
.stitch-sidebar::-webkit-scrollbar-thumb {
  background: transparent;
  border-radius: 4px;
}
.stitch-sidebar:hover::-webkit-scrollbar-thumb {
  background: #c3c6d7;
}

.submenu-enter-active,
.submenu-leave-active {
  transition: all 0.2s ease;
}
.submenu-enter,
.submenu-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
