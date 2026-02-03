<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const nav = [
  { name: "Dashboard", to: "/", icon: "🏠" },
  { name: "Productos", to: "/products", icon: "📦" },
  { name: "Órdenes", to: "/orders", icon: "🧾" },
  { name: "Usuarios", to: "/users", icon: "👤" },
];

const isActive = (to) => route.path === to;

const logout = () => {
  // después: borrar token + redirect
  router.push("/login");
};
</script>

<template>
  <div class="min-h-screen bg-gray-950 text-white">
    <div class="flex">
      <!-- Sidebar -->
      <aside class="hidden md:flex w-64 flex-col border-r border-white/10 bg-black/30">
        <div class="h-16 flex items-center px-6 border-b border-white/10">
          <div class="font-bold text-lg tracking-tight">Proyecto IW3</div>
        </div>

        <nav class="p-4 space-y-2">
          <router-link
            v-for="item in nav"
            :key="item.to"
            :to="item.to"
            class="flex items-center gap-3 rounded-xl px-4 py-3 text-sm transition border border-transparent"
            :class="isActive(item.to)
              ? 'bg-white/10 border-white/10'
              : 'text-gray-300 hover:bg-white/5 hover:border-white/10'"
          >
            <span class="text-lg">{{ item.icon }}</span>
            <span class="font-medium">{{ item.name }}</span>
          </router-link>
        </nav>

        <div class="mt-auto p-4 border-t border-white/10">
          <button
            class="w-full rounded-xl bg-white/5 hover:bg-white/10 border border-white/10 px-4 py-3 text-sm text-gray-200 transition"
            @click="logout"
          >
            Cerrar sesión
          </button>
        </div>
      </aside>

      <!-- Main -->
      <div class="flex-1">
        <!-- Topbar -->
        <header class="h-16 flex items-center justify-between px-6 border-b border-white/10 bg-black/20">
          <div class="md:hidden font-bold">IW3</div>

          <div class="flex items-center gap-3">
            <div class="text-sm text-gray-300">
              {{ route.meta.title ?? "Panel" }}
            </div>
            <div class="h-9 w-9 rounded-full bg-blue-500/20 border border-blue-500/30 flex items-center justify-center">
              <span class="text-blue-300 font-semibold text-sm">A</span>
            </div>
          </div>
        </header>

        <main class="p-6">
          <slot />
        </main>
      </div>
    </div>
  </div>
</template>
