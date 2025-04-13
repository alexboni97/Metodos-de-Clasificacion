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
  