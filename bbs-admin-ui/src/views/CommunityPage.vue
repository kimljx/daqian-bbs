<template>
    <div>
        <div class='crumbs'>
            <el-breadcrumb separator='/'>
                <el-breadcrumb-item><i class='el-icon-takeaway-box'></i> 社区管理</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class='container'>
            <div class='handle-box'>
                <el-input v-model='searchInfo'
                          placeholder='请输入搜索内容'
                          class='handle-input mr10'></el-input>
                <el-button type='primary'
                           icon='el-icon-search'
                           @click='handleSearch'>搜索
                </el-button>
            </div>
            <el-table :data='communities' border height='480' class='table'>
                <el-table-column prop='communityId' label='ID' width='55' align='center'></el-table-column>
                <el-table-column prop='communityName' label='社区名称' width='120' align='center'></el-table-column>
                <el-table-column prop='communityIntroduce' label='社区介绍' align='center'></el-table-column>
                <el-table-column prop='communityUserNum' label='社区人数' width='80' align='center'></el-table-column>
                <el-table-column label='社区封面' width='80' align='center'>
                    <template slot-scope='scope'>
                        <el-image class='table-td-thumb'
                                  :src='scope.row.communityImage'
                                  :preview-src-list='[scope.row.communityImage]'></el-image>
                    </template>
                </el-table-column>
                <el-table-column label='状态' width='70' align='center'>
                    <template slot-scope='scope'>
                        <el-tag v-if='scope.row.enable === 0' type='success'>活跃</el-tag>
                        <el-tag v-else type='danger'>禁用</el-tag>
                    </template>
                </el-table-column>
                
                <el-table-column prop='createTime' width='154' label='添加时间' align='center'></el-table-column>
                <el-table-column label='操作' width='150' align='center'>
                    <template slot-scope='scope'>
                        <el-button type='text'
                                   icon='el-icon-thumb'
                                   @click='updateCommunityStatus(scope.row.communityId)'>编辑状态
                        </el-button>
                        <el-button type='text'
                                   icon='el-icon-delete'
                                   class='red'
                                   @click='deleteCommunity(scope.row.communityId)'>删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        
        
    </div>
</template>

<script>
import VueUploadMultipleImage from 'vue-upload-multiple-image';

export default {
    name: 'CommunityPage',
    components: { VueUploadMultipleImage },
    data() {
        return {
            searchInfo: '',
            communities: [],
            addVisible: false,
        };
    },
    mounted() {
        this.getAllCommunity();
        this.addVisible = false;
    },
    methods: {
        getAllCommunity() {
            this.getRequest('/admin/getAllCommunity', 'all').then(resp => {
                if (resp) {
                    this.communities = resp.obj;
                }
            });
        },
        
        handleSearch() {
            if (this.searchInfo === '') {
                this.getAllCommunity();
            } else {
                this.getRequest('/admin/getCommunityByKeywords', this.searchInfo).then(resp => {
                    if (resp) {
                        this.communities = resp.obj;
                    }
                });
            }
        },
        
        updateCommunityStatus(communityId){
            this.$confirm('确定修改该社区状态吗？', '提示', { type: 'warning' }).then(() => {
                this.putRequest(`/admin/updateCommunityStatus/${communityId}`).then(resp =>{
                    this.getAllCommunity();
                    this.$message.success("修改成功！")
                })
                
            }).catch(() =>{
            
            })
        },
        
        deleteCommunity(communityId) {
            this.$confirm('确定删除该社区吗？', '提示', { type: 'warning' }).then(() => {
                this.deleteRequest("/admin/deleteCommunityByCommunityId",communityId).then(resp => {
                    if (resp) {
                        this.getAllCommunity();
                        this.$message.success('删除成功！');
                    }
                });
            }).catch(() => {
            });
        },
    }
};
</script>

<style>
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

.table-td-thumb {
    display: block;
    margin: auto;
    width: 40px;
    height: 40px;
}

.image-container[data-v-10e59822] {
    background-color: unset !important;
    width: unset !important;
    /*height: 170px!important;*/
}

/*!* 修改信息 上传框样式*!
.upload-demo .el-upload--text {
    height: unset;
    width: 100%;
}

.el-upload {
    text-align: left;
}*/
</style>
