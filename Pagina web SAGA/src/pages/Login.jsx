import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";
import styles from "../styles/Login.module.css";

function Login() {
  const [usuario, setUsuario] = useState("");
  const [contrasena, setContrasena] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const exito = await login(usuario, contrasena);
    if (exito) {
      navigate("/dashboard");
    } else {
      alert("Credenciales inválidas");
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.box}>
        <h2 className={styles.title}>Iniciar Sesión</h2>
        <form onSubmit={handleSubmit} className={styles.form}>
          <input
            type="text"
            placeholder="Usuario"
            value={usuario}
            onChange={(e) => setUsuario(e.target.value)}
          />
          <input
            type="password"
            placeholder="Contraseña"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
          />
          <button type="submit">Entrar</button>
        </form>
      </div>
    </div>
  );
}

export default Login;
