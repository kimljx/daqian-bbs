<template>
	<article class="article-card" @click="articleDetails(articleId)">
		<div class="article-main">
			<div class="article-copy">
				<h2>{{ title }}</h2>
				<p>{{ summary || '暂无摘要' }}</p>
			</div>

			<div class="article-cover" v-if="imageUrl">
				<img :src="imageUrl" alt="文章封面">
			</div>
		</div>

		<div class="article-meta">
			<div class="article-author">
				<img :src="portrait" alt="作者头像">
				<span class="author-name">作者：{{ author || '匿名用户' }}</span>
				<span class="dot">•</span>
				<span>{{ publishTimeText || '刚刚' }}</span>
			</div>

			<div class="article-stats">
				<span><i class="el-icon-view"></i>{{ viewNum || 0 }}</span>
				<span><i class="el-icon-chat-dot-square"></i>{{ commentNum || 0 }}</span>
				<span><i class="el-icon-magic-stick"></i>{{ goodNum || 0 }}</span>
			</div>
		</div>
	</article>
</template>

<script>
export default {
	name: "BBSArticle",
	props: {
		articleId: {
			type: Number,
			required: true
		},
		title: {
			type: String,
			required: true
		},
		imageUrl: {
			type: String,
			default: ''
		},
		summary: {
			type: String,
			default: ''
		},
		author: {
			default: ''
		},
		goodNum: {
			type: Number,
			default: 0
		},
		viewNum: {
			type: Number,
			default: 0
		},
		commentNum: {
			type: Number,
			default: 0
		},
		createTime: {
			type: [String, Number, Date],
			default: ''
		}
	},
	computed: {
		portrait() {
			const user = JSON.parse(window.sessionStorage.getItem('user') || 'null')
			if (user && user.portrait) {
				return `${process.env.VUE_APP_BBS_API}${user.portrait}`
			}
			return require('../assets/portrait.png')
		},
		publishTimeText() {
			if (!this.createTime) return ''
			const create = new Date(this.createTime).getTime()
			if (isNaN(create)) return ''
			const diffMs = Date.now() - create
			const diffMin = Math.floor(diffMs / 60000)
			const diffHour = Math.floor(diffMs / 3600000)
			const diffDay = Math.floor(diffMs / 86400000)
			if (diffMin < 1) return '刚刚'
			if (diffHour < 1) return `${diffMin}分钟前`
			if (diffDay < 1) return `${diffHour}小时前`
			if (diffDay < 30) return `${diffDay}天前`
			return new Date(this.createTime).toLocaleDateString()
		}
	},
	methods: {
		articleDetails(articleId) {
			this.$router.push({ path: `/articleDetails/articleId/${articleId}` })
		}
	}
}
</script>

<style scoped>
.article-card {
	background: #ffffff;
	border: 1px solid transparent;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
	cursor: pointer;
	margin-bottom: 16px;
	padding: 24px;
	transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.article-card:hover {
	border-color: #c1c6d7;
	box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
	transform: translateY(-1px);
}

.article-main {
	display: flex;
	gap: 24px;
}

.article-copy {
	flex: 1;
	min-width: 0;
}

.article-copy h2 {
	color: #1b1c1c;
	font-size: 20px;
	font-weight: 600;
	line-height: 1.4;
	margin: 0 0 8px;
	transition: color 0.2s ease;
}

.article-card:hover h2 {
	color: #0057c2;
}

.article-copy p {
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	color: #414755;
	display: -webkit-box;
	font-size: 14px;
	line-height: 1.6;
	margin: 0;
	overflow: hidden;
	word-break: break-all;
}

.article-cover {
	border-radius: 8px;
	flex: 0 0 192px;
	height: 128px;
	overflow: hidden;
}

.article-cover img {
	height: 100%;
	object-fit: cover;
	transition: transform 0.4s ease;
	width: 100%;
}

.article-card:hover .article-cover img {
	transform: scale(1.05);
}

.article-meta {
	align-items: center;
	display: flex;
	gap: 16px;
	justify-content: space-between;
	margin-top: 16px;
}

.article-author,
.article-stats {
	align-items: center;
	color: #727786;
	display: flex;
	font-size: 12px;
	gap: 8px;
	min-width: 0;
}

.article-author img {
	border-radius: 50%;
	height: 24px;
	object-fit: cover;
	width: 24px;
}

.author-name {
	max-width: 360px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.dot {
	color: #c1c6d7;
}

.article-stats {
	flex-shrink: 0;
	gap: 16px;
}

.article-stats span {
	align-items: center;
	display: flex;
	gap: 4px;
}

@media (max-width: 768px) {
	.article-card {
		padding: 18px;
	}

	.article-main,
	.article-meta {
		flex-direction: column;
		align-items: stretch;
	}

	.article-cover {
		flex-basis: auto;
		height: auto;
		aspect-ratio: 16 / 9;
		width: 100%;
	}

	.article-author {
		flex-wrap: wrap;
	}
}
</style>
