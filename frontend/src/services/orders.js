import { api } from "./api";

export const OrdersService = {
  list: async () => (await api.get("/orden")).data,
  getById: async (id) => (await api.get(`/orden/${id}`)).data,
};
