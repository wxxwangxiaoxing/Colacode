<template>
  <AppLayout>
    <div class="page-stack report-page">
      <section class="report-layout">
        <article class="report-main glass-panel">
          <div class="report-main__hero">
            <div>
              <span class="section-kicker">Interview Report</span>
              <h1>这次面试已经有了明显的结构感，但工程视角还能更进一步。</h1>
              <p class="section-copy">这页不是详情页，而是复盘页。你要从这里直接看出亮点、短板和下一步动作。</p>
            </div>
            <div class="report-main__score">
              <strong>72</strong>
              <span>总分</span>
            </div>
          </div>

          <div class="report-score-grid">
            <div class="report-score-card" v-for="item in scores" :key="item.label">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </div>
          </div>

          <div class="report-section-list">
            <article class="report-section-card" v-for="item in reportSections" :key="item.title">
              <div class="report-section-card__head">
                <strong>{{ item.title }}</strong>
              </div>
              <p>{{ item.summary }}</p>
            </article>
          </div>

          <div class="report-question-list">
            <div class="section-headline">
              <div>
                <span class="section-kicker">Per Question</span>
                <h2>分题点评</h2>
              </div>
            </div>
            <article class="report-question-card" v-for="item in questions" :key="item.title">
              <div class="report-question-card__title">
                <strong>{{ item.title }}</strong>
                <a-tag :color="item.color">{{ item.score }}</a-tag>
              </div>
              <p>{{ item.comment }}</p>
            </article>
          </div>
        </article>

        <aside class="report-side">
          <article class="glass-panel">
            <span class="section-kicker">Weakness Tags</span>
            <div class="topic-cloud" style="margin-top:18px;">
              <span v-for="item in weaknesses" :key="item">{{ item }}</span>
            </div>
          </article>

          <article class="glass-panel report-next-card">
            <span class="section-kicker">Next Action</span>
            <h3>下一步建议</h3>
            <p class="section-copy">优先回到 Redis 与事务专题练习，再做一轮项目表达型模拟面试。</p>
            <a-button type="primary" class="brand-button">去做下一轮训练</a-button>
          </article>
        </aside>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/layouts/AppLayout.vue'

const scores = [
  { label: '知识覆盖', value: '78' },
  { label: '逻辑表达', value: '74' },
  { label: '工程视角', value: '63' },
  { label: '答题节奏', value: '71' }
]

const reportSections = [
  { title: '表现亮点', summary: '对事务代理、自调用问题和回滚条件有较清晰理解，回答结构开始成型。' },
  { title: '主要缺口', summary: '在异常回滚规则、传播行为和线上排查路径上表述还不够具体。' },
  { title: '训练建议', summary: '补一轮 Spring 事务专题题，再结合项目案例回答“如何定位事务失效”。' }
]

const questions = [
  { title: 'Q1. Bean 生命周期', score: '78', color: 'blue', comment: '知识点较完整，但初始化扩展点可以再补一层。' },
  { title: 'Q2. 事务失效场景', score: '72', color: 'gold', comment: '结构清晰，但排查动作不够具体，线上经验感还可以更强。' },
  { title: 'Q3. Redis 一致性', score: '65', color: 'red', comment: '知道常见方案，但缺少方案权衡与边界条件。' }
]

const weaknesses = ['事务传播', '缓存一致性', '项目复盘', '异常回滚', '排查路径']
</script>
