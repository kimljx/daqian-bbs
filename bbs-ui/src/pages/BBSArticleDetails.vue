<template>
  <div class="article-details">
    <div class="article-details-box">
      <div class="article-details-box-right">
        <div class="details-header-label">
          <h2>{{article.articleTitle}}</h2>
          <div class="details-header">
            <div class="details-header-left">
              <div class="time-author-label">
                <h6>发布时间：<span>{{article.createTime}}</span></h6>
                <h6>文章标签：<el-tag v-if="articleLabelName">{{ articleLabelName }}</el-tag><span v-else>-</span></h6>
              </div>
            </div>
            <div class="details-header-author">
              <img class="userinfo-avtar" :src="displayPortrait" alt="">
              <span class="details-header-author-name">{{userinfo.nickname}}</span>
            </div>
          </div>
        </div>

        <!-- 去掉社区名称区域 -->
        <div class="article-detail-content">
          <!--<p>{{article.articleContent}}</p>-->
          <BBSMarkdownEditor :editor="editor"></BBSMarkdownEditor>
        </div>

        <!-- 附件列表：文章内容下、评论上方 -->
        <div class="article-attachments" v-if="articleFileList && articleFileList.length">
          <h4 class="attachments-title">附件</h4>
          <ul class="attachments-list">
            <li v-for="(file, index) in articleFileList" :key="index" class="attachment-item">
              <span class="attachment-name">{{ file.fileName }}</span>
              <el-button v-if="isLoggedIn" type="primary" size="mini" icon="el-icon-download" @click.prevent="downloadFile(file.filePath, file.fileName)">下载</el-button>
            </li>
          </ul>
        </div>

        <!--评论内容-->

        <div class="article-details-comment-reply">
          <BBSComment :key="childKey" />
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { getArticleById, getUserinfoById, getArticleFileByArticleId } from "@/api/article";
import BBSMarkdownEditor from "@/components/BBSMarkdownEditor";
import BBSComment from "@/components/BBSComment";
import { getCommentReply } from "@/api/comment";
import { Message } from "element-ui";

