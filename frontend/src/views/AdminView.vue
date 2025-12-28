<template>
  <div class="admin-page">
    <div class="container">
      <h1>Quản Trị Hệ Thống</h1>

      <div class="admin-stats">
        <div class="stat-card card">
          <h3>{{ pendingBookings.length }}</h3>
          <p>Đơn chờ xác nhận</p>
        </div>
      </div>

      <div class="pending-section">
        <h2>Đơn Đặt Sân Chờ Xác Nhận</h2>

        <div v-if="loading" class="loading">Đang tải...</div>

        <div v-else-if="pendingBookings.length === 0" class="no-bookings card">
          <p>Không có đơn đặt sân nào chờ xác nhận</p>
        </div>

        <div v-else class="bookings-list">
          <div v-for="booking in pendingBookings" :key="booking.id" class="booking-card card">
            <div class="booking-header">
              <h3>{{ booking.courtName }}</h3>
              <span class="status-badge status-pending">Chờ xác nhận</span>
            </div>

            <div class="booking-details">
              <div class="detail-row">
                <strong>👤 Khách hàng:</strong> {{ booking.userFullName }}
              </div>
              <div class="detail-row">
                <strong>📧 Email:</strong> {{ booking.userEmail }}
              </div>
              <div class="detail-row" v-if="booking.userPhone">
                <strong>📱 Số điện thoại:</strong> {{ booking.userPhone }}
              </div>
              <div class="detail-row">
                <strong>📅 Ngày:</strong> {{ formatDate(booking.bookingDate) }}
              </div>
              <div class="detail-row">
                <strong>🕐 Thời gian:</strong> {{ booking.startTime }} - {{ booking.endTime }}
              </div>
              <div class="detail-row" v-if="booking.note">
                <strong>📝 Ghi chú:</strong> {{ booking.note }}
              </div>
              <div class="detail-row">
                <strong>🕒 Đặt lúc:</strong> {{ formatDateTime(booking.createdAt) }}
              </div>
            </div>

            <div class="booking-actions">
              <button
                @click="approveBooking(booking.id)"
                class="btn btn-primary"
                :disabled="processing"
              >
                ✅ Xác nhận
              </button>
              <button
                @click="rejectBooking(booking.id)"
                class="btn btn-danger"
                :disabled="processing"
              >
                ❌ Từ chối
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { adminService } from '../services/api'

export default {
  name: 'AdminView',
  setup() {
    const pendingBookings = ref([])
    const loading = ref(true)
    const processing = ref(false)

    const loadPendingBookings = async () => {
      loading.value = true
      try {
        const response = await adminService.getPendingBookings()
        pendingBookings.value = response.data
      } catch (error) {
        console.error('Error loading pending bookings:', error)
        alert('Không thể tải danh sách đơn đặt sân')
      } finally {
        loading.value = false
      }
    }

    const approveBooking = async (bookingId) => {
      if (!confirm('Xác nhận duyệt đơn đặt sân này?')) return

      processing.value = true
      try {
        await adminService.approveBooking(bookingId)
        alert('Đã xác nhận đơn đặt sân!')
        await loadPendingBookings()
      } catch (error) {
        console.error('Error approving booking:', error)
        alert('Không thể xác nhận đơn đặt sân')
      } finally {
        processing.value = false
      }
    }

    const rejectBooking = async (bookingId) => {
      if (!confirm('Xác nhận từ chối đơn đặt sân này?')) return

      processing.value = true
      try {
        await adminService.rejectBooking(bookingId)
        alert('Đã từ chối đơn đặt sân!')
        await loadPendingBookings()
      } catch (error) {
        console.error('Error rejecting booking:', error)
        alert('Không thể từ chối đơn đặt sân')
      } finally {
        processing.value = false
      }
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
      loadPendingBookings()
    })

    return {
      pendingBookings,
      loading,
      processing,
      approveBooking,
      rejectBooking,
      formatDate,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.admin-page h1 {
  text-align: center;
  color: var(--primary-green);
  margin-bottom: 2rem;
}

.admin-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  text-align: center;
  background: linear-gradient(135deg, var(--primary-yellow), #FFE082);
  padding: 2rem;
}

.stat-card h3 {
  font-size: 3rem;
  color: var(--primary-green);
  margin: 0;
}

.stat-card p {
  font-size: 1.1rem;
  margin: 0.5rem 0 0;
}

.pending-section h2 {
  color: var(--dark-green);
  margin-bottom: 1.5rem;
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
  margin-bottom: 1.5rem;
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

.booking-details {
  display: grid;
  gap: 0.8rem;
  margin-bottom: 1.5rem;
}

.detail-row {
  display: flex;
  gap: 0.5rem;
  line-height: 1.6;
}

.detail-row strong {
  min-width: 150px;
  color: var(--dark-green);
}

.booking-actions {
  display: flex;
  gap: 1rem;
}

.booking-actions button {
  flex: 1;
}

@media (max-width: 768px) {
  .detail-row {
    flex-direction: column;
  }

  .detail-row strong {
    min-width: auto;
  }
}
</style>

