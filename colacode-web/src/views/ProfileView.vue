<template>
  <AppLayout>
    <div class="page-stack profile-page">
      <section class="profile-hero glass-panel">
        <div class="profile-hero__identity">
          <div class="profile-avatar">W</div>
          <div>
            <span class="section-kicker">Personal Dashboard</span>
            <h1>Wxx 的成长驾驶舱</h1>
            <p class="section-copy">这里不强调产品介绍，而是只展示你的进度、短板、资产和下一步动作。</p>
          </div>
        </div>
        <div class="profile-hero__actions">
          <a-button>编辑资料</a-button>
          <a-button type="primary" class="brand-button">查看最新报告</a-button>
        </div>
      </section>

      <section class="dashboard-grid profile-grid">
        <article class="glass-panel span-4 profile-side-panel">
          <span class="section-kicker">Overview</span>
          <div class="profile-metric-list">
            <div class="profile-metric-item" v-for="item in profileStats" :key="item.label">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </div>
          </div>
          <div class="topic-cloud" style="margin-top: 18px;">
            <span v-for="item in profileTags" :key="item">{{ item }}</span>
          </div>
        </article>

        <article class="glass-panel span-8 profile-main-panel">
          <div class="section-headline">
            <div>
              <span class="section-kicker">Growth Board</span>
              <h2>最近成长信号</h2>
            </div>
          </div>
          <div class="profile-card-list">
            <article class="profile-insight-card" v-for="item in profilePanels" :key="item.title">
              <div class="profile-insight-card__top">
                <strong>{{ item.title }}</strong>
                <span>{{ item.value }}</span>
              </div>
              <p>{{ item.summary }}</p>
            </article>
          </div>
        </article>

        <article class="glass-panel span-7">
          <div class="section-headline">
            <div>
              <span class="section-kicker">Recent Records</span>
              <h2>最近的训练记录</h2>
            </div>
          </div>
          <div class="profile-record-list">
            <div class="profile-record-item" v-for="item in records" :key="item.title">
              <strong>{{ item.title }}</strong>
              <p>{{ item.copy }}</p>
            </div>
          </div>
        </article>

        <article class="glass-panel span-5">
          <span class="section-kicker">Next Step</span>
          <div class="profile-next-card">
            <h3>下一步更值得做什么</h3>
            <p class="section-copy">先补缓存一致性专题，再做一轮项目表达型面试，把知识点和表达结构一起补齐。</p>
            <a-button type="primary" class="brand-button">开始下一轮训练</a-button>
          </div>
        </article>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/layouts/AppLayout.vue'

const profileStats = [
  { label: '连续活跃', value: '7 天' },
  { label: '收藏题目', value: '18' },
  { label: '面试记录', value: '6' },
  { label: '本周刷题', value: '25' }
]

const profileTags = ['Java 后端', 'Redis', '项目表达', '系统设计', '错题复盘']

const profilePanels = [
  { title: '最近面试报告', summary: '本轮薄弱项集中在缓存一致性与项目表达，知识覆盖开始稳定。', value: '72 分' },
  { title: '本周练习进度', summary: '已完成 25 道题，正确率 80%，进入第二轮复盘阶段。', value: '80%' },
  { title: '社区互动热度', summary: '最近 7 天共发布 3 条动态，收到 41 条回复。', value: '41' }
]

const records = [
  { title: 'Java 后端核心知识链', copy: '完成 68%，建议本周内做完最后 2 节。' },
  { title: 'Redis 一致性专题', copy: '刚完成第一轮，下一步适合回错题与边界条件。' },
  { title: '项目表达型面试', copy: '上次报告提到“缺少线上排查细节”，建议补一轮。' }
]
</script>
