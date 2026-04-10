import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('colacode_token') || '',
    userInfo: JSON.parse(localStorage.getItem('colacode_user') || 'null')
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    setAuth(payload) {
      this.token = payload?.token || ''
      this.userInfo = payload?.userInfo || null
      localStorage.setItem('colacode_token', this.token)
      localStorage.setItem('colacode_user', JSON.stringify(this.userInfo))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('colacode_token')
      localStorage.removeItem('colacode_user')
    }
  }
})
