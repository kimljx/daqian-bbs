<template>
	<div class="tabs-wrapper">
        <div class="tabs">
            <el-tabs v-model="activeName">
                <el-tab-pane label="粉丝列表" name="fans">
                    <div class="portrait-wrapper" v-if="fans" v-for="(item,index) in fans" :key="index">
                        <div class="portrait">
                            <el-avatar :size="60" :src="formatPortrait(item.portrait)">
                                <img :src="formatPortrait(item.portrait)"/>
                            </el-avatar>
                            <span class="nickname">{{ item.name }}</span>
                        </div>
                        <div class="time">{{ item.createTime }}</div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="关注列表" name="love">
                    <div class="portrait-wrapper" v-if="love" v-for="(item,index) in love" :key="index">
                        <div class="portrait">
                            <el-avatar :size="60" :src="formatPortrait(item.portrait)">
                                <img :src="formatPortrait(item.portrait)"/>
                            </el-avatar>
                            <span class="nickname">{{ item.name }}</span>
                        </div>
                        <div class="time">{{ item.createTime }}</div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
	</div>
</template>

<script>

export default {
	name: "BBSFans",
	components: {},
	data() {
		return {
            activeName:'fans',
            fans:[],
            love:[],
		}
	},
    mounted(){
        this.getFansList()
        this.getLoveList()
    },
    methods:{
        formatPortrait(portrait){
            return portrait ? `${process.env.VUE_APP_BBS_API}${portrait}` : require('../assets/portrait.png')
        },
        // 获取粉丝列表
        getFansList(){
            const userId = JSON.parse(window.sessionStorage.getItem('user')).id
            this.getRequest("/fans/getFans?userId="+userId).then(resp => {
                if(resp){
                    this.fans = resp
                }
            })
        },
        // 获取关注列表
        getLoveList(){
            const userId = JSON.parse(window.sessionStorage.getItem('user')).id
            this.getRequest("/fans/getAttentions?userId="+userId).then(resp => {
                if(resp){
                    this.love = resp
                }
            })
        },
    }
}
</script>

<style scoped>
.tabs-wrapper {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: center;
	align-items: center;
}
.tabs{
    width: 58%;
}
.portrait-wrapper {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 8px;
    border: 1px whitesmoke solid;
    background-color: rgb(254, 253, 253);
    border-radius: 6px;
}
.portrait-wrapper:hover{
    background-color: whitesmoke;
}
.portrait{
    display: flex;
    flex-direction: row;
    align-items: center;
}
.nickname{
    margin-left: 14px;
    font-weight: 500;
}
.time{
    font-size: 14px;
    color: rgb(154, 154, 154);
}

</style>