// 引入 axios
import axios from "axios";

export const getCommentReply =(articleId)=>{
    return axios({
        method:'post',
        url:`${process.env.VUE_APP_BBS_API}/common/comment/getCommentReply/${articleId}`,
    })
}
