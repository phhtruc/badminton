<template>
  <div class="register-page">
    <div class="container">
      <div class="register-card card">
        <h2>Đăng ký tài khoản</h2>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>Họ và tên</label>
            <input
              type="text"
              v-model="form.fullName"
              placeholder="Nhập họ và tên"
              required
            />
          </div>
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
            <label>Số điện thoại</label>
            <input
              type="tel"
              v-model="form.phone"
              placeholder="Nhập số điện thoại"
            />
          </div>
          <div class="form-group">
            <label>Mật khẩu</label>
            <input
              type="password"
              v-model="form.password"
              placeholder="Nhập mật khẩu (tối thiểu 6 ký tự)"
              required
              minlength="6"
            />
          </div>
          <div class="form-group">
            <label>Xác nhận mật khẩu</label>
            <input
              type="password"
              v-model="confirmPassword"
              placeholder="Nhập lại mật khẩu"
              required
            />
          </div>
          <div v-if="error" class="error-message">{{ error }}</div>
          <button type="submit" class="btn btn-primary btn-full" :disabled="loading">
            {{ loading ? 'Đang đăng ký...' : 'Đăng ký' }}
          </button>
        </form>
        <p class="login-link">
          Đã có tài khoản? <router-link to="/login">Đăng nhập ngay</router-link>
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
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const form = ref({
      fullName: '',
      email: '',
      phone: '',
      password: ''
    })
    const confirmPassword = ref('')
    const error = ref('')
    const loading = ref(false)

    const handleRegister = async () => {
      error.value = ''

      if (form.value.password !== confirmPassword.value) {
        error.value = 'Mật khẩu xác nhận không khớp'
        return
      }

      loading.value = true

      try {
        await authStore.register(form.value)
        router.push('/')
      } catch (err) {
        error.value = err.response?.data || 'Đăng ký thất bại. Vui lòng thử lại.'
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      confirmPassword,
      error,
      loading,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.register-card {
  max-width: 450px;
  width: 100%;
  margin: 0 auto;
}

.register-card h2 {
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

.login-link {
  text-align: center;
  margin-top: 1.5rem;
}

.login-link a {
  color: var(--primary-green);
  font-weight: 500;
}
</style>

