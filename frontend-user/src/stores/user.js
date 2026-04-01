import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  
  const isLoggedIn = computed(() => !!token.value)
  
  async function login(data) {
    const res = await loginApi(data)
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data.user))
    return res
  }
  
  async function fetchUserInfo() {
    const res = await getUserInfo()
    userInfo.value = res.data
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res
  }
  
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    fetchUserInfo,
    logout
  }
})