export default {
  name: "BBSArticleDetails",
  components: { BBSComment, BBSMarkdownEditor },
  mounted() {
    const articleId = this.$route.params.articleId
    this.getArticleLabel()
    this.getArticle(articleId)
    this.getComment(articleId)
    this.loadArticleFiles(articleId) // 进入页面即请求附件列表，不依赖文章接口是否成功
  },
  data() {
    return {
      isFans: false,
      article: {},
      articleLabelList: [], // 标签列表，来自 /common/getArticleLabel
      userinfo: {},
      editor: {
        value: '',
        toolbarsFlag: false,
        subfield: false,
        defaultOpen: 'preview'
      },

      isShowInput: true,
      replyContent: '',
      reminder: '回复',
      isShowReminder: false,
      commentCount: 0,
      childKey: 0,
      articleFileList: [], // 文章附件列表
    }
  },

  computed: {
    // 是否已登录（用于控制附件下载按钮显示）
    isLoggedIn() {
      try {
        const user = window.sessionStorage.getItem('user')
        return !!user && !!JSON.parse(user)
      } catch {
        return false
      }
    },
    // 根据 article.articleLabelId 映射为中文标签名称
    articleLabelName() {
      if (!this.article || this.articleLabelList.length === 0) return ''
      const id = this.article.articleLabelId
      if (id == null || id === '') return ''
      const label = this.articleLabelList.find(
        (l) => String(l.labelId) === String(id) || l.labelId === id
      )
      return label ? label.labelName : ''
    },
    displayPortrait() {
      return this.userinfo && this.userinfo.portrait
        ? `${process.env.VUE_APP_BBS_API}${this.userinfo.portrait}`
        : require('../assets/portrait.png')
    }
  },

  methods: {
    // 获取文章标签列表，用于将 articleLabelId 映射为中文名称
    getArticleLabel() {
      this.getRequest('/common/getArticleLabel').then((resp) => {
        if (resp && Array.isArray(resp)) {
          this.articleLabelList = resp
        }
      })
    },
    // 私信
    privateSend() {
      // 判断私信的用户是否是当前用户
      const userId = JSON.parse(window.sessionStorage.getItem('user')).id
      if (userId == this.userinfo.id) {
        Message({
          message: '无法私信自己',
          type: 'warning',
          showClose: true,
          offset: 54
        })
      } else {
        this.$router.push({
          path: "/message/chat/" + this.userinfo.id,
          query: { nickname: this.userinfo.nickname },
        })
      }
    },
    // 关注
    attention() {
      const form = {
        userId: JSON.parse(window.sessionStorage.getItem('user')).id,
        attentionId: this.article.userId
      }
      this.postRequest("/fans/saveFans", form).then(resp => {
        if (resp) {
          // 关注成功
          this.getFansInfo()
          // 获取用户信息
          getUserinfoById(form.userId).then(resp => {
            if (resp) {
              window.sessionStorage.setItem('user', JSON.stringify(resp))
            }
          })
        }
      })
    },
    // 取消关注
    cancelAttention() {
      const form = {
        userId: JSON.parse(window.sessionStorage.getItem('user')).id,
        attentionId: this.article.userId
      }
      this.postRequest("/fans/cancelFans", form).then(resp => {
        if (resp) {
          // 取消成功
          this.getFansInfo()
          // 获取用户信息
          getUserinfoById(form.userId).then(resp => {
            if (resp) {
              window.sessionStorage.setItem('user', JSON.stringify(resp))
            }
          })
        }
      })
    },
    // 获取当前用户是否关注了帖子的作者（未登录不请求）
    getFansInfo() {
      let user = null
      try {
        const u = window.sessionStorage.getItem('user')
        user = u ? JSON.parse(u) : null
      } catch (e) {
        user = null
      }
      if (!user || !user.id) return
      const currentUserId = user.id
      const attentionId = this.article.userId
      this.getRequest("/fans/getFansInfo?userId=" + currentUserId + "&attentionId=" + attentionId).then(resp => {
        this.isFans = resp
      })
    },
    getArticle(articleId) {
      getArticleById(articleId).then(resp => {
        if (resp) {
          this.article = resp;
          this.editor.value = this.article.articleContentHtml
          this.getUserinfoById(this.article.userId)
          this.getFansInfo()
        }
      })
    },
    // 加载文章附件列表
    loadArticleFiles(articleId) {
      getArticleFileByArticleId(articleId).then(resp => {
        if (resp) {
          this.articleFileList = Array.isArray(resp) ? resp : (resp.data || [])
        }
      }).catch(() => {
        this.articleFileList = []
      })
    },
    // 下载附件：先 fetch 为 blob 再触发下载，避免图片等格式被浏览器直接打开而非下载
    downloadFile(filePath, fileName) {
      if (!filePath) return
      const baseUrl = process.env.VUE_APP_BBS_BASE_FILE || ''
      const url = baseUrl + (filePath.startsWith('/') ? filePath : '/' + filePath)

      fetch(url, { method: 'GET', credentials: 'include' })
        .then(resp => {
          if (!resp.ok) throw new Error('下载失败')
          return resp.blob()
        })
        .then(blob => {
          const blobUrl = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = blobUrl
          link.setAttribute('download', fileName || 'download')
          link.style.display = 'none'
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(blobUrl)
        })
        .catch(() => {
          Message({ message: '下载失败，请稍后重试', type: 'warning', showClose: true })
        })
    },
    getComment(articleId) {
      getCommentReply(articleId).then(resp => {
        if (resp) {
          this.commentCount = resp.length
        }
      })
    },
    getUserinfoById(id) {
      getUserinfoById(id).then(resp => {
        if (resp) {
          this.userinfo = resp
        }
      })
    },
  }
}
</script>

<style scoped>
.article-details {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  align-items: center;
  margin-bottom: 80px;
}

.article-details-box {
  display: flex;
  flex-direction: row;
  width: 1200px;
  /*background-color: #55a532;*/
  margin-top: 4px;
}
.article-details-box-right {
  width: 100%;
}

.userinfo {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 6px 0;
  padding: 4px 0px;
  padding-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.userinfo-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  border-bottom: #efefef solid 2px;
  width: 200px;
  padding-bottom: 10px;
  /*background-color: #55a532;*/
}
.userinfo-avtar {
  width: 40px;
  height: 40px;
  border-radius: 50px;
  margin-right: 10px;
  margin-left: 16px;
}

