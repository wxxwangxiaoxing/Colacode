import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/subjects',
    name: 'subjects',
    component: () => import('@/views/SubjectsView.vue'),
    meta: { title: '题库' }
  },
  {
    path: '/practice',
    name: 'practice',
    component: () => import('@/views/PracticeView.vue'),
    meta: { title: '练习' }
  },
  {
    path: '/interview',
    name: 'interview',
    component: () => import('@/views/InterviewHomeView.vue'),
    meta: { title: 'AI 面试' }
  },
  {
    path: '/interview/session',
    name: 'interview-session',
    component: () => import('@/views/InterviewSessionView.vue'),
    meta: { title: '面试会话' }
  },
  {
    path: '/interview/:id/report',
    name: 'interview-report',
    component: () => import('@/views/InterviewReportView.vue'),
    meta: { title: '面试报告' }
  },
  {
    path: '/community',
    name: 'community',
    component: () => import('@/views/CommunityView.vue'),
    meta: { title: '社区' }
  },
  {
    path: '/community/board/:slug',
    name: 'community-board',
    component: () => import('@/views/CommunityBoardView.vue'),
    meta: { title: '社区板块' }
  },
  {
    path: '/community/thread/:id',
    name: 'community-thread',
    component: () => import('@/views/CommunityThreadView.vue'),
    meta: { title: '帖子详情' }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { title: '个人中心' }
  },
  {
    path: '/pricing',
    name: 'pricing',
    component: () => import('@/views/PricingView.vue'),
    meta: { title: '定价' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.afterEach((to) => {
  document.title = `ColaCode | ${to.meta?.title || '程序员学习社区'}`
})

export default router
