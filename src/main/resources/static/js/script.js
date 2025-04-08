async function enviarDatos() {
    const entrada = [
        document.getElementById("categoria1").value,
        document.getElementById("categoria2").value
    ];

    const response = await fetch("/id3/predict", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(entrada)
    });

    const resultado = await response.text();
    document.getElementById("resultado").innerText = "Predicción: " + resultado;
}
