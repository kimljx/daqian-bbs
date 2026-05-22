<template>
	<div id="write">
		<el-container class="me-write-scroll-wrap">
			<el-container class="me-area me-write-box">
				<el-main class="me-write-main">
					<div class="me-write-title">
						<el-input resize="none" type="textarea" autosize v-model="articleForm.title"
							placeholder="请输入文章标题" class="me-write-input">
						</el-input>
					</div>
					<div id="placeholder" style="visibility: hidden;height: 89px;display: none;"></div>
					<BBSMarkdownEditor :editor="articleForm.editor" class="me-write-editor"></BBSMarkdownEditor>
					<!-- 附件上传 -->
					<div class="attachment-upload-section">
						<div class="attachment-upload-header">
							<!-- <span class="attachment-upload-title">附件上传</span> -->
							<el-button type="primary" size="small" :disabled="attachmentList.length >= 5"
								@click="triggerAttachmentSelect">
								添加附件
							</el-button>
							<input ref="attachmentInput" type="file" multiple style="display: none"
								@change="onAttachmentSelected" />
						</div>
						<ul v-if="attachmentList.length > 0" class="attachment-list">
							<li v-for="(item, index) in attachmentList" :key="item.fileId" class="attachment-item">
								<span class="attachment-name">{{ item.fileName }}</span>
								<el-button type="text" size="mini" icon="el-icon-delete"
									@click="removeAttachment(index)">删除</el-button>
							</li>
						</ul>
					</div>

				</el-main>
			</el-container>

			<!--  弹窗-->
			<el-dialog title="封面 摘要 标签" :visible.sync="publishVisible" :close-on-click-modal=false
				custom-class="me-dialog">

				<el-form :model="articleForm" ref="articleForm" :rules="rules">
					<div class="image-summary-row">
						<el-upload action="#" accept="'image/*'" list-type="picture-card" :auto-upload="false"
							:limit="1" :show-file-list="true" :on-change="handleChange" :on-remove="handleHideRemove"
							:before-upload="beforeAvatarUpload" :file-list="fileList" :class="{ hide: hideUpload }">
							<img v-if="imageUrl" :src="imageUrl" class="avatar" alt="" />
							<i v-else class="el-icon-plus avatar-uploader-icon"></i>
						</el-upload>
						<el-form-item prop="summary">
							<el-input class="el-input-summary" maxlength="250" show-word-limit type="textarea"
								v-model="articleForm.summary" :rows="5" placeholder="请输入文章摘要">
							</el-input>
						</el-form-item>
					</div>

					<el-form-item label="文章标签" prop="label">
						<el-radio-group v-model="articleForm.label">
							<el-radio v-for="l in labels" :key="l.labelId" :label="l.labelId" name="labelName">
								{{ l.labelName }}
							</el-radio>
						</el-radio-group>
					</el-form-item>
				</el-form>
				<div slot="footer" class="dialog-footer">
					<el-button @click="publishVisible = false">取 消</el-button>
					<el-button type="primary" @click="publish('articleForm')">发布</el-button>
				</div>
			</el-dialog>
		</el-container>
		<!--  底部发布与取消-->
		<div class="footer">
			<div class="footer-btn">
				<el-button class="publish-btn" type="primary" round size="medium" @click="publishShow">发布文章</el-button>
				<el-button type="danger" round size="medium" @click="cancel">取消发布</el-button>
			</div>
		</div>

	</div>
</template>

<script>
import BBSMarkdownEditor from "@/components/BBSMarkdownEditor";
import { Loading, Message, MessageBox } from "element-ui";
import { getArticleById, getArticleFileByArticleId } from "@/api/article";

