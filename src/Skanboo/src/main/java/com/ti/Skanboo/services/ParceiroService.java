package com.ti.Skanboo.services;

import java.util.List;
import java.util.Objects;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ti.Skanboo.exceptions.AuthorizationException;
import com.ti.Skanboo.models.Parceiro;
import com.ti.Skanboo.models.enums.UsuarioEnum;
import com.ti.Skanboo.repositories.ParceiroRepository;
import com.ti.Skanboo.security.UserSpringSecurity;
import com.ti.Skanboo.utils.InputStreamUtils;
import org.springframework.util.StringUtils;
import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;

@Service
public class ParceiroService {

    @Autowired
    private ParceiroRepository parceiroRepository;

    public Parceiro encontrarPorId(Long id) {

        Parceiro parceiro = this.parceiroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parceiro nao encontrado!"));

        UserSpringSecurity userSpringSecurity = UsuarioService.authenticated();

        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(UsuarioEnum.ADMIN))
            throw new AuthorizationException("Acesso negado!");

        return parceiro;
    }

    public List<Parceiro> listarParceirosCadastrados() {

        UserSpringSecurity userSpringSecurity = UsuarioService.authenticated();

        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(UsuarioEnum.ADMIN))
            throw new AuthorizationException("Acesso negado!");

        List<Parceiro> parceiro = this.parceiroRepository.findAll();

        return parceiro;
    }

    // @Transactional
    // public void salvarContrato(Long parceiroId, MultipartFile arquivo) throws IOException {
    //     Parceiro parceiro = encontrarPorId(parceiroId);
    //     byte[] contratoBytes = InputStreamUtils.lerBytesDoInputStream(arquivo.getInputStream());
    //     parceiro.setContrato(contratoBytes);
    //     parceiroRepository.save(parceiro);
    // }


    @Transactional
    public void salvarContrato(Long parceiroId, MultipartFile arquivo) throws Exception {
        Parceiro parceiro = encontrarPorId(parceiroId);
        try {
            byte[] contratoBytes = InputStreamUtils.lerBytesDoInputStream(arquivo.getInputStream());
            parceiro.setContrato(contratoBytes);
            parceiroRepository.save(parceiro);
        } catch (Exception e) {
            throw new Exception("Erro ao salvar o contrato: " + e.getMessage());
        }
    }

    @Transactional
    public void salvarLogo(Long parceiroId, MultipartFile arquivo) throws Exception {
        Parceiro parceiro = encontrarPorId(parceiroId);
        try {
            byte[] logoBytes = InputStreamUtils.lerBytesDoInputStream(arquivo.getInputStream());
            parceiro.setLogo(logoBytes);
            parceiroRepository.save(parceiro);
        } catch (Exception e) {
            throw new Exception("Erro ao salvar o logo: " + e.getMessage());
        }
    }


    // @Transactional
    // public Parceiro criar(Parceiro obj) {

    //     UserSpringSecurity userSpringSecurity = UsuarioService.authenticated();

    //     if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(UsuarioEnum.ADMIN))
    //         throw new AuthorizationException("Acesso negado!");

    //     obj.setId(null);

    //     return this.parceiroRepository.save(obj);
    // }

    @Transactional
    public Parceiro criar(Parceiro obj, MultipartFile contrato, MultipartFile logo) {
        UserSpringSecurity userSpringSecurity = UsuarioService.authenticated();

        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(UsuarioEnum.ADMIN))
            throw new AuthorizationException("Acesso negado!");

        obj.setId(null);

        Parceiro parceiro = parceiroRepository.save(obj);
        Long parceiroId = parceiro.getId();

        try {
            salvarContrato(parceiroId, contrato);
            salvarLogo(parceiroId, logo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar os arquivos: " + e.getMessage());
        }

        return parceiro;
    }


    @Transactional
    public Parceiro atualizarPorId(Parceiro obj) {

        Parceiro parceiro = encontrarPorId(obj.getId());

        parceiro.setNome(obj.getNome());
        parceiro.setCnpj(obj.getCnpj());
        parceiro.setPlano(obj.getPlano());
        // todo: adicionar contrato e logo
        parceiro.setContrato(obj.getContrato());
        parceiro.setLogo(obj.getLogo());
        
        return this.parceiroRepository.save(parceiro);
    }

    public void deletarPorId(Long id) {

        encontrarPorId(id);

        try {
            this.parceiroRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Nao e possivel excluir parceiro pois ele possui entidades relacionadas!");
        }
    }
}
