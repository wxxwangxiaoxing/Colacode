<template>
  <AppLayout>
    <section class="glass-panel" style="text-align:center;padding:72px 24px;">
      <span class="section-kicker">404</span>
      <h1 class="section-title" style="margin-top:18px;">页面找不到了</h1>
      <p class="section-copy">你访问的页面可能还没开发完成，或者路径已经发生变化。</p>
      <RouterLink to="/">
        <a-button type="primary" class="brand-button" style="margin-top:18px;">返回首页</a-button>
      </RouterLink>
    </section>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/layouts/AppLayout.vue'
</script>
