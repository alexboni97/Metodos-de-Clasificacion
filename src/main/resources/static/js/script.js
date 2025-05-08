
document.addEventListener("DOMContentLoaded", function () {
  if(document.getElementById("form-k-medias")){ 
  document.getElementById("form-k-medias").addEventListener("submit", async function(event) {
    event.preventDefault();

    const payload = {
        x1: parseFloat(document.getElementById("x1").value),
        x2: parseFloat(document.getElementById("x2").value),
        x3: parseFloat(document.getElementById("x3").value),
        x4: parseFloat(document.getElementById("x4").value),
    };

    const response = await fetch("/clasificar-k-medias", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    const data = await response.json();
    const p0 = (data.grados[0] * 100).toFixed(2);
    const p1 = (data.grados[1] * 100).toFixed(2);

    document.getElementById("resultado").innerHTML = `
        <div class="barra-clasificacion" style="display: flex; height: 30px; border: 1px solid #ccc; border-radius: 5px; overflow: hidden; margin-top: 10px;">
            <div style="width: ${p0}%; background-color: #4CAF50; color: white; text-align: center; line-height: 30px;">
              Iris-setosa  (${p0}%)
            </div>
            <div style="width: ${p1}%; background-color: #2196F3; color: white; text-align: center; line-height: 30px;">
              Iris-versicolor  (${p1}%)
            </div>
        </div>
    `;

});}
if(document.getElementById("btn-ejemplos")){

  document.getElementById("btn-ejemplos").addEventListener("click", async function () {
    const ejemplos = [
      { x1: 5.1, x2: 3.5, x3: 1.4, x4: 0.2 },
      { x1: 6.0, x2: 2.7, x3: 4.5, x4: 1.5 },
      { x1: 5.8, x2: 2.7, x3: 3.9, x4: 1.2 }
    ];
    
    const resultados = await Promise.all(ejemplos.map(async muestra => {
      const res = await fetch("/clasificar-k-medias", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(muestra)
      });
      const data = await res.json();
      return { muestra, grados: data.grados };
    }));
    
    let html = "";
    resultados.forEach(({ muestra, grados }, idx) => {
      const p0 = (grados[0] * 100).toFixed(2);
      const p1 = (grados[1] * 100).toFixed(2);
      
      html += `
      <div style="margin-bottom: 10px;">
      <p><strong>Resultado Muestra ${idx + 1}:</strong></p>
      <div style="display: flex; justify-content: space-between; font-weight: bold; margin-bottom: 0px;">
      <span style="color: #4CAF50;">Iris-setosa (${p0}%)</span>
      <span style="color: #2196F3;">Iris-versicolor (${p1}%)</span>
      </div>
      <div style="display: flex; height: 30px; border: 1px solid #ccc; border-radius: 5px; overflow: hidden;">
      <div style="width: ${p0}%; background-color: #4CAF50;"></div>
      <div style="width: ${p1}%; background-color: #2196F3;"></div>
      </div>
      <br>
      </div>
      `;
    });
    
    document.getElementById("ejemplos-container").innerHTML = html;
  });
  
}

  
});
