<template>
	<div class="article-comment" v-if="comments">
		<h3>评论 {{ comments.length }}</h3>
		<div class="hide-reply-button">
			<el-avatar class="header-image" :size="40" :src="getPortrait(myInfo ? myInfo.portrait : '')"></el-avatar>
			<div class="input-button">
				<el-input id="replyInput" placeholder="请输入评论内容..." v-model="replyComment"></el-input>
				<el-button class="comment-button" @click="sendComment">发表评论</el-button>
			</div>
		</div>
		<div class="reply-father" v-for="(item, index) in comments" :key="index">
			<div class="header-info">
				<div class="header-info-left">
					<el-avatar class="header-image" :size="40" :src="getPortrait(item.portrait)"></el-avatar>
					<div class="userinfo">
						<span class="userinfo-name">{{ item.nickname }}</span>
						<span class="userinfo-time">{{ item.commentTime }}</span>
					</div>
				</div>
				<div>
					<el-button type="text" icon="el-icon-delete" v-if="myInfo && item.userId === myInfo.id"
						@click="deleteComment(item.commentId)">删除
					</el-button>
					<template v-if="myInfo">&nbsp;</template>
					<span class="icon-btn-comment" @click="showReplyInput(index, item.nickname, item.userId)"><i
							class="el-icon-chat-square"> 回复</i></span>
				</div>
			</div>
			<div class="talk-box">
				<p><span class="comment">{{ item.commentContent }}</span></p>
			</div>

			<div class="reply-box">
				<div class="reply-header-info" v-for="(reply, r) in (item.reply || [])" :key="r">

					<div class="header-info">
						<div class="header-info-left">
							<el-avatar class="header-image" :size="40" :src="getPortrait(reply.portrait)"></el-avatar>
							<div class="userinfo">
								<span class="userinfo-name">{{ reply.nickname }}</span>
								<span class="userinfo-time">{{ reply.replyTime }}</span>
							</div>
						</div>
						<div>
							<el-button type="text" icon="el-icon-delete" v-if="myInfo && reply.replyUserId === myInfo.id"
								@click="deleteReply(reply.replyId)">删除
							</el-button>
							<template v-if="myInfo">&nbsp;</template>
							<span class="icon-btn"
								@click="showReplyInput(index, reply.replyToNickname, reply.replyToUserId)"><i
									class="el-icon-chat-square"> 回复</i></span>
						</div>
					</div>
					<div class="talk-box-reply">
						<span>回复{{ reply.replyToNickname }}：</span>
						<p><span class="comment">{{ reply.replyContent }}</span></p>
					</div>
				</div>
			</div>
			<!--回复框：未登录也可见，点击发表时跳转登录-->
			<div class="reply-comment" v-if="_inputShow(index)">
				<el-avatar class="header-image" :size="40" :src="getPortrait(myInfo ? myInfo.portrait : '')"></el-avatar>
				<div class="input-button">
					<el-input class="reply-comment-input" placeholder="请输入评论内容..." v-model="replyComment"></el-input>
					<el-button class="" @click="sendCommentReply(index)">发表评论</el-button>
				</div>
			</div>

		</div>
	</div>
</template>

<script>
import { dateStr } from "@/utils/time";
import { Message, MessageBox } from "element-ui";
import { getCommentReply } from "@/api/comment";

