<template>
  <div class="forgot-container">
    <div class="forgot-card">
      <h2>Mot de passe oublié</h2>
      <form @submit.prevent="handleForgot">
        <div class="form-group">
          <label for="username">Nom d'utilisateur ou email</label>
          <input
              id="username"
              v-model="username"
              type="text"
              required
              :disabled="loading"
          />
        </div>
        <button type="submit" :disabled="loading" class="btn-primary">
          {{ loading ? "Envoi..." : "Envoyer le lien de réinitialisation" }}
        </button>
      </form>
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
      <div v-if="success" class="success-message">
        {{ success }}
      </div>
      <div class="links">
        <router-link to="/login">Retour à la connexion</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import axios from "axios";

export default {
  name: "ForgotPasswordView",
  setup() {
    const username = ref("");
    const loading = ref(false);
    const error = ref(null);
    const success = ref(null);

    const handleForgot = async () => {
      error.value = null;
      success.value = null;
      loading.value = true;
      try {

        await axios.post("/api/auth/forgot-password", {
          username: username.value,
        });
        success.value =
            "Si ce compte existe, un email de réinitialisation a été envoyé.";
      } catch (e) {
        error.value =
            e.response?.data?.message ||
            "Erreur lors de la demande de réinitialisation.";
      } finally {
        loading.value = false;
      }
    };

    return {
      username,
      loading,
      error,
      success,
      handleForgot,
    };
  },
};
</script>

<style scoped>
.forgot-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.forgot-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.btn-primary {
  width: 100%;
  padding: 0.75rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  margin-top: 1rem;
  padding: 0.75rem;
  background-color: #f8d7da;
  color: #721c24;
  border-radius: 4px;
}

.success-message {
  margin-top: 1rem;
  padding: 0.75rem;
  background-color: #d4edda;
  color: #155724;
  border-radius: 4px;
}

.links {
  margin-top: 1rem;
  text-align: center;
}
</style>