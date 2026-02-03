import api from "./api";

export async function login(username, password) {
  const form = new URLSearchParams();
  form.append("username", username);
  form.append("password", password);

  // devuelve token como TEXT/PLAIN
  const { data: token } = await api.post("/api/v1/login", form, {
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
  });

  localStorage.setItem("token", token);
  return token;
}

export function logout() {
  localStorage.removeItem("token");
}
