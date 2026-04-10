<template>
  <AppLayout>
    <div class="page-stack community-board-page">
      <section class="glass-panel board-header">
        <div>
          <span class="section-kicker">Board</span>
          <h1>{{ board.title }}</h1>
          <p class="section-copy">{{ board.copy }}</p>
        </div>
        <div class="board-header__stats">
          <div>
            <strong>{{ board.topics }}</strong>
            <span>主题</span>
          </div>
          <div>
            <strong>{{ board.posts }}</strong>
            <span>回复</span>
          </div>
          <div>
            <strong>{{ board.today }}</strong>
            <span>今日新增</span>
          </div>
        </div>
      </section>

      <section class="board-layout">
        <main class="glass-panel board-main">
          <div class="board-toolbar">
            <a-segmented :options="['最新主题', '热门主题', '精华主题']" />
            <a-input-search placeholder="搜索帖子关键词" style="width: 280px;" />
          </div>

          <div class="board-thread-list">
            <RouterLink v-for="thread in board.threads" :key="thread.id" :to="`/community/thread/${thread.id}`" class="board-thread-card">
              <div class="board-thread-card__main">
                <div class="board-thread-card__tags">
                  <a-tag :color="thread.color">{{ thread.category }}</a-tag>
                  <a-tag v-for="tag in thread.tags" :key="tag"># {{ tag }}</a-tag>
                </div>
                <strong>{{ thread.title }}</strong>
                <p>{{ thread.summary }}</p>
                <div class="board-thread-card__meta">
                  <span>{{ thread.author }}</span>
                  <span>{{ thread.time }}</span>
                  <span>{{ thread.lastReply }}</span>
                </div>
              </div>
              <div class="board-thread-card__stats">
                <span>{{ thread.replies }}</span>
                <small>回复</small>
                <span>{{ thread.views }}</span>
                <small>浏览</small>
              </div>
            </RouterLink>
          </div>
        </main>

        <aside class="board-side">
          <article class="glass-panel board-side-card">
            <span class="section-kicker">Pinned</span>
            <div class="forum-pick-list">
              <div class="forum-pick-item" v-for="item in board.pinned" :key="item.title">
                <strong>{{ item.title }}</strong>
                <p class="section-copy">{{ item.copy }}</p>
              </div>
            </div>
          </article>

          <article class="glass-panel board-side-card">
            <span class="section-kicker">Moderators</span>
            <div class="community-people-list">
              <div class="community-person" v-for="item in board.mods" :key="item.name">
                <span class="community-avatar">{{ item.name[0] }}</span>
                <div>
                  <strong>{{ item.name }}</strong>
                  <p>{{ item.copy }}</p>
                </div>
              </div>
            </div>
          </article>
        </aside>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'

const route = useRoute()

