async function forgotPassword() {
    const email = document.getElementById("email").value.trim();
    const btn = document.getElementById("btnEnviar");
    const msg = document.getElementById("mensagem");

    msg.innerText = "";
    msg.className = "";

    if (!email) {
        msg.innerText = "Digite seu email";
        msg.className = "erro";
        return;
    }

    btn.innerText = "Enviando...";
    btn.disabled = true;

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
            msg.innerText = "Email enviado! Verifique sua caixa de entrada.";
            msg.className = "sucesso";
        } else {
            msg.innerText = data.message;
            msg.className = "erro";
        }

    } catch (error) {
        msg.innerText = "Erro ao conectar com o servidor";
        msg.className = "erro";
    } finally {
        btn.innerText = "Enviar link";
        btn.disabled = false;
    }
}