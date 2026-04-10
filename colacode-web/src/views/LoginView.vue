<template>
  <AppLayout>
    <section class="auth-hero auth-hero--login">
      <div class="auth-hero__bg"></div>

      <article class="auth-panel auth-panel--intro">
        <div class="auth-copy">
          <h1 class="auth-title">
            欢迎<span>回来！</span><br />
            登录 ColaCode
          </h1>
          <p class="auth-subtitle">继续你的学习之旅，提升技术，成就未来</p>
        </div>

        <div class="auth-feature-list">
          <div class="auth-feature-item" v-for="item in features" :key="item.title">
            <div class="auth-feature-item__icon" :style="{ background: item.color }">
              <component :is="item.icon" class="auth-feature-item__icon-svg" />
            </div>
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.copy }}</p>
            </div>
          </div>
        </div>

        <div class="auth-illustration">
          <div class="auth-illustration__halo"></div>
          <div class="auth-illustration__screen">
            <div class="auth-illustration__code-line w-72"></div>
            <div class="auth-illustration__code-line w-48"></div>
            <div class="auth-illustration__code-line w-64"></div>
            <div class="auth-illustration__code-line w-36"></div>
          </div>
          <div class="auth-illustration__float auth-illustration__float--left">
            <CodeOutlined />
          </div>
          <div class="auth-illustration__float auth-illustration__float--right">
            <DeploymentUnitOutlined />
          </div>
        </div>
      </article>

      <article class="auth-panel auth-panel--form">
        <div class="auth-tabs">
          <button class="auth-tabs__item auth-tabs__item--active" type="button">账号登录</button>
          <button class="auth-tabs__item" type="button">验证码登录</button>
        </div>

        <a-form layout="vertical" class="auth-clean-form">
          <a-form-item>
            <a-input v-model:value="form.userName" size="large" placeholder="请输入用户名或邮箱">
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-input-password v-model:value="form.password" size="large" placeholder="请输入密码">
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <div class="auth-form-meta">
            <a-checkbox v-model:checked="remember">记住我</a-checkbox>
            <a href="#">忘记密码?</a>
          </div>

          <a-button type="primary" size="large" block class="auth-submit-button">登 录</a-button>
        </a-form>

        <div class="auth-divider">或使用以下方式登录</div>

        <div class="auth-socials">
          <button class="auth-social" type="button" aria-label="GitHub 登录">
            <GithubOutlined />
          </button>
          <button class="auth-social" type="button" aria-label="Google 登录">
            <GoogleOutlined />
          </button>
          <button class="auth-social" type="button" aria-label="消息登录">
            <MessageOutlined />
          </button>
        </div>

        <p class="auth-switch-copy">
          还没有账号?
          <RouterLink to="/register">立即注册</RouterLink>
        </p>
      </article>
    </section>
  </AppLayout>
</template>

<script setup>
import { reactive, ref } from 'vue'
import {
  BookOutlined,
  CodeOutlined,
  DeploymentUnitOutlined,
  GithubOutlined,
  GoogleOutlined,
  LockOutlined,
  MessageOutlined,
  RadarChartOutlined,
  RobotOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import AppLayout from '@/layouts/AppLayout.vue'

const remember = ref(true)
const form = reactive({
  userName: '',
  password: ''
})

const features = [
  { icon: BookOutlined, title: '海量题库练习', copy: '20000+ 道优质编程题目', color: 'linear-gradient(180deg, #4f8cff, #2b67f6)' },
  { icon: RobotOutlined, title: 'AI 模拟面试', copy: '真实场景模拟，智能评估', color: 'linear-gradient(180deg, #46d39a, #22b573)' },
  { icon: MessageOutlined, title: '技术社区交流', copy: '与百万开发者一起成长', color: 'linear-gradient(180deg, #ffb14a, #ff8b2b)' },
  { icon: RadarChartOutlined, title: '学习数据统计', copy: '全方位掌握学习进度', color: 'linear-gradient(180deg, #8b6cff, #6846f7)' }
]
</script>
