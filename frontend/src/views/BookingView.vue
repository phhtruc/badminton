<template>
  <div class="booking-page">
    <div class="container">
      <h1>Đặt Sân Cầu Lông</h1>

      <div class="booking-container">
        <!-- Court Selection -->
        <div class="court-selection card">
          <h3>Chọn sân</h3>
          <select v-model="selectedCourtId" @change="loadBookings">
            <option value="">Tất cả sân</option>
            <option v-for="court in courts" :key="court.id" :value="court.id">
              {{ court.name }}
            </option>
          </select>
        </div>

        <!-- Week Navigation -->
        <div class="week-nav card">
          <button @click="previousWeek" class="btn btn-secondary">← Tuần trước</button>
          <span class="week-range">{{ weekRange }}</span>
          <button @click="nextWeek" class="btn btn-secondary">Tuần sau →</button>
        </div>

        <!-- Calendar Grid -->
        <div class="calendar-container card">
          <div class="calendar-grid">
            <!-- Time Header -->
            <div class="time-header">Giờ</div>
            <div v-for="day in weekDays" :key="day.date" class="day-header">
              <div class="day-name">{{ day.dayName }}</div>
              <div class="day-date">{{ day.dateStr }}</div>
            </div>

            <!-- Time Slots -->
            <template v-for="hour in timeSlots" :key="hour">
              <div class="time-cell">{{ hour }}:00</div>
              <div
                v-for="day in weekDays"
                :key="`${day.date}-${hour}`"
                class="booking-cell"
                @click="openBookingModal(day.date, hour)"
                :class="{
                  'has-booking': hasBooking(day.date, hour),
                  'past': isPast(day.date, hour)
                }"
              >
                <div v-if="getBooking(day.date, hour)" class="booking-info">
                  <strong>{{ getBooking(day.date, hour).courtName }}</strong><br>
                  <small>{{ getBooking(day.date, hour).userFullName }}</small><br>
                  <small>{{ getBooking(day.date, hour).startTime }} - {{ getBooking(day.date, hour).endTime }}</small>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>

      <!-- Booking Modal -->
      <div v-if="showModal" class="modal" @click.self="closeModal">
        <div class="modal-content card">
          <h3>Đặt Sân</h3>
          <form @submit.prevent="submitBooking">
            <div class="form-group">
              <label>Sân</label>
              <select v-model="bookingForm.courtId" required>
                <option value="">Chọn sân</option>
                <option v-for="court in courts" :key="court.id" :value="court.id">
                  {{ court.name }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label>Ngày</label>
              <input type="date" v-model="bookingForm.bookingDate" required readonly />
            </div>
            <div class="form-group">
              <label>Giờ bắt đầu</label>
              <input type="time" v-model="bookingForm.startTime" required />
            </div>
            <div class="form-group">
              <label>Giờ kết thúc</label>
              <input type="time" v-model="bookingForm.endTime" required />
            </div>
            <div class="form-group">
              <label>Ghi chú</label>
              <textarea v-model="bookingForm.note" rows="3"></textarea>
            </div>
            <div v-if="bookingError" class="error-message">{{ bookingError }}</div>
            <div class="modal-actions">
              <button type="submit" class="btn btn-primary" :disabled="submitting">
                {{ submitting ? 'Đang đặt...' : 'Đặt sân' }}
              </button>
              <button type="button" @click="closeModal" class="btn btn-secondary">Hủy</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { courtService, bookingService } from '../services/api'

export default {
  name: 'BookingView',
  setup() {
    const courts = ref([])
    const selectedCourtId = ref('')
    const currentWeekStart = ref(getMonday(new Date()))
    const bookings = ref([])
    const showModal = ref(false)
    const bookingForm = ref({
      courtId: '',
      bookingDate: '',
      startTime: '',
      endTime: '',
      note: ''
    })
    const bookingError = ref('')
    const submitting = ref(false)

    const timeSlots = Array.from({ length: 16 }, (_, i) => i + 6) // 6:00 - 21:00

    const weekDays = computed(() => {
      const days = []
      const dayNames = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
      for (let i = 0; i < 7; i++) {
        const date = new Date(currentWeekStart.value)
        date.setDate(date.getDate() + i)
        days.push({
          date: date.toISOString().split('T')[0],
          dateStr: `${date.getDate()}/${date.getMonth() + 1}`,
          dayName: dayNames[date.getDay()]
        })
      }
      return days
    })

    const weekRange = computed(() => {
      const start = new Date(currentWeekStart.value)
      const end = new Date(currentWeekStart.value)
      end.setDate(end.getDate() + 6)
      return `${start.getDate()}/${start.getMonth() + 1} - ${end.getDate()}/${end.getMonth() + 1}/${end.getFullYear()}`
    })

    function getMonday(date) {
      const d = new Date(date)
      const day = d.getDay()
      const diff = d.getDate() - day + (day === 0 ? -6 : 1)
      d.setDate(diff)
      d.setHours(0, 0, 0, 0)
      return d
    }

    const loadCourts = async () => {
      try {
        const response = await courtService.getAllCourts()
        courts.value = response.data
      } catch (error) {
        console.error('Error loading courts:', error)
      }
    }

    const loadBookings = async () => {
      try {
        const startDate = currentWeekStart.value.toISOString().split('T')[0]
        const endDate = new Date(currentWeekStart.value)
        endDate.setDate(endDate.getDate() + 6)
        const endDateStr = endDate.toISOString().split('T')[0]

        const response = selectedCourtId.value
          ? await bookingService.getCourtWeeklyBookings(selectedCourtId.value, startDate, endDateStr)
          : await bookingService.getWeeklyBookings(startDate, endDateStr)

        bookings.value = response.data
      } catch (error) {
        console.error('Error loading bookings:', error)
      }
    }

    const hasBooking = (date, hour) => {
      return bookings.value.some(b => {
        const bookingHour = parseInt(b.startTime.split(':')[0])
        return b.bookingDate === date && bookingHour === hour
      })
    }

    const getBooking = (date, hour) => {
      return bookings.value.find(b => {
        const bookingHour = parseInt(b.startTime.split(':')[0])
        return b.bookingDate === date && bookingHour === hour
      })
    }

    const isPast = (date, hour) => {
      const now = new Date()
      const cellDate = new Date(date)
      cellDate.setHours(hour, 0, 0, 0)
      return cellDate < now
    }

    const openBookingModal = (date, hour) => {
      if (isPast(date, hour)) return
      if (hasBooking(date, hour)) return

      bookingForm.value = {
        courtId: selectedCourtId.value || '',
        bookingDate: date,
        startTime: `${hour.toString().padStart(2, '0')}:00`,
        endTime: `${(hour + 1).toString().padStart(2, '0')}:00`,
        note: ''
      }
      showModal.value = true
    }

    const closeModal = () => {
      showModal.value = false
      bookingError.value = ''
    }

    const submitBooking = async () => {
      bookingError.value = ''
      submitting.value = true

      try {
        await bookingService.createBooking(bookingForm.value)
        alert('Đặt sân thành công! Vui lòng chờ admin xác nhận.')
        closeModal()
        loadBookings()
      } catch (error) {
        bookingError.value = error.response?.data || 'Đặt sân thất bại. Vui lòng thử lại.'
      } finally {
        submitting.value = false
      }
    }

    const previousWeek = () => {
      currentWeekStart.value = new Date(currentWeekStart.value.setDate(currentWeekStart.value.getDate() - 7))
      loadBookings()
    }

    const nextWeek = () => {
      currentWeekStart.value = new Date(currentWeekStart.value.setDate(currentWeekStart.value.getDate() + 7))
      loadBookings()
    }

    onMounted(() => {
      loadCourts()
      loadBookings()
    })

    return {
      courts,
      selectedCourtId,
      weekDays,
      weekRange,
      timeSlots,
      bookings,
      showModal,
      bookingForm,
      bookingError,
      submitting,
      hasBooking,
      getBooking,
      isPast,
      openBookingModal,
      closeModal,
      submitBooking,
      previousWeek,
      nextWeek,
      loadBookings
    }
  }
}
</script>

<style scoped>
.booking-page h1 {
  text-align: center;
  color: var(--primary-green);
  margin-bottom: 2rem;
}

.court-selection, .week-nav {
  margin-bottom: 1rem;
}

.court-selection select {
  max-width: 300px;
}

.week-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
}

