import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";
import LoginView from "../views/LoginView.vue";
import DashboardView from "../views/DashboardView.vue";
import ErrorCredentialsView from "../views/ErrorCredentialsView.vue";
import ErrorServerView from "../views/ErrorServerView.vue";
import RegisterView from "../views/RegisterView.vue";

const routes = [
    {
        path: "/",
        redirect: "/login",
    },
    {
        path: "/login",
        name: "Login",
        component: LoginView,
        meta: { requiresGuest: true },
    },
    {
        path: "/register",
        name: "Register",
        component: RegisterView,
        meta: { requiresGuest: true },
    },
    {
        path: "/forgot-password",
        name: "ForgotPassword",
        component: () => import("../views/ForgotPasswordView.vue"), // get back later
        meta: { requiresGuest: true },
    },
    {
        path: "/dashboard",
        name: "Dashboard",
        component: DashboardView,
        meta: { requiresAuth: true },
    },
    {
        path: "/error-credentials",
        name: "ErrorCredentials",
        component: ErrorCredentialsView,
    },
    {
        path: "/error-server",
        name: "ErrorServer",
        component: ErrorServerView,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next("/login");
    } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
        next("/dashboard");
    } else {
        next();
    }
});

export default router;