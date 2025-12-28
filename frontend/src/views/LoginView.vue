<template>
  <div class="login-page">
    <div class="container">
      <div class="login-card card">
        <h2>Đăng nhập</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label>Email</label>
            <input
              type="email"
              v-model="form.email"
              placeholder="Nhập email của bạn"
              required
            />
          </div>
          <div class="form-group">
            <label>Mật khẩu</label>
            <input
              type="password"
              v-model="form.password"
              placeholder="Nhập mật khẩu"
              required
            />
          </div>
          <div v-if="error" class="error-message">{{ error }}</div>
          <button type="submit" class="btn btn-primary btn-full" :disabled="loading">
            {{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
          </button>
        </form>
        <p class="register-link">
          Chưa có tài khoản? <router-link to="/register">Đăng ký ngay</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const form = ref({
      email: '',
      password: ''
    })
    const error = ref('')
    const loading = ref(false)

    const handleLogin = async () => {
      error.value = ''
      loading.value = true

      try {
        await authStore.login(form.value)
        router.push('/')
      } catch (err) {
        error.value = err.response?.data || 'Đăng nhập thất bại. Vui lòng thử lại.'
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      error,
      loading,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.login-card {
  max-width: 400px;
  width: 100%;
  margin: 0 auto;
}

.login-card h2 {
  text-align: center;
  color: var(--primary-green);
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--text-dark);
}

.error-message {
  color: #dc3545;
  background: #ffebee;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 1rem;
  text-align: center;
}

.btn-full {
  width: 100%;
  margin-top: 1rem;
}

.register-link {
  text-align: center;
  margin-top: 1.5rem;
}

.register-link a {
  color: var(--primary-green);
  font-weight: 500;
}
</style>