.week-range {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--primary-green);
}

.calendar-container {
  overflow-x: auto;
}

.calendar-grid {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  min-width: 900px;
  gap: 1px;
  background: var(--border-color);
}

.time-header, .day-header, .time-cell, .booking-cell {
  background: white;
  padding: 10px;
  min-height: 60px;
}

.time-header, .day-header {
  font-weight: bold;
  text-align: center;
  background: var(--light-yellow);
}

.day-header {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.day-name {
  font-size: 1.1rem;
  color: var(--primary-green);
}

.time-cell {
  text-align: center;
  font-weight: 500;
  background: var(--light-yellow);
}

.booking-cell {
  cursor: pointer;
  transition: background 0.2s ease;
  position: relative;
}

.booking-cell:hover:not(.has-booking):not(.past) {
  background: #e8f5e9;
}

.booking-cell.has-booking {
  background: linear-gradient(135deg, var(--primary-yellow), #FFE082);
  cursor: default;
}

.booking-cell.past {
  background: #f5f5f5;
  cursor: not-allowed;
}

.booking-info {
  font-size: 0.85rem;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

.modal-content {
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  color: var(--primary-green);
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.modal-actions button {
  flex: 1;
}

.error-message {
  color: #dc3545;
  background: #ffebee;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .calendar-grid {
    grid-template-columns: 60px repeat(7, minmax(100px, 1fr));
  }

  .week-nav {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>

