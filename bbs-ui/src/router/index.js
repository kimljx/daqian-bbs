import VueRouter from 'vue-router';
import BBSCommunity from "@/pages/BBSCommunity";
import BBSLogin from "@/pages/BBSLogin";
import BBSArticleWrite from "@/pages/BBSArticleWrite";
import BBSUserinfo from "@/components/BBSUserinfo";
import BBSForum from "@/pages/BBSForum";
import BBSIndex from "@/pages/BBSIndex";
import BBSArticleDetails from "@/pages/BBSArticleDetails";
import BBSTabs from "@/pages/BBSTabs";
import BBSCollection from "@/pages/BBSCollection";
import BBSCommunityDetails from "@/pages/BBSCommunityDetails";
import BBSInventory from "@/pages/BBSInventory";
import BBSInventoryDetails from "@/pages/BBSInventoryDetails";
import BBSSearchPage from "@/pages/BBSSearchPage";
import BBSStat from "@/pages/BBSStat";
import BBSInformation from "@/pages/BBSInformation";
import BBSMessage from '@/pages/BBSMessage';
import BBSChat from '@/components/BBSChat'
import BBSFans from '@/pages/BBSFans';
import BBSPoints from '@/pages/BBSPoints';
import BBSPointsDetail from '@/pages/BBSPointsDetail';


//解决vue路由重复导航错误
//获取原型对象上的push函数
const originalPush = VueRouter.prototype.push
//修改原型对象中的push方法
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

//创建一个路由器
const router = new VueRouter({
    //mode: 'history',
    routes:[
        {
            name:'index',
            path:'/',
            component:BBSIndex,
            redirect: '/forum'
        },

        {
            name: 'login',
            path: '/login',
            component: BBSLogin,
            meta:{auth:false}
        },
        {
            name:'collection',
            path:"/collection",
            component:BBSCollection,
        },
        {
            name:'forum',
            path: '/forum',
            component: BBSForum,
            meta:{auth:false}
        },
        {
            name:'community',
            path: '/community',
            component:BBSCommunity,
            meta:{auth:false}
        },
        {
            name:'inventory',
            path: '/inventory',
            component:BBSInventory,
            meta:{auth:false}
        },
        {
            name:'write',
            path:'/write',
            component:BBSArticleWrite
        },
        {
            name:'userinfo',
            path:'/userinfo',
            component:BBSUserinfo
        },
        {
            name:'articleDetails',
            path:'/articleDetails/articleId/:articleId',
            component: BBSArticleDetails
        },
        {
            name:'tabs',
            path:'/tabs/:index',
            component:BBSTabs,
        },
        {
            name:'communityDetails',
            path:'/communityDetails/communityId/:communityId',
            component:BBSCommunityDetails,
        },
        {
            name:'inventoryDetails',
            path:'/inventoryDetails/inventoryId/:inventoryId',
            component:BBSInventoryDetails,
        },
        {
            name:'searchDetails',
            path:'/search/keywords/:keywords',
            component:BBSSearchPage,
        },
        {
            name:'stat',
            path:'/stat',
            component:BBSStat,
        },
        // 弃用
        {
            name:'information',
            path:'/information',
            component:BBSInformation,
        },
        {
            name:'message',
            path:'/message',
            component:BBSMessage,
            children: [
                {
                    path:'chat/:id',
                    component:BBSChat,
                }
            ]
        },
        {
            name:'fans',
            path:'/fans',
            component:BBSFans,
        },
        {
            name:'points',
            path:'/points',
            component:BBSPoints,
        },
        {
            name:'pointsDetail',
            path:'/points/detail',
            component:BBSPointsDetail,
        }

    ]
})
export default router

