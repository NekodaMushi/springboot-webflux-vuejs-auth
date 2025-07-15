<template>
  <div class="register-container">
    <div class="register-card">
      <h2>Créer un compte</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">Nom d'utilisateur</label>
          <input
              id="username"
              v-model="form.username"
              type="text"
              required
              :disabled="loading"
              placeholder="Choisissez un nom d'utilisateur"
          />
        </div>
        
        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input
              id="password"
              v-model="form.password"
              type="password"
              required
              :disabled="loading"
              placeholder="Choisissez un mot de passe"
          />
        </div>
        
        <div class="form-group">
          <label for="confirmPassword">Confirmer le mot de passe</label>
          <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              required
              :disabled="loading"
              placeholder="Confirmez votre mot de passe"
          />
        </div>
        
        <button type="submit" :disabled="loading || !isFormValid" class="btn-primary">
          {{ loading ? "Création..." : "Créer le compte" }}
        </button>
      </form>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <div v-if="success" class="success-message">
        {{ success }}
      </div>

      <div class="links">
        <span>Déjà un compte ? </span>
        <router-link to="/login">Se connecter</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

export default {
  name: "RegisterView",
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();

    const form = ref({
      username: "",
      password: "",
      confirmPassword: "",
    });

    const loading = ref(false);
    const error = ref(null);
    const success = ref(null);

    const isFormValid = computed(() => {
      return form.value.username.trim() && 
             form.value.password.trim() && 
             form.value.confirmPassword.trim() &&
             form.value.password === form.value.confirmPassword;
    });

    const handleRegister = async () => {
      error.value = null;
      success.value = null;

      if (form.value.password !== form.value.confirmPassword) {
        error.value = "Les mots de passe ne correspondent pas.";
        return;
      }

      loading.value = true;

      try {
        const result = await authStore.register({
          username: form.value.username,
          password: form.value.password,
        });

        if (result.success) {
          success.value = "Compte créé avec succès ! Redirection vers le dashboard...";
          
          // redirect to dashboard
          setTimeout(() => router.push("/dashboard"), 1500);
          
          // redirect to login
          // setTimeout(() => router.push("/login"), 1500);
        } else {
          error.value = authStore.error || "Erreur lors de la création du compte.";
        }
      } catch (e) {
        error.value = "Erreur lors de la création du compte.";
      } finally {
        loading.value = false;
      }
    };

    return {
      form,
      loading,
      error,
      success,
      isFormValid,
      handleRegister,
    };
  },
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.register-card {
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

.links a {
  color: #007bff;
  text-decoration: none;
}

.links a:hover {
  text-decoration: underline;
}
</style>
