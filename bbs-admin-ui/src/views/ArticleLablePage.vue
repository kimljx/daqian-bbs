<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-collection-tag"></i> 标签管理
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="handle-box">
        <el-button type="primary" icon="el-icon-document-add" class="mr10" @click="openAdd">新增标签</el-button>
        <el-button type="danger" icon="el-icon-delete" class="mr10" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">批量删除</el-button>

        <el-input v-model="searchLabelName" placeholder="标签名称" class="handle-input mr10" clearable @keyup.enter.native="handleSearch"></el-input>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
      </div>

      <el-table :data="labelsRaw" border class="table" ref="multipleTable" header-cell-class-name="table-header" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="labelId" label="标签ID" width="120" align="center"></el-table-column>
        <el-table-column prop="labelName" label="标签名称" min-width="180" align="center"></el-table-column>
        <el-table-column label="是否禁用" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="Number(scope.row.enabled) === 1" type="danger">是</el-tag>
            <el-tag v-else type="success">否</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="openEdit(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background layout="total,sizes,prev, pager, next" @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-sizes="[10, 15, 20, 40, 60, 80, 100]" :current-page="pageParams.pageIndex" :page-size="pageParams.pageSize" :total="total"></el-pagination>
      </div>
    </div>

    <!-- 新增弹窗 -->
    <el-dialog title="新增标签" :visible.sync="addVisible" :close-on-click-modal="false" width="30%">
      <el-form ref="addFormRef" :model="addForm" label-width="90px">
        <el-form-item label="标签名称" required>
          <el-input v-model="addForm.labelName" maxlength="50" show-word-limit placeholder="请输入标签名称"></el-input>
        </el-form-item>
        <el-form-item label="是否禁用">
          <el-switch v-model="addForm.isDisable" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAdd">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog title="编辑标签" :visible.sync="editVisible" :close-on-click-modal="false" width="30%">
      <el-form ref="editFormRef" :model="editForm" label-width="90px">
        <el-form-item label="标签ID">
          <el-input v-model="editForm.labelId" disabled></el-input>
        </el-form-item>
        <el-form-item label="标签名称" required>
          <el-input v-model="editForm.labelName" maxlength="50" show-word-limit placeholder="请输入标签名称"></el-input>
        </el-form-item>
        <el-form-item label="是否禁用">
          <el-switch v-model="editForm.enabled" :active-value="1" :inactive-value="0"></el-switch>
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
  name: 'ArticleLablePage',
  data() {
    return {
      searchLabelName: '',
      labelsRaw: [],
      multipleSelection: [],
      addVisible: false,
      editVisible: false,
      addForm: {
        labelName: '',
        isDisable: 0
      },
      editForm: {
        labelId: null,
        labelName: '',
        isDisable: 0
      },
      pageParams: {
        pageIndex: 1,
        pageSize: 10
      },
      total: 0
    };
  },
  computed: {},
  mounted() {
    this.getArticleLabelPage();
  },
  methods: {
    getArticleLabelPage() {
      const params = {
        pageIndex: this.pageParams.pageIndex,
        pageSize: this.pageParams.pageSize,
        searchInfo: (this.searchLabelName || '').trim()
      };
      this.getRequest('/admin/pageArticleLabel', JSON.stringify(params)).then(resp => {
        if (resp) {
          let list = [];
          let total = 0;
          const obj = resp.obj || resp;
          if (obj && Object.prototype.toString.call(obj.list) === '[object Array]') {
            list = obj.list;
            total = obj.total || 0;
          } else if (Object.prototype.toString.call(obj) === '[object Array]') {
            list = obj;
            total = list.length;
          }
          this.total = total;
          this.labelsRaw = list.map(function (item) {
            if (!item) return item;
            const labelId = item.labelId || item.id || null;
            const labelNameReal = item.labelName || item.name || '';
            const isDisable = typeof item.isDisable !== 'undefined' ? item.isDisable : (item.disable || 0);
            return Object.assign({}, item, {
              labelId: labelId,
              labelName: labelNameReal,
              isDisable: Number(isDisable)
            });
          });
        }
      });
    },
    handleSearch() {
      this.pageParams.pageIndex = 1;
      this.getArticleLabelPage();
    },
    handleSelectionChange(val) {
      this.multipleSelection = val || [];
    },
    handleSizeChange(val) {
      this.pageParams.pageSize = val;
      this.pageParams.pageIndex = 1;
      this.getArticleLabelPage();
    },
    handleCurrentChange(val) {
      this.pageParams.pageIndex = val;
      this.getArticleLabelPage();
    },
    openAdd() {
      this.addForm = {
        labelName: '',
        isDisable: 0
      };
      this.addVisible = true;
    },
    submitAdd() {
      const labelName = (this.addForm.labelName || '').trim();
      if (!labelName) {
        this.$message.warning('标签名称不能为空');
        return;
      }
      const params = {
        labelName,
        enabled: Number(this.addForm.isDisable) === 0 ? 0 : 1
      };
      this.postRequest('/admin/addArticleLabel', params).then(resp => {
        if (resp) {
          this.$message.success('添加成功');
          this.addVisible = false;
          this.getArticleLabelPage();
        }
      });
    },
    openEdit(row) {
      if (!row) return;
      const labelId = row.labelId || row.id || null;
      this.editForm = {
        labelId,
        labelName: row.labelName || '',
        enabled: Number(typeof row.enabled !== 'undefined' ? row.enabled : 0)
      };
      this.editVisible = true;
    },
    submitEdit() {
      const labelId = this.editForm.labelId;
      const labelName = (this.editForm.labelName || '').trim();
      if (!labelId) {
        this.$message.warning('标签ID不能为空');
        return;
      }
      if (!labelName) {
        this.$message.warning('标签名称不能为空');
        return;
      }
        console.log("🚀 ~ this.editForm:", this.editForm.enabled)
      const params = {
        labelId,
        labelName,
        enabled: Number(this.editForm.enabled) === 0 ? 0 : 1
      };
      this.postRequest('/admin/updArticleLabel', params).then(resp => {
        if (resp) {
          this.$message.success('修改成功');
          this.editVisible = false;
          this.getArticleLabelPage();
        }
      });
    },
    handleDelete(row) {
      if (!row) return;
      const labelId = row.labelId || row.id || null;
      if (!labelId) return;
      this.$confirm('确定要删除该标签吗？', '提示', { type: 'warning' }).then(() => {
        this.postRequest('/admin/delArticleLabel', { labelId }).then(resp => {
          if (resp) {
            this.$message.success('删除成功');
            this.getArticleLabelPage();
          }
        });
      }).catch(() => { });
    },
    handleBatchDelete() {
      const rows = this.multipleSelection || [];
      if (rows.length === 0) return;
      const labelIds = rows
        .map(function (r) {
          if (!r) return null;
          return r.labelId || r.id || null;
        })
        .filter(function (v) { return !!v; });
      if (labelIds.length === 0) return;

      this.$confirm(`确定要删除选中的 ${labelIds.length} 个标签吗？`, '提示', { type: 'warning' }).then(() => {
        Promise.all(labelIds.map(labelId => this.postRequest('/admin/delArticleLabel', { labelId })))
          .then(() => {
            this.$message.success('批量删除完成');
            this.getArticleLabelPage();
          });
      }).catch(() => { });
    }
  }
};
</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
}

.handle-input {
    width: 300px;
    display: inline-block;
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