.userinfo-mid {
  display: flex;
  flex-direction: row;
  /*border-bottom: 1px solid #e8e8ed;*/
  width: 200px;
  margin-top: 6px;
  /*background-color: red;*/
}
.userinfo-mid ul {
  display: flex;
  flex-direction: row;
  text-align: center;
  width: 100%;
}
.userinfo-mid ul li {
  position: relative;
  display: inline-block;
  width: 33.333%;
  line-height: 26px;
}

.userinfo-mid ul li p {
  font-size: medium;
}

.userinfo-bottom {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  width: 180px;
  /*background-color: #55a532;*/
}
.userinfo-bottom .el-button {
  margin-top: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  height: 10px;
}
.recommend {
  display: flex;
  flex-direction: column;
  justify-items: center;
  align-items: center;
  margin-top: 26px;
}
.recommend-article {
  display: flex;
  flex-direction: column;
  width: 98%;
  min-height: 300px;
  padding: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.details-header-label {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 6px;
}
.details-header {
  background-color: #f6f6f6;
  height: 50px;
  width: 100%;
  margin: 10px 0px;
  /*padding: 0px 23px;*/
  border-radius: 4px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}
.details-header label {
  background-color: #cce8f3;
  padding: 3px 10px;
  border-bottom-right-radius: 15px;
  border-top-right-radius: 15px;
}
.time-author-label {
  display: inline-flex;
  flex-direction: row;
  align-items: center;
  gap: 24px;
  font-size: 14px;
  color: gray;
  margin-left: 20px;
}
.details-header-left {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.details-header-author {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-right: 10px;
}
.details-header-author .userinfo-avtar {
  width: 32px;
  height: 32px;
  margin-left: 0;
}
.details-header-author-name {
  margin-left: 8px;
  color: #333;
  font-size: 14px;
}
.time-author-label h6 {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 0;
  margin: 0;
  font-size: 14px;
  color: #666;
  font-weight: normal;
}
.time-author-label .el-tag {
  display: inline-flex;
  align-items: center;
  text-align: center;
  height: 24px;
  line-height: 24px;
  font-size: 13px;
}

.details-community {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  height: 40px;
  box-shadow: 1px 2px 10px 1px rgba(0, 0, 0, 0.1);
  padding: 3px 23px;
}

.details-community-left {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.details-community-left img {
  width: 34px;
  height: 34px;
  border-radius: 2px;
  margin-right: 10px;
}

.details-community-right {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.details-community-right span {
  color: #969595;
  margin-right: 20px;
  font-size: 14px;
}
.details-community-right .el-button {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  height: 30px;
  width: 80px;
}

/* 附件列表 */
.article-attachments {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 12px 16px;
  margin-top: 12px;
  margin-bottom: 12px;
  background: #fafafa;
  border-radius: 4px;
}
.attachments-title {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #333;
  font-weight: 600;
}
.attachments-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.attachment-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}
.attachment-item:last-child {
  border-bottom: none;
}
.attachment-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12px;
}
.attachment-item .el-button {
  flex-shrink: 0;
}

.article-details-comment-reply {
  box-shadow: 1px 2px 10px 1px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  padding: 10px 23px;
  min-height: 100px;
  /*width: 100%;*/
}
.article-details-comment-reply h3 {
  margin: 4px 0px;
}
.comment-box {
  display: flex;
  flex-direction: column;
}
.comment-userinfo-row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.comment-userinfo-row i {
  display: flex;
  flex-direction: row;
  align-items: center;
  color: gray;
  font-size: 14px;
  cursor: pointer;
}
.comment-userinfo {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 2px 0px;
}
.comment-userinfo img {
  width: 30px;
  height: 30px;
  border-radius: 20px;
  margin-right: 6px;
}
.comment-userinfo h6 {
  margin-right: 6px;
  color: gray;
}
.comment-box {
  border-bottom: whitesmoke solid 2px;
  margin-bottom: 10px;
}
.span-comment {
  padding-left: 30px;
}
.span-reply {
  padding-left: 60px;
}
.reply-userinfo {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin: 6px 0px;
  padding-left: 30px;
}
.reply-userinfo img {
  width: 26px;
  height: 26px;
  border-radius: 20px;
  margin-right: 6px;
}
.reply-userinfo h6 {
  margin-right: 6px;
  color: gray;
}
</style>
