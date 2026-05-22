<template>
    <div class="detail-container">
        <el-table
            ref="detailTable"
            :data="tableData"
            border
            class="table statistic-table"
            header-cell-class-name="table-header"
            @row-click="handleRowClick"
        >
            <template slot="empty">
                <div class="table-empty-wrap">暂无数据</div>
            </template>
            <el-table-column prop="orgName" label="单位名称" align="center" width="50%">
                <template slot-scope="scope">
                    <span
                        v-if="scope.row.isSelf == 1"
                        class="org-link table-cell-text"
                        @click.stop="handleDrillDown(scope.row)"
                    >{{ scope.row.orgName }}</span>
                    <span v-else class="table-cell-text">{{ scope.row.orgName }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="articleNumber" label="文章数" align="center" width="50%"></el-table-column>
        </el-table>
    </div>
</template>

<script>
export default {
    name: 'StatisticDetailPage',
    props: {
        orgNo: { type: String, default: '' },
        startTime: { type: String, default: '' },
        endTime: { type: String, default: '' }
    },
    data() {
        return {
            tableData: []
        };
    },
    watch: {
        orgNo: { handler: 'loadList', immediate: true },
        startTime: { handler: 'loadList' },
        endTime: { handler: 'loadList' }
    },
    methods: {
        loadList() {
            const orgNo = this.orgNo || '';
            const startTime = this.startTime || '';
            const endTime = this.endTime || '';
            const params = { orgNo, startTime, endTime };
            this.postRequest('/admin/articleStatisticByOrg', params).then(resp => {
                if (resp && resp.obj != null) {
                    this.tableData = Array.isArray(resp.obj) ? resp.obj : [];
                } else {
                    this.tableData = [];
                }
            }).catch(() => {
                this.tableData = [];
            });
        },
        handleRowClick(row) {
            if (row && row.orgNo) {
                this.$emit('drillDown', row);
            }
        },
        handleDrillDown(row) {
            if (row && row.orgNo) {
                this.$emit('drillDown', row);
            }
        }
    }
};
</script>

<style scoped>
.detail-container {
    padding: 0;
}
.table {
    display: block !important;
    width: 100% !important;
    font-size: 14px;
}
.table >>> .el-table__body-wrapper {
    scrollbar-gutter: stable;
}
.table >>> .el-table__header-wrapper,
.table >>> .el-table__body-wrapper {
    display: block;
    width: 100% !important;
    min-width: 100% !important;
}
.table >>> .el-table__header-wrapper {
    overflow: visible !important;
}
.table >>> .el-table__header,
.table >>> .el-table__body {
    table-layout: fixed !important;
    width: 100% !important;
    min-width: 100% !important;
}
.table >>> .el-table td,
.table >>> .el-table th {
    padding: 14px 16px;
    text-align: center;
    box-sizing: border-box;
    overflow: hidden;
}
.table-cell-text {
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 100%;
}
.table >>> .el-table__body,
.table >>> .el-table__header {
    width: 100% !important;
}
.table >>> .el-table__empty-block {
    width: 100%;
    min-height: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
}
.table-empty-wrap {
    width: 100%;
    min-height: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #909399;
    font-size: 14px;
}
.table >>> .el-table__empty-text {
    text-align: center;
}
.org-link {
    color: #409EFF;
    cursor: pointer;
    text-decoration: none;
}
.org-link:hover {
    text-decoration: underline;
}
</style>
