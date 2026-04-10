<template>
  <AppLayout>
    <div class="page-stack community-thread-page">
      <section class="glass-panel thread-breadcrumb">
        <RouterLink to="/community">社区</RouterLink>
        <span>/</span>
        <RouterLink :to="`/community/board/${thread.boardSlug}`">{{ thread.boardName }}</RouterLink>
        <span>/</span>
        <strong>{{ thread.shortTitle }}</strong>
      </section>

      <section class="thread-layout">
        <main class="thread-main">
          <article class="glass-panel thread-main-post">
            <div class="thread-main-post__head">
              <div>
                <div class="thread-main-post__tags">
                  <a-tag :color="thread.color">{{ thread.category }}</a-tag>
                  <a-tag v-for="tag in thread.tags" :key="tag"># {{ tag }}</a-tag>
                </div>
                <h1>{{ thread.title }}</h1>
              </div>
              <div class="thread-main-post__meta">
                <span>{{ thread.views }} 浏览</span>
                <span>{{ displayedReplies.length + 1 }} 回复</span>
              </div>
            </div>

            <div class="thread-author-card">
              <span class="community-avatar">{{ thread.author[0] }}</span>
              <div>
                <strong>{{ thread.author }}</strong>
                <p>{{ thread.role }} · 发布于 {{ thread.time }}</p>
              </div>
            </div>

            <div class="thread-article">
              <p v-for="paragraph in thread.paragraphs" :key="paragraph">{{ paragraph }}</p>
            </div>

            <div class="thread-main-post__footer">
              <a-button type="primary" class="brand-button" @click="scrollToEditor">回复主题</a-button>
              <a-button>收藏</a-button>
              <a-button>分享</a-button>
            </div>
          </article>

          <section ref="editorRef" class="glass-panel thread-editor-card">
            <div class="thread-editor-card__head">
              <div>
                <span class="section-kicker">Reply Editor</span>
                <h2>写下你的回复</h2>
              </div>
              <a-segmented v-model:value="editorMode" :options="['编辑', '预览']" />
            </div>

            <div class="thread-editor-meta">
              <div class="thread-author-card thread-author-card--compact">
                <span class="community-avatar">W</span>
                <div>
                  <strong>Wxx</strong>
                  <p>准备围绕主题补充自己的理解和追问。</p>
                </div>
              </div>
              <div class="thread-editor-meta__actions">
                <a-button>插入代码块</a-button>
                <a-button @click="reapplyQuote" :disabled="!quotedReply">引用楼层</a-button>
              </div>
            </div>

            <div v-if="quotedReply" class="thread-quote-banner">
              <div>
                <strong>正在引用 {{ quotedReply.floor }} · {{ quotedReply.author }}</strong>
                <p>{{ quotedReply.content }}</p>
              </div>
              <a-button type="link" @click="clearQuote">移除引用</a-button>
            </div>

            <div v-if="editorMode === '编辑'" class="thread-editor-input">
              <a-textarea
                v-model:value="replyContent"
                :rows="8"
                placeholder="建议先写场景、再写观点、最后补充你的理由或追问。"
              />
            </div>
            <div v-else class="thread-editor-preview">
              <p v-if="replyContent.trim()">{{ replyContent }}</p>
              <p v-else class="thread-editor-preview__empty">预览区会显示你准备发布的回复内容。</p>
            </div>

            <div class="thread-editor-footer">
              <span>{{ replyContent.trim().length }} 字</span>
              <div class="thread-editor-footer__actions">
                <a-button @click="clearReply">清空</a-button>
                <a-button type="primary" class="brand-button" @click="submitReply">发布回复</a-button>
              </div>
            </div>
          </section>

          <section class="thread-reply-list">
            <article
              class="glass-panel thread-reply-card"
              v-for="reply in displayedReplies"
              :key="reply.floor + reply.author + reply.time"
              :id="replyAnchorId(reply)"
            >
              <div class="thread-reply-card__head">
                <div class="thread-author-card thread-author-card--compact">
                  <span class="community-avatar">{{ reply.author[0] }}</span>
                  <div>
                    <strong>{{ reply.author }}</strong>
                    <p>{{ reply.role }} · {{ reply.time }}</p>
                  </div>
                </div>
                <span class="thread-reply-card__floor">{{ reply.floor }}</span>
              </div>
              <p>{{ reply.content }}</p>
              <div class="thread-reply-card__actions">
                <a-button type="link" @click="quoteReply(reply)">引用回复</a-button>
                <a-button type="link" @click="toggleLike(reply)">
                  {{ isLiked(reply) ? '已点赞' : '点赞' }} {{ reply.likes }}
                </a-button>
                <a-button type="link" @click="copyFloorLink(reply)">复制楼层</a-button>
                <a-button type="link" @click="jumpToFloor(reply)">定位楼层</a-button>
              </div>
            </article>
          </section>
        </main>

        <aside class="thread-side">
          <article class="glass-panel thread-side-card">
            <span class="section-kicker">Thread Summary</span>
            <div class="thread-summary-list">
              <div>
                <strong>{{ thread.boardName }}</strong>
                <span>所属板块</span>
              </div>
              <div>
                <strong>{{ thread.lastReply }}</strong>
                <span>最后活跃</span>
              </div>
              <div>
                <strong>{{ thread.collects }}</strong>
                <span>收藏数</span>
              </div>
            </div>
          </article>

          <article class="glass-panel thread-side-card">
            <span class="section-kicker">Related Threads</span>
            <div class="forum-pick-list">
              <RouterLink v-for="item in thread.related" :key="item.id" :to="`/community/thread/${item.id}`" class="forum-pick-item forum-pick-item--link">
                <strong>{{ item.title }}</strong>
                <p class="section-copy">{{ item.copy }}</p>
              </RouterLink>
            </div>
          </article>
        </aside>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'

