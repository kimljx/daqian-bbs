<template>
	<div>
		<header class="topbar">
			<div class="topbar-inner">
				<div class="topbar-left">
					<div class="brand" @click="transIndex">
						大千智荟交流论坛
					</div>

					<nav class="nav-links">
						<router-link active-class="active" to="/forum">论坛</router-link>
						<router-link v-if="showRankModule" active-class="active" to="/points">排行榜</router-link>
					</nav>
				</div>

				<div class="search-wrap" v-if="isForumPage">
					<i class="el-icon-search"></i>
					<input
						v-model="keywords"
						type="text"
						placeholder="搜索讨论、创意或人员..."
						@keyup.enter="search"
					>
				</div>

				<div class="topbar-actions">
					<button class="publish-btn" type="button" @click="write">
						<i class="el-icon-plus"></i>
						<span>发布</span>
					</button>

					<div class="user-menu" @mouseenter="mouseOver" @mouseleave="mouseLeave" v-if="isLogin && user">
						<img class="avatar" :src="portrait" alt="用户头像">
						<div class="profile-popover" :style="{ display }">
							<div class="profile-user">
								<img :src="portrait" alt="用户头像">
								<p :title="username">{{ username }}</p>
							</div>
							<button type="button" @click="userinfo">个人中心</button>
							<button type="button" @click="myPublish">我的发布</button>
							<button type="button" @click="logout">退出登录</button>
						</div>
					</div>

					<button class="login-btn" type="button" v-else @click="login">登录/注册</button>
				</div>
			</div>
		</header>

		<div class="page-shell">
			<router-view></router-view>
		</div>
	</div>
</template>

<script>
import { Message } from "element-ui";

export default {
	name: "BBSHeader",
	data() {
		return {
			display: 'none',
			isLogin: false,
			user: JSON.parse(window.sessionStorage.getItem('user') || 'null'),
			keywords: '',
			showRankModule: false,
		}
	},
	methods: {
		checkRankSwitch() {
			this.postRequest('/common/listDictByType', { dictType: 'switch' })
				.then(resp => {
					const obj = resp && resp.obj
					const first = Array.isArray(obj) && obj.length ? obj[0] : null
					this.showRankModule = first && String(first.dictValue) === '1'
				})
				.catch(() => {
					this.showRankModule = false
					Message({
						type: 'error',
						message: '排行榜开关读取失败',
						offset: 70
					})
				})
		},
		mouseOver() {
			this.display = 'block'
		},
		mouseLeave() {
			this.display = 'none'
		},
		transIndex() {
			this.$router.push('/forum')
		},
		login() {
			this.$router.push('/login')
		},
		write() {
			this.$router.push('/write')
		},
		userinfo() {
			this.$router.push('/tabs/1')
		},
		myPublish() {
			this.$router.push('/stat')
		},
		logout() {
			window.sessionStorage.clear()
			this.$router.replace('/forum')
			location.reload()
		},
		search() {
			const keywords = (this.keywords || '').trim()
			window.localStorage.setItem('bbs_search_keywords', keywords)

			if (!window.sessionStorage.getItem('tokenStr')) {
				this.$router.push('/login')
				return
			}

			this.$bus.$emit('forumSearch', keywords)
		},
		refreshUser() {
			if (!window.sessionStorage.getItem('tokenStr')) return
			if (window.sessionStorage.getItem('user')) {
				this.user = JSON.parse(window.sessionStorage.getItem('user') || 'null')
				return
			}
			this.getRequest('/common/user/info').then(resp => {
				if (resp) {
					window.sessionStorage.setItem('user', JSON.stringify(resp))
					this.user = resp
				}
			})
		}
	},
	computed: {
		isForumPage() {
			return this.$route.path === '/forum'
		},
		portrait() {
			if (this.user && this.user.portrait) {
				return `${process.env.VUE_APP_BBS_API}${this.user.portrait}`
			}
			return require('../assets/portrait.png')
		},
		username() {
			return this.user && this.user.nickname ? this.user.nickname : '论坛用户'
		}
	},
	mounted() {
		this.checkRankSwitch()
		this.$bus.$on('isLogin', (data) => {
			this.isLogin = data
			this.refreshUser()
		})
		if (window.sessionStorage.getItem("tokenStr")) {
			this.isLogin = true
			this.refreshUser()
		}

		const lastKeywords = window.localStorage.getItem('bbs_search_keywords')
		if (lastKeywords) {
			this.keywords = lastKeywords
		}
	},
	updated() {
		if (window.sessionStorage.getItem('tokenStr') && !window.sessionStorage.getItem('user')) {
			this.refreshUser()
		}
	}
}
</script>

