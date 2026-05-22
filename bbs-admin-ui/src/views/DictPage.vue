<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-notebook-2"></i> 配置管理
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-button
                    v-if="isAdminId1"
                    type="primary"
                    icon="el-icon-document-add"
                    class="mr10"
                    @click="openAdd"
                >新增字典</el-button>
                <el-button type="primary" icon="el-icon-search" @click="loadDictList">刷新</el-button>
            </div>
            <el-table
                :data="dictList"
                border
                class="table"
                header-cell-class-name="table-header"
            >
                <el-table-column prop="dictLabel" label="中文描述" min-width="120" align="center"></el-table-column>
                <el-table-column prop="dictValue" label="值" min-width="100" align="center" :formatter="formatDictValue"></el-table-column>
                <el-table-column prop="remark" label="备注说明" min-width="120" align="center" show-overflow-tooltip></el-table-column>
                <el-table-column label="操作" width="150" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" @click="openEdit(scope.row)">编辑</el-button>
                        <el-button v-if="isAdminId1" type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 新增弹窗 -->
        <el-dialog
            title="新增字典"
            :visible.sync="addVisible"
            :close-on-click-modal="false"
            width="500px"
        >
            <el-form ref="addFormRef" :model="addForm" label-width="100px">
                <el-form-item label="中文描述" required>
                    <el-input v-model="addForm.dictLabel" placeholder="请输入描述" maxlength="100" show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="字典类型" required>
                    <el-input v-model="addForm.dictType" placeholder="请输入字典类型" maxlength="100" show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="值" required>
                    <el-input v-model="addForm.dictValue" placeholder="请输入值" maxlength="100" show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="排序序号">
                    <el-input-number v-model="addForm.dictSort" :min="0" controls-position="right" style="width: 100%;"></el-input-number>
                </el-form-item>
                <el-form-item label="备注说明">
                    <el-input v-model="addForm.remark" type="textarea" :rows="3" placeholder="请输入备注说明" maxlength="200" show-word-limit></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdd">确 定</el-button>
            </span>
        </el-dialog>

        <!-- 编辑弹窗 -->
        <el-dialog
            title="编辑字典"
            :visible.sync="editVisible"
            :close-on-click-modal="false"
            width="500px"
        >
            <el-form ref="editFormRef" :model="editForm" label-width="100px">
                <el-form-item label="ID">
                    <el-input v-model="editForm.id" disabled></el-input>
                </el-form-item>
                <el-form-item label="中文描述">
                    <el-input v-model="editForm.dictLabel" disabled></el-input>
                </el-form-item>
                <el-form-item label="值" required>
                    <el-select
                        v-if="isEditSwitchType"
                        v-model="editForm.dictValue"
                        placeholder="请选择"
                        style="width: 100%;"
                    >
                        <el-option label="关" value="0"></el-option>
                        <el-option label="开" value="1"></el-option>
                    </el-select>
                    <el-date-picker
                        v-else-if="isEditDateType"
                        v-model="editForm.dictValue"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="选择日期"
                        style="width: 100%;"
                    ></el-date-picker>
                    <el-input v-else v-model="editForm.dictValue" placeholder="请输入值" maxlength="100" show-word-limit></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitEdit">保 存</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'DictPage',
    data() {
        return {
            dictList: [],
            addVisible: false,
            editVisible: false,
            addForm: {
                dictType: '',
                dictValue: '',
                dictLabel: '',
                dictSort: 0,
                remark: ''
            },
            editForm: {
                id: null,
                dictType: '',
                dictValue: '',
                dictLabel: '',
                dictSort: 0,
                remark: ''
            }
        };
    },
    computed: {
        isEditSwitchType() {
            return (this.editForm.dictType || '').toString().trim().toLowerCase() === 'switch';
        },
        isEditDateType() {
            const type = (this.editForm.dictType || '').toString().trim().toLowerCase();
            return type === 'points-start-time' || type === 'point-end-time';
        },
        isAdminId1() {
            try {
                const admin = window.sessionStorage.getItem('admin');
                if (admin) {
                    const obj = JSON.parse(admin);
                    return obj && (obj.id === 1 || obj.id === '1');
                }
            } catch (e) {}
            return false;
        }
    },
    mounted() {
        this.loadDictList();
    },
    methods: {
        // 当 dictType 为 switch 时，0 显示为 关，1 显示为 开（el-table formatter: row, column, cellValue, index）
        formatDictValue(row, column, cellValue) {
            if (!row) return '';
            const type = (row.dictType || '').toString().trim().toLowerCase();
            const val = (cellValue != null ? cellValue : (row.dictValue != null ? row.dictValue : '')).toString().trim();
            if (type === 'switch') {
                if (val === '0') return '关';
                if (val === '1') return '开';
            }
            return val;
        },
        getCurrentUsername() {
            try {
                const admin = window.sessionStorage.getItem('admin');
                if (admin) {
                    const obj = JSON.parse(admin);
                    return (obj && obj.username) ? obj.username : '';
                }
            } catch (e) {}
            return '';
        },
        loadDictList() {
            this.postRequest('/admin/listDict', {}).then(resp => {
                if (resp) {
                    const list = resp.obj || resp.data || resp.list || (Array.isArray(resp) ? resp : []);
                    this.dictList = Array.isArray(list) ? list : [];
                }
            });
        },
        openAdd() {
            this.addForm = {
                dictType: '',
                dictValue: '',
                dictLabel: '',
                dictSort: 0,
                remark: ''
            };
            this.addVisible = true;
        },
        submitAdd() {
            const dictType = (this.addForm.dictType || '').trim();
            const dictValue = (this.addForm.dictValue || '').trim();
            const dictLabel = (this.addForm.dictLabel || '').trim();
            if (!dictType) {
                this.$message.warning('字典类型不能为空');
                return;
            }
            if (!dictValue) {
                this.$message.warning('值不能为空');
                return;
            }
            if (!dictLabel) {
                this.$message.warning('中文不能为空');
                return;
            }
            const params = {
                dictType,
                dictValue,
                dictLabel,
                dictSort: Number(this.addForm.dictSort) || 0,
                createBy: this.getCurrentUsername(),
                remark: (this.addForm.remark || '').trim()
            };
            this.postRequest('/admin/addDict', params).then(resp => {
                if (resp) {
                    this.$message.success('添加成功');
                    this.addVisible = false;
                    this.loadDictList();
                }
            });
        },
        openEdit(row) {
            if (!row) return;
            const dictType = (row.dictType || '').toString().trim();
            const isSwitch = dictType.toLowerCase() === 'switch';
            const rawVal = row.dictValue;
            const dictValue = (rawVal != null && rawVal !== '') ? String(rawVal) : (isSwitch ? '0' : '');
            this.editForm = {
                id: row.id,
                dictType: dictType || row.dictType || '',
                dictValue: dictValue,
                dictLabel: row.dictLabel || '',
                dictSort: Number(row.dictSort) || 0,
                remark: row.remark || ''
            };
            this.editVisible = true;
        },
        submitEdit() {
            const id = this.editForm.id;
            const dictType = (this.editForm.dictType || '').trim();
            const dictValue = (this.editForm.dictValue || '').trim();
            const dictLabel = (this.editForm.dictLabel || '').trim();
            const dictSort = Number(this.editForm.dictSort);
            if (id === null || id === undefined || id === '') {
                this.$message.warning('ID不能为空');
                return;
            }
            if (!dictType) {
                this.$message.warning('字典类型不能为空');
                return;
            }
            if (!dictValue) {
                this.$message.warning('值不能为空');
                return;
            }
            if (!dictLabel) {
                this.$message.warning('中文描述不能为空');
                return;
            }
            if (dictSort === '' || isNaN(dictSort) || dictSort < 0) {
                this.$message.warning('排序序号不能为空且必须为非负整数');
                return;
            }
            const params = {
                id,
                dictType,
                dictValue,
                dictLabel,
                dictSort,
                updateBy: this.getCurrentUsername(),
                remark: (this.editForm.remark || '').trim()
            };
            this.postRequest('/admin/updateDict', params).then(resp => {
                if (resp) {
                    this.$message.success('修改成功');
                    this.editVisible = false;
                    this.loadDictList();
                }
            });
        },
        handleDelete(row) {
            if (!row || (row.id === null && row.id !== 0)) {
                this.$message.warning('无法获取记录ID');
                return;
            }
            this.$confirm('确定要删除该字典项吗？', '提示', { type: 'warning' }).then(() => {
                this.postRequest('/admin/deleteDict', { id: row.id }).then(resp => {
                    if (resp) {
                        this.$message.success('删除成功');
                        this.loadDictList();
                    }
                });
            }).catch(() => {});
        }
    }
};
</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
}

.table {
    width: 100%;
    font-size: 14px;
}

.red {
    color: #ff0000;
}

.mr10 {
    margin-right: 10px;
}
</style>
