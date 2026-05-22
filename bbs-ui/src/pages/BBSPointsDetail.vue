<template>
	<div class="points-detail-wrapper">
		<div class="points-detail-box">
			<div class="header-row">
				<el-button type="text" icon="el-icon-arrow-left" @click="goBack">返回</el-button>
				<h2 class="title">排名 · 下转</h2>
			</div>
			<el-tabs v-model="activeTab" @tab-click="onTabClick">
				<el-tab-pane label="当月排名" name="01"></el-tab-pane>
				<el-tab-pane label="累计排名" name="02"></el-tab-pane>
			</el-tabs>
			<div class="table-wrap">
				<el-table :data="rankList" border style="width: 100%" v-loading="loading" class="points-table">
					<el-table-column label="单位" min-width="200">
						<template slot-scope="scope">
							<span
								:class="{ 'is-self': scope.row.isSelf === 1 }"
								@click="onRowClick(scope.row)"
							>{{ scope.row.orgName }}</span>
						</template>
					</el-table-column>
					<el-table-column v-if="activeTab === '01'" prop="posts" label="发帖数" min-width="100"></el-table-column>
					<el-table-column v-else prop="posts" label="累计发帖数" min-width="120"></el-table-column>
					<el-table-column v-if="activeTab === '01'" prop="replies" label="当月回帖数" min-width="120"></el-table-column>
					<el-table-column v-else prop="replies" label="累计回帖数" min-width="120"></el-table-column>
					<el-table-column v-if="activeTab === '01'" prop="points" label="当月活跃度积分" min-width="140"></el-table-column>
					<el-table-column v-else prop="points" label="累计活跃度积分" min-width="140"></el-table-column>
					<el-table-column prop="rankNum" label="排名" min-width="90"></el-table-column>
				</el-table>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	name: 'BBSPointsDetail',
	data() {
		return {
			activeTab: '01',
			orgNo: '',
			rankList: [],
			loading: false
		}
	},
	mounted() {
		this.orgNo = this.$route.query.orgNo || ''
		this.activeTab = this.$route.query.rankType || '01'
		this.fetchRank()
	},
	watch: {
		'$route.query'() {
			this.orgNo = this.$route.query.orgNo || ''
			this.activeTab = this.$route.query.rankType || '01'
			this.fetchRank()
		}
	},
	methods: {
		goBack() {
			if (this.$route.query.orgNo) {
				// 有上级则返回上一级（需要上层传 orgNo 的父级），这里简单返回排名首页
				this.$router.push('/points')
			} else {
				this.$router.push('/points')
			}
		},
		onTabClick() {
			this.fetchRank()
		},
		fetchRank() {
			this.loading = true
			this.postRequest('/common/pointsRank', {
				rankType: this.activeTab,
				orgNo: this.orgNo
			}).then(resp => {
				this.loading = false
				const list = (resp && resp.obj && Array.isArray(resp.obj)) ? resp.obj
					: (resp && resp.list && Array.isArray(resp.list)) ? resp.list
					: (resp && resp.data && Array.isArray(resp.data)) ? resp.data
					: Array.isArray(resp) ? resp : []
				this.rankList = list
			}).catch(() => {
				this.loading = false
				this.rankList = []
			})
		},
		onRowClick(row) {
			if (row.isSelf === 1 && row.orgNo) {
				this.$router.push({
					path: '/points/detail',
					query: { orgNo: row.orgNo, rankType: this.activeTab }
				})
			}
		}
	}
}
</script>

<style scoped>
.points-detail-wrapper {
	display: flex;
	justify-content: center;
}
.points-detail-box {
	width: 1200px;
	margin-top: 20px;
}
.header-row {
	display: flex;
	align-items: center;
	margin-bottom: 8px;
}
.header-row .el-button {
	padding: 0;
	margin-right: 12px;
}
.title {
	color: #8d8b8b;
	line-height: 40px;
	margin: 0;
}
.table-wrap {
	margin-top: 16px;
}
/* 表格列按比例均衡分配宽度 */
.points-table >>> .el-table__body,
.points-table >>> .el-table__header {
	table-layout: fixed;
}
.points-table >>> .el-table__body colgroup col:nth-child(1) { width: 28%; }
.points-table >>> .el-table__body colgroup col:nth-child(2) { width: 18%; }
.points-table >>> .el-table__body colgroup col:nth-child(3) { width: 18%; }
.points-table >>> .el-table__body colgroup col:nth-child(4) { width: 24%; }
.points-table >>> .el-table__body colgroup col:nth-child(5) { width: 12%; }
.points-table >>> .el-table__header colgroup col:nth-child(1) { width: 28%; }
.points-table >>> .el-table__header colgroup col:nth-child(2) { width: 18%; }
.points-table >>> .el-table__header colgroup col:nth-child(3) { width: 18%; }
.points-table >>> .el-table__header colgroup col:nth-child(4) { width: 24%; }
.points-table >>> .el-table__header colgroup col:nth-child(5) { width: 12%; }
.points-table >>> .is-self {
	color: #409EFF;
	cursor: pointer;
}
.points-table >>> .is-self:hover {
	text-decoration: underline;
}
</style>