<style scoped>
.topbar {
	background: #faf9f9;
	border-bottom: 1px solid #c1c6d7;
	box-shadow: 0 1px 6px rgba(0, 0, 0, 0.05);
	height: 64px;
	left: 0;
	position: fixed;
	top: 0;
	width: 100%;
	z-index: 10001;
}

.topbar-inner {
	align-items: center;
	box-sizing: border-box;
	display: flex;
	gap: 24px;
	height: 64px;
	justify-content: space-between;
	margin: 0 auto;
	max-width: 1280px;
	padding: 0 32px;
}

.topbar-left {
	align-items: center;
	display: flex;
	gap: 32px;
	min-width: 0;
}

.brand {
	color: #0057c2;
	cursor: pointer;
	flex-shrink: 0;
	font-size: 20px;
	font-weight: 700;
	line-height: 1.3;
}

.nav-links {
	align-items: center;
	display: flex;
	gap: 16px;
	height: 64px;
}

.nav-links a {
	align-items: center;
	border-bottom: 2px solid transparent;
	color: #414755;
	display: flex;
	font-size: 14px;
	font-weight: 500;
	height: 64px;
	padding: 0 2px;
	text-decoration: none;
	transition: color 0.2s ease, border-color 0.2s ease;
}

.nav-links a:hover,
.nav-links a.active {
	border-color: #0057c2;
	color: #0057c2;
	font-weight: 700;
}

.search-wrap {
	align-items: center;
	background: #f4f3f3;
	border: 1px solid #c1c6d7;
	border-radius: 8px;
	display: flex;
	flex: 1;
	height: 40px;
	max-width: 430px;
	min-width: 220px;
	padding: 0 12px;
	transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-wrap:focus-within {
	border-color: #0057c2;
	box-shadow: 0 0 0 1px rgba(0, 87, 194, 0.12);
}

.search-wrap i {
	color: #727786;
	font-size: 16px;
	margin-right: 8px;
}

.search-wrap input {
	background: transparent;
	border: 0;
	color: #1b1c1c;
	flex: 1;
	font-size: 14px;
	height: 100%;
	outline: none;
}

.topbar-actions {
	align-items: center;
	display: flex;
	flex-shrink: 0;
	gap: 12px;
}

.publish-btn,
.login-btn {
	align-items: center;
	border: 0;
	border-radius: 8px;
	cursor: pointer;
	display: flex;
	font-size: 14px;
	font-weight: 500;
	gap: 6px;
	height: 40px;
	justify-content: center;
	padding: 0 18px;
	transition: opacity 0.2s ease, transform 0.2s ease;
	white-space: nowrap;
}

.publish-btn {
	background: #0057c2;
	color: #ffffff;
}

.login-btn {
	background: #e3e2e2;
	color: #1b1c1c;
}

.publish-btn:hover,
.login-btn:hover {
	opacity: 0.9;
}

.publish-btn:active,
.login-btn:active {
	transform: scale(0.98);
}

.user-menu {
	height: 40px;
	position: relative;
	width: 40px;
}

.avatar {
	border: 1px solid #c1c6d7;
	border-radius: 50%;
	cursor: pointer;
	height: 40px;
	object-fit: cover;
	width: 40px;
}

.profile-popover {
	background: #ffffff;
	border-radius: 8px;
	box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
	min-width: 220px;
	padding: 16px;
	position: absolute;
	right: 0;
	top: 50px;
	z-index: 10;
}

.profile-user {
	align-items: center;
	border-bottom: 1px solid #e9e8e8;
	display: flex;
	gap: 12px;
	margin-bottom: 8px;
	padding-bottom: 12px;
}

.profile-user img {
	border-radius: 50%;
	height: 42px;
	object-fit: cover;
	width: 42px;
}

.profile-user p {
	color: #1b1c1c;
	font-size: 15px;
	font-weight: 600;
	margin: 0;
	min-width: 0;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.profile-popover button {
	background: transparent;
	border: 0;
	border-radius: 6px;
	color: #414755;
	cursor: pointer;
	display: block;
	font-size: 14px;
	height: 36px;
	padding: 0 8px;
	text-align: left;
	width: 100%;
}

.profile-popover button:hover {
	background: #f4f3f3;
	color: #0057c2;
}

.page-shell {
	padding-top: 64px;
}

@media (max-width: 900px) {
	.topbar-inner {
		gap: 14px;
		padding: 0 16px;
	}

	.brand {
		font-size: 17px;
	}

	.nav-links {
		display: none;
	}

	.search-wrap {
		min-width: 120px;
	}
}

@media (max-width: 640px) {
	.search-wrap {
		display: none;
	}

	.publish-btn span {
		display: none;
	}

	.publish-btn {
		padding: 0 12px;
		width: 40px;
	}
}
</style>
