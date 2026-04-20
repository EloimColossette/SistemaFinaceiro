async function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const btn = document.getElementById("btnLogin");

    btn.innerText = "Entrando...";
    btn.disabled = true;

    try {
        const response = await fetch("http://localhost:8080/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email,
                password
            })
        });

        const data = await response.text();

        if (response.ok) {
            localStorage.setItem("token", data);

            window.location.href = "dashboard.html";
        } else {
            document.getElementById("erro").innerText = data;
        }

    } catch (error) {
        document.getElementById("erro").innerText = "Erro ao conectar com o servidor";
    }

    btn.innerText = "Entrar";
    btn.disabled = false;
}