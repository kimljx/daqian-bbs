<template>
    <div>
        <div class='crumbs'>
            <el-breadcrumb separator='/'>
                <el-breadcrumb-item><i class='el-icon-timer'></i> 公告管理</el-breadcrumb-item>
                <el-breadcrumb-item>轮播图管理</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-button
                    type="primary"
                    icon="el-icon-document-add"
                    class="handle-del mr10"
                    @click="addVisible = true">
                    添加
                </el-button>
            </div>
            <el-table
                :data="slideshows"
                border
                class="table"
                ref="multipleTable"
                header-cell-class-name="table-header"
                style="width: 100%"
                :default-sort = "{prop: 'successive', order: 'descending'}"
            >
                <el-table-column prop="slideshowId" label='ID' width="180" align='center'></el-table-column>
                <el-table-column prop="successive" label="优先级" sortable width="180" align='center'></el-table-column>
                <el-table-column label="轮播图图片"  align="center">
                    <template slot-scope="scope">
                        <el-image class="table-td-thumb" :src="scope.row.imageUrl" :preview-src-list="[scope.row.imageUrl]"></el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" sortable align='center'></el-table-column>
                
                <el-table-column label="修改优先级" width="250" align="center">
                    <template slot-scope="scope">
                        <div @click='editSuccessive(scope.row.slideshowId)'>
                            <el-input-number v-model="scope.row.successive"
                                             @change="handleChange"
                                             @blur='handleBlur'
                                             :min="1"
                                             label="修改轮播图优先级">
                            </el-input-number>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    
        <!-- 添加弹出框 -->
        <el-dialog title='添加轮播图'
                   :visible.sync='addVisible'
                   :close-on-click-modal='false'
                   width='30%'>
            <el-form ref='form' :model='addForm' label-width='70px'>
                <el-form-item label='优先级'>
                    <el-input type='number' v-model='addForm.successive' placeholder='请输入优先级（值越大，优先级越高）'></el-input>
                </el-form-item>
                <el-form-item label='轮播图'>
                    <vue-upload-multiple-image
                        class='upload-vue'
                        @upload-success='uploadImageSuccess'
                        @before-remove='beforeRemove'
                        @edit-image='editImage'
                        @data-change='dataChange'
                        :max-image='1'
                        drag-text='点击添加轮播图'
                        browse-text='大千智荟创新创意交流论坛'
                        mark-is-primary-text='轮播图'
                        popup-text='添加系统的轮播图'
                        primary-text='大千智荟创新创意交流论坛-轮播图'
                        :data-images='images' />
                </el-form-item>
            </el-form>
            <span slot='footer' class='dialog-footer'>
                <el-button @click='addVisible = false'>取 消</el-button>
                <el-button type='primary' @click='addSlideshow'>确 定</el-button>
            </span>
        </el-dialog>
        
        
    </div>
</template>

<script>
import VueUploadMultipleImage from 'vue-upload-multiple-image';
export default {
    name: 'SlideshowPage',
    components:{VueUploadMultipleImage},
    
    data(){
        return{
            slideshows:[],
            addVisible:false,
            addForm: {},
            images:[],
            editVisible:false,
            editSlideshowId:null,
        }
        
    },
    mounted() {
        this.getSlideshow()
        this.addVisible = false
    },
    methods:{
        getSlideshow(){
            this.getRequest("/admin","getSlideshow").then(resp =>{
                if (resp){
                    this.slideshows = resp.obj
                }
            })
        },
        addSlideshow(){
            this.postRequest("/admin/addSlideshow",this.addForm).then(resp =>{
                if (resp){
                    this.addVisible = false
                    this.getSlideshow()
                    this.$message.success("添加成功！")
                }
            })
        },
        handleChange(value) {
            console.log(value)
            // 发送请求，保存到数据库
            this.saveSuccessive(this.editSlideshowId,value)
        },
        handleBlur(e){
            console.log(e)
        },
        editSuccessive(slideshowId){
            this.editSlideshowId = slideshowId
        },
        
        saveSuccessive(slideshowId,successive){
            if (slideshowId){
                this.putRequest(`/admin/editSuccessive/${slideshowId}/${successive}`).then(resp =>{
                    if (resp){
            
                    }
                })
            }
            this.getSlideshow()
        },
    
        uploadImageSuccess(formData, index, fileList) {
            if (fileList.length === 1) {
                console.log('fileList=>', fileList);
                let formdata = new FormData();
                formdata.append('image', fileList[0].path);
                this.postRequest('/admin/addSlideshowReturnImageUrl', formdata).then(resp => {
                    if (resp) {
                        this.addForm.imageUrl = resp;
                        console.log(resp)
                    }
                });
            }
        },
        beforeRemove(index, done, fileList) {
            console.log('index', index, fileList);
            const r = confirm('remove image');
            if (r === true) {
                done();
            } else {
            }
        },
        editImage(formData, index, fileList) {
            console.log('edit data', formData, index, fileList);
        },
        dataChange(data) {
            console.log(data);
        },
    }
    
};
</script>

<style>

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
.table-td-thumb {
    display: block;
    margin: auto;
    /*width: 40px;*/
    /*height: 40px;*/
}
.upload-vue .image-container[data-v-10e59822] {
    background-color: unset !important;
    width: unset !important;
    /*height: 170px!important;*/
}
</style>
