package com.ti.Skanboo.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ti.Skanboo.exceptions.AuthorizationException;
import com.ti.Skanboo.exceptions.EntityNotFoundException;
import com.ti.Skanboo.exceptions.OfferCreationException;
import com.ti.Skanboo.exceptions.OfferUpdateException;
import com.ti.Skanboo.exceptions.DuplicateOfferCreationException;
import com.ti.Skanboo.models.Oferta;
import com.ti.Skanboo.models.Postagem;
import com.ti.Skanboo.models.enums.OfertaEnum;
import com.ti.Skanboo.repositories.OfertaRepository;
import com.ti.Skanboo.security.UserSpringSecurity;

import jakarta.transaction.Transactional;

@Service
public class OfertaService {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private PostagemService postagemService;

    public Oferta encontrarPorId(Long id) {

        Oferta oferta = this.ofertaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Oferta nao encontrada!"));

        UserSpringSecurity userSpringSecurity = UsuarioService.authenticated();

        if (Objects.isNull(userSpringSecurity) || !postagemOrigemPertenceAoUsuario(userSpringSecurity, oferta)
                && !postagemOfertadaPertenceAoUsuario(userSpringSecurity, oferta))
            throw new AuthorizationException("Acesso negado!");

        return oferta;
    }

    public List<List<Oferta>> listarOfertasFeitasUsuarioAtivo() {

        List<Postagem> postagens = this.postagemService.listarPostagensUsuarioAtivo();
        List<List<Oferta>> ofertasFeitas = new ArrayList<List<Oferta>>();

        for (Postagem postagem : postagens) {
            List<Oferta> ofertas = this.ofertaRepository.findByPostagemOfertada_Id(postagem.getId());

            if (!ofertas.isEmpty())
                ofertasFeitas.add(ofertas);
        }

        if (ofertasFeitas.isEmpty())
            throw new RuntimeException("Usuario nao fez nenhuma oferta!");

        return ofertasFeitas;
    }

    public List<List<Oferta>> listarOfertasRecebidasUsuarioAtivo() {

        List<Postagem> postagens = this.postagemService.listarPostagensUsuarioAtivo();
        List<List<Oferta>> ofertasRecebidas = new ArrayList<List<Oferta>>();

        for (Postagem postagem : postagens) {
            List<Oferta> ofertas = this.ofertaRepository.findBypostagemOrigem_Id(postagem.getId());

            if (!ofertas.isEmpty())
                ofertasRecebidas.add(ofertas);
        }

        if (ofertasRecebidas.isEmpty())
            throw new RuntimeException("Usuario nao recebeu nenhuma oferta!");

        return ofertasRecebidas;
    }

    public List<Oferta> listarOfertasPostagem(Long id) {

        Postagem postagem = this.postagemService.encontrarPorId(id);
        List<Oferta> ofertas = new ArrayList<Oferta>();

        ofertas.addAll(this.ofertaRepository.findBypostagemOrigem_Id(postagem.getId()));
        ofertas.addAll(this.ofertaRepository.findByPostagemOfertada_Id(postagem.getId()));

        return ofertas;
    }

    @Transactional
    public Oferta criar(Oferta obj) {

        UserSpringSecurity userSpringSecurity = UsuarioService.authenticated();

        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

        this.ofertaDisponivel(obj);

        if (!postagemOrigemPertenceAoUsuario(userSpringSecurity, obj)
                && postagemOfertadaPertenceAoUsuario(userSpringSecurity, obj)) {
            obj.setId(null);
            obj.setData(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()));
            obj.setHora(LocalTime.now());
        } else
            throw new OfferCreationException("Nao e possivel fazer essa oferta!");

