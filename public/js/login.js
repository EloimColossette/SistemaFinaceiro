// LOGIN
async function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const btn = document.getElementById("btnLogin");
    const msg = document.getElementById("mensagem");

    msg.innerText = "";
    msg.className = "";

    btn.innerText = "Entrando...";
    btn.disabled = true;

    try {
        const response = await fetch("http://localhost:8080/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.text();

        if (response.ok) {
            localStorage.setItem("token", data);

            msg.innerText = "Login realizado com sucesso!";
            msg.className = "sucesso";

            setTimeout(() => {
                window.location.href = "dashboard.html";
            }, 1500);

        } else {
            msg.innerText = "Usuário ou senha incorreta";
            msg.className = "erro";
        }

    } catch (error) {
        msg.innerText = "Erro ao conectar com o servidor";
        msg.className = "erro";
    }

    btn.innerText = "Entrar";
    btn.disabled = false;
}


// 🔑 FORGOT PASSWORD (FORA!)
async function forgotPassword() {
    const email = document.getElementById("email").value;
    const msg = document.getElementById("mensagem");

    msg.innerText = "";
    msg.className = "";

    if (!email) {
        msg.innerText = "Digite seu email primeiro";
        msg.className = "erro";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/password/forgot-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email })
        });

        const data = await response.json();

        if (response.ok) {
            msg.innerText = "Email de recuperação enviado!";
            msg.className = "sucesso";
        } else {
            msg.innerText = data.message;
            msg.className = "erro";
        }

    } catch (error) {
        msg.innerText = "Erro ao conectar com servidor";
        msg.className = "erro";
    }
}