const route = useRoute()
const editorRef = ref(null)
const editorMode = ref('编辑')
const replyContent = ref('')
const localReplies = ref([])
const quotedReply = ref(null)
const likedFloors = ref(new Set())

const threadMap = {
  101: {
    shortTitle: 'Redis 双删策略',
    boardSlug: 'qa',
    boardName: '技术问答区',
    category: '问答',
    color: 'blue',
    title: 'Redis 双删策略到底适合哪些场景？',
    author: 'Luna',
    role: '服务端开发',
    time: '今天 19:20',
    views: '2.4k',
    replies: '61',
    collects: '96',
    lastReply: '3 分钟前',
    tags: ['Redis', '缓存一致性'],
    paragraphs: [
      '我发现很多人在回答缓存一致性时，会条件反射地说“延迟双删”，但一旦继续追问什么场景下适合、为什么需要补偿、失败了怎么办，答案就开始松掉。',
      '我自己的理解是，双删适合在写路径明确、读写竞争可控、并且能接受短暂不一致的业务里使用。它不是银弹，更像一种结合业务容忍度的折中方案。',
      '如果是热点数据、高并发覆盖写、或者链路里还有异步回写，那光背一个“双删”就远远不够，至少要补上重试、监控、补偿和边界说明。'
    ],
    repliesList: [
      { author: 'Kite', role: '面试冲刺中', time: '今天 19:28', floor: '2F', likes: 12, content: '我觉得最关键的是把“为什么延迟”讲清楚，不然面试官很容易接着问延迟多久、依据是什么。' },
      { author: 'Stone', role: '后端开发', time: '今天 19:41', floor: '3F', likes: 8, content: '线上场景里我会顺手补一句监控和补偿策略，这样会比单讲方案更有工程感。' }
    ],
    related: [
      { id: 202, title: 'Redis 一致性到底该怎么回答才有工程味？', copy: '从场景、代价和补偿角度补全回答。' },
      { id: 105, title: '一次缓存一致性事故的完整复盘', copy: '把理论方案和线上问题连起来看。' }
    ]
  },
  103: {
    shortTitle: 'Java 面经追问',
    boardSlug: 'interview',
    boardName: '面经交流区',
    category: '面经',
    color: 'purple',
    title: 'Java 校招面试里最容易答空的追问整理',
    author: '张三',
    role: '后端校招生',
    time: '今天 15:06',
    views: '3.2k',
    replies: '92',
    collects: '173',
    lastReply: '6 分钟前',
    tags: ['Java', '面经'],
    paragraphs: [
      '最近连着几场面试下来，我发现真正让我失分的不是第一问，而是后面的追问。很多知识点我知道，但一被继续问就会越答越散。',
      '所以我开始把高频追问整理出来，比如 Bean 生命周期、事务失效、索引失效、缓存一致性，每个点都尽量补一层“为什么”和“怎么排查”。',
      '如果你也有类似情况，建议不要只记答案，先把答题结构练出来。结构比记忆更抗追问。'
    ],
    repliesList: [
      { author: 'Anny', role: '项目表达教练', time: '今天 15:30', floor: '2F', likes: 16, content: '这类帖子最有价值的地方就是把“继续追问会问什么”提前暴露出来。' },
      { author: 'Milo', role: '后端学习者', time: '今天 16:02', floor: '3F', likes: 9, content: '我最近也在按“定义-原理-场景-排查”的顺序练，确实稳很多。' }
    ],
    related: [
      { id: 104, title: '后端项目怎么讲出“工程感”？', copy: '表达结构可以迁移到项目题上。' },
      { id: 102, title: 'MySQL 联合索引最左匹配，什么时候最容易答空？', copy: '把具体知识点追问也练起来。' }
    ]
  }
}

