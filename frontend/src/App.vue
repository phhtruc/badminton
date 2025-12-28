<template>
  <div id="app">
    <header class="header">
      <div class="container">
        <nav class="navbar">
          <div class="logo">
            <router-link to="/">🏸 Đặt Sân Cầu Lông</router-link>
          </div>
          <ul class="nav-menu" :class="{ active: isMenuOpen }">
            <li><router-link to="/" @click="closeMenu">Trang chủ</router-link></li>
            <li v-if="isAuthenticated"><router-link to="/booking" @click="closeMenu">Đặt sân</router-link></li>
            <li v-if="isAuthenticated"><router-link to="/my-bookings" @click="closeMenu">Lịch đặt của tôi</router-link></li>
            <li v-if="isAdmin"><router-link to="/admin" @click="closeMenu">Quản trị</router-link></li>
            <li v-if="isAuthenticated"><router-link to="/profile" @click="closeMenu">Tài khoản</router-link></li>
            <li v-if="!isAuthenticated"><router-link to="/login" @click="closeMenu">Đăng nhập</router-link></li>
            <li v-if="!isAuthenticated"><router-link to="/register" @click="closeMenu">Đăng ký</router-link></li>
            <li v-if="isAuthenticated"><a href="#" @click.prevent="handleLogout">Đăng xuất</a></li>
          </ul>
          <div class="hamburger" @click="toggleMenu">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </nav>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 Hệ thống Đặt Sân Cầu Lông. Tất cả quyền được bảo lưu.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const isMenuOpen = ref(false)

    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const isAdmin = computed(() => authStore.isAdmin)

    const toggleMenu = () => {
      isMenuOpen.value = !isMenuOpen.value
    }

    const closeMenu = () => {
      isMenuOpen.value = false
    }

    const handleLogout = () => {
      authStore.logout()
      router.push('/login')
      closeMenu()
    }

    return {
      isAuthenticated,
      isAdmin,
      isMenuOpen,
      toggleMenu,
      closeMenu,
      handleLogout
    }
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, var(--primary-green), var(--dark-green));
  box-shadow: 0 2px 10px var(--shadow);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
}

.logo a {
  color: white;
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-menu {
  display: flex;
  list-style: none;
  gap: 2rem;
  align-items: center;
}

.nav-menu li a {
  color: white;
  text-decoration: none;
  transition: color 0.3s ease;
  font-weight: 500;
}

.nav-menu li a:hover,
.nav-menu li a.router-link-active {
  color: var(--primary-yellow);
}

.hamburger {
  display: none;
  flex-direction: column;
  cursor: pointer;
  gap: 4px;
}

.hamburger span {
  width: 25px;
  height: 3px;
  background: white;
  transition: all 0.3s ease;
}

.main-content {
  min-height: calc(100vh - 200px);
  padding: 2rem 0;
}

.footer {
  background: var(--text-dark);
  color: white;
  text-align: center;
  padding: 2rem 0;
  margin-top: 3rem;
}

@media (max-width: 768px) {
  .hamburger {
    display: flex;
  }

  .nav-menu {
    position: fixed;
    left: -100%;
    top: 70px;
    flex-direction: column;
    background: var(--dark-green);
    width: 100%;
    text-align: center;
    transition: 0.3s;
    box-shadow: 0 10px 27px rgba(0, 0, 0, 0.05);
    padding: 2rem 0;
  }

  .nav-menu.active {
    left: 0;
  }
}
</style>

