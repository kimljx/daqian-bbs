import Vue from 'vue';
import Router from 'vue-router';
import * as path from 'path';

Vue.use(Router);

export default new Router({
    base: process.env.NODE_ENV === "development" ? "" : "/bbs-admin",
    routes: [{
            path: '/',
            redirect: '/dashboard'
        },
        {
            path: '/',
            component: () => import('../components/common/Home.vue'),
            meta: {
                title: '自述文件'
            },
            children: [{
                    path: '/dashboard',
                    component: () => import('../views/Dashboard.vue'),
                    meta: {
                        title: '系统首页'
                    }
                },
                {
                    path: '/user',
                    component: () => import('../views/UserPage.vue'),
                    meta: {
                        title: '用户管理'
                    }
                },
                {
                    path: '/article',
                    component: () => import('../views/ArticlePage.vue'),
                    meta: {
                        title: '帖子管理'
                    },
                },
                {
                    path: '/articleLable',
                    component: () => import('../views/ArticleLablePage.vue'),
                    meta: {
                        title: '标签管理'
                    }
                },
                {
                    path: '/dict',
                    component: () => import('../views/DictPage.vue'),
                    meta: {
                        title: '配置管理'
                    }
                },
                {
                    path: '/article/id',
                    component: () => import('../views/ArticleDetailsPage.vue'),
                    meta: {
                        title: '文章详情',
                        noCache: false
                    },
                },

                {
                    path: '/community',
                    component: () => import('../views/CommunityPage.vue'),
                    meta: {
                        title: '社区管理'
                    }
                },
                {
                    path: '/slideshow',
                    component: () => import('../views/SlideshowPage.vue'),
                    meta: {
                        title: '轮播图管理'
                    }
                },
                {
                    path: '/statistic',
                    component: () => import('../views/StatisticPage.vue'),
                    meta: {
                        title: '文章统计'
                    }
                },
                {
                    path: '/points',
                    component: () => import('../views/BBSPoints.vue'),
                    meta: {
                        title: '积分排名'
                    }
                },
                {
                    path: '/points/detail',
                    component: () => import('../views/BBSPointsDetail.vue'),
                    meta: {
                        title: '积分排名详情'
                    }
                },
                {
                    path: '/unitManage',
                    component: () => import('../views/BBSUnitManage.vue'),
                    meta: {
                        title: '单位管理'
                    }
                },
                {
                    path: '/sensitiveWord',
                    component: () => import('../views/BBSSensitiveWord.vue'),
                    meta: {
                        title: '敏感词管理'
                    }
                },
                {
                    path: '/404',
                    component: () => import('../views/404.vue'),
                    meta: {
                        title: '404'
                    }
                },
                {
                    path: '/403',
                    component: () => import('../views/403.vue'),
                    meta: {
                        title: '403'
                    }
                },
            ]
        },
        {
            path: '/login',
            component: () => import('../views/Login.vue'),
            meta: {
                title: '管理员登录'
            }
        },
        {
            path: '*',
            redirect: '/404'
        }
    ]
});