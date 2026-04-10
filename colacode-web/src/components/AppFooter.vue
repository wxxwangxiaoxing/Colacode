<template>
  <footer class="site-footer">
    <div class="site-footer__inner site-footer__inner--grid">
      <div class="site-footer__brand">
        <strong>ColaCode</strong>
        <p>程序员专属成长社区，围绕题库、练习、AI 面试与技术交流建立持续训练闭环。</p>
      </div>

      <div class="site-footer__group">
        <h4>产品</h4>
        <RouterLink to="/subjects" class="site-footer__link">题库</RouterLink>
        <RouterLink to="/practice" class="site-footer__link">练习</RouterLink>
        <RouterLink to="/interview" class="site-footer__link">AI 面试</RouterLink>
        <RouterLink to="/pricing" class="site-footer__link">定价</RouterLink>
      </div>

      <div class="site-footer__group">
        <h4>社区</h4>
        <RouterLink to="/community" class="site-footer__link">社区广场</RouterLink>
        <RouterLink to="/community/board/interview" class="site-footer__link">面经交流</RouterLink>
        <RouterLink to="/community/board/project" class="site-footer__link">项目复盘</RouterLink>
        <RouterLink to="/community/board/groups" class="site-footer__link">共学小组</RouterLink>
      </div>

      <div class="site-footer__group">
        <h4>支持</h4>
        <a href="#" class="site-footer__link">关于我们</a>
        <a href="#" class="site-footer__link">联系支持</a>
        <a href="#" class="site-footer__link">使用条款</a>
        <a href="#" class="site-footer__link">隐私政策</a>
      </div>
    </div>

    <div class="site-footer__meta">
      <div class="site-footer__meta-inner">
        <span>© 2026 ColaCode</span>
        <span>程序员专属社区平台</span>
        <span>support@colacode.dev</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>
