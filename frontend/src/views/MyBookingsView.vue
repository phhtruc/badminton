<template>
  <div class="my-bookings-page">
    <div class="container">
      <h1>Lịch Đặt Sân Của Tôi</h1>

      <div v-if="loading" class="loading">Đang tải...</div>

      <div v-else-if="bookings.length === 0" class="no-bookings card">
        <p>Bạn chưa có lịch đặt sân nào.</p>
        <router-link to="/booking" class="btn btn-primary">Đặt sân ngay</router-link>
      </div>

      <div v-else class="bookings-list">
        <div v-for="booking in sortedBookings" :key="booking.id" class="booking-card card">
          <div class="booking-header">
            <h3>{{ booking.courtName }}</h3>
            <span class="status-badge" :class="`status-${booking.status.toLowerCase()}`">
              {{ getStatusText(booking.status) }}
            </span>
          </div>
          <div class="booking-details">
            <p><strong>📅 Ngày:</strong> {{ formatDate(booking.bookingDate) }}</p>
            <p><strong>🕐 Thời gian:</strong> {{ booking.startTime }} - {{ booking.endTime }}</p>
            <p v-if="booking.note"><strong>📝 Ghi chú:</strong> {{ booking.note }}</p>
            <p><strong>🕒 Đặt lúc:</strong> {{ formatDateTime(booking.createdAt) }}</p>
            <p v-if="booking.approvedAt">
              <strong>✅ Xác nhận lúc:</strong> {{ formatDateTime(booking.approvedAt) }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { bookingService } from '../services/api'

export default {
  name: 'MyBookingsView',
  setup() {
    const bookings = ref([])
    const loading = ref(true)

    const sortedBookings = computed(() => {
      return [...bookings.value].sort((a, b) => {
        return new Date(b.bookingDate) - new Date(a.bookingDate)
      })
    })

    const loadBookings = async () => {
      try {
        const response = await bookingService.getMyBookings()
        bookings.value = response.data
      } catch (error) {
        console.error('Error loading bookings:', error)
        alert('Không thể tải danh sách đặt sân')
      } finally {
        loading.value = false
      }
    }

    const getStatusText = (status) => {
      const statusMap = {
        'PENDING': 'Chờ xác nhận',
        'APPROVED': 'Đã xác nhận',
        'REJECTED': 'Bị từ chối'
      }
      return statusMap[status] || status
    }

    const formatDate = (dateStr) => {
      const date = new Date(dateStr)
      return date.toLocaleDateString('vi-VN')
    }

    const formatDateTime = (dateTimeStr) => {
      const date = new Date(dateTimeStr)
      return date.toLocaleString('vi-VN')
    }

    onMounted(() => {
      loadBookings()
    })

    return {
      bookings,
      sortedBookings,
      loading,
      getStatusText,
      formatDate,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.my-bookings-page h1 {
  text-align: center;
  color: var(--primary-green);
  margin-bottom: 2rem;
}

.loading {
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
  color: var(--primary-green);
}

.no-bookings {
  text-align: center;
  padding: 3rem;
}

.no-bookings p {
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
}

.bookings-list {
  display: grid;
  gap: 1.5rem;
}

.booking-card {
  transition: transform 0.3s ease;
}

.booking-card:hover {
  transform: translateY(-3px);
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid var(--border-color);
}

.booking-header h3 {
  color: var(--primary-green);
  margin: 0;
}

.status-badge {
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-approved {
  background: #d4edda;
  color: #155724;
}

.status-rejected {
  background: #f8d7da;
  color: #721c24;
}

.booking-details p {
  margin: 0.5rem 0;
  line-height: 1.6;
}
</style>

