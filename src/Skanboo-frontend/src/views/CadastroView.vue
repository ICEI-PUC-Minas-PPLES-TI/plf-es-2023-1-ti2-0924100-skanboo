<template>
  <section class="cadastro-final">
    <h1>Cadastrar usuário</h1>

    <ul>
      <li v-for="(error, index) of errors" :key="index">
        campo <b>{{ error.field }}</b> - {{ error.defaultMessage }}
      </li>
    </ul>

    <form @submit.prevent="criar">
      <label>Nome</label>
      <input type="text" name="nome" id="nome" v-model="usuario.nome" />

      <label>Email</label>
      <input type="text" name="email" id="email" v-model="usuario.email" />

      <label>Senha</label>
      <input type="password" name="senha" id="senha" v-model="usuario.senha" />

      <label>Data de nascimento</label>
      <input
        type="date"
        name="nascimento"
        id="nascimento"
        v-model="usuario.dataNascimento"
      />

      <label>CPF</label>
      <input
        type="text"
        name="cpf"
        id="cpf"
        placeholder="xxx.xxx.xxx-xx"
        v-model="usuario.cpf"
        v-mask="['###.###.###-##']"
      />

      <!-- <label for="foto">Selecione uma foto de perfil</label>
      <input type="file" id="foto" name="foto" accept="image/*" /> -->

      <label>Telefone</label
      ><input
        type="text"
        id="telefone"
        name="telefone"
        placeholder="(xx)xxxxx-xxxx"
        v-model="usuario.telefone"
        v-mask="['(##) ####-####', '(##) #####-####']"
      />

      <button class="btn">Criar a sua conta</button>
      <p>
        Já tem uma conta? <a href="/loginView"><b>Entrar.</b></a>
      </p>
    </form>
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
        cpf: "",
        email: "",
        senha: "",
        dataNascimento: "",
        uf: "",
        telefone: "",
      },
      usuarioLogin: {
        email: "",
        senha: "",
      },
      errors: [],
    };
  },

  methods: {
    criar() {
      Usuario.criar(this.usuario)
        .then(() => {
          alert("Usuário salvo com sucesso");
          this.errors = [];
          this.usuarioLogin.email = this.usuario.email;
          this.usuarioLogin.senha = this.usuario.senha;
          console.log(this.usuarioLogin);
          this.logar();
        })
        .catch((e) => {
          console.log(this.errors);

          if (e.response && e.response.status === 400) {
            this.errorMessage =
              "Por favor, preencha todos os campos corretamente.";
          } else if (e.response && e.response.status === 401) {
            this.errorMessage = "Favor realizar o Login para continuar.";
          } else if (e.response && e.response.status === 500) {
            this.errorMessage = "Por favor, tente novamente mais tarde.";
          }

          //   else if (e.response && e.response.status === 409) {
          //   this.errorMessage = 'Já existe um usuário com este emai, nome, telefone ou CPF!';
          // }
          else {
            this.errorMessage = "Houve um erro. Por favor, tente novamente.";
          }

          console.log(this.errorMessage);
          alert(this.errorMessage);
        });
    },

    logar() {
      Usuario.logar(this.usuarioLogin)
        .then((resposta) => {
          // alert("Usuário logado com sucesso");
          console.log(resposta);
          const token = resposta.headers.getAuthorization();
          if (!token)
            throw new Error("Ocorreu um erro ao tentar logar usuário!");
          localStorage.setItem("token", token);
          // aqui redireciona pagina
          return this.$router.push({ name: "CadastroEnderecoView" });
          // this.errors = [];
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
.cadastro-final {
  max-width: 500px;
  margin: 0 auto;
  margin-top: -20px;
}
form {
  display: grid;
}

input {
  width: 300px;
  height: 0px;
  background-color: #fcfcfc;
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  color: #9798b0;
  font-size: 16px;
  letter-spacing: -0.006em;
  padding: 20px;
  margin-right: auto;
  margin-left: auto;
}

.btn {
  width: 300px;
  height: 38px;
  background: #f9dc5c;
  border: 1px solid #f9dc5c;
  border-radius: 32px;
  font-weight: 600;
  color: #23272f;
  transition: 0.3s;
  margin-right: auto;
  margin-left: auto;
  margin-top: 30px;
  transition: 0.3s;
}

.btn:hover {
  background: #fcfcfc;
  border: 1px solid #9798b0;
  color: white;
}

label {
  margin-right: auto;
  margin-left: 80px;
  padding: 10px 10px 10px 0;
}

a {
  text-decoration: none;
}
</style>
