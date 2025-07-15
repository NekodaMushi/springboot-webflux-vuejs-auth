import { defineStore } from "pinia";
import axios from "axios";

export const useAuthStore = defineStore("auth", {
    state: () => ({
        token: localStorage.getItem("token") || null,
        username: localStorage.getItem("username") || null,
        user: null,
        loading: false,
        error: null,
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,
    },

    actions: {
        checkAuth() {
            if (this.token) {
                axios.defaults.headers.common["Authorization"] = `Bearer ${this.token}`;
            }
        },

        async login(credentials) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.post("/api/auth/login", credentials);
                
                if (response.data.success) {
                    this.token = response.data.token;
                    this.username = response.data.username;
                    
                    localStorage.setItem("token", this.token);
                    localStorage.setItem("username", this.username);
                    
                    axios.defaults.headers.common["Authorization"] = `Bearer ${this.token}`;
                    
                    return { success: true };
                } else {
                    this.error = response.data.message;
                    return { success: false, error: "credentials" };
                }
            } catch (error) {
                console.error("Erreur de connexion:", error);
                this.error = "Erreur de connexion au serveur";
                return { success: false, error: "server" };
            } finally {
                this.loading = false;
            }
        },

        async register(credentials) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.post("/api/auth/register", credentials);
                
                if (response.data.success) {
                    this.token = response.data.token;
                    this.username = response.data.username;
                    
                    localStorage.setItem("token", this.token);
                    localStorage.setItem("username", this.username);
                    
                    axios.defaults.headers.common["Authorization"] = `Bearer ${this.token}`;
                    
                    return { success: true };
                } else {
                    this.error = response.data.message;
                    return { success: false, error: "registration" };
                }
            } catch (error) {
                console.error("Erreur d'inscription:", error);
                this.error = "Erreur de connexion au serveur";
                return { success: false, error: "server" };
            } finally {
                this.loading = false;
            }
        },

        async logout() {
            this.loading = true;
            
            try {
                await axios.post("/api/auth/logout");
            } catch (error) {
                console.error("Erreur lors de la déconnexion:", error);
            } finally {
                this.token = null;
                this.username = null;
                this.user = null;
                this.error = null;
                
                localStorage.removeItem("token");
                localStorage.removeItem("username");
                
                delete axios.defaults.headers.common["Authorization"];
                
                this.loading = false;
            }
        },

        async deleteAccount() {
            this.loading = true;
            
            try {
                const response = await axios.delete("/api/auth/delete-account");
                
                if (response.data.success) {
                    this.token = null;
                    this.username = null;
                    this.user = null;
                    this.error = null;
                    
                    localStorage.removeItem("token");
                    localStorage.removeItem("username");
                    
                    delete axios.defaults.headers.common["Authorization"];
                    
                    return { success: true };
                } else {
                    this.error = response.data.message;
                    return { success: false, error: response.data.message };
                }
            } catch (error) {
                console.error("Erreur lors de la suppression du compte:", error);
                this.error = "Erreur lors de la suppression du compte";
                return { success: false, error: "Erreur serveur" };
            } finally {
                this.loading = false;
            }
        },

        async fetchUserInfo() {
            try {
                const response = await axios.get("/api/auth/me");
                this.user = response.data;
                return response.data;
            } catch (error) {
                console.error("Erreur lors de la récupération des informations utilisateur:", error);

                if (error.response?.status === 401) {
                    this.logout();
                }
                throw error;
            }
        },
    },
});
