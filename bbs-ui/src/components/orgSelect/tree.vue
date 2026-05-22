<template>
  <div>
    <el-button :disabled="disabled" :class="btnClass" :style="`width:${width}`" :size="buttonSize" @click="openSelect">{{ selectValue.label }}</el-button>
    <!-- 供电单位弹窗 -->
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
    /* 是否可以多选 */
    checkType: {
      type: Boolean,
      default: false
    },
    /* 按钮宽度 */
    width: {
      type: String,
      default: 'auto'
    },
    /* 弹窗宽度 */
    dialogWidth: {
      type: String,
      default: '60%'
    },
    /* 节点唯一值 */
    propsId: {
      type: String,
      default: 'id'
    },
    orgList: {
      type: Array,
      default: []
    },
    /* 默认值 */
    checkValue: {
      type: Object,
      default: {
        label: '请选择'
      }
    },
    buttonSize: {
      type: String,
      default: 'medium'
    },
    /* 弹框标题及提示 */
    labelMsg: {
      type: String,
      default: '供电单位'
    },
    /* 是否允许操作 */
    disabled: {
      type: Boolean,
      default: false
    },
    // 判断是否需要打开弹框时清空复选框选中状态
    cleanSelect: {
      type: String,
      default: ''
    },
    /* 特殊数据 */
    specialData: {
      type: Boolean,
      default: false
    },
    /* 按钮class */
    btnClass: {
      type: String,
      default: ''
    },
    /* 树形选择的class */
    treeClass: {
      type: String,
      default: ''
    },
    /* 是否限制最多选20个 */
    isOverTwenty: {
      type: Boolean,
      default: false
    },
    /* 是否需要监听弹框是否打开 */
    isDialogOpen: {
      type: Boolean,
      default: false
    },
    /* 是否必选 */
    isMust: {
      type: Boolean,
      default: true
    },
    /* 是否是线路收资地图 */
    isLineMap: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      filterText: '',
      selectModal: false,
      defaultProps: {
        children: this.specialData ? 'CHILDRENLIST' : 'children',
        label: this.specialData ? 'ORG_NAME' : 'label'
      },
      selectValue: {
        label: '请选择',
        value: ''
      }
    };
  },

  watch: {
    orgList: {
      deep: true,
      handler(val) {
      },
    },
    selectModal: {
      deep: true,
      handler(val) {
        setTimeout(() => {
          this.$emit('isOpen', val)
        }, 50);
      },
    },
    filterText(val) {
      this.$refs.tree.filter(val);
    },
    checkValue(val) {
      this.selectValue = this.updateValue();
    }
  },
  created() {
    this.selectValue = this.updateValue();
  },
  methods: {
    /*手动更新值,当用户点击页面上其他单位时赋值给组件复现数据时使用 */
    updateValues() {
      this.selectValue = this.updateValue();
    },
    updateValue() {
      let orgAllList = this.arrItem(this.orgList);
      let state = '';
      orgAllList.forEach(item => {
        if (item[this.propsId] == this.checkValue[this.propsId]) {
          state = item.label;
        }
      })
      return {
        [this.propsId]: this.checkValue[this.propsId],
        label: state.length ? state : this.checkValue.label
      }
    },
    /* 打开选择弹框 */
    openSelect() {
      this.selectModal = true;
      // 判断是否需要打开弹框时清空复选框选中状态
      if (this.cleanSelect) {
        this.$nextTick(() => {
          this.$refs.tree.setCheckedKeys([this.checkValue.value])
        })
        this.$nextTick(() => {
          this.$refs.tree.setCheckedKeys([this.checkValue.id]);
        });
      }
    },
    /* 关闭选择弹框 */
    closeSelect() {
      this.selectModal = false;
    },
    /* 监听选择 */
    handleClick(data, checked, indeterminate) {
      if (checked) {
        if (!this.checkType) {
          this.$refs.tree.setCheckedKeys([data[this.propsId]]);
        }
      }
      if (this.checkType) {
        this.setChildNodesChecked(data, checked)
      }
    },
    /* 单选清除上次选择 */
    checkClick(data) {
      let getNode = this.$refs.tree.getCheckedNodes();
      if (getNode.length > 0) {
        if (!this.checkType) {
          getNode.forEach(item => {
            if (data.id == item.id) {
              this.$refs.tree.setChecked(item.id, true);
            } else {
              this.$refs.tree.setChecked(item.id, false);
            }
          })
        }
      }
    },
    /* 多选时设置下级 */
    setChildNodesChecked(node, checked) {
      this.$refs.tree.setChecked(node, checked);
      if (node.children) {
        node.children.forEach(child => {
          this.setChildNodesChecked(child, checked);
        });
      }
    },
    /* 确定选择 */
    sure() {
      let checkedData = this.$refs.tree.getCheckedNodes();
      if (this.isMust && !checkedData.length) {
        this.$message.info(`请选择${this.labelMsg}`);
        return false;
      }
      if (!this.checkType) {
        if (this.isSingleChose) {
          this.selectValue = checkedData[0];
          this.$emit('getCheckValueSingle', checkedData[0]);
        } else {
          if (checkedData && checkedData.length != 0) {
            if (this.specialData) {
              this.selectValue = { label: checkedData[0].ORG_NAME, id: checkedData[0].ORG_NO };
            } else {
              this.selectValue = checkedData[0];
            }
          } else {
            this.selectValue = { label: `请选择${this.labelMsg}`, id: '' }
          }
          this.$emit('getCheckValue', !checkedData.length ? { label: `请选择${this.labelMsg}`, id: '' } : checkedData[0]);
        }
      } else {
        if (checkedData.length > 1) {
          this.selectValue = {
            label: `多条${this.labelMsg}`
          };
        } else {
          this.selectValue = !checkedData.length ? { label: `请选择${this.labelMsg}`, id: '' } : checkedData[0];
        }
        if (!this.isLineMap && checkedData.length > 20) {
          this.$message.error('最多只能选择20条数据！');
          return
        }
        this.$emit('getCheckValue', checkedData);
      }
      this.selectModal = false;
    },
    /* 搜索 */
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    arrItem(oldArr) {
      let newArr = [];
      oldArr.forEach(item => {
        newArr.push({
          [this.propsId]: item[this.propsId],
          'label': item.label
        });
        if (item.children) {
          newArr = newArr.concat(this.arrItem(item.children))
        }
      })
      return newArr;
    },
    searchAll() {
      this.$refs.tree.setCheckedKeys([]);
      this.selectModal = false;
      this.selectValue = {
        label: `全部${this.labelMsg}`,
        [this.propsId]: ''
      };
      this.$emit('getCheckValue', [this.selectValue]);
    }
  }
};
</script>
