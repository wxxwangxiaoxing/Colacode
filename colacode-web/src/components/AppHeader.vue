<template>
  <header class="site-header site-header--light">
    <div class="site-header__inner site-header__inner--auth">
      <RouterLink class="site-brand site-brand--auth" to="/">
        <span class="site-brand__mark">
          <span class="site-brand__diamond"></span>
        </span>
        <span class="site-brand__text">
          <strong>ColaCode</strong>
          <small>程序员专属社区平台</small>
        </span>
      </RouterLink>

      <nav class="site-nav site-nav--auth">
        <RouterLink v-for="item in navItems" :key="item.path" :to="item.path" class="site-nav__link site-nav__link--auth">
          {{ item.label }}
        </RouterLink>
      </nav>

      <div class="site-header__actions site-header__actions--auth">
        <RouterLink to="/login">
          <a-button class="site-ghost-button">登录</a-button>
        </RouterLink>
        <RouterLink to="/register">
          <a-button class="site-outline-button">注册</a-button>
        </RouterLink>
      </div>
    </div>
  </header>
</template>

<script setup>
const navItems = [
  { label: '首页', path: '/' },
  { label: '题库', path: '/subjects' },
  { label: '练习', path: '/practice' },
  { label: '面试', path: '/interview' },
  { label: '社区', path: '/community' },
  { label: '定价', path: '/pricing' }
]
</script>
