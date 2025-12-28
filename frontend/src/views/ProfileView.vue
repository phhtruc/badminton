<template>
  <div class="profile-page">
    <div class="container">
      <div class="profile-card card">
        <h2>Thông Tin Tài Khoản</h2>

        <div class="profile-info" v-if="!editMode">
          <div class="info-item">
            <strong>Email:</strong> {{ user.email }}
          </div>
          <div class="info-item">
            <strong>Họ tên:</strong> {{ user.fullName }}
          </div>
          <div class="info-item">
            <strong>Số điện thoại:</strong> {{ user.phone || 'Chưa cập nhật' }}
          </div>
          <div class="info-item">
            <strong>Vai trò:</strong> {{ user.role === 'ADMIN' ? 'Quản trị viên' : 'Người dùng' }}
          </div>
          <button @click="editMode = true" class="btn btn-primary">Chỉnh sửa</button>
        </div>

        <form v-else @submit.prevent="updateProfile" class="profile-form">
          <div class="form-group">
            <label>Họ tên</label>
            <input type="text" v-model="profileForm.fullName" required />
          </div>
          <div class="form-group">
            <label>Số điện thoại</label>
            <input type="tel" v-model="profileForm.phone" />
          </div>
          <div v-if="profileError" class="error-message">{{ profileError }}</div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="updating">
              {{ updating ? 'Đang cập nhật...' : 'Lưu' }}
            </button>
            <button type="button" @click="cancelEdit" class="btn btn-secondary">Hủy</button>
          </div>
        </form>
      </div>

      <div class="password-card card">
        <h2>Đổi Mật Khẩu</h2>
        <form @submit.prevent="changePassword">
          <div class="form-group">
            <label>Mật khẩu cũ</label>
            <input type="password" v-model="passwordForm.oldPassword" required />
          </div>
          <div class="form-group">
            <label>Mật khẩu mới</label>
            <input type="password" v-model="passwordForm.newPassword" required minlength="6" />
          </div>
          <div class="form-group">
            <label>Xác nhận mật khẩu mới</label>
            <input type="password" v-model="confirmNewPassword" required />
          </div>
          <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
          <div v-if="passwordSuccess" class="success-message">{{ passwordSuccess }}</div>
          <button type="submit" class="btn btn-primary" :disabled="changingPassword">
            {{ changingPassword ? 'Đang đổi...' : 'Đổi mật khẩu' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { authService } from '../services/api'

export default {
  name: 'ProfileView',
  setup() {
    const authStore = useAuthStore()
    const user = ref({})
    const editMode = ref(false)
    const profileForm = ref({
      fullName: '',
      phone: ''
    })
    const passwordForm = ref({
      oldPassword: '',
      newPassword: ''
    })
    const confirmNewPassword = ref('')
    const profileError = ref('')
    const passwordError = ref('')
    const passwordSuccess = ref('')
    const updating = ref(false)
    const changingPassword = ref(false)

    const loadUser = async () => {
      try {
        const response = await authService.getCurrentUser()
        user.value = response.data
        profileForm.value = {
          fullName: user.value.fullName,
          phone: user.value.phone
        }
      } catch (error) {
        console.error('Error loading user:', error)
      }
    }

    const cancelEdit = () => {
      editMode.value = false
      profileForm.value = {
        fullName: user.value.fullName,
        phone: user.value.phone
      }
      profileError.value = ''
    }

    const updateProfile = async () => {
      profileError.value = ''
      updating.value = true

      try {
        await authService.updateProfile(profileForm.value)
        await loadUser()
        editMode.value = false
        alert('Cập nhật thông tin thành công!')
      } catch (error) {
        profileError.value = error.response?.data || 'Cập nhật thất bại'
      } finally {
        updating.value = false
      }
    }

    const changePassword = async () => {
      passwordError.value = ''
      passwordSuccess.value = ''

      if (passwordForm.value.newPassword !== confirmNewPassword.value) {
        passwordError.value = 'Mật khẩu xác nhận không khớp'
        return
      }

      changingPassword.value = true

      try {
        await authService.changePassword(passwordForm.value)
        passwordSuccess.value = 'Đổi mật khẩu thành công!'
        passwordForm.value = { oldPassword: '', newPassword: '' }
        confirmNewPassword.value = ''
      } catch (error) {
        passwordError.value = error.response?.data || 'Đổi mật khẩu thất bại'
      } finally {
        changingPassword.value = false
      }
    }

    onMounted(() => {
      loadUser()
    })

    return {
      user,
      editMode,
      profileForm,
      passwordForm,
      confirmNewPassword,
      profileError,
      passwordError,
      passwordSuccess,
      updating,
      changingPassword,
      cancelEdit,
      updateProfile,
      changePassword
    }
  }
}
</script>

<style scoped>
.profile-page h2 {
  color: var(--primary-green);
  margin-bottom: 1.5rem;
}

.profile-card, .password-card {
  max-width: 600px;
  margin: 0 auto 2rem;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.info-item {
  padding: 1rem;
  background: var(--bg-light);
  border-radius: 5px;
}

.info-item strong {
  display: inline-block;
  min-width: 150px;
  color: var(--primary-green);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-actions {
  display: flex;
  gap: 1rem;
}

.error-message {
  color: #dc3545;
  background: #ffebee;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 1rem;
}

.success-message {
  color: #155724;
  background: #d4edda;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 1rem;
}
</style>

