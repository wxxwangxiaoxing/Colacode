<template>
  <AppLayout>
    <div class="page-stack home-workbench-page">
      <section class="home-command glass-panel">
        <div class="home-command__copy">
          <p class="home-command__eyebrow">今日行动面板</p>
          <h1>今天先补什么，先练什么，一眼看清。</h1>
          <p class="home-command__summary">
            题库、练习、面试和社区信号，都收进一个能马上开始的工作台。
          </p>

          <div class="home-command__signal-row">
            <div class="home-signal-pill">
              <FireOutlined />
              <span>今日热门：Redis 高频追问</span>
            </div>
            <div class="home-signal-pill">
              <CommentOutlined />
              <span>社区新增 12 条面经讨论</span>
            </div>
          </div>

          <div class="home-command__actions">
            <RouterLink to="/practice">
              <a-button type="primary" size="large" class="brand-button">开始今日训练</a-button>
            </RouterLink>
            <RouterLink to="/interview">
              <a-button size="large">进入 AI 面试</a-button>
            </RouterLink>
          </div>

          <div class="home-command__notes">
            <span>弱项提醒：事务与缓存一致性</span>
            <span>推荐动作：做一轮项目表达型模拟面试</span>
          </div>
        </div>

        <div class="home-command__board">
          <div class="home-command__hero-card">
            <div>
              <span>今日学习状态</span>
              <strong>68%</strong>
              <small>还差 8 题达标，先补 Redis 高频追问。</small>
            </div>
            <RouterLink to="/practice" class="home-command__hero-link">继续冲刺</RouterLink>
          </div>

          <div class="home-command__stat-grid">
              <article v-for="item in heroStats" :key="item.label" class="home-command__stat">
                <strong>{{ item.value }}</strong>
                <span>{{ item.label }}</span>
                <small>{{ item.note }}</small>
              </article>
          </div>

          <div class="home-command__trend">
            <div class="home-command__trend-header">
              <div>
                <strong>最近 7 天节奏</strong>
                <p>先稳住节奏，再拉高强度。</p>
              </div>
              <span>稳步上升</span>
            </div>
            <div class="home-command__bars">
              <span v-for="(bar, index) in sparkBars" :key="index" :style="{ height: bar }"></span>
            </div>
          </div>
        </div>
      </section>

      <section class="home-task-panel glass-panel">
        <div class="section-headline home-section-headline">
          <div>
            <h2>今天最值得你先做的 3 件事</h2>
            <p>下一步是什么，这里直接给你答案。</p>
          </div>
          <RouterLink to="/practice" class="home-inline-link">查看全部训练路线</RouterLink>
        </div>

        <div class="home-task-list">
          <article class="home-task-item" v-for="item in focusItems" :key="item.title">
            <div class="home-task-item__stripe" :class="item.accent"></div>
            <div class="home-task-item__body">
              <div class="home-task-item__meta">
                <span>{{ item.tag }}</span>
                <small>{{ item.duration }}</small>
              </div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.copy }}</p>
            </div>
            <RouterLink :to="item.path" class="home-task-item__action">
              {{ item.action }}
            </RouterLink>
          </article>
        </div>
      </section>

      <section class="home-dashboard-grid">
        <article class="glass-panel home-panel home-panel--tracks">
        <div class="section-headline home-section-headline">
          <div>
            <h2>推荐训练路径</h2>
            <p>不是散题推荐，是一条能做完的路线。</p>
          </div>
            <RouterLink to="/subjects" class="home-inline-link">去题库</RouterLink>
          </div>

          <div class="home-track-list">
            <article class="home-track-card" v-for="item in tracks" :key="item.title">
              <div class="home-track-card__top">
                <strong>{{ item.title }}</strong>
                <span>{{ item.tag }}</span>
              </div>
              <p>{{ item.copy }}</p>
              <div class="home-track-card__progress">
                <div class="home-track-card__bar">
                  <span :style="{ width: `${item.progress}%` }"></span>
                </div>
                <small>{{ item.progress }}%</small>
              </div>
            </article>
          </div>
        </article>

        <article class="glass-panel home-panel home-panel--strength">
        <div class="section-headline home-section-headline">
          <div>
            <h2>能力热区</h2>
            <p>看清强项，也看清缺口。</p>
          </div>
          </div>

          <div class="home-strength-score">
            <strong>72</strong>
            <span>当前综合状态</span>
          </div>

          <div class="home-strength-list">
            <div class="home-strength-list__item" v-for="item in strengths" :key="item.label">
              <div>
                <label>{{ item.label }}</label>
                <small>{{ item.note }}</small>
              </div>
              <b>{{ item.score }}</b>
            </div>
          </div>
        </article>
      </section>

      <section class="home-dashboard-grid home-dashboard-grid--lower">
        <article class="glass-panel home-panel home-panel--community">
        <div class="section-headline home-section-headline">
          <div>
            <h2>社区正在讨论什么</h2>
            <p>别人怎么拆题、怎么复盘，这里都能看到。</p>
          </div>
            <RouterLink to="/community" class="home-inline-link">进入社区</RouterLink>
          </div>

          <div class="home-feed-list">
            <article class="home-feed-card" v-for="item in topics" :key="item.title">
              <div class="home-feed-card__meta">
                <span><FireOutlined />{{ item.board }}</span>
                <small>{{ item.author }}</small>
              </div>
              <h3>{{ item.title }}</h3>
              <div class="home-feed-card__stats">
                <span>{{ item.meta }}</span>
                <span>{{ item.views }}</span>
              </div>
            </article>
          </div>
        </article>

        <article class="glass-panel home-panel home-panel--routes">
        <div class="section-headline home-section-headline">
          <div>
            <h2>快捷入口</h2>
            <p>不想犹豫，就直接进。</p>
          </div>
          </div>

          <div class="home-route-grid">
            <RouterLink v-for="item in routes" :key="item.title" :to="item.path" class="home-route-card">
              <div class="home-route-card__top">
                <span class="home-route-card__icon" :class="item.accent">
                  <component :is="item.icon" />
                </span>
                <strong>{{ item.title }}</strong>
              </div>
              <p>{{ item.copy }}</p>
            </RouterLink>
          </div>
        </article>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import {
  CommentOutlined,
  FireOutlined,
  ReadOutlined,
  RobotOutlined,
  ThunderboltOutlined
} from '@ant-design/icons-vue'
import AppLayout from '@/layouts/AppLayout.vue'

