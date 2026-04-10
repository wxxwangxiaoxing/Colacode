<template>
  <AppLayout>
    <div class="page-stack community-page">
      <section class="community-lobby glass-panel">
        <div class="community-lobby__main">
          <div class="community-lobby__eyebrow">Developer Forum</div>
          <h1>后端开发者社区</h1>
          <p>
            围绕面经、项目复盘、技术问答和共学训练持续沉淀。这里应该像论坛首页，先看到板块秩序，再看到活跃讨论。
          </p>
        </div>
        <div class="community-lobby__stats">
          <div v-for="item in stats" :key="item.label" class="community-lobby__stat">
            <strong>{{ item.value }}</strong>
            <span>{{ item.label }}</span>
          </div>
        </div>
        <div class="community-lobby__actions">
          <a-button type="primary" class="brand-button">发布主题</a-button>
          <a-button>发起提问</a-button>
        </div>
      </section>

      <section class="community-hot-strip glass-panel">
        <div class="community-hot-strip__label">今日热议</div>
        <RouterLink v-for="item in hotThreads" :key="item.id" :to="`/community/thread/${item.id}`" class="community-hot-strip__item">
          <strong>{{ item.title }}</strong>
          <span>{{ item.meta }}</span>
        </RouterLink>
      </section>

      <section class="community-forum-layout">
        <main class="community-directory glass-panel">
          <div class="community-directory__head">
            <div>
              <span class="section-kicker">Boards</span>
              <h2>板块目录</h2>
            </div>
            <a-segmented :options="['全部', '热门', '最新活跃']" />
          </div>

          <div class="community-board-table">
            <RouterLink
              v-for="board in boards"
              :key="board.slug"
              :to="`/community/board/${board.slug}`"
              class="community-board-row"
            >
              <div class="community-board-row__icon">
                <component :is="board.icon" />
              </div>
              <div class="community-board-row__info">
                <div class="community-board-row__title">
                  <strong>{{ board.title }}</strong>
                  <span>{{ board.copy }}</span>
                </div>
                <div class="community-board-row__latest">
                  <em>最新主题</em>
                  <span>{{ board.latestTitle }}</span>
                  <small>{{ board.latestMeta }}</small>
                </div>
              </div>
              <div class="community-board-row__metrics">
                <span>{{ board.topics }}</span>
                <small>主题</small>
              </div>
              <div class="community-board-row__metrics">
                <span>{{ board.posts }}</span>
                <small>回复</small>
              </div>
            </RouterLink>
          </div>
        </main>

        <aside class="community-right-rail">
          <article class="glass-panel community-rail-card">
            <span class="section-kicker">热门标签</span>
            <div class="community-tag-cloud">
              <RouterLink v-for="item in hotTopics" :key="item.id" :to="`/community/thread/${item.id}`"># {{ item.label }}</RouterLink>
            </div>
          </article>

          <article class="glass-panel community-rail-card">
            <span class="section-kicker">共学小组</span>
            <div class="community-group-list">
              <div class="community-group-item" v-for="item in groups" :key="item.title">
                <strong>{{ item.title }}</strong>
                <p>{{ item.copy }}</p>
                <span>{{ item.members }}</span>
              </div>
            </div>
          </article>

          <article class="glass-panel community-rail-card">
            <span class="section-kicker">版务公告</span>
            <div class="community-notice-list">
              <div class="community-notice-item" v-for="item in notices" :key="item.title">
                <strong>{{ item.title }}</strong>
                <p>{{ item.copy }}</p>
              </div>
            </div>
          </article>
        </aside>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import {
  ClusterOutlined,
  CommentOutlined,
  MessageOutlined,
  ReadOutlined,
  TeamOutlined,
  TrophyOutlined
} from '@ant-design/icons-vue'
import AppLayout from '@/layouts/AppLayout.vue'

const stats = [
  { label: '今日新增主题', value: '128' },
  { label: '活跃板块', value: '12' },
  { label: '在线成员', value: '412' }
]

const hotThreads = [
  { id: 101, title: 'Redis 双删策略到底适合哪些场景？', meta: '61 回复 · 2.4k 浏览' },
  { id: 103, title: 'Java 校招面试里最容易答空的追问整理', meta: '92 回复 · 3.2k 浏览' },
  { id: 105, title: '一次缓存一致性事故的完整复盘', meta: '28 回复 · 1.2k 浏览' }
]

const boards = [
  {
    slug: 'qa',
    icon: MessageOutlined,
    title: '技术问答区',
    copy: '围绕具体问题、边界条件和排查思路展开讨论。',
    latestTitle: 'MySQL 联合索引最左匹配，什么时候最容易答空？',
    latestMeta: 'Kite · 42 分钟前',
    topics: '1,284',
    posts: '8,932'
  },
  {
    slug: 'interview',
    icon: TrophyOutlined,
    title: '面经交流区',
    copy: '沉淀真实面试过程、追问路径和失分复盘。',
    latestTitle: 'Java 校招面试里最容易答空的追问整理',
    latestMeta: '张三 · 今天',
    topics: '962',
    posts: '6,140'
  },
  {
    slug: 'project',
    icon: ClusterOutlined,
    title: '项目复盘区',
    copy: '把项目背景、方案、结果和问题处理讲成完整故事。',
    latestTitle: '如何把压测和故障处理写进项目表达里',
    latestMeta: 'Stone · 2 小时前',
    topics: '543',
    posts: '3,208'
  },
  {
    slug: 'groups',
    icon: TeamOutlined,
    title: '共学小组区',
    copy: '围绕主题一起训练、讨论和复盘。',
    latestTitle: 'Redis 共学第 3 周：热点 key 与一致性',
    latestMeta: 'Milo · 今天',
    topics: '218',
    posts: '1,430'
  },
  {
    slug: 'hot',
    icon: CommentOutlined,
    title: '热门主题区',
    copy: '快速查看当前最有讨论度的帖子与趋势。',
    latestTitle: 'Spring AOP 在面试里最容易被追问到哪一层？',
    latestMeta: 'Milo · 20 分钟前',
    topics: '320',
    posts: '2,461'
  },
  {
    slug: 'interview',
    icon: ReadOutlined,
    title: '表达训练区',
    copy: '围绕项目讲法、答题结构和复述能力持续训练。',
    latestTitle: '后端项目怎么讲出“工程感”？',
    latestMeta: 'Anny · 今天',
    topics: '176',
    posts: '968'
  }
]

const hotTopics = [
  { id: 201, label: 'Spring AOP' },
  { id: 202, label: 'Redis 一致性' },
  { id: 203, label: '项目复盘' },
  { id: 204, label: 'MySQL 索引' },
  { id: 205, label: '并发锁' },
  { id: 206, label: '校招面经' }
]

const groups = [
  { title: 'Redis 共学小组', copy: '每周围绕缓存题做一轮专题拆解。', members: '128 人在线' },
  { title: '项目表达训练营', copy: '一起修改项目讲法和答题结构。', members: '76 人在线' },
  { title: 'Java 面经速记组', copy: '高频整理面试题和追问模板。', members: '94 人在线' }
]

const notices = [
  { title: '发帖前请先搜索', copy: '很多高频问题已经有高质量讨论，先搜再问会更高效。' },
  { title: '优质复盘鼓励置顶', copy: '项目复盘和面经类高质量内容会被整理进精华索引。' },
  { title: '欢迎围绕训练闭环讨论', copy: '不仅能聊技术，也欢迎聊怎么把技术讲清楚。' }
]
</script>
