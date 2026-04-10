<template>
  <AppLayout>
    <div class="page-stack practice-page">
      <section class="practice-command glass-panel">
        <div class="practice-command__copy">
          <p class="practice-command__eyebrow">训练控制台</p>
          <h1>把练习做成一条能持续推进的训练线。</h1>
          <p class="practice-command__summary">
            继续练什么、补什么、先做什么，这里直接给你。
          </p>

          <div class="practice-command__signals">
            <span>本周目标还差 2 个模块</span>
            <span>当前最优先：Redis 套题第 3 节</span>
            <span>建议补口：项目表达题</span>
          </div>

          <div class="practice-command__actions">
            <a-button type="primary" size="large" class="brand-button">继续上次练习</a-button>
            <a-button size="large">创建新训练计划</a-button>
          </div>
        </div>

        <div class="practice-command__hero-card">
          <span>本周训练完成度</span>
          <strong>74%</strong>
          <small>还差 2 个模块，本周目标就能完成。</small>
          <div class="practice-command__stat-grid">
            <article v-for="item in summaryStats" :key="item.label" class="practice-command__stat">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </article>
          </div>
        </div>
      </section>

      <section class="practice-board">
        <article class="glass-panel practice-lane practice-lane--main">
          <div class="section-headline practice-section-headline">
            <div>
              <h2>当前训练路线</h2>
              <p>继续练、补短板、练表达，都放进同一条路线。</p>
            </div>
          </div>

          <div class="practice-track-list">
            <article class="practice-track-card" v-for="item in tracks" :key="item.title">
              <div class="practice-track-card__head">
                <div>
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.summary }}</p>
                </div>
                <span class="practice-track-card__progress">{{ item.progress }}</span>
              </div>
              <div class="practice-track-card__bar">
                <span :style="{ width: item.percent }"></span>
              </div>
              <div class="practice-track-card__meta">
                <span>{{ item.questions }}</span>
                <span>{{ item.level }}</span>
                <span>{{ item.duration }}</span>
              </div>
            </article>
          </div>
        </article>

        <aside class="practice-lane practice-lane--side">
          <article class="glass-panel practice-panel">
            <div class="section-headline practice-section-headline practice-section-headline--compact">
              <div>
                <h2>下一步任务</h2>
                <p>按顺序推进，更容易完成。</p>
              </div>
            </div>

            <div class="practice-task-list">
              <div class="practice-task-item" v-for="item in tasks" :key="item.title">
                <strong>{{ item.title }}</strong>
                <p class="section-copy">{{ item.copy }}</p>
              </div>
            </div>
          </article>

          <article class="glass-panel practice-panel practice-panel--weakness">
            <div class="section-headline practice-section-headline practice-section-headline--compact">
              <div>
                <h2>建议优先补的缺口</h2>
                <p>先补最影响得分的点。</p>
              </div>
            </div>
            <div class="topic-cloud" style="margin-top: 18px;">
              <span v-for="item in weaknesses" :key="item">{{ item }}</span>
            </div>
          </article>
        </aside>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/layouts/AppLayout.vue'

const summaryStats = [
  { label: '已完成专题', value: '9' },
  { label: '本周刷题', value: '25' },
  { label: '平均正确率', value: '81%' },
  { label: '连续打卡', value: '7d' }
]

const tracks = [
  { title: 'Java 后端核心知识链', summary: 'JVM、集合、并发、Spring、数据库。', progress: '68%', percent: '68%', questions: '32 题', level: '中等', duration: '约 45 分钟' },
  { title: 'Redis 与缓存系统设计', summary: '一致性、热点 key、击穿、补偿机制。', progress: '42%', percent: '42%', questions: '18 题', level: '困难', duration: '约 30 分钟' },
  { title: '项目复盘表达训练', summary: '项目背景、难点、方案权衡、结果。', progress: '15%', percent: '15%', questions: '12 题', level: '核心', duration: '约 20 分钟' }
]

const tasks = [
  { title: '先完成 Redis 套题第 3 节', copy: '这组最接近完成，先收尾。' },
  { title: '回看上一轮错题复盘', copy: '先把最近错题再串一次。' },
  { title: '补一轮项目表达题', copy: '接下来差异点会在表达。' }
]

const weaknesses = ['事务传播', '缓存一致性', '项目难点表达', '索引优化', '并发锁']
</script>
