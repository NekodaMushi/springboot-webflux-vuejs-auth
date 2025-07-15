<template>
  <div class="dashboard-container">
    <div class="dashboard-card">
      <h2>✅ Connexion réussie !</h2>
      <p>Bienvenue {{ username }}, vous êtes maintenant connecté.</p>
      
      <div class="main-actions">
        <button @click="handleLogout" class="btn-secondary" :disabled="loading">
          {{ loading ? "Déconnexion..." : "Déconnecter" }}
        </button>
      </div>

      <div class="secondary-actions">
        <button @click="toggleAdvanced" class="btn-link">
          {{ showAdvanced ? "Masquer les options" : "Options avancées" }}
        </button>
      </div>

      <div v-if="showAdvanced" class="advanced-options">
        <div class="user-info">
          <p v-if="userInfo"><strong>Rôle :</strong> {{ userInfo.roleName }}</p>
          <p v-if="userInfo"><strong>Statut :</strong> {{ userInfo.enabled ? "Actif" : "Inactif" }}</p>
        </div>
        
        <div class="advanced-actions">
          <button @click="refreshUserInfo" class="btn-info" :disabled="loading">
            {{ loading ? "Chargement..." : "Rafraîchir" }}
          </button>
          
          <button @click="showDeleteModal = true" class="btn-danger-small">
            Supprimer mon compte
          </button>
        </div>
      </div>

      <div v-if="message" class="message" :class="messageType">
        {{ message }}
      </div>
    </div>

    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal-card">
        <h3>⚠️ Confirmer la suppression</h3>
        <p>Êtes-vous sûr de vouloir supprimer votre compte ?</p>
        <div class="modal-actions">
          <button @click="showDeleteModal = false" class="btn-secondary">
            Annuler
          </button>
          <button @click="deleteAccount" :disabled="loading" class="btn-danger">
            {{ loading ? "Suppression..." : "Confirmer" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

export default {
  name: "DashboardView",
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    
    const loading = ref(false);
    const message = ref("");
    const messageType = ref("success");
    const userInfo = ref(null);
    const showAdvanced = ref(false);
    const showDeleteModal = ref(false);

    // get username from store
    const username = computed(() => authStore.username || "utilisateur");

    const handleLogout = async () => {
      loading.value = true;
      await authStore.logout();
      router.push("/login");
    };

    const refreshUserInfo = async () => {
      loading.value = true;
      message.value = "";
      
      try {
        const data = await authStore.fetchUserInfo();
        userInfo.value = data;
        message.value = "Informations mises à jour !";
        messageType.value = "success";
      } catch (error) {
        message.value = "Erreur lors du rafraîchissement";
        messageType.value = "error";
      } finally {
        loading.value = false;
      }
    };

    const deleteAccount = async () => {
      loading.value = true;
      
      const result = await authStore.deleteAccount();
      
      if (result.success) {
        router.push("/login");
      } else {
        message.value = result.error || "Erreur lors de la suppression";
        messageType.value = "error";
        showDeleteModal.value = false;
      }
      
      loading.value = false;
    };

    const toggleAdvanced = () => {
      showAdvanced.value = !showAdvanced.value;
      if (showAdvanced.value && !userInfo.value) {
        refreshUserInfo();
      }
    };

    // get user data if already auth. with token
    onMounted(() => {
      if (authStore.token) {
        refreshUserInfo();
      }
    });

    return {
      username,
      loading,
      message,
      messageType,
      userInfo,
      showAdvanced,
      showDeleteModal,
      handleLogout,
      refreshUserInfo,
      deleteAccount,
      toggleAdvanced,
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

.main-actions {
  margin: 1.5rem 0;
}

.secondary-actions {
  margin: 1rem 0;
}

.advanced-options {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.user-info {
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.advanced-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
  flex-wrap: wrap;
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

.btn-secondary:hover:not(:disabled) {
  background-color: #545b62;
}

.btn-link {
  background: none;
  border: none;
  color: #007bff;
  text-decoration: underline;
  cursor: pointer;
  font-size: 0.9rem;
}

.btn-link:hover {
  color: #0056b3;
}

.btn-info {
  padding: 0.5rem 1rem;
  background-color: #17a2b8;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
}

.btn-info:hover:not(:disabled) {
  background-color: #138496;
}

.btn-danger-small {
  padding: 0.5rem 1rem;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
}

.btn-danger-small:hover:not(:disabled) {
  background-color: #c82333;
}

.btn-danger {
  padding: 0.5rem 1rem;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.message {
  margin-top: 1rem;
  padding: 0.75rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  text-align: center;
  max-width: 350px;
  width: 90%;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1.5rem;
}
</style>