        return this.ofertaRepository.save(obj);
    }

    @Transactional
    public Oferta atualizarPorId(Oferta obj) {

        UserSpringSecurity userSpringSecurity = UsuarioService.authenticated();
        Oferta novaOferta = encontrarPorId(obj.getId());

        if (novaOferta.getStatus().equals(OfertaEnum.RECUSADA) || novaOferta.getStatus().equals(OfertaEnum.ACEITA))
            throw new OfferUpdateException("A oferta ja foi recusada ou aceita, seu status nao pode ser atualizado!");

        Boolean postagemOrigemPertenceAoUsuario = this.postagemOrigemPertenceAoUsuario(userSpringSecurity, novaOferta);

        if (obj.getStatus().equals(OfertaEnum.ACEITA)) {
            if (postagemOrigemPertenceAoUsuario)
                aceitarOferta(novaOferta);
            else
                throw new OfferUpdateException("O usuario nao pode atualizar essa oferta!");
        } else if (obj.getStatus().equals(OfertaEnum.RECUSADA))
            novaOferta.setStatus(OfertaEnum.RECUSADA);

        return this.ofertaRepository.save(novaOferta);
    }

    public void deletarPorId(Long id) {

        Oferta oferta = encontrarPorId(id);

        if (!oferta.getStatus().equals(OfertaEnum.RECUSADA))
            throw new RuntimeException("A oferta precisa ser recusada antes de ser deletada!");

        try {
            this.ofertaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Nao e possivel excluir a oferta pois ela possui entidades relacionadas!");
        }
    }

    private void aceitarOferta(Oferta obj) {

        obj.setStatus(OfertaEnum.ACEITA);

        List<Oferta> ofertasRelacionadas = this.listarOfertasRelacionadas(obj);

        for (Oferta oferta : ofertasRelacionadas)
            if (oferta.getId() != obj.getId())
                oferta.setStatus(OfertaEnum.RECUSADA);

    }

    private void ofertaDisponivel(Oferta obj) {

        OfertaEnum[] statusNaoPermitidos = { OfertaEnum.EM_ANDAMENTO, OfertaEnum.ACEITA };
        List<Oferta> ofertasRelacionadas = this.listarOfertasRelacionadas(obj);

        // Verifica se existe oferta com essas postagens e status EM_ANDAMENTO ou ACEITA
        for (OfertaEnum status : statusNaoPermitidos)
            if (ofertaJaCadastrada(obj.getPostagemOrigem(), obj.getPostagemOfertada(), status)
                    || ofertaJaCadastrada(obj.getPostagemOfertada(), obj.getPostagemOrigem(), status))
                throw new DuplicateOfferCreationException("Esta oferta ja existe ou ja foi concluida!");

        // Verifica se a postagem ofertada ou origem ja foi aceita em outra oferta
        if (ofertasRelacionadas.stream().anyMatch(oferta -> oferta.getStatus().equals(OfertaEnum.ACEITA)))
            throw new OfferCreationException("A postagem ofertada ou origem ja foi aceita em outra oferta!");
    }

    private Boolean postagemOrigemPertenceAoUsuario(UserSpringSecurity userSpringSecurity, Oferta oferta) {
        return oferta.getPostagemOrigem().getUsuario().getId().equals(userSpringSecurity.getId());
    }

    private Boolean postagemOfertadaPertenceAoUsuario(UserSpringSecurity userSpringSecurity, Oferta oferta) {
        return oferta.getPostagemOfertada().getUsuario().getId().equals(userSpringSecurity.getId());
    }

    private Boolean ofertaJaCadastrada(Postagem postagemOrigem, Postagem postagemOfertada, OfertaEnum status) {
        return ofertaRepository.existsByPostagemOrigemAndPostagemOfertadaAndStatus(postagemOrigem, postagemOfertada,
                status);
    }

    private List<Oferta> listarOfertasRelacionadas(Oferta obj) {

        Long postagemOrigemId = obj.getPostagemOrigem().getId();
        Long postagemOfertadaId = obj.getPostagemOfertada().getId();
        List<Oferta> ofertasRelacionadas = new ArrayList<Oferta>();

        ofertasRelacionadas.addAll(this.listarOfertasPostagem(postagemOrigemId));
        ofertasRelacionadas.addAll(this.listarOfertasPostagem(postagemOfertadaId));

        return ofertasRelacionadas;
    }
}
