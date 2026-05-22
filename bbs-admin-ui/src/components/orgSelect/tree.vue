<template>
  <div>
    <el-button :disabled="disabled" :class="btnClass" :style="`width:${width}`" :size="buttonSize" @click="openSelect">{{ selectValue.label }}</el-button>
    <el-dialog :class="treeClass" :title="`选择${labelMsg}`" :visible.sync="selectModal" :close-on-click-modal="false" @closed="closeSelect" :width="dialogWidth" append-to-body>
      <el-input placeholder="输入关键字进行过滤" v-model="filterText" clearable />
      <el-tree style="margin-top:10px;max-height:554px;overflow:auto;" :data="orgList" show-checkbox :node-key="propsId" ref="tree" check-strictly highlight-current @check="checkClick" :props="defaultProps" @check-change="handleClick" :filter-node-method="filterNode">
      </el-tree>
      <section class="margin_top_20">
        <el-button class="sure_btn" type="primary" @click.stop="sure">确认</el-button>
        <el-button v-if="checkType" @click="searchAll">重置</el-button>
      </section>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    checkType: { type: Boolean, default: false },
    width: { type: String, default: 'auto' },
    dialogWidth: { type: String, default: '60%' },
    propsId: { type: String, default: 'id' },
    orgList: { type: Array, default: () => [] },
    checkValue: { type: Object, default: () => ({ label: '请选择' }) },
    buttonSize: { type: String, default: 'medium' },
    labelMsg: { type: String, default: '单位' },
    disabled: { type: Boolean, default: false },
    cleanSelect: { type: String, default: '' },
    specialData: { type: Boolean, default: false },
    btnClass: { type: String, default: '' },
    treeClass: { type: String, default: '' },
    isOverTwenty: { type: Boolean, default: false },
    isDialogOpen: { type: Boolean, default: false },
    isMust: { type: Boolean, default: true },
    isLineMap: { type: Boolean, default: false }
  },
  data() {
    return {
      filterText: '',
      selectModal: false,
      defaultProps: {
        children: this.specialData ? 'CHILDRENLIST' : 'children',
        label: this.specialData ? 'ORG_NAME' : 'label'
      },
      selectValue: { label: '请选择', value: '' }
    };
  },
  watch: {
    orgList: { deep: true, handler() {} },
    selectModal: { deep: true, handler(val) { setTimeout(() => { this.$emit('isOpen', val); }, 50); } },
    filterText(val) {
      this.$nextTick(() => {
        if (this.$refs.tree && typeof this.$refs.tree.filter === 'function') {
          this.$refs.tree.filter(val);
        }
      });
    },
    checkValue: { handler(val) { this.selectValue = this.updateValue(); }, immediate: true }
  },
  created() {
    try {
      this.selectValue = this.updateValue();
    } catch (e) {
      this.selectValue = { label: this.labelMsg ? `请选择${this.labelMsg}` : '请选择', value: '', id: '' };
    }
  },
  methods: {
    updateValues() { this.selectValue = this.updateValue(); },
    updateValue() {
      const checkVal = this.checkValue || {};
      const orgAllList = this.arrItem(this.orgList || []);
      let state = '';
      orgAllList.forEach(item => {
        if (item[this.propsId] == checkVal[this.propsId]) state = item.label;
      });
      return {
        [this.propsId]: checkVal[this.propsId],
        label: state || checkVal.label || '请选择'
      };
    },
    openSelect() {
      this.selectModal = true;
      if (this.cleanSelect && this.$refs.tree) {
        this.$nextTick(() => {
          this.$refs.tree.setCheckedKeys([this.checkValue[this.propsId]].filter(Boolean));
        });
      }
    },
    closeSelect() { this.selectModal = false; },
    handleClick(data, checked) {
      if (checked && !this.checkType && this.$refs.tree) {
        this.$refs.tree.setCheckedKeys([data[this.propsId]]);
      }
      if (this.checkType) this.setChildNodesChecked(data, checked);
    },
    checkClick(data) {
      if (!this.$refs.tree) return;
      const getNode = this.$refs.tree.getCheckedNodes();
      if (getNode.length > 0 && !this.checkType) {
        getNode.forEach(item => {
          this.$refs.tree.setChecked(item[this.propsId], data[this.propsId] === item[this.propsId]);
        });
      }
    },
    setChildNodesChecked(node, checked) {
      this.$refs.tree.setChecked(node, checked);
      if (node.children) {
        node.children.forEach(child => this.setChildNodesChecked(child, checked));
      }
    },
    sure() {
      if (!this.$refs.tree) return;
      const checkedData = this.$refs.tree.getCheckedNodes();
      if (this.isMust && !checkedData.length) {
        this.$message.info(`请选择${this.labelMsg}`);
        return;
      }
      if (!this.checkType) {
        if (checkedData && checkedData.length) {
          this.selectValue = this.specialData
            ? { label: checkedData[0].ORG_NAME, id: checkedData[0].ORG_NO }
            : checkedData[0];
        } else {
          this.selectValue = { label: `请选择${this.labelMsg}`, id: '' };
        }
        this.$emit('getCheckValue', !checkedData.length ? { label: `请选择${this.labelMsg}`, id: '' } : (this.specialData ? { label: checkedData[0].ORG_NAME, id: checkedData[0].ORG_NO } : checkedData[0]));
      } else {
        if (checkedData.length > 20 && !this.isLineMap) {
          this.$message.error('最多只能选择20条数据！');
          return;
        }
        this.selectValue = checkedData.length > 1 ? { label: `多条${this.labelMsg}` } : (!checkedData.length ? { label: `请选择${this.labelMsg}`, id: '' } : checkedData[0]);
        this.$emit('getCheckValue', checkedData);
      }
      this.selectModal = false;
    },
    filterNode(value, data) {
      if (!value) return true;
      const label = (data.label || data.orgName || '').toString();
      return label.indexOf(value) !== -1;
    },
    arrItem(oldArr) {
      let newArr = [];
      (oldArr || []).forEach(item => {
        newArr.push({ [this.propsId]: item[this.propsId], label: item.label || item.orgName });
        if (item.children) newArr = newArr.concat(this.arrItem(item.children));
      });
      return newArr;
    },
    searchAll() {
      if (this.$refs.tree) this.$refs.tree.setCheckedKeys([]);
      this.selectModal = false;
      this.selectValue = { label: `全部${this.labelMsg}`, [this.propsId]: '' };
      this.$emit('getCheckValue', this.selectValue);
    }
  }
};
</script>

<style scoped>
.margin_top_20 { margin-top: 20px; }
</style>
