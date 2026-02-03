<script setup>
import { computed, onMounted, onUnmounted, ref } from "vue";
import { useRoute } from "vue-router";
import AppLayout from "../layouts/AppLayout.vue";
import { OrdersService } from "../services/orders";
import { statusMeta } from "../utils/orderStatus";

const route = useRoute();
const id = route.params.id;

const loading = ref(true);
const error = ref("");
const order = ref(null);

let timer = null;

const load = async () => {
  error.value = "";
  try {
    order.value = await OrdersService.getById(id);
  } catch (e) {
    error.value = e?.message || "Error cargando la orden";
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  await load();
  // polling cada 2s (suficiente para “tiempo real” en demo)
  timer = setInterval(load, 2000);
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});

const preset = computed(() => Number(order.value?.presetKg ?? order.value?.preset ?? order.value?.monitor?.presetKg ?? 0));
const mass = computed(() => Number(order.value?.massKg ?? order.value?.monitor?.massKg ?? 0));
const flow = computed(() => Number(order.value?.flowKgh ?? order.value?.monitor?.flowKgh ?? 0));

const progress = computed(() => {
  if (!preset.value) return 0;
  return Math.min(100, Math.max(0, (mass.value / preset.value) * 100));
});

const eta = computed(() => {
  // (Preset - Masa) / (Caudal kg/h) → horas
  const remaining = Math.max(0, preset.value - mass.value);
  if (!flow.value || remaining === 0) return null;
  const hours = remaining / flow.value;
  const totalSeconds = Math.floor(hours * 3600);
  const mm = Math.floor(totalSeconds / 60);
  const ss = totalSeconds % 60;
  return `${mm}m ${ss}s`;
});

const fmt = (n, unit = "") => (n === null || n === undefined ? "—" : `${Number(n).toLocaleString()}${unit}`);
</script>

<template>
  <AppLayout>
    <div v-if="loading" class="text-gray-300">Cargando...</div>

    <div v-else>
      <div v-if="error" class="mb-4 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-red-200">
        {{ error }}
      </div>

      <div v-if="order" class="space-y-6">
        <!-- Header -->
        <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-4">
          <div>
            <div class="text-sm text-gray-400">Orden</div>
            <h1 class="text-2xl font-bold">#{{ order.number ?? order.id }}</h1>
            <div class="mt-2 flex items-center gap-3">
              <span class="px-2 py-1 rounded-lg text-xs border" :class="statusMeta(order.status).cls">
                {{ statusMeta(order.status).label }}
              </span>
              <span class="text-sm text-gray-300">Camión: {{ order.truck?.plate ?? "—" }}</span>
              <span class="text-sm text-gray-300">Cliente: {{ order.client?.name ?? "—" }}</span>
            </div>
          </div>

          <div class="rounded-2xl border border-white/10 bg-white/5 p-4">
            <div class="text-xs text-gray-400">ETA estimada</div>
            <div class="text-2xl font-bold text-white">{{ eta ?? "—" }}</div>
            <div class="text-xs text-gray-400 mt-1">basado en caudal actual</div>
          </div>
        </div>

        <!-- Progress -->
        <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
          <div class="flex justify-between text-sm text-gray-300">
            <span>Progreso de carga</span>
            <span>{{ progress.toFixed(1) }}%</span>
          </div>
          <div class="mt-3 h-3 rounded-full bg-black/40 border border-white/10 overflow-hidden">
            <div class="h-full bg-blue-600" :style="{ width: progress + '%' }"></div>
          </div>
          <div class="mt-3 text-xs text-gray-400">
            {{ fmt(mass, " kg") }} / {{ fmt(preset, " kg") }}
          </div>
        </div>

        <!-- Metrics -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
            <div class="text-xs text-gray-400">Masa acumulada</div>
            <div class="text-2xl font-bold mt-1">{{ fmt(order.massKg ?? order.monitor?.massKg, " kg") }}</div>
          </div>

          <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
            <div class="text-xs text-gray-400">Caudal</div>
            <div class="text-2xl font-bold mt-1">{{ fmt(order.flowKgh ?? order.monitor?.flowKgh, " kg/h") }}</div>
          </div>

          <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
            <div class="text-xs text-gray-400">Temperatura</div>
            <div class="text-2xl font-bold mt-1">{{ fmt(order.temperature ?? order.monitor?.temperature, " °C") }}</div>
          </div>

          <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
            <div class="text-xs text-gray-400">Densidad</div>
            <div class="text-2xl font-bold mt-1">{{ fmt(order.density ?? order.monitor?.density) }}</div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>
