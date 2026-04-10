<template>
  <AppLayout>
    <div class="page-stack subjects-page">
      <section class="subjects-command glass-panel">
        <div class="subjects-command__copy">
          <p class="subjects-command__eyebrow">题库工作台</p>
          <h1>先筛对题，再开始练。</h1>
          <p class="subjects-command__summary">
            先收窄范围，再决定今天补短板还是冲高频。
          </p>

          <div class="subjects-command__signals">
            <span>推荐筛选：Java / Redis / 场景题</span>
            <span>建议时长：35 分钟</span>
            <span>本周高频：缓存一致性</span>
          </div>
        </div>

        <div class="subjects-command__hero-card">
          <span>当前题库状态</span>
          <strong>1,024</strong>
          <small>覆盖 Java、数据库、缓存和系统设计。</small>
          <div class="subjects-command__hero-meta">
            <b>256</b>
            <label>高频题</label>
            <b>72%</b>
            <label>与你当前路线匹配</label>
          </div>
        </div>
      </section>

      <section class="subjects-shell">
        <aside class="subjects-sidebar glass-panel">
          <div class="subjects-sidebar__top">
            <h2>快速锁定你要练的题</h2>
            <p>先缩小范围，再开始做题。</p>
          </div>

          <div class="subjects-filter-group" v-for="group in filters" :key="group.title">
            <div class="subjects-filter-group__head">
              <strong>{{ group.title }}</strong>
              <a href="#">重置</a>
            </div>
            <div class="chip-row">
              <a-tag v-for="item in group.items" :key="item" color="blue">{{ item }}</a-tag>
            </div>
          </div>
        </aside>

        <section class="subjects-main glass-panel">
          <div class="subjects-toolbar">
            <div>
              <h1>题库</h1>
              <p>你当前路线相关的高频题都在这里。</p>
            </div>
            <div class="subjects-toolbar__actions">
              <a-input-search placeholder="搜索题目、标签或知识点" style="width: 320px;" />
              <a-segmented :options="['推荐', '最新', '最热']" />
            </div>
          </div>

          <div class="subjects-toolbar__meta">
            <span>共 1,024 道题</span>
            <span>已筛选：Java / Redis / 场景题</span>
            <span>适合当前阶段：专项补短板</span>
          </div>

          <div class="subjects-list">
            <article class="subject-row" v-for="item in subjects" :key="item.title">
              <div class="subject-row__main">
                <div class="subject-row__title-wrap">
                  <strong>{{ item.title }}</strong>
                  <a-tag :color="item.color">{{ item.level }}</a-tag>
                </div>
                <p>{{ item.summary }}</p>
                <div class="subject-row__meta">
                  <span>{{ item.category }}</span>
                  <span>{{ item.type }}</span>
                  <span>{{ item.count }}</span>
                </div>
              </div>
              <div class="subject-row__actions">
                <a-button type="link">查看详情</a-button>
                <a-button>开始练习</a-button>
              </div>
            </article>
          </div>
        </section>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/layouts/AppLayout.vue'

const filters = [
  { title: '技术方向', items: ['后端', '数据库', '缓存', '算法'] },
  { title: '题型', items: ['单选', '多选', '问答', '场景题'] },
  { title: '难度', items: ['简单', '中等', '困难'] }
]

const subjects = [
  { title: 'Spring Bean 生命周期有哪些关键阶段？', summary: '从实例化、依赖注入到初始化回调，考察基础理解与表达。', level: '中等', category: 'Spring', type: '问答题', count: '2.1k 次练习', color: 'blue' },
  { title: 'MySQL 索引失效的典型场景', summary: '联合索引、函数和隐式类型转换。', level: '高频', category: 'MySQL', type: '场景题', count: '3.5k 次练习', color: 'gold' },
  { title: 'Redis 缓存一致性怎么设计？', summary: '双删、延迟双删和 MQ 补偿。', level: '困难', category: 'Redis', type: '系统设计', count: '1.8k 次练习', color: 'red' },
  { title: 'Java 锁升级与 synchronized 优化路径', summary: '把锁升级过程串成一个完整知识点。', level: '高频', category: 'Java 并发', type: '问答题', count: '2.7k 次练习', color: 'purple' }
]
</script>
