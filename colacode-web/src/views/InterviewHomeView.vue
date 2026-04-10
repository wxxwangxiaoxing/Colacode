<template>
  <AppLayout>
    <div class="page-stack interview-home-page">
      <section class="interview-launch glass-panel">
        <div class="interview-launch__content">
          <span class="section-kicker">AI Mock Interview</span>
          <h1>先定方向，再开始答题，把一场模拟面试做成有节奏的正式训练。</h1>
          <p class="section-copy">
            面试首页不应该像首页翻版，它更像启动台。这里要明确流程、方向和进入一场面试的仪式感。
          </p>
          <div class="interview-launch__actions">
            <a-button type="primary" size="large" class="brand-button">开始新面试</a-button>
            <a-button size="large">查看历史报告</a-button>
          </div>
        </div>

        <div class="interview-launch__flow">
          <div class="interview-flow-step" v-for="item in timeline" :key="item.title">
            <span class="interview-flow-step__index">{{ item.index }}</span>
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.copy }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="dashboard-grid interview-home-grid">
        <article class="glass-panel span-8 interview-direction-panel">
          <div class="section-headline">
            <div>
              <span class="section-kicker">Directions</span>
              <h2>建议从这些方向开始</h2>
            </div>
          </div>
          <div class="interview-direction-list">
            <article class="interview-direction-card" v-for="direction in directions" :key="direction.title">
              <div class="interview-direction-card__head">
                <strong>{{ direction.title }}</strong>
                <a-tag color="green">{{ direction.level }}</a-tag>
              </div>
              <p>{{ direction.copy }}</p>
            </article>
          </div>
        </article>

        <article class="glass-panel span-4 interview-value-panel">
          <span class="section-kicker">Why This Flow</span>
          <div class="interview-value-list">
            <div class="interview-value-item" v-for="item in benefits" :key="item.title">
              <strong>{{ item.title }}</strong>
              <p class="section-copy">{{ item.copy }}</p>
            </div>
          </div>
        </article>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/layouts/AppLayout.vue'

const timeline = [
  { index: '01', title: '关键词分析', copy: '先围绕岗位方向和薄弱标签收窄题路。' },
  { index: '02', title: '会话式答题', copy: '按题逐步推进，保持专注和连续表达。' },
  { index: '03', title: '完整复盘', copy: '用总分、分题点评和下一步建议形成闭环。' }
]

const directions = [
  { title: 'Java 后端基础', copy: 'Spring、事务、IOC、AOP、Bean 生命周期与工程表达。', level: '高频' },
  { title: 'Redis / MySQL', copy: '缓存一致性、索引设计、锁与事务、性能瓶颈定位。', level: '高频' },
  { title: '项目复盘表达', copy: '怎么讲项目难点、方案权衡和落地结果，而不是流水账。', level: '核心' }
]

const benefits = [
  { title: '不是只给题', copy: '从方向建立到报告建议，都围绕完整训练流程设计。' },
  { title: '不是只给分', copy: '分数之外，还能看到命中点、短板和下一步动作。' },
  { title: '不是一次性的', copy: '后续可以把薄弱标签回流到题库和练习继续强化。' }
]
</script>
