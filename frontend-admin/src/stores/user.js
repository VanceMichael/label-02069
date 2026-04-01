import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_userInfo') || 'null'))
  
  const isLoggedIn = computed(() => !!token.value)
  
  async function login(data) {
    const res = await loginApi(data)
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('admin_token', res.data.token)
    localStorage.setItem('admin_userInfo', JSON.stringify(res.data.user))
    return res
  }
  
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_userInfo')
  }
  
  return { token, userInfo, isLoggedIn, login, logout }
})
