<template>
  <section class="container">
    <h1>Alterar informações da conta</h1>

    <ul>
      <li v-for="(error, index) of errors" :key="index">
        campo <b>{{ error.field }}</b> - {{ error.defaultMessage }}
      </li>
    </ul>

    <div class="usuario">

      <div class="informacoes">
        <div class="dados-usuario">
          <div class="dados-conta">
            <form @submit.prevent="atualizar">

              <h2>Dados pessoais</h2>
              <label for="fotos">Adicionar foto</label>
              <input type="file" id="fotos" name="fotos" />

              <label for="nome">Editar nome</label>
              <input type="text" id="nome" :placeholder="usuario.nome ? usuario.nome : ''" v-model="usuario.nome" />

              <label>Editar telefone</label><input type="text" id="telefone" name="telefone" placeholder="(xx)xxxxx-xxxx"
                v-model="usuario.telefone" v-mask="['(##) ####-####', '(##) #####-####']" />

              <button class="salvar">Salvar alterações</button>
              <button class="cancelar">
                <router-link :to="{ name: 'PerfilUsuarioView' }">Cancelar</router-link>
              </button>
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
        telefone: "",
        uf: "",
        foto: null,
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
      this.usuario.senha = "000000"
      this.uploadFoto().then((foto) => {
        this.usuario.foto = foto;
        console.log(this.usuario)

        Usuario.atualizar(this.usuario)
          .then(() => {
            this.$router.push({ name: 'PerfilUsuarioView' });
            alert('Usuario editado com sucesso!');
            this.errors = [];
            

          })
          .catch((e) => {
            //alert('Todos os campos do usuario devem ser preenchidos!');
            this.errors = e.response.data.errors;
            console.log(this.errors);

            if (e.response && e.response.status === 400) {
              // Bad request error
              this.errorMessage = 'Por favor, preencha todos os campos corretamente. "Bad request"';
            } else if (e.response && e.response.status === 401) {
              // Unauthorized error
              this.errorMessage = 'Favor realizar o Login para continuar. "Unauthorized"';
            } else if (e.response && e.response.status === 500) {
              // Internal server error
              this.errorMessage = 'Por favor, tente novamente mais tarde. "Internal server error"';
            } else {
              // Other errors
              this.errorMessage = 'Houve um erro. Por favor, tente novamente.';
            }

            console.log(this.errorMessage);
            alert(this.errorMessage);

          });
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
  margin-left: 17px;
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
