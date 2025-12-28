<template>
  <div class="home">
    <div class="hero">
      <div class="container">
        <h1>🏸 Chào mừng đến với Hệ thống Đặt Sân Cầu Lông</h1>
        <p>Đặt sân nhanh chóng, dễ dàng và tiện lợi</p>
        <div class="hero-actions">
          <router-link to="/booking" class="btn btn-primary" v-if="isAuthenticated">Đặt sân ngay</router-link>
          <router-link to="/register" class="btn btn-primary" v-else>Đăng ký ngay</router-link>
          <router-link to="/login" class="btn btn-secondary" v-if="!isAuthenticated">Đăng nhập</router-link>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="features">
        <h2>Tính năng nổi bật</h2>
        <div class="feature-grid">
          <div class="feature-card card">
            <div class="icon">📅</div>
            <h3>Đặt lịch dễ dàng</h3>
            <p>Giao diện lịch trực quan, chọn giờ linh hoạt</p>
          </div>
          <div class="feature-card card">
            <div class="icon">✅</div>
            <h3>Xác nhận nhanh</h3>
            <p>Admin xác nhận đặt sân qua thông báo và email</p>
          </div>
          <div class="feature-card card">
            <div class="icon">🏆</div>
            <h3>Sân chất lượng</h3>
            <p>2 sân tiêu chuẩn thi đấu, mở cửa từ 6h-22h</p>
          </div>
          <div class="feature-card card">
            <div class="icon">📱</div>
            <h3>Responsive</h3>
            <p>Sử dụng tốt trên mọi thiết bị</p>
          </div>
        </div>
      </div>

      <div class="courts-section">
        <h2>Sân của chúng tôi</h2>
        <div class="courts-grid" v-if="courts.length > 0">
          <div v-for="court in courts" :key="court.id" class="court-card card">
            <h3>{{ court.name }}</h3>
            <p>{{ court.description }}</p>
            <p><strong>Giờ mở cửa:</strong> {{ court.openTime }} - {{ court.closeTime }}</p>
          </div>
        </div>
        <div v-else>
          <p>Đang tải thông tin sân...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { courtService } from '../services/api'

export default {
  name: 'HomeView',
  setup() {
    const authStore = useAuthStore()
    const courts = ref([])
    const isAuthenticated = computed(() => authStore.isAuthenticated)

    const loadCourts = async () => {
      try {
        const response = await courtService.getAllCourts()
        courts.value = response.data
      } catch (error) {
        console.error('Error loading courts:', error)
      }
    }

    onMounted(() => {
      loadCourts()
    })

    return {
      isAuthenticated,
      courts
    }
  }
}
</script>

<style scoped>
.hero {
  background: linear-gradient(135deg, var(--primary-yellow), #FFB700);
  padding: 4rem 0;
  text-align: center;
  border-radius: 10px;
  margin-bottom: 3rem;
}

.hero h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: var(--text-dark);
}

.hero p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  color: var(--text-dark);
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.features, .courts-section {
  margin: 3rem 0;
}

.features h2, .courts-section h2 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
  color: var(--dark-green);
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.courts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.court-card h3 {
  color: var(--primary-green);
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 1.8rem;
  }

  .hero p {
    font-size: 1rem;
  }

  .feature-grid, .courts-grid {
    grid-template-columns: 1fr;
  }
}
</style>

