<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { onMounted } from "vue";
import { useAuthStore } from "./stores/auth";

export default {
  name: "App",
  setup() {
    const authStore = useAuthStore();

    onMounted(() => {
      authStore.checkAuth();
    });
  },
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  line-height: 1.6;
  color: #333;
}

#app {
  min-height: 100vh;
}
</style>