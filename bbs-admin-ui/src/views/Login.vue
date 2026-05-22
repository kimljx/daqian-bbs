<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">大千智荟创新创意交流论坛</div>
            <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.username" placeholder="用户名">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="密码" v-model="param.password" @keyup.enter.native="submitForm()">
                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm()">登 录</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            param: {
                // username: '京茶吉鹿',
                // password: '123456'
                username: '',
                password: ''
            },
            rules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
            }
        };
    },
    methods: {
        submitForm() {
            
            this.$refs.login.validate((valid) => {
                if (valid) {
                    this.postRequest("/common/login",{ ...this.param, channel: '02' }).then(resp => {
                        if (resp && resp.obj) {
                            //存储用户token
                            const tokenStr = resp.obj.tokenHead+resp.obj.token;
                            window.sessionStorage.setItem('tokenStr',tokenStr);
                            this.$message.success('登录成功!');
                            window.sessionStorage.setItem('admin', JSON.stringify(resp.obj.user));
                            this.$router.push('/');
                        } else {
                            // 登录失败：显示后端返回的 message 或默认提示
                            const msg = (resp && resp.message) ? resp.message : '登录失败，请检查用户名和密码';
                            this.$message.error(msg);
                        }
                    }).catch(err => {
                        const msg = (err.response && err.response.data && err.response.data.message)
                            ? err.response.data.message
                            : '登录失败，请稍后重试';
                        this.$message.error(msg);
                    });
                } else {
                    this.$message.error('请输入账号和密码');
                    console.log('error submit!!');
                    return false;
                }
            });
        }
        
    }
};
</script>

<style scoped>
.login-wrap {
    position: relative;
    width: 100%;
    height: 100%;
    background-image: url(../assets/img/login-bg.jpg);
    background-size: 100%;
}
.ms-title {
    width: 100%;
    line-height: 50px;
    text-align: center;
    font-size: 20px;
    color: #fff;
    border-bottom: 1px solid #ddd;
}
.ms-login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 350px;
    margin: -190px 0 0 -175px;
    border-radius: 5px;
    background: rgba(255, 255, 255, 0.3);
    overflow: hidden;
}
.ms-content {
    padding: 30px 30px;
}
.login-btn {
    text-align: center;
}
.login-btn button {
    width: 100%;
    height: 36px;
    margin-bottom: 10px;
}
.login-tips {
    font-size: 12px;
    line-height: 30px;
    color: #fff;
}
</style>
