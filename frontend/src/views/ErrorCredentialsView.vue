<template>
  <div class="error-container">
    <div class="error-card">
      <h2>❌ Erreur d'authentification</h2>
      <p>Identifiants incorrects. Veuillez vérifier votre nom d'utilisateur et mot de passe.</p>
      <button @click="goBack" class="btn-primary">
        Retour à la connexion
      </button>
    </div>
  </div>
</template>

<script>
import { useRouter } from "vue-router";

export default {
  name: "ErrorCredentialsView",
  setup() {
    const router = useRouter();

    const goBack = () => {
      router.push("/login");
    };

    return {
      goBack,
    };
  },
};
</script>

<style scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.error-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  width: 100%;
  max-width: 400px;
  border-left: 4px solid #dc3545;
}

.btn-primary {
  padding: 0.75rem 1.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: #0056b3;
}
</style>