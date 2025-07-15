<template>
  <div class="login-container">
    <div class="login-card">
      <h2>Connexion</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Nom d'utilisateur</label>
          <input
              id="username"
              v-model="credentials.username"
              type="text"
              required
              :disabled="loading"
          />
        </div>

        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input
              id="password"
              v-model="credentials.password"
              type="password"
              required
              :disabled="loading"
          />
        </div>

        <button type="submit" :disabled="loading" class="btn-primary">
          {{ loading ? "Connexion..." : "Entrer" }}
        </button>
      </form>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <div class="links">
        <router-link to="/forgot-password">Mot de passe oublié ?</router-link>
        <span> | </span>
        <router-link to="/register">Créer un compte</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

export default {
  name: "LoginView",
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();

    const credentials = ref({
      username: "",
      password: "",
    });

    const loading = ref(false);
    const error = ref(null);

    const handleLogin = async () => {
      loading.value = true;
      error.value = null;

      const result = await authStore.login(credentials.value);

      if (result.success) {
        router.push("/dashboard");
      } else {
        if (result.error === "credentials") {
          router.push("/error-credentials");
        } else {
          router.push("/error-server");
        }
      }

      loading.value = false;
    };

    return {
      credentials,
      loading,
      error,
      handleLogin,
    };
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
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

.links {
  margin-top: 1rem;
  text-align: center;
}
.links a {
  color: #007bff;
  text-decoration: none;
  margin: 0 0.5rem;
}
.links a:hover {
  text-decoration: underline;
}
</style>