const heroStats = [
  { label: '本周刷题', value: '25', note: '比上周多 6 题' },
  { label: '模拟面试', value: '6', note: '最近一次 72 分' },
  { label: '连续活跃', value: '7d', note: '节奏稳定' },
  { label: '收藏知识点', value: '18', note: '待复习 5 个' }
]

const sparkBars = ['42%', '58%', '49%', '74%', '63%', '81%', '68%']

const focusItems = [
  {
    tag: '高优先级',
    duration: '18 分钟',
    title: '补齐事务与缓存一致性',
    copy: '最近两次面试里，这块分最低。',
    action: '进入题库',
    path: '/subjects',
    accent: 'home-task-item__stripe--blue'
  },
  {
    tag: '进行中',
    duration: '25 分钟',
    title: '继续 Java 后端专题练习',
    copy: '已经做到 68%，今天适合收尾。',
    action: '继续练习',
    path: '/practice',
    accent: 'home-task-item__stripe--green'
  },
  {
    tag: '建议完成',
    duration: '30 分钟',
    title: '做一轮项目表达型面试',
    copy: '知识开始稳了，下一步该练表达。',
    action: '开始面试',
    path: '/interview',
    accent: 'home-task-item__stripe--orange'
  }
]

const tracks = [
  {
    title: 'Redis 高并发专题',
    copy: '穿透、双删、热点 Key',
    progress: 72,
    tag: '本周重点'
  },
  {
    title: 'MySQL 索引与执行计划',
    copy: '联合索引、回表、慢 SQL',
    progress: 61,
    tag: '继续推进'
  },
  {
    title: '项目复盘表达训练',
    copy: 'STAR、亮点、方案权衡',
    progress: 48,
    tag: '适合冲刺'
  }
]

const strengths = [
  { label: 'Java 基础', score: 81, note: '基础稳定' },
  { label: 'MySQL', score: 69, note: '还要补索引' },
  { label: 'Redis', score: 64, note: '追问还不够稳' },
  { label: '项目表达', score: 58, note: '建议用面试拉升' }
]

const topics = [
  { title: 'Redis 双删策略到底适合哪些场景？', board: '技术问答', author: 'Luna', meta: '61 回复', views: '2.4k 浏览' },
  { title: 'Java 校招面试里最容易答空的追问整理', board: '面经交流', author: '张三', meta: '92 回复', views: '3.2k 浏览' },
  { title: '这周最值得读的项目复盘：索引优化与并发锁权衡', board: '项目复盘', author: 'Kite', meta: '34 回复', views: '1.8k 浏览' }
]

const routes = [
  { title: '高频题库', copy: '先补知识点。', path: '/subjects', icon: ReadOutlined, accent: 'home-route-card__icon--blue' },
  { title: '专题训练', copy: '按路线持续推进。', path: '/practice', icon: ThunderboltOutlined, accent: 'home-route-card__icon--green' },
  { title: 'AI 面试', copy: '跑完一轮问答复盘。', path: '/interview', icon: RobotOutlined, accent: 'home-route-card__icon--orange' },
  { title: '面经社区', copy: '看别人怎么拆题。', path: '/community', icon: CommentOutlined, accent: 'home-route-card__icon--purple' }
]
</script>
