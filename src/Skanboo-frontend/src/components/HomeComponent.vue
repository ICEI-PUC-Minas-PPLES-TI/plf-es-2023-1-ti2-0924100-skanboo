<template>
  <section class="products">
    <h1>Anúncios para você</h1>

    <div class="container carregando">
      <div v-for="postagem in postagensPaginadas" :key="postagem.id" class="card">
        <router-link :to="{ name: 'TheProductPage', params: { id: postagem.id }, query: { currentPage: $route.name } }">
          <h2>{{ formatarTitulo(postagem.titulo) }}</h2>
          <div class="card-img-produto">
            <img :src="postagem.foto" alt="" class="card-img" />
          </div>
          <button class="btn-ver-produto">Ver produto</button>
        </router-link>
      </div>
    </div>

    <div class="pagination">
      <div class="center">
        <div class="arrow">
          <a href="#" @click="paginaAnterior"><img src="../assets/left-arrow.png" alt="" /></a>
        </div>
        <div class="pagination">
          <a
            v-for="pagina in numeroPaginas"
            :key="pagina"
            :class="{ active: pagina === paginaAtual }"
            @click="irParaPagina(pagina)"
            >{{ pagina }}</a
          >
        </div>
        <div class="arrow">
          <a href="#" @click="proximaPagina"><img src="../assets/right-arrow.png" alt="" /></a>
        </div>
      </div>
    </div>

    <div class="carrossel-parceiros">
      <h1>Apoios e Parcerias:</h1>
      <div
        class="carrosel-container-parceiros"
        :style="{ transform: `translateX(-${currentSlideIndexParceiro * 100}%)` }"
      >
        <div class="carrosel-item-parceiro" v-for="parceiro in carrosselParceiros" :key="parceiro.id">
          <router-link :to="{ name: 'TheEditPartner', params: { id: parceiro.id } }">
            <div class="card-img-carrosel-parceiro">
              <img :src="parceiro.foto" alt="" class="card-img" />
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import Postagem from '../services/PostagemService';
import Parceiro from '@/services/ParceiroService';
import Oferta from '../services/OfertaService';

