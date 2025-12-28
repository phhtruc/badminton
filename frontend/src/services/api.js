import axios from 'axios'

const API_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor - Handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // If error is 401 and we haven't retried yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const refreshToken = localStorage.getItem('refreshToken')

        if (!refreshToken) {
          throw new Error('No refresh token')
        }

        // Call refresh token API
        const response = await axios.post(`${API_URL}/auth/refresh-token`, {
          refreshToken: refreshToken
        })

        const { accessToken, refreshToken: newRefreshToken, accessTokenExpiry } = response.data

        // Update tokens in localStorage
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', newRefreshToken)
        localStorage.setItem('accessTokenExpiry', accessTokenExpiry.toString())

        // Update the failed request with new token
        originalRequest.headers.Authorization = `Bearer ${accessToken}`

        // Retry the original request
        return api(originalRequest)
      } catch (refreshError) {
        // Refresh token failed, redirect to login
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('accessTokenExpiry')
        localStorage.removeItem('user')

        // Redirect to login page
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }

        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  }
)

// Auth Service
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  refreshToken: (data) => api.post('/auth/refresh-token', data),
  logout: () => api.post('/auth/logout'),
  getCurrentUser: () => api.get('/auth/me'),
  updateProfile: (data) => api.put('/auth/profile', data),
  changePassword: (data) => api.put('/auth/change-password', data)
}

// Court Service
export const courtService = {
  getAllCourts: () => api.get('/courts/list'),
  getCourtById: (id) => api.get(`/courts/${id}`)
}

// Booking Service
export const bookingService = {
  createBooking: (data) => api.post('/bookings', data),
  getWeeklyBookings: (startDate, endDate) =>
    api.get('/bookings/weekly', { params: { startDate, endDate } }),
  getCourtWeeklyBookings: (courtId, startDate, endDate) =>
    api.get(`/bookings/court/${courtId}/weekly`, { params: { startDate, endDate } }),
  getMyBookings: () => api.get('/bookings/my-bookings')
}

// Admin Service
export const adminService = {
  getPendingBookings: () => api.get('/admin/bookings/pending'),
  approveBooking: (bookingId) => api.put(`/admin/bookings/${bookingId}/approve`),
  rejectBooking: (bookingId) => api.put(`/admin/bookings/${bookingId}/reject`)
}

// Notification Service
export const notificationService = {
  getNotifications: () => api.get('/notifications'),
  getUnreadNotifications: () => api.get('/notifications/unread'),
  markAsRead: (id) => api.put(`/notifications/${id}/read`),
  markAllAsRead: () => api.put('/notifications/read-all')
}

export default api
