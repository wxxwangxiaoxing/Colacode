import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    searchKeyword: '',
    commandVisible: false
  }),
  actions: {
    toggleCommandPanel(visible) {
      this.commandVisible = typeof visible === 'boolean' ? visible : !this.commandVisible
    },
    setSearchKeyword(keyword) {
      this.searchKeyword = keyword
    }
  }
})