export default {
	name: 'BBSArticleWrite',

	data() {
		return {
			publishVisible: false,
			labels: [],
			imageUrl: '',
			imageFile: {},
			hideUpload: false,
			articleForm: {
				title: '',
				summary: '',
				label: '',
				editor: {
					value: '',
					ref: '',//保存mavonEditor实例
					default_open: 'edit',
					toolbars: {
						bold: true, // 粗体
						italic: true, // 斜体
						header: true, // 标题
						underline: true, // 下划线
						strikethrough: true, // 中划线
						mark: true, // 标记
						superscript: true, // 上角标
						subscript: true, // 下角标
						quote: true, // 引用
						ol: true, // 有序列表
						ul: true, // 无序列表
						imagelink: true, // 图片链接
						code: true, // code
						fullscreen: true, // 全屏编辑
						readmodel: true, // 沉浸式阅读
						help: true, // 帮助
						undo: true, // 上一步
						redo: true, // 下一步
						trash: true, // 清空
						navigation: true, // 导航目录
						//subfield: true, // 单双栏模式
						preview: true, // 预览
						boxShadow: true,  //边框阴影
						ishljs: true, //代码高亮
						scrollStyle: false
					}
				}
			},
			rules: {
				summary: [
					{ required: true, message: '请输入摘要', trigger: 'blur' },
					{ max: 250, message: '不能大于80个字符', trigger: 'blur' }
				],
				/*community: [
					{required: false, message: '请选择要发布的社区', trigger: 'change'}
				],
				label: [
					{required: false, message: '请选择标签', trigger: 'change'}
				]*/
			},
			fileList: [],
			// 附件列表，每项 { fileId, fileName }，最多 5 个
			attachmentList: [],
		}
	},
	mounted() {
		this.getArticleLabel()
		if (this.$route.query.articleId) {
			this.getArticleByArticleId(this.$route.query.articleId)
		}
	},
	methods: {
		/*获取标签*/
		getArticleLabel() {
			this.getRequest('/common/getArticleLabel').then(resp => {
				if (resp) {
					this.labels = resp
				}
			})
		},

		publishShow() {
			if (!this.articleForm.title) {
				Message({
					message: '标题不能为空',
					type: 'warning',
					showClose: true,
					offset: 54
				})
				return
			}
			if (this.articleForm.title.length > 30) {
				Message({
					message: '标题最多30个字',
					type: 'warning',
					showClose: true,
					offset: 54
				})
				return
			}
			if (!this.articleForm.editor.ref.d_render) {
				Message({
					message: '内容不能为空',
					type: 'warning',
					showClose: true,
					offset: 54
				})
				return
			}

			this.publishVisible = true;
		},
		// 点击添加附件，触发隐藏的 file input
		triggerAttachmentSelect() {
			if (this.attachmentList.length >= 5) {
				Message({ message: '最多上传5个附件', type: 'warning', showClose: true, offset: 54 });
				return;
			}
			this.$refs.attachmentInput.value = '';
			this.$refs.attachmentInput.click();
		},
		// 禁止上传的附件类型（可执行文件等）
		isForbiddenAttachment(file) {
			const forbiddenExts = ['.exe', '.bat', '.cmd', '.com', '.scr', '.msi', '.vbs', '.js', '.jse', '.wsf', '.wsh', '.ps1'];
			const name = (file.name || '').toLowerCase();
			return forbiddenExts.some(ext => name.endsWith(ext));
		},
		// 选择附件后逐个上传
		onAttachmentSelected(e) {
			const files = e.target.files;
			if (!files || !files.length) return;
			const userStr = window.sessionStorage.getItem('user');
			if (!userStr) {
				Message({ message: '请先登录', type: 'warning', showClose: true, offset: 54 });
				return;
			}
			// 过滤掉禁止的类型（如 exe 等可执行文件）
			const forbidden = Array.from(files).filter(f => this.isForbiddenAttachment(f));
			if (forbidden.length) {
				Message({
					message: '不允许上传可执行文件（如 .exe、.bat、.cmd 等），请选择其他类型附件',
					type: 'warning',
					showClose: true,
					offset: 54
				});
			}
			const allowed = Array.from(files).filter(f => !this.isForbiddenAttachment(f));
			if (!allowed.length) return;
			const userId = JSON.parse(userStr).id;
			const remaining = 5 - this.attachmentList.length;
			const toUpload = allowed.slice(0, remaining);
			const that = this;
			toUpload.forEach(file => {
				const formData = new FormData();
				formData.append('userId', userId);
				formData.append('file', file);
				that.postRequest('/articleFile/upload', formData).then(resp => {
					if (resp && resp.code === 200 && resp.obj && resp.obj.fileId != null) {
						that.attachmentList.push({ fileId: resp.obj.fileId, fileName: file.name });
					} else {
						Message({ message: (resp && resp.message) || '上传失败', type: 'error', showClose: true, offset: 54 });
					}
				}).catch(() => {
					Message({ message: '上传失败', type: 'error', showClose: true, offset: 54 });
				});
			});
			e.target.value = '';
		},
		removeAttachment(index) {
			this.attachmentList.splice(index, 1);
		},
		handleChange(file, fileList) {
			// file指的就是选择的文件对象
			this.imageFile = file;
			this.hideUpload = fileList.length >= 1
		},
		handleHideRemove(file, fileList) {
			this.hideUpload = fileList.length >= 1
			this.hideUpload = false
			console.log("sc")
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

		publish(articleForm) {
			if (this.$route.query.articleId) {
				this.editArticle(articleForm)
			} else {
				this.saveArticle(articleForm)
			}
		},

		saveArticle(articleForm) {
			const that = this
			this.$refs[articleForm].validate((valid) => {
				if (valid) {
					/*let labels = this.articleForm.label.map(function (item) {
						return {id: item};
					});*/
					const article = {
						articleTitle: this.articleForm.title,
						articleContent: this.articleForm.editor.value,
						articleContentHtml: this.articleForm.editor.ref.d_render,
						articleSummary: this.articleForm.summary,
						articleTypeId: 0,
						articleCommunityId: 0,
						articleLabelId: this.articleForm.label = isNaN(parseInt(this.articleForm.label)) ? 0 : parseInt(this.articleForm.label),
						userId: JSON.parse(window.sessionStorage.getItem('user')).id,
						articleAuthor: JSON.parse(window.sessionStorage.getItem('user')).nickname,
						articleImage: '',
						files: this.attachmentList.map(a => a.fileId)
					}

					this.publishVisible = false;

					const loading = Loading.service({
						lock: true,
						text: '发布中，请稍后...'
					})
					// 发送请求给后端（如果有封面图片，先保存图片再保存文章数据）

					/*保存封面图片返回图片地址*/
					const file = this.imageFile.raw
					console.log("file", file)
					const userId = JSON.parse(window.sessionStorage.getItem('user')).id
					if (file != undefined) {
						const formData = new FormData()
						formData.append("userId", userId)
						formData.append("image", file)
						this.postRequest("/article/coverImg", formData).then(resp => {
							if (resp) {
								article.articleImage = resp
								this.postRequest('/article/publish', article).then(resp => {
									if (resp) {
										loading.close()
										that.$router.push('/forum').then(() => { window.location.reload() })
									}
								})
							}
						})
					} else {
						this.postRequest('/article/publish', article).then(resp => {
							if (resp) {
								loading.close()
								that.$router.push('/forum').then(() => { window.location.reload() })
							}
						})
					}
					console.log(article)

				}
			})
		},

		editArticle(articleForm) {
			const that = this
			this.$refs[articleForm].validate((valid) => {
				if (valid) {
					const article = {
						articleId: this.$route.query.articleId,
						articleTitle: this.articleForm.title,
						articleContent: this.articleForm.editor.value,
						articleContentHtml: this.articleForm.editor.ref.d_render,
						articleSummary: this.articleForm.summary,
						articleTypeId: 0,
						articleCommunityId: 0,
						articleLabelId: this.articleForm.label = isNaN(parseInt(this.articleForm.label)) ? 0 : parseInt(this.articleForm.label),
						articleImage: null,
						files: this.attachmentList.map(a => a.fileId)
					}

					this.publishVisible = false;

					const loading = Loading.service({
						lock: true,
						text: '发布中，请稍后...'
					})

					// 判断用户是否有图片

					if (this.hideUpload) { // 有封面
						// 判断是否修改过封面
						if (JSON.stringify(this.imageFile) !== "{}") { // 修改
							const file = this.imageFile.raw
							console.log("file", file)
							const userId = JSON.parse(window.sessionStorage.getItem('user')).id
							if (file != undefined) {
								const formData = new FormData()
								formData.append("userId", userId)
								formData.append("image", file)
								this.postRequest("/article/coverImg", formData).then(resp => {
									if (resp) {
										article.articleImage = resp
										this.submitEditArticle(article)
									}
								})
							}
						} else {
							this.submitEditArticle(article)
						}
					} else {
						this.submitEditArticle(article)
					}
					loading.close()
				}
			})
		},

		submitEditArticle(article) {
			this.postRequest('/article/editArticle', article).then(resp => {
				if (resp) {
					this.$router.push('/forum').then(() => { window.location.reload() })
				}
			})
		},

		cancel() {
			MessageBox({
				message: '文章将不会保存, 是否继续?',
				title: '提示',
				showCancelButton: true,
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning',
			}).then(() => {
				this.$router.push('/forum')
			})
		},

		getArticleByArticleId(articleId) {
			getArticleById(articleId).then(resp => {
				if (resp) {
					console.log(resp)
					// 封面图片回显
					this.fileList.push({ url: resp.articleImage })
					this.hideUpload = true

					this.articleForm.title = resp.articleTitle
					this.articleForm.editor.value = resp.articleContent
					this.articleForm.summary = resp.articleSummary
					this.articleForm.label = resp.articleLabelId

					// 编辑时加载文章原有附件列表并回显
					this.loadArticleFiles(articleId)
				}
			})
		},
		// 根据文章ID获取附件列表并回显（与 BBSArticleDetails 一致）
		loadArticleFiles(articleId) {
			getArticleFileByArticleId(articleId).then(resp => {
				if (resp) {
					const list = Array.isArray(resp) ? resp : (resp.data || [])
					// 统一为 { fileId, fileName } 格式，与添加附件后的样式一致
					this.attachmentList = list.map(item => ({
						fileId: item.fileId != null ? item.fileId : item.id,
						fileName: item.fileName || item.name || '附件'
					}))
				}
			}).catch(() => {
				this.attachmentList = []
			})
		},

	},
	components: { BBSMarkdownEditor },
	//组件内的守卫 调整body的背景色
	beforeRouteEnter(to, from, next) {
		window.document.body.style.backgroundColor = '#fff';
		next();
	},
	beforeRouteLeave(to, from, next) {
		window.document.body.style.backgroundColor = '#f5f5f5';
		next();
	}
}
</script>

<style>
#write {
	/* height: 100vh; */
	height: calc(100vh - 48px);
	display: flex;
	flex-direction: column;
}

