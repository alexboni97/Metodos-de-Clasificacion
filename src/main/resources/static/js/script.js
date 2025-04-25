function calcularMedias() {
    fetch("/mediaBayes")
      .then(response => response.json())
      .then(data => {
        const setosaMedia = data.setosa.join(", ");
        const versicolorMedia = data.versicolor.join(", ");
        document.getElementById("media-setosa").textContent = setosaMedia;
        document.getElementById("media-versicolor").textContent = versicolorMedia;
      })
      .catch(error => {
        console.error("Error al obtener las medias:", error);
      });
  }
document.addEventListener('DOMContentLoaded', function () {
    
  document.getElementById("calcula-todo-k-medias").addEventListener('click',()=>{
    fetch("/calcular-todo-k-medias",{
      method:"POST",
      headers:{
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": config.csrf.value // si usas CSRF con Spring Security
      },
      body:{}
    }).then(data=>{
      data.forEach(sol => {
        const row= document.getElementById(`#tabla-ejemplos-k-medias tbody tr[data-id="${sol.id}"]`)
        if(row){
          const celda = row.querySelector(".sol-k-medias")
          if(celda){
            celda.textContent=sol.solucion
          }
        }
      });
    }).catch(error=>{
      console.log("error al mostrar soluciones de los ejemplos: ",error)
    })

  })
})