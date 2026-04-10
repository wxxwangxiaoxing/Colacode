<template>
  <AppLayout>
    <section class="auth-hero auth-hero--register auth-hero--balanced">
      <div class="auth-hero__bg"></div>

      <article class="auth-panel auth-panel--intro auth-panel--intro-wide">
        <div class="auth-copy auth-copy--wide">
          <div class="auth-kicker-row">
            <span class="section-kicker">Create Account</span>
            <span class="auth-kicker-note">把成长变成长期资产</span>
          </div>
          <h1 class="auth-title auth-title--register">
            创建<span>账号</span><br />
            开始你的进阶之旅
          </h1>
          <p class="auth-subtitle auth-subtitle--register">
            加入 ColaCode，把刷题、面试、社区互动沉淀成真正可持续的成长记录。
            不是一次性使用的工具，而是一套围绕程序员能力提升构建的学习工作台。
          </p>
        </div>

        <div class="auth-feature-list auth-feature-list--compact auth-feature-list--spacious">
          <div class="auth-feature-item auth-feature-item--soft" v-for="item in benefits" :key="item.title">
            <div class="auth-feature-item__icon" :style="{ background: item.color }">
              <component :is="item.icon" class="auth-feature-item__icon-svg" />
            </div>
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.copy }}</p>
            </div>
          </div>
        </div>

        <div class="auth-info-card">
          <div class="auth-info-card__header">
            <strong>注册后你将获得</strong>
            <span>一站式学习闭环</span>
          </div>
          <div class="auth-info-card__grid">
            <div class="auth-info-stat" v-for="item in stats" :key="item.label">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </div>
      </article>

      <article class="auth-panel auth-panel--form auth-panel--form-wide">
        <div class="auth-tabs auth-tabs--single auth-tabs--register">
          <button class="auth-tabs__item auth-tabs__item--active" type="button">注册账号</button>
        </div>

        <p class="auth-card-copy">
          创建一个专属账户，保存你的学习记录、面试报告与社区成长轨迹。
        </p>

        <a-form layout="vertical" class="auth-clean-form auth-clean-form--register">
          <div class="auth-form-grid">
            <a-form-item class="auth-form-grid__full">
              <a-input v-model:value="form.userName" size="large" placeholder="用户名 *">
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item>
              <a-input v-model:value="form.email" size="large" placeholder="邮箱">
                <template #prefix>
                  <MailOutlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item>
              <a-input v-model:value="form.phone" size="large" placeholder="手机号">
                <template #prefix>
                  <MobileOutlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item>
              <a-input-password v-model:value="form.password" size="large" placeholder="密码 *">
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item>
              <a-input-password v-model:value="form.confirmPassword" size="large" placeholder="确认密码 *">
                <template #prefix>
                  <SafetyOutlined />
                </template>
              </a-input-password>
            </a-form-item>
          </div>

          <div class="auth-form-meta auth-form-meta--stack auth-form-meta--register">
            <a-checkbox v-model:checked="agreed">我已阅读并同意服务条款与隐私政策</a-checkbox>
          </div>

          <a-button type="primary" size="large" block class="auth-submit-button">注 册</a-button>
        </a-form>

        <p class="auth-switch-copy auth-switch-copy--tight">
          已有账号?
          <RouterLink to="/login">立即登录</RouterLink>
        </p>
      </article>
    </section>
  </AppLayout>
</template>

<script setup>
import { reactive, ref } from 'vue'
import {
  BookOutlined,
  BulbOutlined,
  LockOutlined,
  MailOutlined,
  MobileOutlined,
  RobotOutlined,
  SafetyOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import AppLayout from '@/layouts/AppLayout.vue'

const agreed = ref(true)
const form = reactive({
  userName: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const benefits = [
  { icon: RobotOutlined, title: '面试闭环训练', copy: '从关键词分析到报告复盘，一套流程跑通。', color: 'linear-gradient(180deg, #4f8cff, #2b67f6)' },
  { icon: BookOutlined, title: '题库与套题', copy: '按知识点和方向拆分训练，沉淀长期能力。', color: 'linear-gradient(180deg, #46d39a, #22b573)' },
  { icon: BulbOutlined, title: '经验社区', copy: '围绕面经、踩坑和项目表达进行高质量互动。', color: 'linear-gradient(180deg, #ffb14a, #ff8b2b)' }
]

const stats = [
  { value: '20k+', label: '优质题库' },
  { value: 'AI', label: '模拟面试' },
  { value: '7 x 24', label: '成长记录' },
  { value: '社区', label: '经验沉淀' }
]
</script>
