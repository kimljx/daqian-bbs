<template>
	<div class="">
		<div class="userinfo">
			<div class="userinfo-head">
				<el-tooltip class="item" effect="dark" content="点击上传头像" placement="bottom">
					<img :src="getPortrait" @click="dialogVisible = true">
				</el-tooltip>
				<h4>{{ initNickname }}</h4>
			</div>
			<!--保存用户信息区域-->
			<el-row class="save-userinfo" v-if="isShowSave">
				<i class="el-icon-folder-add">我的资料更新</i>
				<el-button class="save-info save" size="mini " type="primary" @click="saveUserinfo" round>保存</el-button>
				<el-button class="save-info cancel" size="mini" type="danger" @click="cancelUserinfo" round>取消
				</el-button>
			</el-row>
			<div class="basic-info" :v-model="user">
				<h3>基本信息</h3>
				<ul>
					<li>
						<span>用户昵称</span>
						<span class="info">{{ user.nickname || '未设置' }}</span>
					</li>
					<li>
						<span class="left">用户ID</span> {{ user.id }}
					</li>
					<li @mouseenter="showEditIcon(2)" @mouseleave="noShowEditIcon(2)">
						<span>电话</span>
						<span class="info" v-show="!isShowPhone">{{ user.phone || '未设置' }}</span>
						<el-input
							class="el-input"
							v-model="user.phone"
							placeholder="请输入手机号"
							v-show="isShowPhone">
						</el-input>
						<i class="el-icon-edit" v-if="isShowEditPhone" @click="isEdit(2)">{{ reminder }}</i>
					</li>
				</ul>
			</div>
		
		</div>
		<!--上传头像框-->
		<el-dialog title="上传头像" :visible.sync="dialogVisible">
			<div style="display: flex;flex-direction: row">
				<el-upload
					action="#"
					accept="'image/*'"
					list-type="picture-card"
					:auto-upload="false"
					:limit="1"
					:show-file-list="true"
					:on-change="handleChange"
					:on-remove="handleRemove"
					:before-upload="beforeAvatarUpload"
					:class="{hide:hideUpload}"
				>
					<img v-if="imageUrl" :src="imageUrl" class="avatar"/>
					<i v-else class="el-icon-plus avatar-uploader-icon"></i>
				</el-upload>
			</div>
			
			<div slot="footer" class="dialog-footer">
				<el-button @click="uploadPortrait" type="primary" :loading="isUploadPic">上传</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script>
import {Message} from "element-ui";

export default {
	
	name: "BBSUserinfo",
	data() {
		return {
			user: JSON.parse(window.sessionStorage.getItem('user')),
			dialogVisible: false,
			isUploadPic: false,
			uploadDisabled: false,
			imageFile: {},
			imageUrl: '',
			initNickname: '',
			isShowEditPhone: false,
			isShowPhone: false,
			reminder: '编辑',
			isShowSave: false,
			hideUpload: false,
		};
	},
	mounted() {
		this.initNickname = this.user.nickname == null ? '你还没有名字' : this.user.nickname
		//this.initNickname = this.user.nickname
		
	},
	watch: {
		// 判断用户的信息是否修改
		user: {
			handler() {
				if (JSON.stringify(this.user) != window.sessionStorage.getItem('user')) {
					this.isShowSave = true
				} else {
					this.isShowSave = false
				}
			},
			deep: true,
			immediate: true
		}
	},
	computed: {
		// 默认头像显示
		getPortrait() {
			return this.user.portrait != null ? `${process.env.VUE_APP_BBS_API}${this.user.portrait}` : require('../assets/portrait.png')
		},
	},
	methods: {
		showEditIcon(n) {
			if (n === 2) {
				this.isShowEditPhone = true
			}
		},
		noShowEditIcon(n) {
			if (n === 2) {
				this.isShowEditPhone = false
			}
		},
		isEdit(n) {
			if (n === 2) {
				this.isShowPhone = !this.isShowPhone
			}
		},
		
		handleChange(file,fileList) {
			// file指的就是选择的文件对象
			this.imageFile = file;
			
			this.hideUpload = fileList.length >= 1
		},
		handleRemove(file, fileList) {
			this.hideUpload = !(fileList.length === 0)
			console.log(fileList.length)
			//this.hideUpload = false
		},
		//上传头像
		uploadPortrait() {
			//获取到用户的id
			const userId = JSON.parse(window.sessionStorage.getItem('user')).id
			// 获取用户选择的图片
			const file = this.imageFile.raw
			
			const formData = new FormData()
			formData.append('userId', userId)
			formData.append("file", file)
			
			this.putRequest('/user/updatePortrait', formData).then(resp => {
				if (resp) {
					//此处应该刷新个人信息//待完善
					this.dialogVisible = false;
					this.isUploadPic = false;
					this.getRequest('/common/user/info').then(resp => {
						if (resp) {
							window.sessionStorage.setItem('user', JSON.stringify(resp));
							//this.user = resp
							this.user.portrait = resp.portrait
						}
					})
				}
			})
			
		},
		
		//判断图片格式和类型
		beforeAvatarUpload(file) {
			const isJPG = file.type === "image/jpeg";
			const isPNG = file.type === "image/png";
			const isLt2M = file.size / 1024 / 1024 < 2;
			
			if (!isJPG && !isPNG) {
				Message({
					type: "error",
					message: "文件类型只能是JPG, PNG!",
					offset: 54
				})
			}
			if (!isLt2M) {
				Message({
					type: "error",
					message: "文件大小不能超过 2MB!",
					offset: 54
				})
			}
			return (isJPG || isPNG) && isLt2M;
		},
		
		cancelUserinfo() {
			location.reload()
		},
		// 校验手机号格式（中国大陆 1 开头的 11 位数字）
		validatePhone(phone) {
			const reg = /^1[3-9]\d{9}$/
			return reg.test(String(phone).trim())
		},
		saveUserinfo() {
			const phone = this.user.phone
			// 电话非空判定
			if (phone === null || phone === undefined || String(phone).trim() === '') {
				Message({
					type: 'warning',
					message: '请输入电话',
					offset: 54
				})
				return
			}
			// 电话格式校验
			if (!this.validatePhone(phone)) {
				Message({
					type: 'warning',
					message: '请输入正确的手机号格式（11位，1开头）',
					offset: 54
				})
				return
			}
			const obj = {
				id: this.user.id,
				phone: this.user.phone
			}
			this.putRequest('/user/updateUserinfo', obj).then(resp => {
				if (resp) {
					this.isShowSave = false
					this.isShowPhone = false
					this.getRequest('/common/user/info').then(resp => {
						if (resp) {
							window.sessionStorage.setItem('user', JSON.stringify(resp))
							this.user.phone = resp.phone
							this.initNickname = resp.nickname
						}
					})
				}
			})
		}
	}
}
</script>

