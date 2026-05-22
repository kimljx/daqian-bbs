// 引入 axios
import axios from "axios";


export const getArticleById =(articleId)=>{
    return axios({
        method:'post',
        url: `${process.env.VUE_APP_BBS_API}/common/article/getArticleById/articleId/${articleId}`,
    })
}

export const getUserinfoById =(id)=>{
    return axios({
        method:'post',
        url:`${process.env.VUE_APP_BBS_API}/common/user/getUserinfoById/${id}`
    })
}

// 根据文章ID获取文章附件列表（POST，传参方式同 getCommentReply）
export const getArticleFileByArticleId = (articleId) => {
    return axios({
        method: 'post',
        url: `${process.env.VUE_APP_BBS_API}/common/getArticleFileByArticleId/${articleId}`,
    })
}
