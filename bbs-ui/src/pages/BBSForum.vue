<template>
	<div class="forum-page">
		<section class="forum-hero">
			<div class="forum-hero-inner">
				<h1>欢迎来到大千智荟创新创意交流论坛</h1>
				<p>汇聚国网智慧，激发创新灵感，共同打造卓越的企业级知识共享平台。</p>
			</div>
		</section>

		<main class="forum-layout">
			<aside class="forum-left">
				<nav class="category-card">
					<button
						v-for="item in categories"
						:key="item.label"
						type="button"
						:class="['category-item', item.theme]"
					>
						<i :class="item.icon"></i>
						<span>{{ item.label }}</span>
					</button>
				</nav>
			</aside>

			<section class="forum-feed">
				<div class="empty-card" v-if="!articleList.length">
					<i class="el-icon-document"></i>
					<p>暂无帖子，换个关键词试试。</p>
				</div>

				<BBSArticle
					v-for="a in articleList"
					:key="a.id || a.articleId"
					:articleId="a.articleId"
					:title="a.articleTitle"
					:image-url="a.articleImage ? imageSuxx + a.articleImage : ''"
					:summary="a.articleSummary"
					:author="a.articleAuthor"
					:good-num="a.articleGoodNum"
					:view-num="a.articleViewNum"
					:comment-num="a.articleCommentNum || a.commentNum || 0"
					:create-time="a.createTime || a.articleCreateTime"
				/>
			</section>

			<aside class="forum-right">
				<BBSHotspot :hotspot-list="hotspotList" />
			</aside>
		</main>
	</div>
</template>

<script>
import BBSArticle from "@/components/BBSArticle";
import BBSHotspot from "@/components/BBSHotspot";

export default {
	name: "BBSForum",
	components: { BBSArticle, BBSHotspot },
	data() {
		return {
			imageSuxx: process.env.VUE_APP_BBS_BASE_FILE,
			articleList: [],
			hotspotList: [],
			categories: [
				{ label: '技术交流', icon: 'el-icon-thumb', theme: 'category-red' },
				{ label: '求助问答', icon: 'el-icon-question', theme: 'category-blue' },
				{ label: '资源共享', icon: 'el-icon-folder-opened', theme: 'category-indigo' }
			],
		}
	},
	mounted() {
		const lastKeywords = window.localStorage.getItem('bbs_search_keywords') || ''
		this.getArticleList(lastKeywords)
		this.getHotspot()
		this.$bus.$on('forumSearch', this.handleForumSearch)
	},
	beforeDestroy() {
		this.$bus && this.$bus.$off('forumSearch', this.handleForumSearch)
	},
	methods: {
		handleForumSearch(keywords) {
			this.getArticleList(keywords || '')
		},
		getArticleList(keywords = '') {
			const kw = (keywords || '').toString()
			this.getRequest(`/common/article/getArticle?keywords=${encodeURIComponent(kw)}`).then(resp => {
				if (resp) {
					this.articleList = resp
				}
			})
		},
		getHotspot() {
			this.getRequest("/common/article/getHot").then(resp => {
				if (resp) {
					this.hotspotList = resp
				}
			})
		}
	}
}
</script>

<style scoped>
.forum-page {
	background: #f5f7fa;
	min-height: calc(100vh - 64px);
	padding-bottom: 40px;
}

.forum-hero {
	background: linear-gradient(135deg, #0057c2 0%, #004398 100%);
	box-shadow: 0 2px 10px rgba(0, 67, 152, 0.16);
	color: #ffffff;
	margin-bottom: 24px;
	padding: 48px 24px;
	text-align: center;
}

.forum-hero-inner {
	margin: 0 auto;
	max-width: 1280px;
}

.forum-hero h1 {
	font-size: 32px;
	font-weight: 700;
	line-height: 1.2;
	margin: 0 0 8px;
}

.forum-hero p {
	color: rgba(255, 255, 255, 0.86);
	font-size: 16px;
	line-height: 1.6;
	margin: 0 auto;
	max-width: 720px;
}

.forum-layout {
	display: grid;
	gap: 24px;
	grid-template-columns: 180px minmax(0, 1fr) 300px;
	margin: 0 auto;
	max-width: 1280px;
	padding: 0 32px;
}

.forum-left,
.forum-right {
	align-self: start;
	position: sticky;
	top: 84px;
}

.category-card {
	background: #ffffff;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
	padding: 8px;
}

.category-item {
	align-items: center;
	background: transparent;
	border: 0;
	border-radius: 8px;
	color: #1b1c1c;
	cursor: pointer;
	display: flex;
	font-size: 14px;
	font-weight: 500;
	gap: 12px;
	height: 44px;
	padding: 0 12px;
	text-align: left;
	transition: background-color 0.2s ease, color 0.2s ease;
	width: 100%;
}

.category-item i {
	font-size: 18px;
}

.category-red i {
	color: #f87171;
}

.category-red:hover {
	background: #fef2f2;
	color: #ef4444;
}

.category-blue i {
	color: #3b82f6;
}

.category-blue:hover {
	background: #eff6ff;
	color: #2563eb;
}

.category-indigo i {
	color: #2563eb;
}

.category-indigo:hover {
	background: #eef2ff;
	color: #1d4ed8;
}

.empty-card {
	align-items: center;
	background: #ffffff;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
	color: #727786;
	display: flex;
	flex-direction: column;
	gap: 8px;
	padding: 48px 24px;
}

.empty-card i {
	color: #c1c6d7;
	font-size: 32px;
}

.empty-card p {
	margin: 0;
}

@media (max-width: 1100px) {
	.forum-layout {
		grid-template-columns: 160px minmax(0, 1fr);
	}

	.forum-right {
		display: none;
	}
}

@media (max-width: 768px) {
	.forum-hero {
		padding: 36px 18px;
	}

	.forum-hero h1 {
		font-size: 24px;
	}

	.forum-layout {
		grid-template-columns: 1fr;
		padding: 0 16px;
	}

	.forum-left {
		position: static;
	}

	.category-card {
		display: grid;
		grid-template-columns: repeat(3, minmax(0, 1fr));
	}

	.category-item {
		justify-content: center;
		padding: 0 8px;
	}
}
</style>
