<template>
	<div class="message-wrapper">
        <div class="message" style="min-height: 544px;">
            <div class="mid">
                <div class="">
                    <simplebar style="max-height: 530px" id="scrollElement">
                    <div class="list-unstyled chat-list">
                        <div v-if="conversations.length">
                        <router-link tag="div" replace v-for="(conversation, key) in conversations" :key="key" :to="chatLocation(conversation)">
                            <div class="conversation" @contextmenu.prevent.stop="(e) => showRightClickMenu(e, conversation)">
                                <div class="conversation-left">
                                    {{ conversation.data.nickname.substr(-2, 2) }}
                                </div>
                                <div class="conversion-right">
                                    <div class="row conversation-name-time">
                                        <div class="conversation-name">
                                            {{ conversation.data.nickname }}
                                        </div>
                                        <div class="conversation-time">
                                            <div>
                                                {{ formatDate(conversation.lastMessage.timestamp) }}
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="conversation-content" v-if="conversation.lastMessage.recalled">
                                            <div v-if="conversation.type == 'private'">
                                                {{conversation.lastMessage.senderId ==currentUser.id? "你": `"${conversation.data.nickname}"`}}撤回了一条消息
                                            </div>
                                            </div>
                                            <div class="conversation-content" v-else>
                                            <div class="unread-text" style="color:#d02129"
                                                v-if="
                                                conversation.lastMessage.read == false && conversation.lastMessage.senderId ==currentUser.id">
                                                [未读]
                                            </div>
                                            <div v-if="conversation.type == 'private'">
                                                {{conversation.lastMessage.senderId == currentUser.id? "我": conversation.data.nickname}}:
                                            </div>
                                            <div v-else>
                                                {{conversation.lastMessage.senderId == currentUser.id? "我": conversation.lastMessage.senderData.nickname}}:
                                            </div>
                                            <span class="text" v-if="conversation.lastMessage.type == 'text'">
                                                {{ conversation.lastMessage.payload.text }}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </router-link>
                        </div>
                        <div v-else class="no-conversation">- 当前没有会话 -</div>
                    </div>

                    <div v-if="rightClickMenu.visible"
                        :style="{
                        left: rightClickMenu.x + 'px',
                        top: rightClickMenu.y + 'px',
                        }"
                        class="action-box"
                    >
                        <div class="action-item" @click="topConversation">
                        {{ rightClickMenu.conversation.top ? "取消置顶" : "置顶" }}
                        </div>
                        <div class="action-item" @click="deleteConversation">删除聊天</div>
                    </div>
                    </simplebar>
                </div>

            </div>
            <div class="right">
                <router-view :key="$route.params.id" />
            </div>
        </div>
	</div>
</template>

<script>
import { formatDate } from "@/utils/utils.js";
import simplebar from "simplebar-vue";

export default {
	name: "BBSMessage",
	components:{ simplebar },
	data(){
		return{
            hatData: [],
            currentUser: null,
            unreadAmount: null,
            conversations: [],
            // Conversation右键菜单
            rightClickMenu: {
                conversation: null,
                visible: false,
                x: null,
                y: null,
            },
		}
	},
	mounted() {
        this.currentUser = JSON.parse(window.sessionStorage.getItem('user'));
	},
  created() {
      this.currentUser = JSON.parse(window.sessionStorage.getItem('user'));
      if (this.goEasy.getConnectionStatus() == "disconnected") {
      this.connectGoEasy(); //连接goeasy
      }
      this.goEasy.im.on(
          this.GoEasy.IM_EVENT.CONVERSATIONS_UPDATED,
          this.setUnreadNumber
      );

      //隐藏Conversation右键菜单
      document.addEventListener("click", () => {
          this.hideRightClickMenu();
      });
      this.listenConversationUpdate(); //监听会话列表变化
      this.loadConversations(); //加载会话列表
  },
  beforeDestroy() {
      this.goEasy.im.off(
      this.GoEasy.IM_EVENT.CONVERSATIONS_UPDATED,
      this.renderConversations
      );
  },
	methods:{
        connectGoEasy() {
            this.goEasy.connect({
                id: this.currentUser.id,
                data: { nickname: this.currentUser.nickname },
                onSuccess: function() {
                //连接成功
                console.log("GoEasy 连接成功."); //连接成功
                },
                onFailed: function(error) {
                //连接失败
                console.log(
                    "错误连接 GoEasy, 错误码:" +
                    error.code +
                    ",错误信息:" +
                    error.content
                );
                },
                onProgress: function(attempts) {
                //连接或自动重连中
                console.log("GoEasy 正在连接中...", attempts);
                },
            });
        },
        setUnreadNumber(content) {
            this.unreadAmount = content.unreadTotal;
        },
        formatDate,
        loadConversations() {
            this.goEasy.im.latestConversations({
                onSuccess: (result) => {
                let content = result.content;
                this.renderConversations(content);
                },
                onFailed: (error) => {
                console.log(
                    "获取最新会话列表失败, code:" +
                    error.code +
                    "content:" +
                    error.content
                );
                },
            });
        },
        listenConversationUpdate() {
            // 监听会话列表变化
            this.goEasy.im.on(
                this.GoEasy.IM_EVENT.CONVERSATIONS_UPDATED,
                this.renderConversations
            );
        },
        renderConversations(content) {
            this.conversations = content.conversations;
            console.log("---- conversations ----",this.conversations);
        },

        showRightClickMenu(e, conversation) {
            this.rightClickMenu.conversation = conversation;
            this.rightClickMenu.visible = true;
            this.rightClickMenu.x = e.pageX;
            this.rightClickMenu.y = e.pageY;
        },
        hideRightClickMenu() {
            this.rightClickMenu.visible = false;
        },
        topConversation() {
            let conversation = this.rightClickMenu.conversation;
            let description = conversation.top ? "取消置顶" : "置顶";
            this.goEasy.im.topConversation({
                conversation: conversation,
                top: !conversation.top,
                onSuccess: function() {
                console.log(description, "成功");
                },
                onFailed: function(error) {
                console.log(description, "失败：", error);
                },
            });
        },
        deleteConversation() {
            if (confirm("确认要删除这条会话吗？")) {
                let conversation = this.rightClickMenu.conversation;
                this.goEasy.im.removeConversation({
                conversation: conversation,
                onSuccess: function() {
                    console.log("删除会话成功");
                },
                onFailed: function(error) {
                    console.log(error);
                },
                });
            }
        },
        chatLocation(conversation) {
            let path = "/message/chat/" + conversation.userId;
            return {
                path: path,
                query: {
                nickname: conversation.data.nickname,
                },
            };
        },
	}
}
</script>

