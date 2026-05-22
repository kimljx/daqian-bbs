<template>
  <div>
    <h4>单位管理</h4>
    <div class="tree_container" v-loading="loading">
      <el-tree class="tree_div" :data="orgTree"  node-key="id" default-expand-all :expand-on-click-node="false">
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span>{{ node.label }}</span>
          <span>
            <el-button type="text" size="mini" @click="() => append(data)">
              新增
            </el-button>
            <el-button v-if="data.id.length != 5" type="text" size="mini" @click="() => remove(node, data)">
              删除
            </el-button>
          </span>
        </span>
      </el-tree>
    </div>

    <el-dialog title="新增单位" :visible.sync="dialogVisible" width="30%" :close-on-click-modal="false">
      <el-form ref="form" label-width="80px">
        <el-form-item label="单位名称">
          <el-input v-model="addOrgName"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">确认</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: 'BBSUnitManage',
  data() {
    return {
      loading: false,
      orgTree: [],
      dialogVisible: false,
      addPOrgNo: '',
      addOrgName: ''
    }
  },
  mounted() {
    this.getAllOrgTree();
  },
  methods: {
    async getAllOrgTree() {
      this.loading = true;
      let res = await this.getRequestUrl('/common/saOrgTree');
      if (res.code == 200) {
        this.orgTree = res.obj;
      } else {
        this.orgTree = []
      }
      this.loading = false;
    },
    append(data) {
      this.addPOrgNo = data.id;
      this.dialogVisible = true;
    },
    async onSubmit() {
      if (!this.addOrgName.length) {
        this.$message.error('请输入单位名称');
        return;
      }
      let res = await this.getRequestUrl(`/saOrg/addSaOrg?pOrgNo=${this.addPOrgNo}&orgName=${this.addOrgName}`);
      if (res.code == 200) {
        this.$message.success('新增成功！');
        this.dialogVisible = false;
        this.addOrgName = '';
        this.getAllOrgTree();
      } else {
        this.$message.error(res.message);
      }
    },
    remove(node, data) {
      this.$confirm('确定删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        let res = await this.getRequestUrl(`/saOrg/deleteSaOrgByOrgNo?orgNo=${data.id}`);
        if (res.code == 200) {
          this.$message.success('删除成功！');
          this.getAllOrgTree();
        } else {
          this.$message.error(res.message);
        }
      }).catch((err) => {});
    },
  }
}
</script>
<style scoped>
.tree_container {
    margin-top: 10px;
    padding: 20px;
    background-color: #fff;

    .tree_div {
        width: 500px;
    }
}

.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
}
</style>
