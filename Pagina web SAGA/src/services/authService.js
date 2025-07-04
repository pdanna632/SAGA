import axios from "axios";

export const login = async (usuario, contrasena) => {
  try {
    const response = await axios.post("http://localhost:8080/api/login", {
      usuario,
      contrasena,
    });

    if (response.data.estado === "exito") {
      // Puedes guardar token o estado aquí
      localStorage.setItem("logueado", "true");
      return true;
    }

    return false;
  } catch (error) {
    console.error("Error al iniciar sesión:", error);
    return false;
  }
};