/* 主内容区可上下滚动，仅保留最外层右侧滚动条 */
.me-write-scroll-wrap {
	flex: 1;
	min-height: 0;
	overflow-y: auto;
	overflow-x: hidden;
	/* padding-bottom: 56px; */
	max-height: calc(100% - 57px);
}

/* 内层不产生滚动条，只由最外层滚动 */
.me-write-scroll-wrap .me-write-box,
.me-write-scroll-wrap .me-write-box .el-main.me-write-main {
	overflow: visible !important;
}

.me-write-box {
	max-width: 1444px;
	margin: 8px auto 0;
	border: #efeded solid 2px;
}

.me-write-main {
	padding: 0;
}

.me-write-title {}

.me-write-input textarea {
	font-size: 32px;
	font-weight: 600;
	height: 10px;
	border: none;
}

.me-write-editor {
	min-height: 650px !important;
}

.me-header-left {
	margin-top: 10px;
}

.me-title img {
	max-height: 2.4rem;
	max-width: 100%;
}

.me-write-toolbar-fixed {
	position: fixed;
	width: 700px !important;
	top: 60px;
}

.v-note-op {
	box-shadow: none !important;
}

.auto-textarea-input,
.auto-textarea-block {
	font-size: 18px !important;
}

.footer {
	position: fixed;
	left: 0;
	bottom: 0;
	width: 100%;
	z-index: 1000;
	padding: 5px;
	background-color: white;
	border-top: #e8e8ed solid 1px;
	display: flex;
	align-items: center;
	justify-content: center;
	text-align: center;
}

.footer-btn {
	display: flex;
	flex-direction: row;
	justify-content: center;
	width: 1000px;

}

.publish-btn {

	background-color: hotpink;
}

.image-summary-row {
	display: flex;
	flex-direction: row;
	width: 100%;
}

.el-upload--picture-card {
	display: flex;
	flex-direction: row;
	justify-content: center;
	align-items: center;
	margin-right: 15px;
	height: 117px;
}

.el-upload-list--picture-card .el-upload-list__item {
	height: 117px;
}

.el-input-summary {
	width: 520px;
}

.hide .el-upload--picture-card {
	display: none;
}

.attachment-upload-section {
	margin-top: 16px;
	padding: 12px 0;
	border-top: 1px solid #ebeef5;
}

.attachment-upload-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 8px;
	padding-left: 5px;
}

.attachment-upload-title {
	font-size: 14px;
	color: #303133;
	font-weight: 500;
}

.attachment-list {
	list-style: none;
	padding: 0;
	margin: 8px 0 0;
}

.attachment-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 6px 10px;
	background: #f5f7fa;
	border-radius: 4px;
	margin-bottom: 6px;
	font-size: 13px;
}

.attachment-name {
	color: #606266;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	max-width: 400px;
}
</style>
