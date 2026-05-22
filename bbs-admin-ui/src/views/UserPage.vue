<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-friend"></i> 用户管理
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container table-page">
            <div class="handle-box">
                <el-button
                    type="primary"
                    icon="el-icon-delete"
                    class="handle-del mr10"
                    @click="delAllSelection"
                >批量删除</el-button>
                <el-input v-model="searchInfo" placeholder="请输入用户名/姓名" class="handle-input mr10"></el-input>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            </div>
            <el-table
                :data="users"
                border
                class="table"
                ref="multipleTable"
                header-cell-class-name="table-header"
                @selection-change="handleSelectionChange"
                style="width: 100%"
            >
                <el-table-column type="selection" width="55" align="center" :selectable="canShowOperation"></el-table-column>
                <el-table-column prop="id" label="ID" min-width="60" align="center"></el-table-column>
                <el-table-column prop="username" min-width="100" label="用户名" align="center"></el-table-column>
                <el-table-column prop="nickname" min-width="90" label="姓名" align="center"></el-table-column>
                <el-table-column prop="phone" min-width="120" label="手机号" align="center"></el-table-column>
                <el-table-column label="角色" min-width="110" align="center">
                    <template slot-scope="scope">
                        <span v-if="scope.row.userType == 1">用户</span>
                        <span v-else-if="scope.row.userType == 2">管理员</span>
                        <span v-else-if="scope.row.userType == 3">超级管理员</span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column label="状态" min-width="80" align="center">
                    <template slot-scope="scope">
                        <el-tag v-if='scope.row.isAlive === 0' type='success'>活跃</el-tag>
                        <el-tag v-else type='danger'>禁用</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" min-width="160" label="注册时间" align="center"></el-table-column>
                <el-table-column label="操作" min-width="220" align="center">
                    <template slot-scope="scope">
                        <template v-if="canShowOperation(scope.row)">
                            <template v-if="currentUserType === 3 && (scope.row.userType == 1 || scope.row.userType == 2)">
                                <el-button type="text" icon="el-icon-s-custom" @click="handleUpdateUserRole(scope.row)">
                                    {{ scope.row.userType == 1 ? '转为管理员' : '转为普通用户' }}
                                </el-button>
                                <el-button type="text" icon="el-icon-warning-outline"  @click="handleUpdateAlive(scope.$index, scope.row.id)">修改状态</el-button>
                                <el-button v-if="scope.row.userType != 3" type="text" icon="el-icon-office-building" @click="handleOpenOrgDialog(scope.row)">修改单位</el-button>
                                <el-button type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.$index, scope.row.id)">删除</el-button>
                            </template>
                            <template v-else>
                                <el-button type="text" icon="el-icon-warning-outline"  @click="handleUpdateAlive(scope.$index, scope.row.id)">修改状态</el-button>
                                <el-button v-if="scope.row.userType != 3" type="text" icon="el-icon-office-building" @click="handleOpenOrgDialog(scope.row)">修改单位</el-button>
                                <el-button type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.$index, scope.row.id)">删除</el-button>
                            </template>
                        </template>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination
                    background
                    layout="total,sizes,prev, pager, next"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :page-sizes="[20,40,60,80,100]"
                    :current-page="pageParams.pageIndex"
                    :page-size="pageParams.pageSize"
                    :total="total"
                ></el-pagination>
            </div>
        </div>
        <el-dialog
            title="修改单位"
            :visible.sync="orgDialogVisible"
            width="520px"
            :close-on-click-modal="false"
        >
            <el-input
                v-model="orgFilterText"
                placeholder="输入关键字筛选单位"
                clearable
                class="org-filter-input"
            />
            <el-tree
                ref="orgTree"
                class="org-tree-panel"
                :data="orgTreeData"
                node-key="id"
                default-expand-all
                highlight-current
                :current-node-key="orgCheckValue.id"
                :props="{ children: 'children', label: 'label' }"
                :filter-node-method="filterOrgNode"
                @node-click="handleOrgNodeClick"
            />
            <span slot="footer" class="dialog-footer">
                <el-button @click="orgDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleConfirmUpdateOrg">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'UserPage',
    data() {
        return {
            searchInfo:'',
            users: [],
            multipleSelection: [],
            delList: [],
            editVisible: false,
            pageParams:{
                pageIndex:1,
                pageSize:15,
            },
            total: 0,
            form: {},
            idx: -1,
            id: -1,
            orgDialogVisible: false,
            orgTreeData: [],
            orgCheckValue: { label: '请选择单位', id: '' },
            currentEditUserId: null,
            orgFilterText: ''
        };
    },
    computed: {
        // 当前登录用户的 userType（1：用户 2：管理员 3：超级管理员）
        currentUserType() {
            try {
                const admin = window.sessionStorage.getItem('admin');
                if (!admin) return 0;
                const user = JSON.parse(admin);
                return user.userType != null ? Number(user.userType) : 0;
            } catch (e) {
                return 0;
            }
        }
    },
    watch: {
        orgFilterText(val) {
            this.$nextTick(() => {
                if (this.$refs.orgTree && typeof this.$refs.orgTree.filter === 'function') {
                    this.$refs.orgTree.filter(val);
                }
            });
        }
    },
    mounted() {
        this.getAllUserPage();
    },
    methods: {
        // 是否显示操作按钮/是否可勾选：行 userType=3 置灰不可选；行 userType=2 仅当前登录为 3 时可勾选；行 userType=1 正常可勾选
        canShowOperation(row) {
            if (row.userType == 3) return false;  // 超级管理员行：勾选框置灰不可选
            if (row.userType == 2) return this.currentUserType == 3;  // 管理员行：仅超级管理员可勾选
            return true;  // 用户行：正常可勾选
        },
        getAllUserPage() {
            const params = {
                pageIndex: this.pageParams.pageIndex,
                pageSize: this.pageParams.pageSize,
                searchInfo:this.searchInfo
            }
            //console.log(JSON.stringify(params))
            this.getRequest("/getAllUser",JSON.stringify(params)).then(resp => {
                console.log(resp)
                if (resp){
                    this.total = resp.obj.total
                    this.users = resp.obj.list;
                }
            })
        },
        loadOrgTree() {
            if (!this.orgTreeData.length) {
                return this.getRequestUrl('/common/saOrgTree').then(resp => {
                    if (resp && resp.obj != null) {
                        this.orgTreeData = this.normalizeOrgTree(resp.obj);
                    } else {
                        this.orgTreeData = [];
                    }
                }).catch(() => {
                    this.orgTreeData = [];
                });
            }
            return Promise.resolve();
        },
        normalizeOrgTree(nodes) {
            if (!nodes || !Array.isArray(nodes) || !nodes.length) return [];
            return nodes.map(node => ({
                id: node.orgNo != null ? node.orgNo : node.id,
                label: node.orgName != null ? node.orgName : node.label,
                children: node.children && node.children.length ? this.normalizeOrgTree(node.children) : undefined
            }));
        },
        filterOrgNode(value, data) {
            if (!value) return true;
            const label = (data.label || '').toString();
            return label.indexOf(value) !== -1;
        },
        handleOrgNodeClick(data) {
            this.orgCheckValue = {
                id: data.id || '',
                label: data.label || '请选择单位'
            };
        },
        handleOpenOrgDialog(row) {
            this.currentEditUserId = row.id;
            this.orgCheckValue = {
                id: row.orgNo || '',
                label: row.orgName || '请选择单位'
            };
            this.orgFilterText = '';
            this.loadOrgTree().then(() => {
                this.$nextTick(() => {
                    if (this.$refs.orgTree && this.orgCheckValue.id) {
                        this.$refs.orgTree.setCurrentKey(this.orgCheckValue.id);
                    }
                });
            });
            this.orgDialogVisible = true;
        },
        handleConfirmUpdateOrg() {
            if (!this.currentEditUserId) {
                this.$message.error('用户信息有误，请刷新后重试');
                return;
            }
            if (!this.orgCheckValue || !this.orgCheckValue.id) {
                this.$message.warning('请选择单位');
                return;
            }
            this.$confirm('确定要修改该用户单位吗？', '提示', { type: 'warning' }).then(() => {
                const params = {
                    id: this.currentEditUserId,
                    orgNo: this.orgCheckValue.id
                };
                this.postRequest('/user/modOrgNo', params).then(resp => {
                    if (resp) {
                        this.$message.success('修改单位成功');
                        this.orgDialogVisible = false;
                        this.currentEditUserId = null;
                        this.getAllUserPage();
                    }
                });
            }).catch(() => {});
        },
        // 触发搜索按钮
        handleSearch() {
            this.getAllUserPage()
        },
        // 删除操作
        handleDelete(index, userId) {
            // 二次确认删除
            this.$confirm('确定要删除吗？', '提示', { type: 'warning' }).then(() => {
                this.postRequest("/admin/deleteUserByUserId", { userId }).then(resp =>{
                    if (resp){
                        this.$message.success('删除成功');
                        this.getAllUserPage()
                    }
                })
            }).catch(() => {});
        },
        // 更新用户角色（转为管理员/转为普通用户），仅当前登录 userType 为 3 时显示并可用
        handleUpdateUserRole(row) {
            const roleType = row.userType == 1 ? '02' : '01';
            const tip = row.userType == 1 ? '确定将该用户转为管理员吗？' : '确定将该用户转为普通用户吗？';
            this.$confirm(tip, '提示', { type: 'warning' }).then(() => {
                this.postRequest('/updateUserRole', { userId: row.id, roleType }).then(resp => {
                    if (resp) {
                        this.$message.success('修改成功');
                        this.getAllUserPage();
                    }
                });
            }).catch(() => {});
        },
        // 更新用户状态
        handleUpdateAlive(index,userId){
            this.$confirm('确定要修改状态吗？', '提示', { type: 'warning' }).then(() => {
                this.postRequest("/admin/updateUserAliveByUserId", { userId }).then(resp =>{
                    if (resp){
                        this.$message.success('修改成功');
                        this.getAllUserPage()
                    }
                })
            }).catch(() => {});
        },
        
        // 多选操作
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        delAllSelection() {
            const  users = this.multipleSelection
            if (!users || users.length === 0) {
                this.$message.warning('请先选择需要删除的用户');
                return;
            }
            let userIds = []
            users.forEach(function(user){
                userIds.push(user.id)
            })
            userIds = userIds.join(',')
            // 二次确认删除
            this.$confirm('确定要删除选中的用户吗？', '提示', { type: 'warning' }).then(() => {
                this.postRequest("/admin/batchDeleteUsersByUserIds", { userIds }).then(resp =>{
                    if (resp){
                        this.$message.success('修改成功');
                        this.getAllUserPage()
                    }
                })
            }).catch(() => {});
        },
        
        handleSizeChange(val){
            console.log("每页页",val,"条")
            this.pageParams.pageSize = val
            this.getAllUserPage();
        },
        // 分页导航
        handleCurrentChange(val) {
            console.log("当前页",val)
            //this.$set(this.query, 'pageIndex', val);
            this.pageParams.pageIndex = val
            this.getAllUserPage();
        }
    }
};
</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
}

.handle-select {
    width: 120px;
}

.handle-input {
    width: 300px;
    display: inline-block;
}
.table-page {
    width: 100%;
    box-sizing: border-box;
}
.table {
    width: 100% !important;
    font-size: 14px;
}
.red {
    color: #ff0000;
}
.mr10 {
    margin-right: 10px;
}
.table-td-thumb {
    display: block;
    margin: auto;
    width: 40px;
    height: 40px;
}
/* 勾选框不可选时置灰 */
.table .el-checkbox.is-disabled .el-checkbox__inner {
    background-color: #f5f7fa;
    border-color: #dcdfe6;
    cursor: not-allowed;
}
.table .el-checkbox.is-disabled {
    cursor: not-allowed;
}
.org-filter-input {
    margin-bottom: 10px;
}
.org-tree-panel {
    max-height: 420px;
    overflow: auto;
}
</style>
