<template>
  <AppLayout>
    <div class="page-stack pricing-page">
      <section class="pricing-hero glass-panel">
        <div>
          <span class="section-kicker">Pricing</span>
          <h1>按阶段选择训练密度，而不是一次买断所有功能。</h1>
          <p class="section-copy">定价页应该服务转化，所以重点是方案差异、权益对比和清晰的主推方案。</p>
        </div>
        <div class="pricing-hero__note">支持月付与季度付费，适合校招冲刺和长期成长两种节奏。</div>
      </section>

      <section class="pricing-grid">
        <article class="pricing-plan" :class="{ 'pricing-plan--featured': plan.primary }" v-for="plan in plans" :key="plan.name">
          <div class="pricing-plan__head">
            <span class="section-kicker">{{ plan.tag }}</span>
            <h3>{{ plan.name }}</h3>
            <div class="pricing-plan__price">{{ plan.price }}</div>
            <p>{{ plan.copy }}</p>
          </div>
          <ul class="pricing-plan__list">
            <li v-for="item in plan.items" :key="item">{{ item }}</li>
          </ul>
          <a-button :type="plan.primary ? 'primary' : 'default'" :class="plan.primary ? 'brand-button' : ''">选择方案</a-button>
        </article>
      </section>

      <section class="glass-panel pricing-compare">
        <div class="section-headline">
          <div>
            <span class="section-kicker">Compare</span>
            <h2>权益差异</h2>
          </div>
        </div>
        <div class="pricing-compare__table">
          <div class="pricing-compare__row pricing-compare__row--head">
            <span>权益</span>
            <span>体验版</span>
            <span>强化版</span>
            <span>成长版</span>
          </div>
          <div class="pricing-compare__row" v-for="item in compareRows" :key="item.label">
            <span>{{ item.label }}</span>
            <span>{{ item.free }}</span>
            <span>{{ item.pro }}</span>
            <span>{{ item.plus }}</span>
          </div>
        </div>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/layouts/AppLayout.vue'

const plans = [
  { tag: 'Free', name: '体验版', price: '¥0', copy: '适合初次体验题库与基础面试能力。', items: ['基础题库', '部分社区内容', '有限面试次数'], primary: false },
  { tag: 'Pro', name: '强化版', price: '¥39 / 月', copy: '适合冲刺校招与社招面试，强调训练闭环。', items: ['无限面试会话', '完整报告', '专题练习推荐'], primary: true },
  { tag: 'Plus', name: '成长版', price: '¥99 / 季', copy: '适合长期沉淀个人技术成长记录。', items: ['成长面板', '高级社区权益', '专项训练路线'], primary: false }
]

const compareRows = [
  { label: '题库访问', free: '基础', pro: '完整', plus: '完整' },
  { label: 'AI 面试次数', free: '有限', pro: '无限', plus: '无限' },
  { label: '报告深度', free: '基础', pro: '完整', plus: '完整' },
  { label: '成长面板', free: '-', pro: '-', plus: '支持' }
]
</script>
