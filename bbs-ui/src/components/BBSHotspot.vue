<template>
	<section class="hotspot">
		<div class="hotspot-title">
			<h3>热榜</h3>
		</div>

		<ul>
			<li
				v-for="(h, index) in hotspotList"
				:key="h.articleId || index"
				@click="articleDetails(h.articleId)"
			>
				<span class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
				<span class="title">{{ h.articleTitle }}</span>
			</li>
		</ul>
	</section>
</template>

<script>
export default {
	name: "BBSHotspot",
	props: {
		hotspotList: {
			type: Array,
			required: true
		}
	},
	methods: {
		articleDetails(articleId) {
			if (!articleId) return
			this.$router.push({ path: `/articleDetails/articleId/${articleId}` })
		}
	}
}
</script>

<style scoped>
.hotspot {
	background: #ffffff;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
	padding: 24px;
}

.hotspot-title {
	border-bottom: 1px solid #e9e8e8;
	margin-bottom: 16px;
	padding-bottom: 8px;
}

.hotspot h3 {
	color: #1b1c1c;
	font-size: 20px;
	font-weight: 600;
	line-height: 1.4;
	margin: 0;
}

.hotspot ul {
	list-style: none;
	margin: 0;
	padding: 0;
}

.hotspot li {
	align-items: center;
	cursor: pointer;
	display: flex;
	gap: 16px;
	padding: 8px 0;
}

.hotspot li:hover .title {
	color: #0057c2;
}

.rank {
	align-items: center;
	background: #e3e2e2;
	border-radius: 50%;
	color: #727786;
	display: flex;
	flex: 0 0 24px;
	font-size: 12px;
	font-weight: 700;
	height: 24px;
	justify-content: center;
	width: 24px;
}

.rank-1 {
	background: #fee2e2;
	color: #dc2626;
}

.rank-2 {
	background: #ffedd5;
	color: #ea580c;
}

.rank-3 {
	background: #fef3c7;
	color: #a16207;
}

.title {
	color: #1b1c1c;
	font-size: 14px;
	font-weight: 500;
	line-height: 1.5;
	overflow: hidden;
	text-overflow: ellipsis;
	transition: color 0.2s ease;
	white-space: nowrap;
}
</style>
