<script setup>
import { ref } from "vue";
import AppLayout from "../layouts/AppLayout.vue";

const q = ref("");
const products = ref([
  { id: 1, name: "Camión 12T", price: 120000, stock: 3 },
  { id: 2, name: "Repuesto Filtro", price: 15000, stock: 40 },
  { id: 3, name: "Aceite 5W30", price: 9000, stock: 12 },
]);

const filtered = () =>
  products.value.filter((p) =>
    p.name.toLowerCase().includes(q.value.toLowerCase())
  );
</script>

<template>
  <AppLayout>
    <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-4 mb-6">
      <div>
        <h1 class="text-2xl font-bold">Productos</h1>
        <p class="text-gray-400 mt-1">Gestioná tu catálogo</p>
      </div>

      <div class="flex gap-3">
        <input
          v-model="q"
          placeholder="Buscar..."
          class="w-full md:w-72 rounded-xl bg-black/30 border border-white/10 px-4 py-2 text-white placeholder:text-gray-500
                 focus:outline-none focus:ring-2 focus:ring-blue-500/60"
        />
        <button
          class="rounded-xl bg-blue-600 hover:bg-blue-500 px-4 py-2 font-semibold transition"
        >
          ➕ Nuevo
        </button>
      </div>
    </div>

    <div class="rounded-2xl border border-white/10 bg-white/5 overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-black/30 text-gray-300">
          <tr>
            <th class="text-left px-5 py-4">Nombre</th>
            <th class="text-left px-5 py-4">Precio</th>
            <th class="text-left px-5 py-4">Stock</th>
            <th class="text-right px-5 py-4">Acciones</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="p in filtered()"
            :key="p.id"
            class="border-t border-white/10 hover:bg-white/5 transition"
          >
            <td class="px-5 py-4 font-medium">{{ p.name }}</td>
            <td class="px-5 py-4 text-gray-300">${{ p.price.toLocaleString() }}</td>
            <td class="px-5 py-4">
              <span
                class="px-2 py-1 rounded-lg text-xs border"
                :class="p.stock <= 5
                  ? 'bg-red-500/10 border-red-500/30 text-red-200'
                  : 'bg-green-500/10 border-green-500/30 text-green-200'"
              >
                {{ p.stock }}
              </span>
            </td>
            <td class="px-5 py-4 text-right">
              <button class="text-blue-300 hover:text-blue-200 mr-4">Editar</button>
              <button class="text-red-300 hover:text-red-200">Eliminar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </AppLayout>
</template>