const boardMap = {
  hot: {
    title: '热门主题',
    copy: '聚合当前最有讨论度的技术与面试话题。',
    topics: '320',
    posts: '2,461',
    today: '38',
    threads: [
      { id: 201, category: '热门', color: 'red', title: 'Spring AOP 在面试里最容易被追问到哪一层？', summary: '把代理、织入、事务和源码定位串起来讲，才不容易越问越乱。', tags: ['Spring', 'AOP'], author: 'Milo', time: '20 分钟前', lastReply: '最后回复 2 分钟前', replies: '46', views: '2.3k' },
      { id: 202, category: '热门', color: 'gold', title: 'Redis 一致性到底该怎么回答才有工程味？', summary: '不是背方案，而是讲清楚场景、代价、边界和补偿。', tags: ['Redis', '系统设计'], author: 'Luna', time: '1 小时前', lastReply: '最后回复 10 分钟前', replies: '63', views: '3.1k' }
    ],
    pinned: [
      { title: '社区发帖指南', copy: '发帖前尽量带上场景、问题和你的当前思路。' },
      { title: '热门主题整理索引', copy: '方便你快速回到最近讨论度高的帖子。' }
    ],
    mods: [
      { name: 'Anny', copy: '负责热门主题整理与精华推荐。' },
      { name: 'Luna', copy: '负责高频技术问题汇总。' }
    ]
  },
  qa: {
    title: '技术问答区',
    copy: '围绕具体技术点、边界条件和排查思路展开讨论。',
    topics: '1,284',
    posts: '8,932',
    today: '76',
    threads: [
      { id: 101, category: '问答', color: 'blue', title: 'Redis 双删策略到底适合哪些场景？', summary: '从缓存一致性、热点数据与补偿机制角度讨论双删的边界。', tags: ['Redis', '缓存一致性'], author: 'Luna', time: '18 分钟前', lastReply: '最后回复 刚刚', replies: '61', views: '2.4k' },
      { id: 102, category: '问答', color: 'cyan', title: 'MySQL 联合索引最左匹配，什么时候最容易答空？', summary: '整理几个最容易讲混的索引失效场景，欢迎补充案例。', tags: ['MySQL', '索引'], author: 'Kite', time: '42 分钟前', lastReply: '最后回复 9 分钟前', replies: '37', views: '1.6k' }
    ],
    pinned: [
      { title: '提问模板', copy: '描述背景、现象、你尝试过什么，再抛出问题。' },
      { title: '经典问答索引', copy: '先搜再问，很多高频问题已经有高质量讨论。' }
    ],
    mods: [
      { name: 'Stone', copy: '偏数据库与线上排查问题。' },
      { name: 'Luna', copy: '偏缓存与系统设计问题。' }
    ]
  },
  interview: {
    title: '面经交流区',
    copy: '沉淀真实面试过程、追问路径和失分复盘。',
    topics: '962',
    posts: '6,140',
    today: '42',
    threads: [
      { id: 103, category: '面经', color: 'purple', title: 'Java 校招面试里最容易答空的追问整理', summary: '把高频追问点和补充表达方式集中整理。', tags: ['Java', '面经'], author: '张三', time: '今天', lastReply: '最后回复 6 分钟前', replies: '92', views: '3.2k' },
      { id: 104, category: '复盘', color: 'gold', title: '后端项目怎么讲出“工程感”？', summary: '从项目背景、约束、落地和结果四个角度重写表达。', tags: ['项目表达', '校招'], author: 'Anny', time: '今天', lastReply: '最后回复 15 分钟前', replies: '48', views: '2.1k' }
    ],
    pinned: [
      { title: '面经发帖规范', copy: '建议写清岗位、轮次、题目和自己卡住的点。' },
      { title: '高频追问整理', copy: '快速回看面试中最常见的二次追问。' }
    ],
    mods: [
      { name: 'Kite', copy: '负责校招面经与表达结构。' },
      { name: 'Anny', copy: '负责项目复盘与讲法优化。' }
    ]
  },
  project: {
    title: '项目复盘区',
    copy: '把项目背景、方案、结果和问题处理讲成完整故事。',
    topics: '543',
    posts: '3,208',
    today: '21',
    threads: [
      { id: 105, category: '复盘', color: 'green', title: '一次缓存一致性事故的完整复盘', summary: '从事故现象、监控、补偿失败到最终修复，完整还原链路。', tags: ['Redis', '线上问题'], author: 'Luna', time: '1 小时前', lastReply: '最后回复 12 分钟前', replies: '28', views: '1.2k' },
      { id: 106, category: '表达', color: 'blue', title: '如何把压测和故障处理写进项目表达里', summary: '讨论什么信息值得讲、什么细节会加分。', tags: ['项目表达', '压测'], author: 'Stone', time: '2 小时前', lastReply: '最后回复 26 分钟前', replies: '16', views: '876' }
    ],
    pinned: [
      { title: '优质项目复盘模板', copy: '按背景、目标、难点、方案、结果组织内容。' },
      { title: '项目讲法案例库', copy: '适合准备项目面试前集中阅读。' }
    ],
    mods: [
      { name: 'Anny', copy: '负责项目讲法与复盘质量。' },
      { name: 'Stone', copy: '负责系统设计与压测案例。' }
    ]
  },
  groups: {
    title: '共学小组区',
    copy: '围绕主题一起训练、讨论和复盘。',
    topics: '218',
    posts: '1,430',
    today: '14',
    threads: [
      { id: 301, category: '共学', color: 'cyan', title: 'Redis 共学第 3 周：热点 key 与一致性', summary: '本周从热点 key 到双删边界，集中讨论一轮。', tags: ['Redis', '共学'], author: 'Milo', time: '今天', lastReply: '最后回复 5 分钟前', replies: '29', views: '932' },
      { id: 302, category: '共学', color: 'purple', title: '项目表达训练营：这周改哪一段最能提分？', summary: '把项目描述按工程感重构，欢迎贴你的版本。', tags: ['项目表达', '共学'], author: 'Anny', time: '今天', lastReply: '最后回复 13 分钟前', replies: '21', views: '801' }
    ],
    pinned: [
      { title: '共学入门说明', copy: '加入前先看每周主题与参与方式。' },
      { title: '共学打卡索引', copy: '方便回顾每周讨论与训练记录。' }
    ],
    mods: [
      { name: 'Milo', copy: '负责共学节奏与题目安排。' },
      { name: 'Anny', copy: '负责表达型训练营。' }
    ]
  }
}

const board = computed(() => boardMap[route.params.slug] || boardMap.qa)
</script>