<style scoped>
.message-wrapper{
    /* background-color: red; */
    width: 100%;
    height: calc(100vh - 48px);
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: whitesmoke;
}
.message{
    width: 88%;
    height: 666px;
    background-color: white;
    padding: 20px;
    border-radius: 20px;
    /* background-color: yellow; */
    display: flex;
    flex-direction: row;
}
.left{
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 88px;
    /* background-color: rgb(238, 238, 132); */
}
.menu-item {
  color: #303133;
  cursor: pointer;
  height: 56px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  margin: 40px 0;
}
.message-icon{
    font-size: 30px;
    margin: 10px 0;
}
.mid{
    width: 388px;
    height: 100%;
    padding: 0 10px;
    border-right: 2px solid whitesmoke;
    /* background-color: rgb(192, 233, 192); */
}

.right{
    width: 100%;
    height: 100%;
    padding: 0 20px;
    /* background-color: rgb(236, 182, 236); */
}


.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  cursor: pointer;
}

.user-avatar:hover + .user-profile {
  display: block;
}

.user-profile {
  display: none;
  color: #ffffff;
  position: absolute;
  top: 0;
  left: 70px;
  width: 250px;
  height: 200px;
  background: #ffffff;
  z-index: 999;
}

.user-profile-main {
  border: 1px solid #ebeef5;
  background-color: #fff;
  color: #303133;
  border-radius: 4px;
}

.user-profile-header {
  padding: 18px 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 15px;
  font-weight: bold;
}

.user-profile-header img {
  width: 45px;
  height: 45px;
}

.user-profile-info {
  display: flex;
  padding: 10px 20px;
  font-size: 14px;
  color: #666666;
  line-height: 28px;
}

.user-profile-info-title {
  width: 70px;
}


.conversation{
    display: flex;
    flex-direction: row;
    align-items: center;
    border-radius: 8px;
    /* background-color: red; */
    margin-bottom: 8px;
}
.conversation-left{
    background-color: whitesmoke;
    border-radius: 50%;
    border: 2px solid green;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    width: 44px;
    height: 44px;
}
.conversation-right{

}

.menu-unread {
  position: absolute;
  top: -5px;
  right: 5px;
  width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 50%;
  background-color: #5664d2;
  color: #ffffff;
}

.router-link-active i {
  color: #5664d2 !important;
}

.iconfont {
  padding: 15px;
  font-size: 28px;
  color: #606266;
  cursor: pointer;
}

.iconfont:active {
  color: #5664d2;
}


.conversations {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  color: #333333;
}

.conversation-list {
  width: 220px;
}

.conversation-list-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: white;
  border-right: #dbd6d6 1px solid;
}

.conversation-list-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.conversation-list-content::-webkit-scrollbar {
  display: none;
}

.no-conversation {
  text-align: center;
  color: #666666;
}

.conversation {
  display: flex;
  padding: 10px 5px;
  cursor: pointer;
}

.unread-count {
  position: absolute;
  top: -10px;
  left: 30px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  color: white;
  background: #d02129;
}

.unread-count .unread {
  display: block;
  font-size: 12px;
  text-align: center;
  line-height: 18px;
}

.conversation-message {
  flex: 1;
  padding-left: 5px;
  display: flex;
  width: 160px;
  flex-direction: column;
  justify-content: space-around;
}

.conversation-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-align: right;
}

.conversation-name-time {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.conversation-name {
  font-size: 12px;
  font-weight: 500;
  margin-left: 10px;
}

.conversation-time {
  color: #b9b9b9;
  display: flex;
  flex-direction: column;
}

.conversation-bottom {
  display: flex;
  color: #666666;
}

.conversation-content {
  display: flex;
  width: 160px;
  color: #b3b3b3;
  margin-left: 10px;
}

.conversation-content .text {
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  white-space: nowrap;
  word-break: break-all;
}

.conversation-bottom .unread-text {
  color: #d02129;
  width: 35px !important;
}

.conversation .avatar {
  width: 40px;
  height: 40px;
  position: relative;
}

.conversation .avatar img {
  width: 100%;
  border-radius: 10%;
}

.router-link-active {
  background: #eeeeee;
}

.action-box {
  width: 100px;
  height: 60px;
  background: #ffffff;
  border: 1px solid #cccccc;
  position: fixed;
  z-index: 100;
  border-radius: 5px;
}

.action-box .action-item {
  padding-left: 15px;
  line-height: 30px;
  font-size: 13px;
  color: #262628;
  cursor: pointer;
}

.action-box .action-item:hover {
  background: #dddddd;
}

.chat {
  flex: 1;
  display: flex;
}

</style>