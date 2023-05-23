<template>
  <section class="container">
    <h1>Cadastrar parceiro</h1>

    <ul>
      <li v-for="(error, index) of errors" :key="index">
        campo <b>{{ error.field }}</b> - {{ error.defaultMessage }}
      </li>
    </ul>

    <div class="parceiro">
      <div class="dados-parceiro">
        <form @submit.prevent="criar">
            <label for="titulo">Nome da empresa</label>
            <input 
            type="text" 
            id="nome" 
            maxlength="30"
            v-model="parceiro.nome"
            />

            <label for="logo">Logo da empresa</label><br />
            <input 
            type="file" 
            id="logo" 
            name="logo"
            />

            <label for="preferencias">CNPJ</label>
            <input 
            type="text" 
            id="cnpj" 
            v-mask="['##.###.###/####-##']" 
            v-model="parceiro.cnpj"
            />

            <fieldset>
              <legend>Plano de parceria</legend>
              <input 
              type="radio" 
              id="standard" 
              name="plano" 
              value="standard" checked />
              <label for="sim">Standard</label>

              <input 
              type="radio" 
              id="premium" 
              name="plano" 
              value="premium" />
              <label for="premium">Premium</label>

              <input 
              type="radio" 
              id="basic" 
              name="plano" 
              value="basic" />
              <label for="basic">Basic</label>
            </fieldset>
            <br /><br />

            <label for="contrato">Adicionar contrato</label>
            <input 
            type="file" 
            id="contrato" 
            name="contrato" /><br />
          <button class="editar">Enviar</button>
        </form>
      </div>
    </div>
  </section>
</template>

<script>
import Parceiro from "../services/ParceiroService";
import { mask } from "vue-the-mask";
export default {
  directives: { mask },
  data() {
    return {
      parceiro: {
        nome: "",
        cnpj: "",
        plano: "",
      },
      errors: [],
    };
  },

  methods: {
    criar() {
      Parceiro.criar(this.parceiro)
        .then((/*resposta*/) => {
          alert("Parceiro salvo com sucesso");
          this.errors = [];
        })
        .catch((e) => {
          this.errors = e.response.data.errors;
          console.log(this.errors);
        });
    },
  },
};
</script>

<style scoped>
.container {
  max-width: 720px;
  margin: 0 auto;
  text-align: left;
  margin-bottom: 30px;
}

.parceiro {
  border: 1px solid #eae9e9;
  border-radius: 4px;
  padding: 20px;
}

.informacoes {
  display: flex;
  justify-content: flex-start;
}

li {
  list-style: none;
}

.foto-Parceiro {
  width: 120px;
  height: 120px;
  border-radius: 100%;
  background-color: lightgray;
  align-items: center;
  margin-left: 150px;
}

button {
  box-sizing: border-box;
  padding: 2px 6px 2px 8px;
  gap: 4px;
  width: 300px;
  height: 32px;
  background: #f9dc5c;
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  font-weight: 600;
  color: #515864;
  transition: 0.3s;
  margin-left: 0;
  margin-top: 15px;
}

.info-Parceiro p {
  color: #c0c2c7;
}

input {
  display: block;
  box-sizing: border-box;
  width: 680px;
  height: 40px;
  background-color: #fcfcfc;
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  color: #9798b0;
  font-size: 16px;
  letter-spacing: -0.006em;
  padding: 20px;
  margin-bottom: 15px;
}

input::placeholder {
  color: #9798b0;
}

label {
  display: inline-block;
  color: #515864;
  margin-bottom: 5px;
}

textarea {
  display: block;
  box-sizing: border-box;
  width: 680px;
  height: 200px;
  background-color: #fcfcfc;
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  color: #9798b0;
  font-size: 16px;
  letter-spacing: -0.006em;
  padding: 20px;
  margin-bottom: 15px;
}

h1,
h2 {
  padding: 10px;
  padding-left: 0;
}

input[type="radio"] {
  display: inline-block;
  width: auto;
  height: auto;
}

fieldset {
  padding: 10px;
}

legend {
  display: inline-block;
  padding: 0 10px;
}
</style>
