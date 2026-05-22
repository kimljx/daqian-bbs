<template>
	<div class="stat-wrapper">
		<div class="stat">
			<h2 class="title">我的发布</h2>
			<div v-for="a in articleList" :key="a.id" class="content-wrapper">
				<BBSArticle
					:articleId="a.articleId"
					:title="a.articleTitle"
					:image-url="a.articleImage"
					:summary="a.articleSummary"
					:author="a.articleAuthor"
					:good-num="a.articleGoodNum"
					:view-num="a.articleViewNum"
					:create-time="a.createTime || a.articleCreateTime"
				/>
				<div class="edit-delete">
					<el-button type="warning" plain icon="el-icon-delete" @click="deleteArticle(a.articleId)">删除</el-button>
					<el-button type="info" plain icon="el-icon-edit-outline" @click="editArticle(a.articleId)">修改</el-button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import BBSArticle from "@/components/BBSArticle";

export default {
	name: "BBSStat",
	components:{BBSArticle},
	data(){
		return{
			articleList:[],
		}
	},
	mounted() {
		this.getMyPublish()
	},
	methods:{
		getMyPublish(){
			if (window.sessionStorage.getItem("user")){
				const userId = JSON.parse(window.sessionStorage.getItem('user')).id
				this.getRequest("/article/getArticleByUserId?userId="+ userId).then(resp=>{
					if (resp){
						this.articleList = resp
					}
				})
			}
		},
		deleteArticle(articleId){
			this.$confirm('确定删除该文章？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				// 发送请求
				this.postRequest('/article/deleteArticleByArticleId', { articleId }).then(resp =>{
					if (resp){
						this.getMyPublish()
					}
				})
			}).catch(() => {});
		},
		
		editArticle(articleId){
			this.$router.push({path: "/write",query:{articleId}})
		},
		
	}
}
</script>

<style scoped>
.stat-wrapper {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: center;
}

.stat {
	display: flex;
	flex-direction: column;
	width: 1200px;
	/*background-color: yellow;*/
	margin-top: 20px;
}
.title{
	color: #8d8b8b;
	line-height: 60px;
}
.content-wrapper{
	display: flex;
	flex-direction: row;
	/*align-items: center;*/
	justify-content: space-between;
}
.edit-delete{
	display: flex;
	flex-direction: row;
	margin-left: 20px;
	margin-top: 24px;
	height: 40px;
}
</style>
