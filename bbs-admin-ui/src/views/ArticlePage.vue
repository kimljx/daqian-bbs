<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-read"></i> 帖子管理
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <el-tabs v-model="message">
                <el-tab-pane :label="`已审核(${done.length})`" name="first">
                    <el-table :data="done" :show-header="false" style="width: 100%">
                        <el-table-column>
                            <template slot-scope="scope">
                                <span class="message-title"
                                      @click='handleRead(scope.row.articleId)'>
                                    《 {{scope.row.articleTitle}} 》
                                </span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="createTime" width="150"></el-table-column>
                        <el-table-column width="120">
                            <template slot-scope="scope">
                                <el-button type="danger"
                                           @click="handleDel(scope.row.articleId)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="handle-row">
                        <el-button type="danger"
                                   @click='handleBatchDeleteArticlesByAlive'>
                            删除全部文章
                        </el-button>
                    </div>
                </el-tab-pane>
                
                <el-tab-pane :label="`未审核(${notDone.length})`" name="second">
                    <template v-if="message === 'second'">
                        <el-table :data="notDone" :show-header="false" style="width: 100%">
                            <el-table-column>
                                <template slot-scope="scope">
                                    <span class="message-title"
                                          @click='handleRead(scope.row.articleId)'>
                                        《 {{scope.row.articleTitle}} 》
                                    </span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="createTime" width="150"></el-table-column>
                            <el-table-column width="120">
                                <template slot-scope="scope">
                                    <el-button type="primary"
                                               @click="handleAudit(scope.row.articleId)">通过审核</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <div class="handle-row">
                            <el-button type="primary" @click='handleBatchAudit'>全部通过审核</el-button>
                        </div>
                    </template>
                </el-tab-pane>
                
            </el-tabs>
        </div>

        <!-- 帖子详情弹窗 -->
        <el-dialog
            title="帖子详情"
            :visible.sync="detailVisible"
            width="80%"
            top="5vh"
            class="article-detail-dialog"
            :close-on-click-modal="false"
            @open="onDetailDialogOpen"
            @closed="onDetailDialogClosed">
            <div v-loading="detailLoading" class="detail-content">
                <div class="plugins-tips" v-if="detailTitle">
                    <div class="content-title">标题：《{{ detailTitle }}》</div>
                </div>
                <mavon-editor
                    v-if="detailVisible"
                    ref="detailMd"
                    v-model="detailEditor.value"
                    v-bind="detailEditor"/>
                <div class="plugins-tips" v-if="detailFileList && detailFileList.length > 0">
                    <div class="content-title">附件列表</div>
                    <ul class="attachment-list">
                        <li v-for="(file, index) in detailFileList" :key="index" class="attachment-item">
                            <span class="attachment-name">{{ file.fileName }}</span>
                            <el-button type="primary" size="mini" icon="el-icon-download" @click="downloadFile(file.filePath, file.fileName)">下载</el-button>
                        </li>
                    </ul>
                </div>
                <div class="plugins-tips">
                    <div class="content-title" v-if="detailCommentCount > 0">评论</div>
                    <div class="content-title" v-else>暂无评论</div>
                </div>
                <div class="comment-list" v-if="detailCommentCount > 0">
                    <div class="comment-item" v-for="(item, index) in detailComments" :key="item.commentId || index">
                        <div class="comment-header">
                            <el-avatar class="comment-avatar" :size="40" :src="getAvatarUrl(item.portrait)"></el-avatar>
                            <div class="comment-userinfo">
                                <span class="comment-name">{{ item.nickname || '未知用户' }}</span>
                                <span class="comment-time">{{ item.commentTime }}</span>
                            </div>
                        </div>
                        <div class="comment-content">{{ item.commentContent }}</div>
                        <div class="reply-list" v-if="item.reply && item.reply.length">
                            <div class="reply-item" v-for="(reply, rIdx) in item.reply" :key="reply.replyId || rIdx">
                                <div class="reply-header">
                                    <el-avatar class="reply-avatar" :size="36" :src="getAvatarUrl(reply.portrait)"></el-avatar>
                                    <div class="reply-userinfo">
                                        <span class="reply-name">{{ reply.nickname || '未知用户' }}</span>
                                        <span class="reply-time">{{ reply.replyTime }}</span>
                                    </div>
                                </div>
                                <div class="reply-content">
                                    <span v-if="reply.replyToNickname">回复 {{ reply.replyToNickname }}：</span>
                                    {{ reply.replyContent }}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="detailVisible = false">关 闭</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

