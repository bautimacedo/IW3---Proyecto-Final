import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import HomeView from "../views/HomeView.vue";
import OrdersView from "../views/OrdersView.vue";
import OrderDetailView from "../views/OrderDetailView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/login", component: LoginView, meta: { title: "Login" } },
    { path: "/", component: HomeView, meta: { title: "Dashboard" } },
    { path: "/orders", component: OrdersView, meta: { title: "Órdenes" } },
    { path: "/orders/:id", component: OrderDetailView, meta: { title: "Detalle orden" } },
  ],
});

export default router;
