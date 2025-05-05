function calcularMedias() {
  fetch("/mediaBayes")
    .then((response) => response.json())
    .then((data) => {
      const setosaMedia = data.setosa.join(", ");
      const versicolorMedia = data.versicolor.join(", ");
      document.getElementById("media-setosa").textContent = setosaMedia;
      document.getElementById("media-versicolor").textContent = versicolorMedia;
    })
    .catch((error) => {
      console.error("Error al obtener las medias:", error);
    });
}
document.addEventListener("DOMContentLoaded", function () {
  document.getElementById("form-k-medias").addEventListener("submit", async function(event) {
    event.preventDefault();

    const payload = {
        x1: parseFloat(document.getElementById("x1").value),
        x2: parseFloat(document.getElementById("x2").value),
        x3: parseFloat(document.getElementById("x3").value),
        x4: parseFloat(document.getElementById("x4").value),
        epsilon: parseFloat(document.getElementById("epsilon").value) || 0.01,
        b: parseFloat(document.getElementById("b").value) || 2.0
    };

    const response = await fetch("/clasificar-k-medias", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    const data = await response.json();
    const porcentajes = data.grados.map(g => (g * 100).toFixed(2) + "%");

    document.getElementById("resultado").innerHTML = `
        <p><strong>Clúster 0:</strong> ${porcentajes[0]}</p>
        <p><strong>Clúster 1:</strong> ${porcentajes[1]}</p>
    `;
});
  document
    .getElementById("calcula-todo-k-medias")
    .addEventListener("click", () => {
      fetch("/calcular-todo-k-medias", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": config.csrf.value, // si usas CSRF con Spring Security
        },
        body: {},
      })
        .then((data) => {
          data.forEach((sol) => {
            const row = document.getElementById(
              `#tabla-ejemplos-k-medias tbody tr[data-id="${sol.id}"]`
            );
            if (row) {
              const celda = row.querySelector(".sol-k-medias");
              if (celda) {
                celda.textContent = sol.solucion;
              }
            }
          });
        })
        .catch((error) => {
          console.log("error al mostrar soluciones de los ejemplos: ", error);
        });
    });
});