export default {
    name: 'ArticlePage',
    components: { mavonEditor },
    data() {
        return {
            message: 'first',
            showHeader: false,
            notDone: [],
            done: [],
            // 详情弹窗
            detailVisible: false,
            detailLoading: false,
            detailArticleId: null,
            detailTitle: '',
            detailEditor: {
                value: '',
                toolbarsFlag: false,
                subfield: false,
                defaultOpen: 'preview'
            },
            detailComments: [],
            detailFileList: [],
            avatarBase: process.env.VUE_APP_BBS_API || ''
        }
    },
    computed: {
        detailCommentCount() {
            if (!this.detailComments.length) return 0
            return this.detailComments.reduce((sum, item) => {
                return sum + 1 + (item.reply && item.reply.length ? item.reply.length : 0)
            }, 0)
        }
    },
    methods: {
        getAvatarUrl(portrait) {
            if (!portrait) return require('../assets/img/img.jpeg')
            const path = portrait.startsWith('/') ? portrait : `/${portrait}`
            return `${this.avatarBase}${path}`
        },
        handleRead(articleId) {
            this.detailArticleId = articleId
            this.detailVisible = true
        },
        onDetailDialogOpen() {
            if (this.detailArticleId) {
                this.loadArticleDetail(this.detailArticleId)
            }
        },
        onDetailDialogClosed() {
            this.detailArticleId = null
            this.detailTitle = ''
            this.detailEditor.value = ''
            this.detailComments = []
            this.detailFileList = []
        },
        loadArticleDetail(articleId) {
            this.detailLoading = true
            this.getRequest("/admin/getArticleByArticleId", articleId).then(resp => {
                this.detailLoading = false
                if (resp) {
                    this.detailEditor.value = resp.obj.articleContent
                    this.detailTitle = resp.obj.articleTitle
                    this.getArticleFileByArticleId(articleId)
                    this.getCommentByArticleId(articleId)
                }
            }).catch(() => {
                this.detailLoading = false
            })
        },
        getArticleFileByArticleId(articleId) {
            this.postRequest(`/common/getArticleFileByArticleId/${articleId}`, {}).then(res => {
                let list = []
                if (Array.isArray(res)) list = res
                else if (res && Array.isArray(res.obj)) list = res.obj
                else if (res && Array.isArray(res.listBean)) list = res.listBean
                this.detailFileList = list
            }).catch(() => {
                this.detailFileList = []
            })
        },
        getCommentByArticleId(articleId) {
            this.postRequest(`/common/comment/getCommentReply/${articleId}`).then(res => {
                if (res && Array.isArray(res)) {
                    this.detailComments = res
                } else {
                    this.detailComments = []
                }
            }).catch(() => {
                this.detailComments = []
            })
        },
        downloadFile(filePath, fileName) {
            if (!filePath) return
            const base = process.env.VUE_APP_BBS_BASE_API || ''
            const url = base.endsWith('/') ? base + filePath.replace(/^\//, '') : (filePath.startsWith('/') ? base + filePath : base + '/' + filePath)
            // 使用 <a> 标签触发下载，避免 iframe 方式被 X-Frame-Options: deny 拦截（如 .bmp 等附件）
            const a = document.createElement('a')
            a.href = url
            a.download = fileName || ''
            a.style.display = 'none'
            document.body.appendChild(a)
            a.click()
            document.body.removeChild(a)
        },
        handleDel(articleId) {
            this.$confirm('删除文章会连评论一并删除，确定要删除该文章吗？', '提示', { type: 'warning' }).then(() => {
                this.postRequest("/admin/deleteArticleByArticleId", { articleId }).then(resp =>{
                    if (resp){
                        this.$message.success('删除成功！');
                        this.getAliveArticles();
                        this.getNotAliveArticles();
                    }
                })
            }).catch(() => {});
        },
        handleAudit(articleId){
            console.log("审核的文章Id",articleId)
            this.$confirm('确定此片文章通过审核吗？', '提示', { type: 'warning' }).then(() => {
                this.postRequest("/admin/auditArticleByArticleId", { articleId }).then(resp =>{
                    if (resp){
                        this.$message.success('审核通过！');
                        this.getAliveArticles();
                        this.getNotAliveArticles();
                    }
                })
            }).catch(() => {});
        },
    
        handleBatchDeleteArticlesByAlive(){
            this.$confirm('确定要删除该文章吗？', '提示', { type: 'error' }).then(() => {
                this.postRequest("/admin/handleBatchDeleteArticlesByAlive/all", {}).then(resp =>{
                    if (resp){
                        this.getNotAliveArticles()
                        this.getAliveArticles()
                    }
                })
            }).catch(() => {});
        },
        handleBatchAudit(){
            this.$confirm('确定全部通过审核吗？', '提示', { type: 'warning' }).then(() => {
                this.postRequest("/admin/batchAudit/", {}).then(resp =>{
                    if (resp){
                        this.$message.success('全部审核通过！');
                        this.getAliveArticles();
                        this.getNotAliveArticles();
                    }
                })
            }).catch(() => {});
        },
     
        getAliveArticles(){
            this.getRequest("/admin/getAliveArticles","all").then(resp =>{
                if (resp){
                    this.done = resp.obj
                }
            })
        },
        getNotAliveArticles(){
            this.getRequest("/admin/getNotAliveArticles","all").then(resp =>{
                if (resp){
                    this.notDone = resp.obj
                }
            })
        }
    },
    mounted() {
        this.getAliveArticles();
        this.getNotAliveArticles()
    }
    
}

</script>

<style scoped>
.message-title {
    cursor: pointer;
}
.handle-row {
    margin-top: 30px;
}
.detail-content {
    min-height: 200px;
}
.content-title {
    font-weight: 400;
    font-size: 22px;
    color: #1f2f3d;
}
.attachment-list {
    list-style: none;
    padding: 0;
    margin: 10px 0 0 0;
}
.attachment-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #ebeef5;
}
.attachment-item:last-child {
    border-bottom: none;
}
.attachment-name {
    flex: 1;
    margin-right: 12px;
}

