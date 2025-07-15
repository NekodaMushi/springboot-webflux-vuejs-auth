import { defineStore } from "pinia";
import axios from "axios";

export const useAuthStore = defineStore("auth", {
    state: () => ({
        token: localStorage.getItem("token") || null,
        user: null,
        loading: false,
        error: null,
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,
    },

    actions: {
        async login(credentials) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.post("/api/auth/login", credentials);
                this.token = response.data.token;
                this.user = response.data.user;
                localStorage.setItem("token", this.token);

                axios.defaults.headers.common["Authorization"] = `Bearer ${this.token}`;

                return { success: true };
            } catch (error) {
                this.error = error.response?.data?.message || "Erreur de connexion";
                return {
                    success: false,
                    error: error.response?.status === 401 ? "credentials" : "server"
                };
            } finally {
                this.loading = false;
            }
        },

        logout() {
            this.token = null;
            this.user = null;
            localStorage.removeItem("token");
            delete axios.defaults.headers.common["Authorization"];
        },

        checkAuth() {
            if (this.token) {
                axios.defaults.headers.common["Authorization"] = `Bearer ${this.token}`;
            }
        },
    },
});