<template>
  <section class="container">
    <h1>Informações da conta</h1>
    <div class="usuario">
      <span class="nota-usuario">{{ usuario.notaFinal }}</span>
      <div class="foto-usuario">
        <img :src="usuario.foto" alt="" />
      </div>

      <div class="informacoes">
        <div class="dados-usuario">
          <p class="nomeUsuario">Nome</p>
          <p class="nascimentoUsuario">Nascimento</p>
          <p class="emailUsuario">Email</p>
          <p class="telefoneUsuario">Telefone</p>
          <p class="senhaUsuario">Senha</p>
          <p class="CPFUsuario">CPF</p>
          <p class="cep">Endereço</p>
        </div>

        <div class="info-usuario">
          <p class="nome">{{ usuario.nome }}</p>
          <p class="nascimento">
            {{
              usuario.dataNascimento
                ? `${usuario.dataNascimento[2]}/${usuario.dataNascimento[1]}/${usuario.dataNascimento[0]}`
                : ''
            }}
          </p>
          <p class="email">{{ usuario.email }}</p>
          <p class="telefone">{{ usuario.telefone }}</p>
          <p class="senha">************</p>
          <p class="CPF">{{ usuario.cpf }}</p>
          <p class="cepRuaNumero">{{ endereco.cidade }}, Rua {{ endereco.rua }}, {{ endereco.numero }}</p>
        </div>
      </div>
      <button class="editar-endereco">
        <a href="/editarEnderecoView">Editar endereço</a>
      </button>

      <button class="editar"><a href="/editarUsuarioView">Editar</a></button>
      <button class="sair" @click="logout">Sair</button>
    </div>
  </section>
</template>

<script>
import Usuario from '../services/UsuarioService';
import Endereco from '../services/EnderecoService';

export default {
  data() {
    return {
      usuario: {
        nome: '',
        nascimento: '',
        email: '',
        telefone: '',
        senha: '',
        cpf: '',
        foto: null,
        notaFinal: null,
      },
      endereco: {
        uf: '',
        cep: '',
        cidade: '',
        bairro: '',
        rua: '',
        numero: null,
        complemento: '',
      },
      loaded: false,
    };
  },

  mounted() {
    if (!window.location.hash) {
      window.location = window.location + '#loaded';
      window.location.reload();
    }
    this.carregarInformacoesUsuario();
  },

  methods: {
    carregarInformacoesUsuario() {
      Usuario.exibirInfo()
        .then((resposta) => {
          this.usuario = resposta.data;
          this.verificarNotaUsuario(this.usuario.notaFinal);
        })
        .catch((error) => {
          console.error(error);
        });

      Endereco.exibirInfo()
        .then((resposta) => {
          this.endereco = resposta.data[0];
        })
        .catch((error) => {
          console.error(error);
        });
    },

    logout() {
      localStorage.removeItem('token');
      sessionStorage.removeItem('perfil');
      this.$router.push({ name: 'loginView' });
    },

    verificarNotaUsuario(nota) {
      if (nota >= 4) document.querySelector('.nota-usuario').classList.add('nota-alta');
      else if (nota >= 2 && nota < 4) document.querySelector('.nota-usuario').classList.add('nota-media');
      else document.querySelector('.nota-usuario').classList.add('nota-baixa');
    },
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
  position: relative;
  border: 1px solid #eae9e9;
  border-radius: 4px;
  padding: 20px;
}

.informacoes {
  display: flex;
  justify-content: flex-start;
}

.informacoes-endereco {
  display: flex;
  justify-content: center;
  border: 2px solid #c0c2c7;
  border-radius: 10px;
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

.nota-usuario {
  position: absolute;
  top: 18%;
  left: 58%;
  transform: translate(-50%, -50%);
  background: white;
  font-weight: bold;
  width: 30px;
  height: 30px;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nota-alta {
  border: 2px solid rgb(91, 160, 77);
  color: rgb(91, 160, 77);
}

.nota-media {
  border: 2px solid #f9dc5c;
  color: #bea638;
}

.nota-baixa {
  border: 2px solid #f26a63;
  color: #f26a63;
}

img {
  border-radius: 50% 50% 50% 50%;
  width: 120px;
  height: 120px;
}

button {
  box-sizing: border-box;
  padding: 2px 6px 2px 8px;
  gap: 4px;
  width: 300px;
  height: 32px;
  background: #f9dc5c;
  border: 1px solid #f9dc5c;
  border-radius: 32px;
  font-weight: 600;
  transition: 0.3s;
  margin-left: 30px;
  margin-top: 30px;
  transition: 0.3s;
}

button:hover {
  background: #ffe574;
}

.editar-endereco {
  box-sizing: border-box;
  padding: 2px 6px 2px 8px;
  gap: 4px;
  width: 150px;
  height: 28px;
  background: #fcfcfc;
  border: 1px solid #e2e2e2;
  border-radius: 16px;
  font-weight: 400;
  color: #515864 !important;
  transition: 0.3s;
  margin-left: 30px;
  margin-top: 0px;
  transition: 0.3s;
}

.editar-endereco:hover {
  background: white;
}

p {
  font-size: 1rem;
  text-align: left !important;
  padding: 10px;
}

.informacoes-endereco p {
  font-size: 1rem;
  text-align: left !important;
  padding: 0px;
}

.dados-usuario {
  margin-left: 30px;
}

.info-usuario p {
  color: #c0c2c7;
  margin-left: 50px;
}

.info-endereco {
  color: #c0c2c7;
  margin-left: 50px;
}

h1,
h2 {
  padding: 10px;
  padding-left: 0;
}

.sair {
  cursor: pointer;
  box-sizing: border-box;
  padding: 2px 6px 2px 8px;
  gap: 4px;
  width: 300px;
  height: 32px;
  background: #fcfcfc;
  border: 1px solid #e2e2e2;
  border-radius: 32px;
  font-weight: 600;
  color: #515864;
  transition: 0.3s;
  margin-left: 30px;
  margin-top: 0px;
  transition: 0.3s;
}

.sair:hover {
  background: white;
}

.gerenciar-produtos a {
  line-height: 2em;
  text-decoration: none;
  text-align: left !important;
}
</style>
