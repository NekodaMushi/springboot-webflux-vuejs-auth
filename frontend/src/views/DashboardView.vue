<template>
  <div class="dashboard-container">
    <div class="dashboard-card">
      <h2>✅ Connexion réussie !</h2>
      <p>Bienvenue, vous êtes maintenant connecté.</p>
      <button @click="handleLogout" class="btn-secondary">
        Déconnecter
      </button>
    </div>
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

export default {
  name: "DashboardView",
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();

    const handleLogout = () => {
      authStore.logout();
      router.push("/login");
    };

    return {
      handleLogout,
    };
  },
};
</script>

<style scoped>
.dashboard-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.dashboard-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  width: 100%;
  max-width: 400px;
}

.btn-secondary {
  padding: 0.75rem 1.5rem;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-secondary:hover {
  background-color: #545b62;
}
</style>