import axios from 'axios'

const API_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Auth Service
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
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
