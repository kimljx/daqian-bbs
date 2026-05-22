<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-warning-outline"></i> 敏感词管理
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <div class="container">
            <div class="plugins-tips sensitive-card">
                <div class="content-title">敏感词管理</div>

                <div class="add-row">
                    <el-input
                        v-model="keyword"
                        class="add-input"
                        placeholder="输入敏感词，按回车添加"
                        clearable
                        @keyup.enter.native="handleAdd"
                    />
                    <el-button type="primary" class="add-btn" @click="handleAdd" :loading="adding">添加</el-button>
                </div>

                <div class="list-wrap" v-loading="loading">
                    <div v-if="!list || list.length === 0" class="empty-wrap">暂无敏感词</div>
                    <div v-else class="list">
                        <div class="list-item" v-for="item in list" :key="item.id">
                            <div class="keyword">{{ item.keyword }}</div>
                            <el-button type="danger" class="del-btn" @click="handleDelete(item)" :loading="deletingId === item.id">删除</el-button>
                        </div>
                    </div>
                </div>

            
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'BBSSensitiveWord',
    data() {
        return {
            loading: false,
            adding: false,
            deletingId: null,
            keyword: '',
            list: []
        };
    },
    mounted() {
        this.loadList();
    },
    methods: {
        async loadList() {
            if (typeof this.getRequestUrl !== 'function') {
                this.list = [];
                return;
            }
            this.loading = true;
            try {
                const res = await this.getRequestUrl('/sensitiveWord/getList');
                if (res && res.code == 200) {
                    const arr = (res.obj != null ? res.obj : []);
                    this.list = Array.isArray(arr) ? arr : [];
                } else {
                    this.list = [];
                    if (res && res.message) this.$message.error(res.message);
                }
            } catch (e) {
                this.list = [];
            } finally {
                this.loading = false;
            }
        },
        async handleAdd() {
            const kw = (this.keyword || '').trim();
            if (!kw) {
                this.$message.warning('请输入敏感词');
                return;
            }
            if (this.adding) return;
            this.adding = true;
            try {
                const url = `/sensitiveWord/addSensitiveWord?keyword=${encodeURIComponent(kw)}`;
                const res = await this.getRequestUrl(url);
                if (res && res.code == 200) {
                    this.$message.success('添加成功');
                    this.keyword = '';
                    await this.loadList();
                } else {
                    this.$message.error((res && res.message) ? res.message : '添加失败');
                }
            } catch (e) {
                this.$message.error('添加失败');
            } finally {
                this.adding = false;
            }
        },
        handleDelete(item) {
            if (!item || item.id == null) return;
            this.$confirm('确定删除该敏感词吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => this.doDelete(item)).catch(() => {});
        },
        async doDelete(item) {
            if (this.deletingId != null) return;
            this.deletingId = item.id;
            try {
                const res = await this.getRequestUrl(`/sensitiveWord/delSensitiveWord?id=${item.id}`);
                if (res && res.code == 200) {
                    this.$message.success('删除成功');
                    await this.loadList();
                } else {
                    this.$message.error((res && res.message) ? res.message : '删除失败');
                }
            } catch (e) {
                this.$message.error('删除失败');
            } finally {
                this.deletingId = null;
            }
        }
    }
};
</script>

<style scoped>
.sensitive-card {
    padding: 20px;
}
.add-row {
    display: flex;
    align-items: center;
    gap: 12px;
    margin: 12px 0 18px;
}
.add-input {
    flex: 1;
}
.add-btn {
    width: 90px;
}
.list-wrap {
    background: #fff;
    border-radius: 6px;
    border: 1px solid #ebeef5;
    overflow: hidden;
}
.empty-wrap {
    padding: 30px 0;
    text-align: center;
    color: #909399;
}
.list-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 18px;
    border-bottom: 1px solid #f0f2f5;
}
.list-item:last-child {
    border-bottom: none;
}
.keyword {
    font-size: 16px;
    color: #303133;
    font-weight: 600;
}
.del-btn {
    min-width: 90px;
}
.footer-tip {
    margin-top: 14px;
    font-size: 13px;
    color: #909399;
    display: flex;
    align-items: center;
    gap: 6px;
}
</style>
