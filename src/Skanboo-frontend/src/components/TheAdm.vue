<template>
  <section class="container">
    <h1>Informações do administrador</h1>
    <div class="usuario">
      <div class="informacoes">
        <div class="dados-usuario">
          <p class="nomeUsuario">Nome</p>
          <p class="nascimentoUsuario">Nascimento</p>
          <p class="emailUsuario">Email</p>
          <p class="telefoneUsuario">Telefone</p>
          <p class="senhaUsuario">Senha</p>
          <p class="CPFUsuario">CPF</p>
        </div>

        <div class="info-usuario">
          <p class="nome">{{ usuario.nome }}</p>
          <p class="nascimento">
            {{
              usuario.dataNascimento
                ? `${usuario.dataNascimento[2]}/${usuario.dataNascimento[1]}/${usuario.dataNascimento[0]}`
                : ""
            }}
          </p>
          <p class="email">{{ usuario.email }}</p>
          <p class="telefone">{{ usuario.telefone }}</p>
          <p class="senha">*****</p>
          <p class="CPF">{{ usuario.cpf }}</p>
        </div>
      </div>
      <button class="editar"><a href="/editarUsuarioView">Editar</a></button>
      <button class="sair"><a href="http://">Sair</a></button>
    </div>

    <h1>Gerenciar tarefas</h1>
    <div class="gerenciar-produtos container usuario">
      <a href="/cooptarparceirosview">Cooptar parceiros</a><br />
    </div>
  </section>
</template>

<script>
import Usuario from "../services/UsuarioService";

export default {
  data() {
    return {
      usuario: {
        nome: "",
        nascimento: "",
        email: "",
        telefone: "",
        senha: "",
        cpf: "",
      },
    };
  },

  mounted() {
    Usuario.exibirInfo()
      .then((resposta) => {
        if (resposta.data && resposta.data.dataNascimento) {
          this.usuario = resposta.data;
        } else {
          // Handle the case when dataNascimento is missing or undefined
          console.error("Invalid API response");
        }
      })
      .catch((error) => {
        // Handle any error that occurs during the API request
        console.error(error);
      });
  },
};
</script>

<style scoped>
.container {
  max-width: 500px;
  margin: 0 auto;
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
  margin-left: 180px;
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
  margin-left: 30px;
  margin-top: 30px;
}
p {
  font-size: 1rem;
  text-align: left !important;
  padding: 10px;
}
.dados-usuario {
  margin-left: 30px;
}
.info-usuario p {
  color: #c0c2c7;
  margin-left: 50px;
}

h1,
h2 {
  padding: 10px;
  padding-left: 0;
}

.sair {
  box-sizing: border-box;
  padding: 2px 6px 2px 8px;
  gap: 4px;
  width: 300px;
  height: 32px;
  background: #fcfcfc;
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  font-weight: 600;
  color: #515864;
  transition: 0.3s;
  margin-left: 30px;
  margin-top: 0px;
}

.gerenciar-produtos a {
  line-height: 2em;
  text-decoration: none;
  text-align: left !important;
}
</style>
