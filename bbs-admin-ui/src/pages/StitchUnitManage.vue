<template>
  <div class="bg-surface min-h-screen">
    <div class="max-w-7xl mx-auto px-page-margin-desktop py-6">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="font-headline-lg text-headline-lg text-on-surface flex items-center gap-2">
            <span class="material-symbols-outlined text-primary">account_tree</span>
            单位管理
          </h1>
          <p class="text-body-md text-secondary mt-1">管理组织单位结构</p>
        </div>
      </div>

      <!-- Org Tree Card -->
      <div class="bg-container border border-border rounded-xl p-card-padding" v-loading="loading">
        <div v-if="orgTree.length === 0" class="py-12 text-center flex flex-col items-center gap-2 text-on-surface-variant">
          <span class="material-symbols-outlined text-[48px] opacity-20">account_tree</span>
          <p class="text-body-md">暂无组织数据</p>
        </div>
        <div v-else class="max-w-xl">
          <OrgTreeNodes :nodes="orgTree" @append="openAdd" @remove="remove" />
        </div>
      </div>

      <!-- Add Dialog -->
      <div v-if="dialogVisible" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="dialogVisible = false">
        <div class="fixed inset-0 bg-black/30"></div>
        <div class="relative bg-container w-full max-w-md rounded-xl shadow-2xl overflow-hidden">
          <div class="flex items-center justify-between p-5 border-b border-outline-variant">
            <h3 class="font-headline-sm text-headline-sm text-on-surface flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">add_circle</span>
              新增单位
            </h3>
            <button class="text-outline hover:text-error transition-colors" @click="dialogVisible = false">
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>
          <div class="p-5">
            <div>
              <label class="font-label-md text-label-md text-secondary ml-0.5 mb-1.5 block">单位名称</label>
              <input v-model="addOrgName" class="w-full px-4 py-2.5 bg-surface border border-outline-variant rounded focus:border-primary focus:ring-1 focus:ring-primary outline-none font-body-md text-body-md" placeholder="请输入单位名称" @keyup.enter="onSubmit">
            </div>
          </div>
          <div class="flex justify-end gap-3 p-5 border-t border-outline-variant bg-surface-container-lowest">
            <button class="px-5 py-2 border border-outline rounded text-on-surface hover:bg-surface-variant transition-all font-label-md text-label-md" @click="dialogVisible = false">取消</button>
            <button class="px-7 py-2 bg-primary text-on-primary rounded hover:opacity-90 transition-all font-label-md text-label-md shadow-sm" @click="onSubmit">确认</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const OrgTreeNodes = {
  name: 'OrgTreeNodes',
  props: { nodes: { type: Array, default: () => [] } },
  render(h) {
    if (!this.nodes || !this.nodes.length) return null
    return h('div', { class: 'space-y-1' }, this.nodes.map(node =>
      h('div', [
        h('div', {
          class: 'flex items-center justify-between px-4 py-2.5 bg-surface-container-low rounded-lg hover:bg-surface-container border border-outline-variant/50 group transition-colors mb-1',
          style: { marginLeft: (node._depth || 0) * 24 + 'px' }
        }, [
          h('span', { class: 'font-body-md text-on-surface font-medium flex items-center gap-2' }, [
            h('span', { class: 'material-symbols-outlined text-[18px] text-outline' }, node.children && node.children.length ? 'folder' : 'description'),
            node.label
          ]),
          h('span', { class: 'flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity' }, [
            h('button', {
              class: 'px-2 py-1 text-[11px] font-medium text-primary bg-primary/5 rounded hover:bg-primary/10 transition-colors',
              on: { click: () => this.$emit('append', node) }
            }, '新增'),
            node.id && node.id.length !== 5 ? h('button', {
              class: 'px-2 py-1 text-[11px] font-medium text-error bg-error/5 rounded hover:bg-error/10 transition-colors ml-1',
              on: { click: () => this.$emit('remove', node) }
            }, '删除') : null
          ].filter(Boolean))
        ]),
        node.children && node.children.length
          ? h(OrgTreeNodes, {
              props: { nodes: node.children.map(c => ({ ...c, _depth: (node._depth || 0) + 1 })) },
              on: { append: n => this.$emit('append', n), remove: n => this.$emit('remove', n) }
            })
          : null
      ])
    ))
  }
}

export default {
  name: 'StitchUnitManage',
  components: { OrgTreeNodes },
  data() {
    return {
      loading: false,
      orgTree: [],
      dialogVisible: false,
      addPOrgNo: '',
      addOrgName: ''
    }
  },
  mounted() { this.getAllOrgTree() },
  methods: {
    async getAllOrgTree() {
      this.loading = true
      try {
        const res = await this.getRequestUrl('/common/saOrgTree')
        this.orgTree = res.code == 200 ? (res.obj || []).map(n => ({ ...n, _depth: 0 })) : []
      } catch (e) { this.orgTree = [] }
      this.loading = false
    },
    openAdd(data) {
      this.addPOrgNo = data.id
      this.addOrgName = ''
      this.dialogVisible = true
    },
    async onSubmit() {
      if (!this.addOrgName.trim()) { this.$message.error('请输入单位名称'); return }
      try {
        const res = await this.getRequestUrl(`/saOrg/addSaOrg?pOrgNo=${this.addPOrgNo}&orgName=${this.addOrgName}`)
        if (res.code == 200) {
          this.$message.success('新增成功！')
          this.dialogVisible = false
          this.addOrgName = ''
          this.getAllOrgTree()
        } else { this.$message.error(res.message) }
      } catch (e) { this.$message.error('新增失败') }
    },
    remove(data) {
      this.$confirm('确定删除吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          const res = await this.getRequestUrl(`/saOrg/deleteSaOrgByOrgNo?orgNo=${data.id}`)
          if (res.code == 200) { this.$message.success('删除成功！'); this.getAllOrgTree() }
          else { this.$message.error(res.message) }
        } catch (e) { this.$message.error('删除失败') }
      }).catch(() => {})
    }
  }
}
</script>