.comment-list { margin-top: 12px; }
.comment-item {
    padding: 16px 0;
    border-bottom: 1px solid #ebeef5;
}
.comment-item:last-child { border-bottom: none; }
.comment-header { display: flex; align-items: center; }
.comment-avatar { flex-shrink: 0; margin-right: 12px; }
.comment-userinfo { display: flex; flex-direction: column; gap: 2px; }
.comment-name { font-weight: 500; font-size: 14px; color: #1f2f3d; }
.comment-time { font-size: 12px; color: #909399; }
.comment-content {
    margin-top: 10px;
    margin-left: 52px;
    font-size: 14px;
    color: #606266;
    line-height: 1.5;
}
.reply-list {
    margin-top: 12px;
    margin-left: 52px;
    padding-left: 12px;
    border-left: 2px solid #e4e7ed;
}
.reply-item {
    padding: 12px 0;
    border-bottom: 1px solid #f2f6fc;
}
.reply-item:last-child { border-bottom: none; }
.reply-header { display: flex; align-items: center; }
.reply-avatar { flex-shrink: 0; margin-right: 10px; }
.reply-userinfo { display: flex; flex-direction: column; gap: 2px; }
.reply-name { font-weight: 500; font-size: 13px; color: #1f2f3d; }
.reply-time { font-size: 12px; color: #909399; }
.reply-content {
    margin-top: 8px;
    margin-left: 46px;
    font-size: 13px;
    color: #606266;
    line-height: 1.5;
}
</style>
<style>
/* 弹窗内 mavon-editor 需要全局样式 */
.article-detail-dialog .v-note-wrapper {
    min-height: 200px;
}
.article-detail-dialog .v-note-show .v-show-content {
    min-height: 180px;
}
</style>
