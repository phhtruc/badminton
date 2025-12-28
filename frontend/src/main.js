import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/main.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// Make environment variables available globally (optional)
app.config.globalProperties.$apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

app.mount('#app')
