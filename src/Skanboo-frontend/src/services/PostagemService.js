import { http } from './config';

export default {
  criar: (postagem) => {
    return http.post('postagem', postagem, {
      headers: {
        Authorization: localStorage.getItem('token')
      }
    });
  },

  exibirInfo: () => {
    return http.get('postagem/usuario', {
      headers: {
        Authorization: localStorage.getItem('token')
      }
    });
  },

  atualizar: (postId, postagem) => {
    return http.put(`postagem/${postId}`, postagem, {
      headers: {
        Authorization: localStorage.getItem('token')
      }
    });
  },
};
