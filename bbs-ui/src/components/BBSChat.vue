<template>
    <div class="">
      <h5 class="font-size-15 mb-1 text-truncate">{{ friend.nickname }}</h5>
      <div class="chat-main" ref="scrollView">
        <simplebar style="height: 588px;">
          <div class="" ref="messageList">
            <div v-if="history.loading" class="history-loading">
              <img src="../assets/goeasy/pending.gif" />
            </div>
            <div
              v-else
              :class="history.allLoaded ? 'history-loaded' : 'load'"
              @click="loadHistoryMessage(false)"
            >
              {{
                history.allLoaded ? "已经没有更多的历史消息" : "获取历史消息"
              }}
            </div>
            <div v-for="(message, index) in history.messages" :key="index">
              <div class="time-tips">
                {{ renderMessageDate(message, index) }}
              </div>
              <div class="message-recalled" v-if="message.recalled">
                <div v-if="message.senderId !== currentUser.id">
                  {{ friend.nickname }}撤回了一条消息
                </div>
                <div v-else class="message-recalled-self">
                  <div>你撤回了一条消息</div>
                  <span
                    v-if="
                      message.type == 'text' &&
                        Date.now() - message.timestamp < 60 * 1000
                    "
                    @click="editRecalledMessage(message.payload.text)"
                    >重新编辑</span
                  >
                </div>
              </div>
              <div class="message-item" v-else>
                <div
                  class="message-item-checkbox"
                  v-if="
                    messageSelector.visible && message.status !== 'sending'
                  "
                >
                  <input
                    class="input-checkbox"
                    type="checkbox"
                    :value="message.messageId"
                    v-model="messageSelector.ids"
                    @click="selectMessages"
                  />
                </div>
                <div
                  class="message-item-content"
                  :class="{ self: message.senderId == currentUser.id }"
                >
                  <div class="sender-info">
                    <div
                      class="avatar-xs mr-3"
                      style="width:44px;height:44px"
                    >
                      <span
                        class="avatar-title rounded-circle bg-light text-body"
                        v-if="currentUser.id == message.senderId"
                        >{{ currentUser.nickname.substr(-2, 2) }}</span
                      >
                      <span
                        class="avatar-title rounded-circle bg-light text-body"
                        v-else
                        >{{ friend.nickname.substr(-2, 2) }}
                      </span>
                    </div>
                  </div>
                  <div
                    class="message-content"
                    @click.right="showActionPopup(message)"
                  >
                    <div class="message-payload">
                      <div
                        class="pending"
                        v-if="message.status == 'sending'"
                      ></div>
                      <div
                        class="send-fail"
                        v-if="message.status == 'fail'"
                      ></div>
                      <div
                        v-if="message.type == 'text'"
                        class="content-text"
                        v-html="message.payload.text"
                      ></div>
                    </div>
                    <div
                      v-if="currentUser.id == message.senderId"
                      :class="
                        message.read ? 'message-read' : 'message-unread'
                      "
                    >
                      <div v-if="message.senderId == currentUser.id">
                        {{ message.read ? "已读" : "未读" }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </simplebar>
      </div>
  

      <div class="col-md-12">
        <div class="action-delete" v-if="messageSelector.visible">
          <i class="ri-delete-bin-6-line" @click="deleteMultipleMessages"></i>
          <div>删除</div>
        </div>

        <div v-else>
          <div class="p-3 chat-input-section row chat-bottom">
            <div class="bottom-input">
              <div class="position-relative">
                <el-input
                  ref="input"
                  @keyup.enter="sendTextMessage"
                  v-model="text"
                  maxlength="1400"
                  autocomplete="off"
                  placeholder="请输入消息~~~~"
                  class="form-control chat-input"
                ></el-input>
              </div>
            </div>
            <div class="bottom-button">
              <el-button type="primary" plain class="send-button btn btn-primary chat-send w-md waves-effect waves-light"
                @click="sendTextMessage"
              >
                <span class="d-none d-sm-inline-block mr-2">发送</span>
                <i class="mdi mdi-send"></i>
              </el-button>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 消息删除撤回弹窗 -->
      <div
        class="action-popup"
        v-if="actionPopup.visible"
        @click="actionPopup.visible = false"
      >
        <div class="action-popup-main">
          <div class="action-item" @click="deleteSingleMessage">删除</div>
          <div
            class="action-item"
            v-if="actionPopup.recallable"
            @click="recallMessage"
          >
            撤回
          </div>
          <div class="action-item" @click="showCheckBox">多选</div>
          <div class="action-item" @click="actionPopup.visible = false">取消</div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import simplebar from "simplebar-vue";
  import { formatDate } from "@/utils/utils.js";
  
  export default {
    name: "BBSChat",
    components: { simplebar },
    data() {
      return {
        currentUser: null,
        friend: null,
  
        to: {}, //用于创建消息时传入
  
        history: {
          messages: [],
          allLoaded: false,
          loading: true,
        },
  
        text: "",
  
        // 展示消息删除弹出框
        actionPopup: {
          visible: false,
          message: null,
          recallable: false,
        },
        // 消息选择
        messageSelector: {
          visible: false,
          ids: [],
        },
      };
    },
    created() {
      this.friend = {
        id: this.$route.params.id,
        nickname: this.$route.query.nickname,
      };
      this.currentUser = JSON.parse(window.sessionStorage.getItem('user'));
      this.to = {
        type: this.GoEasy.IM_SCENE.PRIVATE,
        id: this.friend.id,
        data: { nickname: this.friend.nickname },
      };
  
      this.loadHistoryMessage(true);
  
      this.goEasy.im.on(
        this.GoEasy.IM_EVENT.PRIVATE_MESSAGE_RECEIVED,
        this.onReceivedPrivateMessage
      );
    },
    beforeDestroy() {
      this.goEasy.im.off(
        this.GoEasy.IM_EVENT.PRIVATE_MESSAGE_RECEIVED,
        this.onReceivedPrivateMessage
      );
    },
    methods: {
      formatDate,
      onReceivedPrivateMessage(message) {
        if (message.senderId == this.friend.id) {
          this.history.messages.push(message);
          this.markPrivateMessageAsRead();
        }
        this.scrollToBottom();
      },
  
      sendTextMessage() {
        if (!this.text.trim()) {
          console.log("输入为空");
          return;
        }
        this.goEasy.im.createTextMessage({
          text: this.text,
          to: this.to,
          onSuccess: (message) => {
            this.sendMessage(message);
            this.text = "";
          },
          onFailed: (err) => {
            console.log("创建消息err:", err);
          },
        });
      },
  
      sendMessage(message) {
        this.history.messages.push(message);
        this.scrollToBottom();
        this.goEasy.im.sendMessage({
          message: message,
          onSuccess: (message) => {
            console.log("发送成功", message);
          },
          onFailed: function(error) {
            if (error.code == 507) {
              alert("发送语音/图片/视频/文件失败，没有配置OSS存储");
              console.log(
                "发送语音/图片/视频/文件失败，没有配置OSS存储，详情参考：https://www.goeasy.io/cn/docs/goeasy-2.x/im/message/media/send-media-message.html"
              );
            } else {
              console.log("发送失败:", error);
            }
          },
        });
      },
      showActionPopup(message) {
        const MAX_RECALLABLE_TIME = 3 * 60 * 1000; //3分钟以内的消息才可以撤回
        this.messageSelector.ids = [message.messageId];
        if (
          Date.now() - message.timestamp < MAX_RECALLABLE_TIME &&
          message.senderId == this.currentUser.id &&
          message.status == "success"
        ) {
          this.actionPopup.recallable = true;
        } else {
          this.actionPopup.recallable = false;
        }
        this.actionPopup.visible = true;
      },
      deleteSingleMessage() {
        this.actionPopup.visible = false;
        this.deleteMessage();
      },
      deleteMultipleMessages() {
        if (this.messageSelector.ids.length > 0) {
          this.messageSelector.visible = false;
          this.deleteMessage();
        }
      },
      deleteMessage() {
        let conf = confirm("确认删除？");
        if (conf == true) {
          let selectedMessages = [];
          this.history.messages.forEach((message) => {
            if (this.messageSelector.ids.includes(message.messageId)) {
              selectedMessages.push(message);
            }
          });
          this.goEasy.im.deleteMessage({
            messages: selectedMessages,
            onSuccess: () => {
              selectedMessages.forEach((message) => {
                let index = this.history.messages.indexOf(message);
                if (index > -1) {
                  this.history.messages.splice(index, 1);
                }
              });
              this.messageSelector.ids = [];
            },
            onFailed: (error) => {
              console.log("error:", error);
            },
          });
        } else {
          this.messageSelector.ids = [];
        }
      },
      recallMessage() {
        let selectedMessages = [];
        this.history.messages.forEach((message) => {
          if (this.messageSelector.ids.includes(message.messageId)) {
            selectedMessages.push(message);
          }
        });
        this.actionPopup.visible = false;
        this.goEasy.im.recallMessage({
          messages: selectedMessages,
          onSuccess: () => {
            console.log("撤回成功");
          },
          onFailed: (error) => {
            console.log("撤回失败,error:", error);
          },
        });
      },
      editRecalledMessage(text) {
        this.text = text;
      },
  
      showCheckBox() {
        this.messageSelector.ids = [];
        this.messageSelector.visible = true;
        this.actionPopup.visible = false;
      },
      selectMessages(e) {
        if (e.target.checked) {
          this.messageSelector.ids.push(e.target.value);
        } else {
          let index = this.messageSelector.ids.indexOf(e.target.value);
          if (index > -1) {
            this.messageSelector.ids.splice(index, 1);
          }
        }
      },
      loadHistoryMessage(scrollToBottom) {
        this.history.loading = true;
        //历史消息
        let lastMessageTimeStamp = null;
        let lastMessage = this.history.messages[0];
        if (lastMessage) {
          lastMessageTimeStamp = lastMessage.timestamp;
        }
        this.goEasy.im.history({
          userId: this.friend.id,
          lastTimestamp: lastMessageTimeStamp,
          limit: 10,
          onSuccess: (result) => {
            this.history.loading = false;
            let messages = result.content;
            console.log("获取历史消息：", messages);
            if (messages.length == 0) {
              this.history.allLoaded = true;
            } else {
              if (lastMessageTimeStamp) {
                this.history.messages = messages.concat(this.history.messages);
              } else {
                this.history.messages = messages;
              }
              if (messages.length < 10) {
                this.history.allLoaded = true;
              }
              if (scrollToBottom) {
                this.scrollToBottom();
                //收到的消息设置为已读
                this.markPrivateMessageAsRead();
              }
            }
          },
          onFailed: (error) => {
            //获取失败
            this.history.loading = false;
            console.log(
              "获取历史消息失败, code:" +
                error.code +
                ",错误信息:" +
                error.content
            );
          },
        });
      },
      markPrivateMessageAsRead() {
        this.goEasy.im.markMessageAsRead({
          id: this.to.id,
          type: this.to.type,
          onSuccess: function() {
            console.log("标记私聊已读成功");
          },
          onFailed: function(error) {
            console.log("标记私聊已读失败", error);
          },
        });
      },
      scrollToBottom() {
        this.$nextTick(() => {
          this.$refs.scrollView.scrollTop = this.$refs.messageList.scrollHeight;
        });
      },
      renderMessageDate(message, index) {
        if (index == 0) {
          return this.formatDate(message.timestamp);
        } else {
          if (
            message.timestamp - this.history.messages[index - 1].timestamp >
            5 * 60 * 1000
          ) {
            return this.formatDate(message.timestamp);
          }
        }
        return "";
      },
    },
  };
  </script>
  
  <style scoped>
  .chat-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    position: relative;
  }
  
  .chat-title {
    height: 40px;
    padding: 0 15px;
    display: flex;
    align-items: center;
    font-size: 18px;
  }
  
  .chat-avatar {
    width: 35px;
    height: 35px;
  }
  
  .chat-name {
    font-size: 15px;
    margin-left: 10px;
    white-space: nowrap;
  }
  
  .chat-main {
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    flex: 1;
    scrollbar-width: thin;
  }
  
  .chat-main::-webkit-scrollbar {
    width: 0;
  }
  
  .chat-main .history-loaded {
    text-align: center;
    font-size: 12px;
    color: #cccccc;
    line-height: 20px;
  }
  
  .chat-main .load {
    text-align: center;
    font-size: 12px;
    color: #d02129;
    line-height: 20px;
    cursor: pointer;
  }
  
  .history-loading {
    width: 100%;
    text-align: center;
  }
  
  .time-tips {
    color: #999;
    text-align: center;
    font-size: 12px;
  }
  
  .message-item {
    display: flex;
  }
  
  .message-item-checkbox {
    height: 50px;
    margin-right: 15px;
    display: flex;
    align-items: center;
  }
  
  .input-checkbox {
    position: relative;
  }
  
  .message-item-checkbox input[type="checkbox"]::before,
  .message-item-checkbox input[type="checkbox"]:checked::before {
    content: "";
    position: absolute;
    top: -3px;
    left: -3px;
    background: #ffffff;
    width: 18px;
    height: 18px;
    border: 1px solid #cccccc;
    border-radius: 50%;
  }
  
  .message-item-checkbox input[type="checkbox"]:checked::before {
    content: "\2713";
    background-color: #d02129;
    width: 18px;
    color: #ffffff;
    text-align: center;
    font-weight: bold;
  }
  
  .message-item-content {
    flex: 1;
    margin: 5px 0;
    overflow: hidden;
    display: flex;
  }
  
  .sender-info {
    margin: 0 5px;
  }
  
  .sender-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }
  
  .message-content {
    max-width: calc(100% - 100px);
  }
  
  .message-payload {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    align-items: center;
  }
  
  .pending {
    background: url("../assets/goeasy/pending.gif") no-repeat center;
    background-size: 13px;
    width: 15px;
    height: 15px;
  }
  
  .send-fail {
    background: url("../assets/goeasy/failed.png") no-repeat center;
    background-size: 15px;
    width: 18px;
    height: 18px;
    margin-right: 3px;
  }
  
  .message-read {
    color: gray;
    font-size: 12px;
    text-align: end;
    height: 16px;
  }
  
  .message-unread {
    color: #d02129;
    font-size: 12px;
    text-align: end;
    height: 16px;
  }
  
  .content-text {
    display: flex;
    align-items: center;
    text-align: left;
    background: #eeeeee;
    font-size: 14px;
    font-weight: 500;
    padding: 6px 8px;
    margin: 3px 0;
    line-height: 25px;
    white-space: pre-line;
    overflow-wrap: anywhere;
    border-radius: 8px;
    word-break: break-all;
  }
  
  .content-image {
    display: block;
    cursor: pointer;
  }
  
  .content-image img {
    height: 100%;
  }
  
  .content-audio {
    -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  }
  
  .content-audio .audio-facade {
    min-width: 12px;
    background: #eeeeee;
    border-radius: 7px;
    display: flex;
    font-size: 14px;
    padding: 8px;
    margin: 5px 0;
    line-height: 25px;
    cursor: pointer;
  }
  
  .content-audio .audio-facade-bg {
    background: url("../assets/goeasy/voice.png") no-repeat center;
    background-size: 15px;
    width: 20px;
  }
  
  .content-audio .audio-facade-bg.play-icon {
    background: url("../assets/goeasy/play.gif") no-repeat center;
    background-size: 20px;
  }
  
  .content-order {
    border-radius: 5px;
    border: 1px solid #eeeeee;
    padding: 8px;
    display: flex;
    flex-direction: column;
  }
  
  .content-order .order-id {
    font-size: 12px;
    color: #666666;
    margin-bottom: 5px;
  }
  
  .content-order .order-body {
    display: flex;
    font-size: 13px;
    padding: 5px;
  }
  
  .content-order .order-img {
    width: 70px;
    height: 70px;
    border-radius: 5px;
  }
  
  .content-order .order-name {
    margin-left: 10px;
    width: 135px;
    color: #606164;
  }
  
  .content-order .order-count {
    font-size: 12px;
    color: #666666;
    flex: 1;
  }
  
  .content-file {
    width: 240px;
    height: 65px;
    font-size: 15px;
    background: #ffffff;
    border: 1px solid #eeeeee;
    display: flex;
    align-items: center;
    padding: 10px;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .content-file:hover {
    background: #f1f1f1;
  }
  
  .file-info {
    width: 194px;
    text-align: left;
  }
  
  .file-name {
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    word-break: break-all;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  
  .file-size {
    font-size: 12px;
    color: #ccc;
  }
  
  .file-img {
    width: 50px;
    height: 50px;
  }
  
  .message-item .self {
    overflow: hidden;
    display: flex;
    justify-content: flex-start;
    flex-direction: row-reverse;
  }
  
  .message-item .self .audio-facade {
    flex-direction: row-reverse;
  }
  
  .message-item .self .audio-facade-bg {
    background: url("../assets/goeasy/voice.png") no-repeat center;
    background-size: 15px;
    width: 20px;
    -moz-transform: rotate(180deg);
    -webkit-transform: rotate(180deg);
    -o-transform: rotate(180deg);
    transform: rotate(180deg);
  }
  
  .message-item .self .play-icon {
    background: url("../assets/goeasy/play.gif") no-repeat center;
    background-size: 20px;
    -moz-transform: rotate(180deg);
    -webkit-transform: rotate(180deg);
    -o-transform: rotate(180deg);
    transform: rotate(180deg);
  }
  
  .message-recalled {
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 28px;
    font-size: 13px;
    text-align: center;
    color: grey;
    margin-top: 10px;
  }
  
  .message-recalled-self {
    display: flex;
  }
  
  .message-recalled-self span {
    margin-left: 5px;
    color: #d02129;
    cursor: pointer;
  }
  
  .chat-footer {
    border-top: 1px solid #dcdfe6;
    width: 100%;
    height: 180px;
    background: #ffffff;
  }
  
  .action-delete {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: #ffffff;
  }
  
  

  .chat-bottom {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    margin-top: 20px;
  }
  .bottom-input {
    width: 80%;
  }
  .bottom-button {
    width: 20%;
    display: flex;
    justify-content: flex-end;
  }
  .bottom-button .send-button{
    width: 80%;
  }

  .delete-btn {
    width: 25px;
    height: 25px;
    padding: 10px;
    background: #f5f5f5;
    border-radius: 50%;
    cursor: pointer;
    margin-bottom: 10px;
  }
  
  .action-box {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  
  .action-bar {
    display: flex;
    flex-direction: row;
    padding: 0 10px;
  }
  
  .action-bar .action-item {
    text-align: left;
    padding: 10px 0;
    position: relative;
  }
  
  .action-bar .action-item .iconfont {
    font-size: 22px;
    margin: 0 10px;
    z-index: 3;
    color: #606266;
    cursor: pointer;
  }
  
  .action-bar .action-item .iconfont:focus {
    outline: none;
  }
  
  .action-bar .action-item .iconfont:hover {
    color: #d02129;
  }
  
  .emoji-box {
    width: 210px;
    position: absolute;
    top: -111px;
    left: -11px;
    z-index: 2007;
    background: #fff;
    border: 1px solid #ebeef5;
    padding: 12px;
    text-align: justify;
    font-size: 14px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    word-break: break-all;
    border-radius: 4px;
  }
  
  .emoji-item {
    width: 38px;
    height: 38px;
    margin: 0 2px;
  }
  
  .input-box {
    padding: 0 10px;
    flex: 1;
  }
  
  .input-content {
    border: none;
    resize: none;
    display: block;
    padding: 5px 15px;
    box-sizing: border-box;
    width: 100%;
    color: #606266;
    outline: none;
    background: #ffffff;
    word-break: break-all;
  }
  
  .send-box {
    padding: 5px 10px;
    text-align: right;
  }
  
  /* .send-button {
    width: 70px;
    height: 30px;
    font-size: 15px;
    border: 1px solid #d02129;
    background-color: #ffffff;
    color: #d02129;
    border-radius: 5px;
  } */
  
  .action-popup {
    width: 100%;
    height: 100%;
    position: absolute;
    top: -10px;
    background: rgba(51, 51, 51, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .action-popup-main {
    width: 150px;
    height: 120px;
    background: #ffffff;
    z-index: 100;
    border-radius: 10px;
    overflow: hidden;
  }
  
  .action-popup-main .action-item {
    text-align: center;
    line-height: 40px;
    font-size: 15px;
    color: #262628;
    border-bottom: 1px solid #efefef;
    cursor: pointer;
  }
  
  .image-preview {
    max-width: 750px;
    max-height: 500px;
    background: rgba(0, 0, 0, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    position: fixed;
    margin: auto;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 9998;
  }
  
  .image-preview img {
    max-width: 750px;
    max-height: 500px;
  }
  
  .image-preview .close {
    font-size: 50px;
    line-height: 24px;
    cursor: pointer;
    color: #ffffff;
    position: absolute;
    top: 10px;
    right: 5px;
    z-index: 1002;
  }
  
  .order-box {
    width: 848px;
    position: absolute;
    left: -281px;
    right: 0;
    top: 0;
    bottom: 0;
    z-index: 2007;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(33, 33, 33, 0.7);
  }
  
  .order-list {
    width: 300px;
    background: #f1f1f1;
    border-radius: 5px;
  }
  
  .order-list .title {
    font-weight: 600;
    font-size: 15px;
    color: #000000;
    margin-left: 10px;
    margin-right: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .order-list .title span {
    font-size: 28px;
    font-weight: 400;
    cursor: pointer;
  }
  
  .order-list .order-item {
    padding: 10px;
    background: #ffffff;
    margin: 10px;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .order-list .order-id {
    font-size: 12px;
    color: #666666;
    margin-bottom: 5px;
  }
  
  .order-list .order-body {
    display: flex;
    font-size: 13px;
    justify-content: space-between;
  }
  
  .order-list .order-img {
    width: 50px;
    height: 50px;
    border-radius: 5px;
  }
  
  .order-list .order-name {
    width: 160px;
  }
  
  .order-list .order-count {
    font-size: 12px;
    color: #666666;
    flex: 1;
  }
  </style>
  