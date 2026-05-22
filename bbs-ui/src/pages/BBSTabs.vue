<template>
	<div class="tabs">
		<div class="tabs-box">
			<el-tabs :tab-position="tabPosition" class="el-tabs-box" v-model="activeName">
				<el-tab-pane class="el-tab-pan" name="1" label="信息设置">
					<BBSUserinfo></BBSUserinfo>
				</el-tab-pane>
				<el-tab-pane class="el-tab-pan" name="2" label="密码修改">
					<BBSModPassWord></BBSModPassWord>
				</el-tab-pane>
			</el-tabs>
		</div>
	</div>
</template>

<script>
import BBSUserinfo from "@/components/BBSUserinfo";
import BBSModPassWord from "./BBSModPassWord.vue";
export default {
	name: "BBSTabs",
	components: { BBSUserinfo, BBSModPassWord },
	data() {
		return {
			tabPosition:'left',
			activeName: this.$route.params.index
		}
	}
}
</script>

<style scoped>
.tabs {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: center;
	align-items: center;
}

.tabs-box {
	display: flex;
	flex-direction: row;
	width: 1200px;
	/*background-color: yellow;*/
	margin-top: 20px;
}
.el-tabs-box{
	height: 100%;
	width: 100%;
}


</style>