export default {
  data() {
    return {
      postagem: {
        id: '',
        titulo: '',
        imagem: '',
        ofertas: [],
      },
      postagens: [],
      paginaAtual: 1,
      postagensPorPagina: 16,
      currentSlideIndex: 0,
      carrosselPostagens: 0,
      parceiro: {
        id: '',
        imagem: '',
      },
      parceiros: [],
      paginaAtualParceiro: 1,
      parceirosPorPagina: 3,
      currentSlideIndexParceiro: 0,
      carrosselParceiros: [],
      timer: null,
    };
  },

  mounted() {
    this.exibirTodasPostagens();
    this.exibirTodosParceiros();
    this.iniciarTemporizador();
  },

  computed: {
    postagensPaginadas() {
      const indiceInicial = (this.paginaAtual - 1) * this.postagensPorPagina;
      const indiceFinal = indiceInicial + this.postagensPorPagina;
      return this.postagens.slice(indiceInicial, indiceFinal);
    },

    numeroPaginas() {
      return Math.ceil(this.postagens.length / this.postagensPorPagina);
    },
  },

  methods: {
    exibirTodasPostagens() {
      Postagem.exibirTodasPostagens().then((resposta) => {
        this.postagens = resposta.data;

        const promises = resposta.data.map((postagem) => {
          return this.listarOfertasPostagem(postagem.id);
        });

        Promise.all(promises).then(() => {
          this.postagens = this.postagens.filter((postagem) => {
            if (postagem.ofertas && postagem.ofertas.length > 0) {
              return !postagem.ofertas.some((oferta) => {
                return oferta.status === 'ACEITA';
              });
            }
            document.querySelector('.container').classList.remove('carregando');
            return true;
          });
        });
      });
    },

    listarOfertasPostagem(id) {
      return Oferta.listarOfertasPostagem(id).then((resposta) => {
        const postagem = this.postagens.find((postagem) => postagem.id === id);
        if (postagem) {
          postagem.ofertas = resposta.data;
        }
      });
    },

    obterPostagensAleatorias() {
      const postagensAleatorias = this.postagens.sort(() => 0.5 - Math.random());
      return postagensAleatorias.slice(0, 4);
    },

    proximoSlide() {
      if (this.currentSlideIndex < this.carrosselPostagens.length - 1) {
        this.currentSlideIndex++;
      } else if (this.currentSlideIndex == this.carrosselPostagens.length - 1) this.currentSlideIndex = 0;
    },

    slideAnterior() {
      if (this.currentSlideIndex > 0) {
        this.currentSlideIndex--;
      } else if (this.currentSlideIndex == 0) this.currentSlideIndex = this.carrosselPostagens.length - 1;
    },

    irParaPagina(pagina) {
      this.paginaAtual = pagina;
    },

    paginaAnterior() {
      if (this.paginaAtual > 1) {
        this.paginaAtual--;
      }
    },

    proximaPagina() {
      if (this.paginaAtual < this.numeroPaginas) {
        this.paginaAtual++;
      }
    },

    exibirTodosParceiros() {
      Parceiro.exibirTodosParceiros().then((resposta) => {
        this.parceiros = resposta.data;
        this.carrosselParceiros = this.obterParceirosAleatorios();
      });
    },

    obterParceirosAleatorios() {
      const parceirosAleatorios = this.parceiros.sort(() => 0.5 - Math.random());
      return parceirosAleatorios.slice(0, 20);
    },

    iniciarTemporizador() {
      this.timer = setInterval(() => {
        this.proximoSlideParceiro();
      }, 3000);
    },

    proximoSlideParceiro() {
      clearInterval(this.timer); // Cancela o temporizador atual
      if (this.currentSlideIndexParceiro < this.carrosselParceiros.length - 1) {
        this.currentSlideIndexParceiro++;
      } else {
        this.currentSlideIndexParceiro = 0;
      }
      this.iniciarTemporizador(); // Reinicia o temporizador
    },

    slideAnteriorParceiro() {
      clearInterval(this.timer); // Cancela o temporizador atual
      if (this.currentSlideIndexParceiro > 0) {
        this.currentSlideIndexParceiro--;
      } else {
        this.currentSlideIndexParceiro = this.carrosselParceiros.length - 1;
      }
      this.iniciarTemporizador(); // Reinicia o temporizador
    },

    formatarTitulo(titulo) {
      const maxLength = 22;
      if (titulo.length > maxLength) {
        return titulo.substring(0, maxLength - 3) + '(...)';
      }
      return titulo;
    },
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
  text-decoration: none;
}

body,
h1,
h2,
h3,
ul,
li,
p,
dd,
dt,
dl {
  margin: 0;
  padding: 0;
}

img {
  display: block;
  max-width: 100%;
}

.container {
  width: 1156px;
  display: flex;
  justify-content: left;
  margin-top: 20px;
  gap: 20px;
  flex-wrap: wrap;
  margin-left: auto;
  margin-right: auto;
}

.carregando {
  display: none;
}

.card {
  display: block;
  width: 274px;
  height: 370px;
  background: #ffffff;
  border: 1px solid #e5e9eb;
  border-radius: 4px;
  transition: all 300ms;
  flex: none;
}

.card:hover .card-img-produto img {
  opacity: 1;
}

.card img,
.carrossel-item img {
  margin-top: 10px;
  border-radius: 4px;
  min-height: 250px;
}

.card h2 {
  text-align: center;
  font-weight: bold;
  padding: 8px;
  box-sizing: border-box;
  margin-left: 5px;
  margin-top: 5px;
  font-size: 15px;
  color: #252c32;
  kerning: -0.6%;
}

.card-img-produto img {
  max-height: 250px;
  width: 250px;
  background-color: grey;
  object-fit: cover;
  display: block;
  margin-left: auto;
  margin-right: auto;
  opacity: 0.8;
  transition: 300ms;
}

.btn-ver-produto {
  cursor: pointer;
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  gap: 4px;
  position: absolute;
  width: 250px;
  height: 32px;
  background: #f9dc5c;
  border: 1px solid #f9dc5c;
  border-radius: 16px;
  font-weight: bold;
  color: #252c32;
  margin-left: 11px;
  text-transform: none;
}

.btn-ver-produto:hover {
  background: #ffe677;
  border: 1px solid #f9dc5c;
  color: white;
}

.like {
  background-color: transparent;
  border: none;
}

.like img {
  width: 20px;
  margin-left: 212px;
  margin-top: -20px;
}

.center {
  display: flex;
  justify-content: space-around;
  text-align: center;
  margin-top: 100px;
  margin-bottom: 100px;
}

.pagination {
  display: inline-block;
  font-size: 14px;
}

.pagination a {
  cursor: pointer;
  color: rgba(102, 112, 133, 1);
  float: left;
  padding: 10px 16px;
  text-decoration: none;
  transition: background-color 0.3s;
  border: 0 solid #ddd;
  border-radius: 100%;
  margin: 0 4px;
}

.pagination a.active {
  background-color: #f9dc5c;
  color: rgba(102, 112, 133, 1);
  border: 0 solid #f9dc5c;
}

.pagination a:hover:not(.active) {
  background-color: #f5f5f5;
  padding: 11px 16px;
}

.arrow img {
  width: 24px;
}

.arrow img:hover {
  background-color: transparent;
}

.carrossel {
  width: 100%;
  height: 400px;
  overflow: hidden;
  transition: transform 0.5s ease-in-out;
}

.carrossel-container {
  display: flex;
  margin: 30px 0 15px 0;
  transition: transform 0.5s ease-in-out;
}

.carrossel-item {
  flex: 0 0 100%;
  padding: 0 10px;
  display: flex;
  flex-wrap: nowrap;
  justify-content: center;
}

.card-img-carrossel {
  width: 100%;
  height: 300px;
}

.carrossel-item img {
  width: 500px;
  height: 300px;
  border-radius: 10px;
}

.carrossel-navigation {
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.carrossel-navigation .arrow:hover {
  background-color: #f5f5f5;
}

/* CARROUSEL PARCEIROS */

.carrossel-parceiros {
  max-width: 100%;
  height: 500px;
  overflow: hidden;
  transition: transform 0.5s ease-in-out;
}

.carrosel-item-parceiro {
  flex: 0 0 100%;
  padding: 0 10px;
  margin-top: 10px;
  display: flex;
  flex-wrap: nowrap;
  justify-content: center;
}

.carrosel-item-parceiro img {
  margin-top: 10px;
  border-radius: 4px;
  max-height: 130px;
  max-width: 130px;
}

.carrosel-container-parceiros {
  display: flex;
  margin: 30px 0 15px 0;
  transition: transform 0.5s ease-in-out;
}

.card-img-carrosel-parceiro {
  width: 100%;
  height: 300px;
  margin-bottom: 0px;
}

.carrosel-navigation-parceiro {
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.carrosel-navigation-parceiro .arrow:hover {
  background-color: #f5f5f5;
}
</style>
