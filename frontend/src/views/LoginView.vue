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
            <div class="password-input-wrapper">
              <input
                :type="showPassword ? 'text' : 'password'"
                v-model="form.password"
                placeholder="Nhập mật khẩu"
                required
              />
              <button
                type="button"
                class="toggle-password"
                @click="showPassword = !showPassword"
              >
                {{ showPassword ? '👁️' : '👁️‍🗨️' }}
              </button>
            </div>
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
    const showPassword = ref(false)
    const error = ref('')
    const loading = ref(false)

    const handleLogin = async () => {
      error.value = ''
      loading.value = true

      try {
        await authStore.login(form.value)
        router.push('/')
      } catch (err) {
        if (err.response?.data?.error) {
          error.value = err.response.data.error
        } else if (err.response?.data) {
          error.value = err.response.data
        } else {
          error.value = 'Email hoặc mật khẩu không đúng.'
        }
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      showPassword,
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

.password-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.password-input-wrapper input {
  padding-right: 45px;
}

.toggle-password {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  padding: 5px;
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