<style scoped>
.userinfo {
	background-color: white;
	/*max-width: 1200px;*/
	/*margin: 10px auto 0;*/
	border: #efeded solid 1px;
	padding: 10px;
}

.userinfo-head {
	display: flex;
	flex-direction: row;
	align-items: center;
	/*border-bottom: #efeded solid 1px;*/
	padding-bottom: 10px;
}

.userinfo-head img {
	width: 80px;
	height: 80px;
	border-radius: 50%;
}

.userinfo-head img:hover {
	opacity: 0.3;
}

.userinfo-head h4 {
	margin-left: 15px;
	font-weight: 600;
}

.basic-info h3 {
	line-height: 50px;
	border-bottom: #e8e8ed solid 1px;
}

.basic-info ul li {
	display: flex;
	flex-direction: row;
	padding: 12px 0px;
	/*width: 66px;*/
	text-align: justify;
	text-align-last: justify;
	align-items: center;
	list-style-type: none;
	/*background-color: green;*/
}

.basic-info ul li span {
	display: inline-block;
	width: 4em;
	text-align: justify;
	text-align-last: justify;
	margin-right: 30px;
}

.basic-info span {
	letter-spacing: inherit;
	text-align: justify;
}

.basic-info .info {
	width: auto;
}

.el-input {
	display: flex;
	flex-direction: row;
	width: 180px;
	text-align-last: left;
}

.el-icon-edit {
	margin-left: 20px;
	color: skyblue;
	cursor: pointer;
}

.cascader {
	display: flex;
	flex-direction: row;
	text-align-last: left;
	width: 180px;
}

.el-input-textarea {
	display: flex;
	flex-direction: row;
	width: 500px;
	text-align-last: left;
}

.el-icon-folder-add {
	color: skyblue;
}

.save-userinfo {
	padding: 20px 0;
	margin-right: 20px;
}

.el-icon-folder-add {
	margin-right: 80px;
}

.save-info {
	width: 80px;
	margin-right: 20px;
}

.save {
	background-color: #a7def6;
	border: #a7def6 solid 1px;
}

.cancel {
	background-color: #f1aeae;
	border: #f1aeae solid 1px;
}

.el-upload--picture-card{
	display: flex;
	flex-direction: row;
	justify-content: center;
	align-items: center;
	margin-right: 15px;
	height: 117px;
}
.el-upload-list--picture-card .el-upload-list__item{
	height: 117px;
}
.hide .el-upload--picture-card {
	display: none;
}

</style>
