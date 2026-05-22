<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-tickets"></i> 文章详情页
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class='container'>
            <div class="plugins-tips">
                <div class="content-title">标题:《{{articleTitle}}》</div>
            </div>
            <mavon-editor ref='md' v-model='editor.value' v-bind='editor'/>

            <!-- 附件列表 -->
            <div class="plugins-tips" v-if="fileList && fileList.length > 0">
                <div class="content-title">附件列表</div>
                <ul class="attachment-list">
                    <li v-for="(file, index) in fileList" :key="index" class="attachment-item">
                        <span class="attachment-name">{{ file.fileName }}</span>
                        <el-button type="primary" size="mini" icon="el-icon-download" @click="downloadFile(file.filePath)">下载</el-button>
                    </li>
                </ul>
            </div>

            <!-- 评论 -->
            <div class="plugins-tips">
                <div class="content-title">评论数: {{commentCount}}</div>
            </div>
            <div class="comment-list">
                <div class="comment-item" v-for="(item, index) in comments" :key="item.commentId || index">
                    <!-- 评论：评论人员头像、名称、时间 -->
                    <div class="comment-header">
                        <el-avatar class="comment-avatar" :size="40" :src="getAvatarUrl(item.portrait)"></el-avatar>
                        <div class="comment-userinfo">
                            <span class="comment-name">{{ item.nickname || '未知用户' }}</span>
                            <span class="comment-time">{{ item.commentTime }}</span>
                        </div>
                    </div>
                    <div class="comment-content">{{ item.commentContent }}</div>
                    <!-- 回复列表 -->
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
    </div>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

export default {
    name: 'ArticleDetailsPage',
    components:{mavonEditor},
    data(){
        return{
            articleTitle:'',
            editor:{
                value:'',
                toolbarsFlag: false,
                subfield: false,
                defaultOpen: 'preview'
            },
            comments:[],
            fileList: [],
            avatarBase: process.env.VUE_APP_BBS_API || '',
        }
    },
    computed: {
        commentCount() {
            if (!this.comments.length) return 0
            const total = this.comments.reduce((sum, item) => {
                return sum + 1 + (item.reply && item.reply.length ? item.reply.length : 0)
            }, 0)
            return total
        }
    },
    mounted() {
        this.$set(this.editor, 'ref', this.$refs.md)
        this.getArticleByArticleId(this.$route.query.articleId)
    },
    
    activated() {
        this.getArticleByArticleId(this.$route.query.articleId)
    },
    methods:{
        getAvatarUrl(portrait) {
            if (!portrait) return require('../assets/img/img.jpeg')
            const path = portrait.startsWith('/') ? portrait : `/${portrait}`
            return `${this.avatarBase}${path}`
        },
        getArticleByArticleId(articleId){
            console.log(articleId)
            this.getRequest("/admin/getArticleByArticleId",articleId).then(resp =>{
                if (resp){
                    console.log(resp)
                    this.editor.value = resp.obj.articleContent
                    this.articleTitle = resp.obj.articleTitle
                    
                    this.getArticleFileByArticleId(articleId)
                    this.getCommentByArticleId(articleId)
                }
            })
        },
        getArticleFileByArticleId(articleId) {
            this.postRequest(`/common/getArticleFileByArticleId/${articleId}`, {}).then(res => {
                // 接口直接返回 list 数组，每项包含 fileName、filePath
                let list = []
                if (Array.isArray(res)) {
                    list = res
                } else if (res && Array.isArray(res.obj)) {
                    list = res.obj
                } else if (res && Array.isArray(res.listBean)) {
                    list = res.listBean
                }
                this.fileList = list
            }).catch(() => {
                this.fileList = []
            })
        },
        downloadFile(filePath) {
            if (!filePath) return
            const base = process.env.VUE_APP_BBS_BASE_API || ''
            const url = base.endsWith('/') ? base + filePath.replace(/^\//, '') : (filePath.startsWith('/') ? base + filePath : base + '/' + filePath)
            const iframe = document.createElement('iframe')
            iframe.setAttribute('style', 'position:fixed;left:-9999px;top:0;width:0;height:0;border:0;visibility:hidden;')
            document.body.appendChild(iframe)
            iframe.src = url
            setTimeout(() => {
                if (iframe.parentNode) document.body.removeChild(iframe)
            }, 6000)
        },
        getCommentByArticleId(articleId){
            this.postRequest(`/common/comment/getCommentReply/${articleId}`).then(res => {
                if (res && Array.isArray(res)){
                    this.comments = res
                } else {
                    this.comments = []
                }
            }).catch(() => {
                this.comments = []
            })
        }
        
        /*handleNo(commentId){
            this.$confirm('确定该评论审核不通过吗？', '提示', { type: 'warning' }).then(() => {
                this.handleAlive(commentId,1)
                
            }).catch(() => {});
        },
        handleYes(commentId){
            this.$confirm('确定该评论通过审核吗？', '提示', { type: 'warning' }).then(() => {
                this.handleAlive(commentId,0)
        
            }).catch(() => {});
        },
        
        handleDelete(commentId){
            this.$confirm('确定删除该评论吗？', '提示', { type: 'warning' }).then(() => {
                this.deleteRequest("/admin/deleteCommentByCommentId",commentId).then(resp =>{
                    if (resp){
                        this.$message.success('删除成功！');
                        this.getCommentByArticleId(this.$route.query.articleId)
                    }
                })
            }).catch(() => {});
        },
        handleAlive(commentId,num){
            const params = {
                commentId:commentId,
                setAlive: num,
            }
            this.putRequest(`/admin/updateAliveByCommentId/${JSON.stringify(params)}`).then(resp =>{
                if (resp){
                    this.$message.success('操作成功！');
                    this.getCommentByArticleId(this.$route.query.articleId)
                }
            })
        }*/
        
        
    }
};
</script>

<style scoped>
.content-title{
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

/* 评论列表：评论人员头像、名称、时间 + 回复人员头像、名称、时间 */
.comment-list {
    margin-top: 12px;
}
.comment-item {
    padding: 16px 0;
    border-bottom: 1px solid #ebeef5;
}
.comment-item:last-child {
    border-bottom: none;
}
.comment-header {
    display: flex;
    align-items: center;
}
.comment-avatar {
    flex-shrink: 0;
    margin-right: 12px;
}
.comment-userinfo {
    display: flex;
    flex-direction: column;
    gap: 2px;
}
.comment-name {
    font-weight: 500;
    font-size: 14px;
    color: #1f2f3d;
}
.comment-time {
    font-size: 12px;
    color: #909399;
}
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
.reply-item:last-child {
    border-bottom: none;
}
.reply-header {
    display: flex;
    align-items: center;
}
.reply-avatar {
    flex-shrink: 0;
    margin-right: 10px;
}
.reply-userinfo {
    display: flex;
    flex-direction: column;
    gap: 2px;
}
.reply-name {
    font-weight: 500;
    font-size: 13px;
    color: #1f2f3d;
}
.reply-time {
    font-size: 12px;
    color: #909399;
}
.reply-content {
    margin-top: 8px;
    margin-left: 46px;
    font-size: 13px;
    color: #606266;
    line-height: 1.5;
}
</style>