export default {
	name: 'ArticleComment',

	data() {
		let user = null
		try {
			const u = window.sessionStorage.getItem('user')
			user = u ? JSON.parse(u) : null
		} catch (e) {
			user = null
		}
		return {
			headSux: process.env.VUE_APP_BBS_API,
			defaultPortrait: require('../assets/portrait.png'),
			myInfo: user,
			articleId: this.$route.params.articleId,
			toNickname: '',
			toUserId: -1,
			index: '0',
			replyComment: '',
			comments: [],
		}
	},
	mounted() {
		this.getCommentReply()
	},

	methods: {
		getPortrait(portrait) {
			return portrait ? `${this.headSux}${portrait}` : this.defaultPortrait
		},

		showReplyInput(index, nickname, userId) {
			if (this.comments[this.index]) this.comments[this.index].inputShow = false
			this.index = index
			if (this.comments[index]) this.comments[index].inputShow = true
			this.toNickname = nickname
			this.toUserId = userId
		},
		_inputShow(i) {
			return this.comments[i] && this.comments[i].inputShow
		},
		sendComment() {
			if (!this.myInfo) {
				this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
				return
			}
			if (!this.replyComment) {
				Message({
					showClose: true,
					type: 'warning',
					message: '评论不能为空',
					offset: 54
				})
			} else {
				let a = {}
				//let input =  document.getElementById('replyInput')
				let timeNow = new Date().getTime();
				let time = dateStr(timeNow);
				a.nickname = this.myInfo.nickname
				a.commentContent = this.replyComment
				a.portrait = this.myInfo.portrait
				a.commentTime = time
				console.log(1111, a)
				this.comments.push(a)
				//console.log(a)
				const tempData = {
					commentContent: this.replyComment,
					commentUserId: this.myInfo.id,
					commentArticleId: this.articleId,
				}
				this.putRequest("/comment/userComment", tempData).then(resp => {
					if (resp) {
						this.replyComment = ''
					}
				})

				//input.innerHTML = '';
			}


		},
		sendCommentReply(i) {
			if (!this.myInfo) {
				this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
				return
			}
			if (!this.replyComment) {
				Message({
					showClose: true,
					type: 'warning',
					message: '评论不能为空',
					offset: 54,
				})
			} else {
				let a = {}
				let timeNow = new Date().getTime();
				let time = dateStr(timeNow);
				a.portrait = this.myInfo.portrait
				a.nickname = this.myInfo.nickname
				a.replyTime = time
				a.replyToNickname = this.toNickname
				a.replyToUserId = this.toUserId
				a.replyContent = this.replyComment
				console.log(1111, a)
				this.comments[i].reply.push(a)

				const tempData = {
					replyUserId: this.myInfo.id,
					replyToUserId: this.toUserId,
					commentId: this.comments[i].commentId,
					replyContent: this.replyComment,
				}
				this.putRequest("/reply/userReply", tempData).then(resp => {
					if (resp) {
						this.replyComment = ''
					}
				})

				//document.getElementsByClassName("reply-comment-input")[i].innerHTML = ""
			}
		},

		getCommentReply() {
			getCommentReply(this.articleId).then(resp => {
				if (resp && Array.isArray(resp)) {
					this.comments = resp
				} else {
					this.comments = []
				}
			}).catch(() => {
				this.comments = []
			})
		},

		deleteComment(e) {
			MessageBox({
				message: '确定删除该评论, 是否继续?',
				title: '提示',
				showCancelButton: true,
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning',
			}).then(() => {
				console.log(e)
				const commentId = e;
				this.postRequest('/comment/deleteCommentById', { commentId }).then(resp => {
					if (resp) {
						// 重新获取评论
						getCommentReply(this.articleId).then(resp => {
							if (resp) {
								this.comments = resp
							}
						})
					}
				})
			})
		},
		deleteReply(e) {
			MessageBox({
				message: '确定删除该评论, 是否继续?',
				title: '提示',
				showCancelButton: true,
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning',
			}).then(() => {
				console.log(e)
				const replyId = e;
				this.postRequest('/reply/deleteReplyById', { replyId }).then(resp => {
					if (resp) {
						// 重新获取评论
						getCommentReply(this.articleId).then(resp => {
							if (resp) {
								this.comments = resp
							}
						})
					}
				})
			})
		},


	}

}
</script>


<style scoped>
.article-comment {
	padding: 10px 23px;
}

.article-comment h3 {
	margin: 10px 0px;
}

.hide-reply-button {
	display: flex;
	flex-direction: row;
}

.hide-reply-button .input-button {
	display: flex;
	flex-direction: row;
	width: 800px;
	margin-left: 20px;
}

/*.hide-reply-button>>>.el-input__inner{
	border: skyblue solid 1px;
}
.hide-reply-button>>>.el-input__inner:focus{
	border: skyblue solid 1px;
	border-top-right-radius: 0;
	border-bottom-right-radius: 0;
}
.comment-button{
	border-top-left-radius: 0;
	border-bottom-left-radius: 0;
	background-color: skyblue;
	margin-left: 0;
}*/
.userinfo {
	display: flex;
	flex-direction: column;
	margin-left: 10px;
}

.userinfo .userinfo-name {
	color: #000;
	font-size: 18px;
	font-weight: bold;
}

.userinfo .userinfo-time {
	font-size: 14px;
}

.header-info {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: space-between;
}

.icon-btn-comment {
	margin-right: 10px;
	cursor: pointer;
}

.icon-btn {
	cursor: pointer;
}

.header-info-left {
	display: flex;
	flex-direction: row;
	margin: 20px 0px 4px 0px;
	/*上、右、下、左*/
}

.talk-box {
	margin: 0 50px;
}

.talk-box-reply {
	display: flex;
	flex-direction: row;
	margin: 0px 50px 12px 50px;

}

.talk-box p {
	margin: 0;
}

.comment {
	font-size: 16px;
	color: #000;
}

.reply-box {
	margin: 10px 0 0 50px;
	background-color: #efefef;
	padding: 0px 10px;
	border-radius: 5px;
}

.reply-header-info {
	border-bottom: 1px solid rgba(178, 186, 194, .3)
}

.reply-comment {
	display: flex;
	flex-direction: row;
	margin-left: 50px;
	margin-top: 10px
}

.reply-comment .input-button {
	display: flex;
	flex-direction: row;
	margin-left: 20px;
	width: 700px;
}
</style>