const fallback = {
  shortTitle: '帖子详情',
  boardSlug: 'qa',
  boardName: '技术问答区',
  category: '讨论',
  color: 'blue',
  title: '这是一篇论坛主题帖示例',
  author: 'ColaCode',
  role: '社区成员',
  time: '今天',
  views: '980',
  replies: '12',
  collects: '8',
  lastReply: '1 小时前',
  tags: ['示例'],
  paragraphs: ['这里是帖子正文示例。后续接真实接口后，这里会展示完整帖子内容和回复。'],
  repliesList: [{ author: 'Demo', role: '成员', time: '刚刚', floor: '2F', likes: 1, content: '这里是回帖示例。' }],
  related: []
}

const thread = computed(() => threadMap[route.params.id] || fallback)
const displayedReplies = computed(() => [...thread.value.repliesList, ...localReplies.value])

watch(
  () => route.params.id,
  () => {
    replyContent.value = ''
    editorMode.value = '编辑'
    localReplies.value = []
    quotedReply.value = null
    likedFloors.value = new Set()
  },
  { immediate: true }
)

function scrollToEditor() {
  editorRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function clearReply() {
  replyContent.value = ''
  quotedReply.value = null
}

function replyAnchorId(reply) {
  return `floor-${String(reply.floor).toLowerCase()}`
}

function quoteReply(reply) {
  quotedReply.value = reply
  replyContent.value = `> 引用 ${reply.floor} ${reply.author}\n> ${reply.content}\n\n`
  editorMode.value = '编辑'
  scrollToEditor()
}

function reapplyQuote() {
  if (!quotedReply.value) return
  quoteReply(quotedReply.value)
}

function clearQuote() {
  quotedReply.value = null
}

function isLiked(reply) {
  return likedFloors.value.has(reply.floor)
}

function toggleLike(reply) {
  const next = new Set(likedFloors.value)
  if (next.has(reply.floor)) {
    next.delete(reply.floor)
    reply.likes -= 1
  } else {
    next.add(reply.floor)
    reply.likes += 1
  }
  likedFloors.value = next
}

function copyFloorLink(reply) {
  const link = `${window.location.origin}${window.location.pathname}#${replyAnchorId(reply)}`
  if (navigator?.clipboard?.writeText) {
    navigator.clipboard.writeText(link)
  }
}

function jumpToFloor(reply) {
  document.getElementById(replyAnchorId(reply))?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

function submitReply() {
  const content = replyContent.value.trim()
  if (!content) {
    return
  }

  const floorNo = thread.value.repliesList.length + localReplies.value.length + 2
  localReplies.value.push({
    author: 'Wxx',
    role: '社区成员',
    time: '刚刚',
    floor: `${floorNo}F`,
    likes: 0,
    content
  })
  replyContent.value = ''
  quotedReply.value = null
  editorMode.value = '编辑'
}
</script>
