<template>
	<div class="points-wrapper">
		<div class="points-box">
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

		<!-- 详情弹窗 -->
		<el-dialog
			:title="'详情 - ' + (detailOrgName || '')"
			:visible.sync="detailVisible"
			width="73%"
			top="8vh"
			class="points-detail-dialog"
			@open="onDetailOpen"
			@close="onDetailClose"
		>
			<div class="detail-content">
				<div class="table-wrap">
					<el-table :data="detailList" border style="width: 100%" v-loading="detailLoading" class="points-table">
						<el-table-column label="单位" min-width="200">
							<template slot-scope="scope">
								<span
									:class="{ 'is-self': scope.row.isSelf === 1 }"
									@click="onDetailRowClick(scope.row)"
								>{{ scope.row.orgName }}</span>
							</template>
						</el-table-column>
						<el-table-column v-if="detailRankType === '01'" prop="posts" label="发帖数" min-width="100"></el-table-column>
						<el-table-column v-else prop="posts" label="累计发帖数" min-width="120"></el-table-column>
						<el-table-column v-if="detailRankType === '01'" prop="replies" label="当月回帖数" min-width="120"></el-table-column>
						<el-table-column v-else prop="replies" label="累计回帖数" min-width="120"></el-table-column>
						<el-table-column v-if="detailRankType === '01'" prop="points" label="当月活跃度积分" min-width="140"></el-table-column>
						<el-table-column v-else prop="points" label="累计活跃度积分" min-width="140"></el-table-column>
						<el-table-column prop="rankNum" label="排名" min-width="90"></el-table-column>
					</el-table>
				</div>
			</div>
			<span slot="footer" class="dialog-footer">
				<el-button type="primary" @click="detailVisible = false">关闭</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
export default {
	name: 'BBSPoints',
	data() {
		return {
			activeTab: '01',
			rankList: [],
			loading: false,
			detailVisible: false,
			detailOrgNo: '',
			detailOrgName: '',
			detailRankType: '01', // 打开弹窗时沿用父级页面的类型，不再在弹窗内切换
			detailList: [],
			detailLoading: false
		}
	},
	mounted() {
		this.fetchRank()
	},
	methods: {
		onTabClick() {
			this.fetchRank()
		},
		fetchRank() {
			this.loading = true
			this.postRequest('/common/pointsRank', {
				rankType: this.activeTab,
				orgNo: ''
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
				this.detailOrgNo = row.orgNo
				this.detailOrgName = row.orgName || ''
				this.detailRankType = this.activeTab
				this.detailVisible = true
			}
		},
		onDetailOpen() {
			this.fetchDetail()
		},
		onDetailClose() {
			this.detailOrgNo = ''
			this.detailOrgName = ''
			this.detailList = []
		},
		fetchDetail() {
			if (!this.detailOrgNo) return
			this.detailLoading = true
			this.postRequest('/common/pointsRank', {
				rankType: this.detailRankType,
				orgNo: this.detailOrgNo
			}).then(resp => {
				this.detailLoading = false
				const list = (resp && resp.obj && Array.isArray(resp.obj)) ? resp.obj
					: (resp && resp.list && Array.isArray(resp.list)) ? resp.list
					: (resp && resp.data && Array.isArray(resp.data)) ? resp.data
					: Array.isArray(resp) ? resp : []
				this.detailList = list
			}).catch(() => {
				this.detailLoading = false
				this.detailList = []
			})
		},
		onDetailRowClick(row) {
			if (row.isSelf === 1 && row.orgNo) {
				this.detailOrgNo = row.orgNo
				this.detailOrgName = row.orgName || ''
				this.fetchDetail()
			}
		}
	}
}
</script>

<style scoped>
.points-wrapper {
	width: 100%;
	min-width: 0;
}
.points-box {
	width: 100%;
	margin-top: 20px;
	box-sizing: border-box;
	font-size: 16px;
}
.points-box >>> .el-tabs__item {
	font-size: 16px;
}
.points-box >>> .el-tabs__nav {
	font-size: 16px;
}
.title {
	color: #8d8b8b;
	line-height: 60px;
}
.table-wrap {
	margin-top: 16px;
}
/* 表格列按比例均衡分配宽度 */
.points-table >>> .el-table__body,
.points-table >>> .el-table__header {
	table-layout: fixed;
	font-size: 15px;
}
.points-table >>> .el-table th,
.points-table >>> .el-table td {
	font-size: 15px;
	padding: 12px 0;
}
.points-table >>> .el-table th {
	font-size: 16px;
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

/* 详情弹窗 */
.points-detail-dialog >>> .el-dialog__body {
	max-height: 70vh;
	overflow-y: auto;
}
.detail-content {
	font-size: 15px;
}
.detail-content .table-wrap {
	margin-top: 16px;
}
</style>
