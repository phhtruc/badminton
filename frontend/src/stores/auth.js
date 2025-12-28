import { defineStore } from 'pinia'
import { authService } from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    accessTokenExpiry: parseInt(localStorage.getItem('accessTokenExpiry') || '0'),
    isAuthenticated: !!localStorage.getItem('accessToken')
  }),

  getters: {
    isAdmin: (state) => state.user?.role === 'ADMIN',
    currentUser: (state) => state.user,
    isTokenExpired: (state) => {
      if (!state.accessTokenExpiry) return true
      return Date.now() >= state.accessTokenExpiry
    }
  },

  actions: {
    async login(credentials) {
      try {
        const response = await authService.login(credentials)
        this.setAuth(response.data)
        return response.data
      } catch (error) {
        throw error
      }
    },

    async register(userData) {
      try {
        const response = await authService.register(userData)
        this.setAuth(response.data)
        return response.data
      } catch (error) {
        throw error
      }
    },

    setAuth(data) {
      this.accessToken = data.accessToken
      this.refreshToken = data.refreshToken
      this.accessTokenExpiry = data.accessTokenExpiry
      this.user = {
        id: data.id,
        email: data.email,
        fullName: data.fullName,
        nickname: data.nickname,
        role: data.role
      }
      this.isAuthenticated = true

      // Save to localStorage
      localStorage.setItem('accessToken', this.accessToken)
      localStorage.setItem('refreshToken', this.refreshToken)
      localStorage.setItem('accessTokenExpiry', this.accessTokenExpiry.toString())
      localStorage.setItem('user', JSON.stringify(this.user))
    },

    async refreshAccessToken() {
      try {
        if (!this.refreshToken) {
          throw new Error('No refresh token available')
        }

        const response = await authService.refreshToken({ refreshToken: this.refreshToken })
        this.setAuth(response.data)
        return response.data.accessToken
      } catch (error) {
        // If refresh fails, logout user
        this.logout()
        throw error
      }
    },

    async fetchCurrentUser() {
      try {
        const response = await authService.getCurrentUser()
        this.user = response.data
        localStorage.setItem('user', JSON.stringify(this.user))
      } catch (error) {
        // If token is invalid, try to refresh
        if (error.response?.status === 401) {
          try {
            await this.refreshAccessToken()
            // Retry fetching user
            const response = await authService.getCurrentUser()
            this.user = response.data
            localStorage.setItem('user', JSON.stringify(this.user))
          } catch (refreshError) {
            this.logout()
          }
        } else {
          this.logout()
        }
      }
    },

    async logout() {
      try {
        // Call logout API to clear refresh token on backend
        if (this.accessToken) {
          await authService.logout()
        }
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        // Clear local state
        this.user = null
        this.accessToken = null
        this.refreshToken = null
        this.accessTokenExpiry = 0
        this.isAuthenticated = false

        // Clear localStorage
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('accessTokenExpiry')
        localStorage.removeItem('user')
      }
    },

    initFromStorage() {
      const user = localStorage.getItem('user')
      const accessToken = localStorage.getItem('accessToken')
      const accessTokenExpiry = localStorage.getItem('accessTokenExpiry')

      if (user && accessToken) {
        this.user = JSON.parse(user)
        this.accessToken = accessToken
        this.accessTokenExpiry = parseInt(accessTokenExpiry || '0')
        this.isAuthenticated = true

        // Check if token is expired
        if (this.isTokenExpired) {
          // Try to refresh token
          this.refreshAccessToken().catch(() => {
            this.logout()
          })
        }
      }
    }
  }
})
