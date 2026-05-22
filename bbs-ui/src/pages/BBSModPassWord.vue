<template>
	<div class="mod-pwd">
		<div class="mod-pwd-box">
			<h3>密码修改</h3>
			<el-form :model="form" label-width="120px" class="form">
				<el-form-item label="用户名">
					<el-input v-model="form.username" disabled placeholder="用户名"></el-input>
				</el-form-item>
				<el-form-item label="原密码">
					<el-input
						v-model="form.password"
						type="password"
						placeholder="请输入原密码"
						show-password
						clearable
					></el-input>
				</el-form-item>
				<el-form-item label="新密码">
					<el-input
						v-model="form.newPassword"
						type="password"
						placeholder="请输入新密码"
						show-password
						clearable
					></el-input>
				</el-form-item>
				<el-form-item label="重复新密码">
					<el-input
						v-model="form.confirmPassword"
						type="password"
						placeholder="请再次输入新密码"
						show-password
						clearable
					></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="submit">提交</el-button>
					<el-button @click="resetForm">重置</el-button>
				</el-form-item>
			</el-form>
		</div>
	</div>
</template>

<script>
import { Message } from 'element-ui';

export default {
	name: 'BBSModPassWord',
	data() {
		return {
			user: JSON.parse(window.sessionStorage.getItem('user') || '{}'),
			form: {
				username: '',
				password: '',
				newPassword: '',
				confirmPassword: '',
			},
		};
	},
	mounted() {
		this.form.username = this.user.username || '';
	},
	methods: {
		submit() {
			const { password, newPassword, confirmPassword } = this.form;
			if (!password || !password.trim()) {
				Message({
					type: 'warning',
					message: '原密码不能为空！',
					offset: 54,
				});
				return;
			}
			if (!newPassword || !newPassword.trim()) {
				Message({
					type: 'warning',
					message: '新密码不能为空！',
					offset: 54,
				});
				return;
			}
			if (!confirmPassword || !confirmPassword.trim()) {
				Message({
					type: 'warning',
					message: '重复新密码不能为空！',
					offset: 54,
				});
				return;
			}
			if (password.trim() === newPassword.trim()) {
				Message({
					type: 'warning',
					message: '新密码不能与原密码相同！',
					offset: 54,
				});
				return;
			}
			if (newPassword !== confirmPassword) {
				Message({
					type: 'warning',
					message: '新密码与重复新密码不一致！',
					offset: 54,
				});
				return;
			}
			const params = {
				id: this.user.id,
				password: this.form.password.trim(),
				newPassword: this.form.newPassword.trim(),
			};
			this.postRequest('/user/modPwd', params).then((resp) => {
				if (resp) {
					Message({
						type: 'success',
						message: '密码修改成功，请重新登录！',
						offset: 54,
					});
					window.sessionStorage.clear();
					this.$bus.$emit('isLogin', false);
					this.$router.replace('/login');
					setTimeout(() => {
						location.reload();
					}, 600);
				}
			});
		},
		resetForm() {
			this.form.password = '';
			this.form.newPassword = '';
			this.form.confirmPassword = '';
		},
	},
};
</script>

<style scoped>
.mod-pwd {
	background-color: white;
	border: #efeded solid 1px;
	padding: 20px;
}

.mod-pwd-box h3 {
	line-height: 50px;
	border-bottom: #e8e8ed solid 1px;
	margin-bottom: 24px;
}

.form {
	max-width: 480px;
}
</style>
