<template>
  <section class="container">
    <h1>Alterar informações da conta</h1>

    <ul>
      <li v-for="(error, index) of errors" :key="index">
        campo <b>{{ error.field }}</b> - {{ error.defaultMessage }}
      </li>
    </ul>

    <div class="usuario">
      <!--<div class="foto-usuario">
               
            </div>-->

      <div class="informacoes">
        <div class="dados-usuario">
          <div class="dados-conta">
            <form @submit.prevent="atualizar">
              <!-- <h2>Dados da conta</h2>
              <label for="email">Email</label>
              <input type="text" id="email" :placeholder="usuario.email ? usuario.email : ''" v-model="usuario.email" />

              <label for="email">Trocar senha</label>
              <input type="text" id="senha" placeholder="*****" />
              <input type="text" id="nova-senha" placeholder="Nova senha" />
              <input type="text" id="nova-senha" placeholder="Confirmar senha" /> -->

              <h2>Dados pessoais</h2>

              <label for="fotos">Adicionar foto</label>
              <input type="file" id="fotos" name="fotos" /><br />

              <label for="nome">Editar nome</label>
              <input type="text" id="nome" :placeholder="usuario.nome ? usuario.nome : ''" v-model="usuario.nome" />

              <!-- <label for="nome">Editar nascimento</label>
              <input type="date" id="nascimento" /> -->

              <label for="telefone">Editar telefone</label>
              <input type="text" id="telefone" name="telefone" class="form-control cel-sp-mask"
                :placeholder="usuario.telefone ? usuario.telefone : ''" v-model="usuario.telefone" />

              <label for="uf">UF</label>
              <select v-model="usuario.uf">
                <option value="">Selecione</option>
                <option value="AC">Acre</option>
                <option value="AL">Alagoas</option>
                <option value="AP">Amapá</option>
                <option value="AM">Amazonas</option>
                <option value="BA">Bahia</option>
                <option value="CE">Ceará</option>
                <option value="DF">Distrito Federal</option>
                <option value="ES">Espirito Santo</option>
                <option value="GO">Goiás</option>
                <option value="MA">Maranhão</option>
                <option value="MS">Mato Grosso do Sul</option>
                <option value="MT">Mato Grosso</option>
                <option value="MG">Minas Gerais</option>
                <option value="PA">Pará</option>
                <option value="PB">Paraíba</option>
                <option value="PR">Paraná</option>
                <option value="PE">Pernambuco</option>
                <option value="PI">Piauí</option>
                <option value="RJ">Rio de Janeiro</option>
                <option value="RN">Rio Grande do Norte</option>
                <option value="RS">Rio Grande do Sul</option>
                <option value="RO">Rondônia</option>
                <option value="RR">Roraima</option>
                <option value="SC">Santa Catarina</option>
                <option value="SP">São Paulo</option>
                <option value="SE">Sergipe</option>
                <option value="TO">Tocantins</option>
              </select>

              <label for="nome">Editar nome</label>
              <input type="text" id="nome" :placeholder="usuario.nome ? usuario.nome : ''" v-model="usuario.nome" />

              <!-- <label for="cpf">Editar CPF</label>
              <input type="text" id="cpf" name="cpf" class="form-control cpf-mask"
                :placeholder="usuario.cpf ? usuario.cpf : ''" v-model="usuario.cpf" /> -->
              <button class="salvar">Salvar alterações</button>
              <button class="cancelar">Cancelar</button>
            </form>
          </div>
        </div>
      </div>

    </div>
  </section>
</template>

<script>
import Usuario from "../services/UsuarioService";
import { mask } from "vue-the-mask";

export default {
  directives: { mask },
  data() {
    return {
      usuario: {
        nome: "",
        nascimento: "",
        email: "",
        telefone: "",
        uf: "",
        senha: "",
        cpf: "",
      },
    };
  },

  mounted() {
    Usuario.exibirInfo().then((resposta) => {
      console.log(resposta.data);
      this.usuario = resposta.data;
    })
  },

  methods: {
    atualizar() {
      const dadosUsuario = {
        nome: this.usuario.nome,
        cpf: this.usuario.cpf,
        uf: this.usuario.uf,
        email: this.usuario.email,
        senha: "teste",
        dataNascimento: "1997-04-20",
        telefone: this.usuario.telefone
      }
      Usuario.atualizar(dadosUsuario)
        .then((/*resposta*/) => {
          alert("Informações do usuario editadas com sucesso");
          this.errors = [];
        })
        .catch((e) => {
          this.errors = e.response.data.errors;
          console.log(this.errors);
          console.log(this.usuario);
        });
    },
    uploadFoto() {
      return new Promise((resolve, reject) => {
        const fileInput = document.querySelector('input[type=file]');
        const file = fileInput.files[0];

        const reader = new FileReader();
        reader.addEventListener(
          'load',
          () => {
            resolve(reader.result);
          },
          false
        );

        reader.addEventListener('error', reject);

        if (file) {
          reader.readAsDataURL(file);
        } else {
          resolve(null);
        }
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

.usuario {
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

.foto-usuario {
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
  font-size: 14px;
  color: #515864;
  transition: 0.3s;
  margin-top: 10px;
  margin-left: 0;
}

.info-usuario p {
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

select {
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
  padding: 0px;
  margin-bottom: 15px;
  margin-left: 0;
}

input::placeholder {
  color: #9798b0;
}

label {
  display: inline-block;
  color: #515864;
  margin-bottom: 5px;
  text-align: left !important;
}

h1,
h2 {
  padding: 10px;
  padding-left: 0;
}

.cancelar {
  width: 300px;
  height: 30px;
  background-color: #fcfcfc;
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  color: #9798b0;
  font-size: 14px;
  letter-spacing: -0.006em;
}
</style>
