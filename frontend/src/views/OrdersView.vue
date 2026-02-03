<script setup>
import { computed, onMounted, ref } from "vue";
import AppLayout from "../layouts/AppLayout.vue";
import { OrdersService } from "../services/orders";
import { statusMeta } from "../utils/orderStatus";

const loading = ref(true);
const error = ref("");
const q = ref("");
const statusFilter = ref("ALL");
const orders = ref([]);

const load = async () => {
  loading.value = true;
  error.value = "";
  try {
    orders.value = await OrdersService.list();
  } catch (e) {
    error.value = e?.message || "Error cargando órdenes";
  } finally {
    loading.value = false;
  }
};

onMounted(load);

const statuses = computed(() => {
  const set = new Set((orders.value || []).map(o => (o.status || "").toUpperCase()).filter(Boolean));
  return ["ALL", ...Array.from(set)];
});

const filtered = computed(() => {
  const qq = q.value.trim().toLowerCase();
  return (orders.value || []).filter((o) => {
    const s = (o.status || "").toUpperCase();
    const okStatus = statusFilter.value === "ALL" || s === statusFilter.value;
    const hay = `${o.id ?? ""} ${o.number ?? ""} ${o.truck?.plate ?? ""} ${o.client?.name ?? ""}`.toLowerCase();
    const okQ = !qq || hay.includes(qq);
    return okStatus && okQ;
  });
});

// Helpers UI (por si tu dto trae datos de monitoreo)
const fmt = (n) => (n === null || n === undefined ? "—" : Number(n).toLocaleString());
</script>

<template>
  <AppLayout>
    <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-4 mb-6">
      <div>
        <h1 class="text-2xl font-bold">Órdenes</h1>
        <p class="text-gray-400 mt-1">Monitoreo y estado de carga</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 sm:items-center">
        <select
          v-model="statusFilter"
          class="rounded-xl bg-black/30 border border-white/10 px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-blue-500/60"
        >
          <option v-for="s in statuses" :key="s" :value="s">
            {{ s === "ALL" ? "Todos los estados" : s }}
          </option>
        </select>

        <input
          v-model="q"
          placeholder="Buscar (id, patente, cliente...)"
          class="w-full sm:w-72 rounded-xl bg-black/30 border border-white/10 px-4 py-2 text-white placeholder:text-gray-500
                 focus:outline-none focus:ring-2 focus:ring-blue-500/60"
        />

        <button
          class="rounded-xl bg-white/5 hover:bg-white/10 border border-white/10 px-4 py-2 font-semibold transition"
          @click="load"
        >
          ↻ Actualizar
        </button>
      </div>
    </div>

    <div v-if="error" class="mb-4 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-red-200">
      {{ error }}
    </div>

    <div class="rounded-2xl border border-white/10 bg-white/5 overflow-hidden">
      <div v-if="loading" class="p-6 text-gray-300">Cargando...</div>

      <table v-else class="w-full text-sm">
        <thead class="bg-black/30 text-gray-300">
          <tr>
            <th class="text-left px-5 py-4">Orden</th>
            <th class="text-left px-5 py-4">Estado</th>
            <th class="text-left px-5 py-4">Camión</th>
            <th class="text-left px-5 py-4">Preset</th>
            <th class="text-left px-5 py-4">Masa</th>
            <th class="text-left px-5 py-4">Temp</th>
            <th class="text-left px-5 py-4">Dens.</th>
            <th class="text-left px-5 py-4">Caudal</th>
            <th class="text-right px-5 py-4">Acciones</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="o in filtered"
            :key="o.id"
            class="border-t border-white/10 hover:bg-white/5 transition"
          >
            <td class="px-5 py-4 font-medium">
              <div class="text-white">#{{ o.number ?? o.id }}</div>
              <div class="text-xs text-gray-400">ID: {{ o.id }}</div>
            </td>

            <td class="px-5 py-4">
              <span
                class="px-2 py-1 rounded-lg text-xs border"
                :class="statusMeta(o.status).cls"
              >
                {{ statusMeta(o.status).label }}
              </span>
            </td>

            <td class="px-5 py-4 text-gray-200">
              {{ o.truck?.plate ?? "—" }}
            </td>

            <td class="px-5 py-4 text-gray-200">
              {{ fmt(o.presetKg ?? o.preset ?? o.monitor?.presetKg) }}
            </td>

            <td class="px-5 py-4 text-gray-200">
              {{ fmt(o.massKg ?? o.monitor?.massKg) }}
            </td>

            <td class="px-5 py-4 text-gray-200">
              {{ fmt(o.temperature ?? o.monitor?.temperature) }}
            </td>

            <td class="px-5 py-4 text-gray-200">
              {{ fmt(o.density ?? o.monitor?.density) }}
            </td>

            <td class="px-5 py-4 text-gray-200">
              {{ fmt(o.flowKgh ?? o.monitor?.flowKgh) }}
            </td>

            <td class="px-5 py-4 text-right">
              <router-link
                :to="`/orders/${o.id}`"
                class="text-blue-300 hover:text-blue-200 font-semibold"
              >
                Ver
              </router-link>
            </td>
          </tr>

          <tr v-if="filtered.length === 0">
            <td colspan="9" class="px-5 py-10 text-center text-gray-400">
              No hay órdenes con esos filtros.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </AppLayout>
</template>
