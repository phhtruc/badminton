<template>
  <div class="register-page">
    <div class="container">
      <div class="register-card card">
        <h2>Đăng ký tài khoản</h2>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>Họ và tên <span class="required">*</span></label>
            <input
              type="text"
              v-model="form.fullName"
              placeholder="Nhập họ và tên"
              required
              minlength="2"
              maxlength="100"
              @blur="validateFullName"
            />
            <span v-if="errors.fullName" class="error-text">{{ errors.fullName }}</span>
            <span v-if="serverErrors.fullName" class="error-text">{{ serverErrors.fullName }}</span>
          </div>

          <div class="form-group">
            <label>Biệt danh</label>
            <input
              type="text"
              v-model="form.nickname"
              placeholder="Nhập biệt danh (không bắt buộc)"
              maxlength="50"
            />
            <span class="hint-text">Biệt danh giúp người khác dễ nhận diện bạn hơn</span>
            <span v-if="serverErrors.nickname" class="error-text">{{ serverErrors.nickname }}</span>
          </div>

          <div class="form-group">
            <label>Email <span class="required">*</span></label>
            <input
              type="email"
              v-model="form.email"
              placeholder="Nhập email của bạn"
              required
              @blur="validateEmail"
            />
            <span class="hint-text">Nhập đúng email để nhận thông báo khi đặt sân!</span>
            <span v-if="errors.email" class="error-text">{{ errors.email }}</span>
            <span v-if="serverErrors.email" class="error-text">{{ serverErrors.email }}</span>
          </div>

          <div class="form-group">
            <label>Số điện thoại <span class="required">*</span></label>
            <input
              type="tel"
              v-model="form.phone"
              placeholder="Nhập số điện thoại (10 chữ số)"
              pattern="[0-9]{10}"
              required
              @blur="validatePhone"
            />
            <span v-if="errors.phone" class="error-text">{{ errors.phone }}</span>
            <span v-if="serverErrors.phone" class="error-text">{{ serverErrors.phone }}</span>
          </div>

          <div class="form-group">
            <label>Mật khẩu <span class="required">*</span></label>
            <div class="password-input-wrapper">
              <input
                :type="showPassword ? 'text' : 'password'"
                v-model="form.password"
                placeholder="Nhập mật khẩu (tối thiểu 8 ký tự)"
                required
                minlength="8"
                @input="validatePassword"
                @blur="validatePassword"
              />
              <button
                type="button"
                class="toggle-password"
                @click="showPassword = !showPassword"
              >
                {{ showPassword ? '👁️' : '👁️‍🗨️' }}
              </button>
            </div>
            <span class="hint-text">Mật khẩu phải có ít nhất 8 ký tự, bao gồm cả chữ và số</span>
            <span v-if="errors.password" class="error-text">{{ errors.password }}</span>
            <span v-if="serverErrors.password" class="error-text">{{ serverErrors.password }}</span>
          </div>

          <div class="form-group">
            <label>Xác nhận mật khẩu <span class="required">*</span></label>
            <div class="password-input-wrapper">
              <input
                :type="showConfirmPassword ? 'text' : 'password'"
                v-model="confirmPassword"
                placeholder="Nhập lại mật khẩu"
                required
                @blur="validateConfirmPassword"
              />
              <button
                type="button"
                class="toggle-password"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                {{ showConfirmPassword ? '👁️' : '👁️‍🗨️' }}
              </button>
            </div>
            <span v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</span>
          </div>

          <div v-if="error" class="error-message">{{ error }}</div>

          <button
            type="submit"
            class="btn btn-primary btn-full"
            :disabled="loading || !isFormValid"
          >
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
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    const form = reactive({
      fullName: '',
      nickname: '',
      email: '',
      phone: '',
      password: ''
    })

    const confirmPassword = ref('')
    const showPassword = ref(false)
    const showConfirmPassword = ref(false)
    const error = ref('')
    const loading = ref(false)

    const errors = reactive({
      fullName: '',
      email: '',
      phone: '',
      password: '',
      confirmPassword: ''
    })

    const serverErrors = reactive({
      fullName: '',
      nickname: '',
      email: '',
      phone: '',
      password: ''
    })

    const isFormValid = computed(() => {
      return form.fullName.length >= 2 &&
             form.email.includes('@') &&
             form.phone.length === 10 &&
             form.password.length >= 8 &&
             /(?=.*[a-zA-Z])(?=.*[0-9])/.test(form.password) &&
             form.password === confirmPassword.value
    })

    const clearServerErrors = () => {
      serverErrors.fullName = ''
      serverErrors.nickname = ''
      serverErrors.email = ''
      serverErrors.phone = ''
      serverErrors.password = ''
    }

    const validateFullName = () => {
      if (form.fullName.length < 2) {
        errors.fullName = 'Họ tên phải có ít nhất 2 ký tự'
      } else if (form.fullName.length > 100) {
        errors.fullName = 'Họ tên không được quá 100 ký tự'
      } else {
        errors.fullName = ''
      }
    }

    const validateEmail = () => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(form.email)) {
        errors.email = 'Email không hợp lệ'
      } else {
        errors.email = ''
      }
    }

    const validatePhone = () => {
      if (!form.phone) {
        errors.phone = 'Số điện thoại không được để trống'
      } else if (!/^[0-9]{10}$/.test(form.phone)) {
        errors.phone = 'Số điện thoại phải có đúng 10 chữ số'
      } else {
        errors.phone = ''
      }
    }

    const validatePassword = () => {
      if (form.password.length < 8) {
        errors.password = 'Mật khẩu phải có ít nhất 8 ký tự'
      } else if (!/(?=.*[a-zA-Z])(?=.*[0-9])/.test(form.password)) {
        errors.password = 'Mật khẩu phải bao gồm cả chữ và số'
      } else {
        errors.password = ''
      }
    }

    const validateConfirmPassword = () => {
      if (confirmPassword.value !== form.password) {
        errors.confirmPassword = 'Mật khẩu xác nhận không khớp'
      } else {
        errors.confirmPassword = ''
      }
    }

    const handleRegister = async () => {
      error.value = ''
      clearServerErrors()

      // Validate all fields
      validateFullName()
      validateEmail()
      validatePhone()
      validatePassword()
      validateConfirmPassword()

      // Check if there are any errors
      if (Object.values(errors).some(err => err !== '')) {
        error.value = 'Vui lòng kiểm tra lại thông tin đã nhập'
        return
      }

      loading.value = true

      try {
        await authStore.register(form)
        router.push('/')
      } catch (err) {
        console.error('Register error:', err)

        // Handle validation errors from backend
        if (err.response?.data?.errors) {
          const backendErrors = err.response.data.errors
          Object.keys(backendErrors).forEach(key => {
            if (serverErrors.hasOwnProperty(key)) {
              serverErrors[key] = backendErrors[key]
            }
          })
          error.value = err.response.data.error || 'Vui lòng kiểm tra lại thông tin đã nhập'
        }
        // Handle general error message
        else if (err.response?.data?.error) {
          error.value = err.response.data.error
        }
        // Handle string error response
        else if (typeof err.response?.data === 'string') {
          error.value = err.response.data
        }
        // Default error
        else {
          error.value = 'Đăng ký thất bại. Vui lòng thử lại.'
        }
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      confirmPassword,
      showPassword,
      showConfirmPassword,
      error,
      loading,
      errors,
      serverErrors,
      isFormValid,
      validateFullName,
      validateEmail,
      validatePhone,
      validatePassword,
      validateConfirmPassword,
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
  padding: 2rem 0;
}

.register-card {
  max-width: 500px;
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

.required {
  color: #dc3545;
}

.hint-text {
  display: block;
  font-size: 0.85rem;
  color: #666;
  margin-top: 0.25rem;
}

.error-text {
  display: block;
  color: #dc3545;
  font-size: 0.85rem;
  margin-top: 0.25rem;
}

.password-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
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

.btn-full:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 1.5rem;
}

.login-link a {
  color: var(--primary-green);
  font-weight: 500;
}

@media (max-width: 768px) {
  .register-card {
    padding: 1.5rem;
  }
}
</style>
