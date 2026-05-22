<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-timer"></i> 文章统计
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <span class="query-label">单位：</span>
                <org-tree
                    class="handle-org mr10"
                    :org-list="orgTreeData"
                    :check-value="orgCheckValue"
                    label-msg="单位"
                    :is-must="false"
                    @getCheckValue="getCheckValue"
                />
                <span class="query-label ml10">发布日期：</span>
                <el-date-picker
                    v-model="dateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd"
                    class="handle-date mr10"
                />
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
                <el-button icon="el-icon-refresh-left" @click="handleReset">重置</el-button>
            </div>
            <el-table
                ref="statTable"
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
                            @click.stop="openDetailDialog(scope.row)"
                        >{{ scope.row.orgName }}</span>
                        <span v-else class="table-cell-text">{{ scope.row.orgName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="articleNumber" label="文章数" align="center" width="50%"></el-table-column>
            </el-table>
        </div>
        <el-dialog
            :visible.sync="detailVisible"
            title="文章统计明细"
            width="900px"
            append-to-body
            class="statistic-detail-dialog"
            @close="onDetailClose"
        >
            <statistic-detail-page
                v-if="detailVisible"
                :org-no="detailOrgNo"
                :start-time="detailStartTime"
                :end-time="detailEndTime"
                @drillDown="onDetailDrillDown"
            />
        </el-dialog>
    </div>
</template>

<script>
import OrgTree from '@/components/orgSelect/tree.vue';
import StatisticDetailPage from './StatisticDetailPage.vue';

export default {
    name: 'StatisticPage',
    components: { OrgTree, StatisticDetailPage },
    data() {
        return {
            orgTreeData: [],
            orgCheckValue: { label: '请选择单位', id: '' },
            dateRange: null,
            tableData: [],
            detailVisible: false,
            detailOrgNo: '',
            detailStartTime: '',
            detailEndTime: ''
        };
    },
    mounted() {
        this.loadOrgTree();
        this.handleSearch();
        var vm = this;
        this.$nextTick(function() { vm.syncTableHeaderWidth(); });
        window.addEventListener('resize', this.syncTableHeaderWidth);
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.syncTableHeaderWidth);
    },
    methods: {
        loadOrgTree() {
            if (typeof this.getRequestUrl !== 'function') {
                this.orgTreeData = [];
                return;
            }
            this.getRequestUrl('/common/saOrgTree').then(resp => {
                if (resp && resp.obj != null) {
                    this.orgTreeData = this.normalizeOrgTree(resp.obj);
                } else {
                    this.orgTreeData = [];
                }
            }).catch(() => {
                this.orgTreeData = [];
            });
        },
        normalizeOrgTree(nodes) {
            if (!nodes || !Array.isArray(nodes) || !nodes.length) return [];
            return nodes.map(node => ({
                id: node.orgNo != null ? node.orgNo : node.id,
                label: node.orgName != null ? node.orgName : node.label,
                children: node.children && node.children.length ? this.normalizeOrgTree(node.children) : undefined
            }));
        },
        getCheckValue(val) {
            this.orgCheckValue = Array.isArray(val) ? (val[0] || { label: '请选择单位', id: '' }) : (val || { label: '请选择单位', id: '' });
        },
        getCurrentUserOrgNo() {
            try {
                const admin = window.sessionStorage.getItem('admin');
                if (!admin) return '';
                const obj = JSON.parse(admin);
                return (obj && obj.orgNo) ? obj.orgNo : '';
            } catch (e) {
                return '';
            }
        },
        syncTableHeaderWidth() {
            const vm = this;
            vm.$nextTick(function() {
                try {
                    const table = vm.$refs.statTable;
                    if (!table || !table.$el) return;
                    const bodyWrapper = table.$el.querySelector('.el-table__body-wrapper');
                    const headerWrapper = table.$el.querySelector('.el-table__header-wrapper');
                    const headerTable = table.$el.querySelector('.el-table__header');
                    if (!headerWrapper || !headerTable) return;
                    var w = bodyWrapper ? bodyWrapper.offsetWidth : 0;
                    if (!w && table.$el.offsetWidth) w = table.$el.offsetWidth;
                    if (!w && table.$el.parentElement) w = table.$el.parentElement.offsetWidth;
                    if (w > 0) {
                        headerWrapper.style.width = w + 'px';
                        headerWrapper.style.minWidth = w + 'px';
                        headerTable.style.width = w + 'px';
                        headerTable.style.minWidth = w + 'px';
                    }
                    if (table.doLayout) table.doLayout();
                } catch (e) {
                    console.warn('syncTableHeaderWidth', e);
                }
            });
        },
        handleSearch() {
            let startTime = '';
            let endTime = '';
            if (this.dateRange && this.dateRange.length === 2) {
                startTime = this.dateRange[0] || '';
                endTime = this.dateRange[1] || '';
                if (startTime && endTime && endTime < startTime) {
                    this.$message.warning('结束日期不能早于开始日期');
                    return;
                }
            }
            const orgNo = (this.orgCheckValue && this.orgCheckValue.id) ? this.orgCheckValue.id : this.getCurrentUserOrgNo();
            const params = { orgNo, startTime, endTime };
            this.postRequest('/admin/articleStatisticByOrg', params).then(resp => {
                if (resp && resp.obj != null) {
                    this.tableData = Array.isArray(resp.obj) ? resp.obj : [];
                } else {
                    this.tableData = [];
                }
                this.syncTableHeaderWidth();
            });
        },
        handleReset() {
            this.orgCheckValue = { label: '请选择单位', id: '' };
            this.dateRange = null;
            this.tableData = [];
        },
        handleRowClick(row) {
            if (row && row.orgNo) {
                this.openDetailDialog(row);
            }
        },
        openDetailDialog(row) {
            if (!row || !row.orgNo) return;
            let startTime = '';
            let endTime = '';
            if (this.dateRange && this.dateRange.length === 2) {
                startTime = this.dateRange[0] || '';
                endTime = this.dateRange[1] || '';
            }
            this.detailOrgNo = row.orgNo;
            this.detailStartTime = startTime;
            this.detailEndTime = endTime;
            this.detailVisible = true;
        },
        onDetailClose() {
            this.detailOrgNo = '';
            this.detailStartTime = '';
            this.detailEndTime = '';
        },
        onDetailDrillDown(row) {
            this.openDetailDialog(row);
        }
    }
};
</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}
.query-label {
    font-size: 14px;
    color: #606266;
}
.handle-org {
    min-width: 160px;
}
.handle-date {
    width: 240px;
}
.mr10 { margin-right: 10px; }
.ml10 { margin-left: 10px; }
.table {
    display: block !important;
    width: 100% !important;
    font-size: 14px;
}
/* 表头与内容区同宽：整表块级撑满，表头/表体容器与内层 table 同宽 */
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
/* 表头与内容区同列宽：固定表格布局，列宽由 el-table-column 的 width/min-width 决定 */
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
/* 内容区单元格文字不撑开列宽，超出显示省略号 */
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
/* 暂无数据时在列表内容区正中显示 */
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
