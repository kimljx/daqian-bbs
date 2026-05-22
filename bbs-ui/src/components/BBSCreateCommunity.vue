<template>
	<div class="create-community">
		<div class="create-community-box">
			<h3>吴彦祖论坛社区入驻申请表单</h3>
			
			<el-form ref="form" :model="form" :label-position="labelPosition" label-width="100px" :rules="rules">
				<el-form-item label="姓名" :rules="[{required:true,message:'必填参数'}]">
					<el-input v-model="form.name"></el-input>
				</el-form-item>
				
				<el-form-item label="手机号" :rules="[{required:true}]">
					<el-input v-model="form.phone"></el-input>
				</el-form-item>
				
				<el-form-item label="邮箱号" :rules="[{required:true}]">
					<el-input v-model="form.email"></el-input>
				</el-form-item>
				
				<el-form-item label="社区名称" :rules="[{required:true}]">
					<el-input v-model="form.communityName"></el-input>
				</el-form-item>
				
				<el-form-item label="社区封面" :rules="[{required:true}]">
					<el-upload
						class="upload-cover"
						action="#"
						accept="'image/*'"
						list-type="picture-card"
						:on-upload="false"
						:limit="1"
						:show-file-list="true"
						:on-change="handleChange"
						:before-upload="beforeAvatarUpload"
					>
						<img v-if="imageUrl" :src="imageUrl" class="avatar" alt=""/>
						<i v-else class="el-icon-plus avatar-uploader-icon"></i>
					</el-upload>
				</el-form-item>
				
				<el-form-item label="社区描述" :rules="[{required:true}]">
					<el-input type="textarea" :rows="4" maxlength="250" v-model="form.communityDesc"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="onSubmit">立即创建</el-button>
					<el-button>取消</el-button>
				</el-form-item>
			</el-form>
		</div>
		
	</div>
</template>

<script>
import {Message} from "element-ui";

export default {
	name: "BBSCreateCommunity",
	data() {
		return {
			labelPosition:'left',
			hideUpload:false,
			form: {
				createUserId:JSON.parse(window.sessionStorage.getItem('user')).id,
			},
			imageFile:{},
			imageUrl:'',
			rules:{},
			communityImage: '',
		}
	},
	methods: {
		onSubmit() {
			// 上传图片
			//this.uploadPortrait()
			
			//获取到用户的id
			const userId = JSON.parse(window.sessionStorage.getItem('user')).id
			// 获取用户选择的图片
			const file = this.imageFile.raw
			if (file){
				const formData = new FormData()
				formData.append('userId', userId)
				formData.append("file", file)
				
				this.putRequest('/community/createCommunityReturnImage', formData).then(resp => {
					if (resp) {
						// 保存信息
						this.saveCommunity(resp)
					}
				})
			}
			else {
				Message({
					type:"warning",
					message:"请上传社区封面图！",
					offset:54
				})
			}
			
		},
		
		handleChange(file,fileList){
			this.imageFile = file
		},
		//判断图片格式和类型
		beforeAvatarUpload(file){
			const isJPG = file.type === "image/jpeg";
			const isPNG = file.type === "image/png";
			const isLt2M = file.size / 1024 / 1024 < 2;
			
			if (!isJPG && !isPNG) {
				Message({
					type: "warning",
					message: "文件类型只能是JPG, PNG!",
					offset: 54
				})
			}
			if (!isLt2M) {
				Message({
					type: "warning",
					message: "文件大小不能超过 2MB!",
					offset: 54
				})
			}
			return (isJPG || isPNG) && isLt2M;
		},
		
		//上传封面
		/*uploadPortrait() {
			//获取到用户的id
			const userId = JSON.parse(window.sessionStorage.getItem('user')).id
			// 获取用户选择的图片
			const file = this.imageFile.raw
			if (file){
				const formData = new FormData()
				formData.append('userId', userId)
				formData.append("file", file)
				
				this.putRequest('/community/createCommunityReturnImage', formData).then(resp => {
					if (resp) {
						this.communityImage = resp
					}
				})
			}
		},*/
		
		// 保存提交
		saveCommunity(communityImage){
			if (this.form.createUserId && this.form.name && this.form.phone &&
				this.form.email && this.form.communityName && this.form.communityDesc){
				const formData = this.form
				formData.image = communityImage
				console.log("formData",formData)
				this.postRequest('/community/createCommunity',formData).then(resp=>{
					if (resp){
						console.log(resp)
						this.$router.push('/community')
					}
				})
			}else {
				Message({
					type: "warning",
					message:'确保提交信息完整！',
					offset: 54
				})
			}
		}
	}
}
</script>

<style scoped>
.create-community{
	display: flex;
	flex-direction: row;
	border: #efeded solid 1px;
	padding: 10px;
	/*box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)*/
	
}
.create-community-box{
	display: flex;
	flex-direction: column;
	
	/*background-color: #55a532;*/
}
.create-community-box h3{
	margin-bottom: 14px;
	/*margin-left: 10px;*/
}
.el-input{
	width: 400px;
}
.upload-cover{
	display: flex;
	flex-direction: row;
	/*background-color: #55a532;*/
}


</style>
