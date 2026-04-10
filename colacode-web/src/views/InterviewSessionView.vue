<template>
  <AppLayout>
    <div class="page-stack session-page">
      <section class="session-layout">
        <article class="session-main glass-panel">
          <div class="session-main__top">
            <div>
              <span class="section-kicker">Interview Session</span>
              <h1>Java 后端模拟面试</h1>
              <p class="section-copy">专注答题页，所有信息都围绕“当前这一题怎么答得更完整”服务。</p>
            </div>
            <div class="session-main__status">
              <a-tag color="processing">进行中</a-tag>
              <strong>第 2 / 5 题</strong>
            </div>
          </div>

          <div class="session-question-card">
            <div class="session-question-card__index">Q2</div>
            <div class="session-question-card__body">
              <h2>请解释 Spring 事务失效的常见场景，并给出排查思路。</h2>
              <p>建议从代理对象、自调用、异常回滚规则和方法可见性四个角度展开。</p>
            </div>
          </div>

          <div class="session-answer-box">
            <div class="session-answer-box__head">
              <strong>你的回答</strong>
              <span>建议控制在 2 - 4 分钟表达量</span>
            </div>
            <a-textarea :rows="10" placeholder="请输入你的回答..." />
          </div>

          <div class="session-main__actions">
            <a-button>中断保存</a-button>
            <a-button>查看上一题结果</a-button>
            <a-button type="primary" class="brand-button">提交答案</a-button>
          </div>
        </article>

        <aside class="session-side">
          <article class="glass-panel session-progress-card">
            <span class="section-kicker">Progress</span>
            <div class="session-progress-card__ring">2 / 5</div>
            <p class="section-copy">已完成 1 题，当前正在作答第 2 题。</p>
            <div class="session-progress-card__steps">
              <span class="done">Q1</span>
              <span class="active">Q2</span>
              <span>Q3</span>
              <span>Q4</span>
              <span>Q5</span>
            </div>
          </article>

          <article class="glass-panel session-coach-card">
            <span class="section-kicker">Coach Tips</span>
            <div class="timeline-list">
              <div class="timeline-item" v-for="item in sessionTips" :key="item.title">
                <strong>{{ item.title }}</strong>
                <p class="section-copy">{{ item.copy }}</p>
              </div>
            </div>
          </article>
        </aside>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/layouts/AppLayout.vue'

const sessionTips = [
  { title: '回答结构', copy: '先定义，再讲为什么失效，最后讲怎么排查与修复。' },
  { title: '评分关注点', copy: '知识覆盖、逻辑结构、表达完整度、工程视角。' },
  { title: '当前建议', copy: '多补一句线上问题如何定位，会显得更有工程经验。' }
]
</script>
