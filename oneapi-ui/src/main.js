import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'

import './style.css'
import Antd from 'ant-design-vue'
import App from './App.vue'

import './style.css'

import Providers from "@/pages/providers.vue";
import Provider from '@/pages/Provider.vue'
import Account  from "@/pages/accounts.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', component: Providers },
        { path: '/accounts/:providerId', component: Account },
        { path: '/provider/:providerId', component: Provider }
    ],
});

const app = createApp(App);
app.use(Antd).use(router).mount('#app');
