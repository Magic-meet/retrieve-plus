import { createRouter, createWebHistory } from 'vue-router'

import Papers from "@/components/Page/Papers.vue";
import User from "@/components/Page/User.vue";
import Main from "@/components/Page/Main.vue";
import Home from "@/components/Page/Home.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: Main,
      redirect: "home",
      children: [
        { path: 'home' ,component:Home},
        { path: 'papers', component: Papers },
        { path: 'user', component: User },
      ]
    }
  ]
})

export default router
