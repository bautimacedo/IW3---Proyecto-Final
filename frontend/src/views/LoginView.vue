<script setup>
import { ref } from "vue";

const username = ref("");
const password = ref("");

const onSubmit = () => {
  console.log(username.value, password.value);
  // después lo conectamos al backend
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-950 via-gray-900 to-black px-4">
    <div class="w-full max-w-md">
      <!-- Title -->
      <div class="mb-8 text-center">
        <h1 class="text-3xl font-bold text-white tracking-tight">
          Proyecto IW3
        </h1>
        <p class="text-gray-400 mt-2">
          Iniciá sesión para continuar
        </p>
      </div>

      <!-- Card -->
      <div class="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl shadow-2xl p-8">
        <h2 class="text-xl font-semibold text-white mb-6">
          Iniciar sesión
        </h2>

        <form @submit.prevent="onSubmit" class="space-y-4">
          <div>
            <label class="text-sm text-gray-300">Usuario</label>
            <input
              v-model="username"
              placeholder="admin"
              class="mt-2 w-full rounded-xl bg-black/40 border border-white/10 px-4 py-3 text-white placeholder-gray-500
                     focus:outline-none focus:ring-2 focus:ring-blue-500/60"
            />
          </div>

          <div>
            <label class="text-sm text-gray-300">Contraseña</label>
            <input
              v-model="password"
              type="password"
              placeholder="••••••••"
              class="mt-2 w-full rounded-xl bg-black/40 border border-white/10 px-4 py-3 text-white placeholder-gray-500
                     focus:outline-none focus:ring-2 focus:ring-blue-500/60"
            />
          </div>

          <button
            type="submit"
            class="w-full rounded-xl bg-blue-600 py-3 font-semibold text-white shadow-lg shadow-blue-600/30
                   hover:bg-blue-500 active:scale-[0.98] transition"
          >
            Entrar
          </button>
        </form>

        <div class="mt-6 text-xs text-gray-400 flex justify-between">
          <span>© 2026 IW3</span>
          <span>Vue · Vite · Tailwind</span>
        </div>
      </div>
    </div>
  </div>
</template>
