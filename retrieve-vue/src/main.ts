import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from "axios";
import { apiBaseUrl } from "@/config/runtimeConfig.js";
// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
    components,
    directives,
})


const app = createApp(App)

app.use(router)
app.use(vuetify)

axios.defaults.baseURL = apiBaseUrl

app.mount('#app')