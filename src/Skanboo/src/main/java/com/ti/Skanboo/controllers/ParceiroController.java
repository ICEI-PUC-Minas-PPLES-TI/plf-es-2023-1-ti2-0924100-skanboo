package com.ti.Skanboo.controllers;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;


import com.ti.Skanboo.models.Parceiro;
import com.ti.Skanboo.services.ParceiroService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/parceiro")
@CrossOrigin(origins = "*")
public class ParceiroController {

    @Autowired
    private ParceiroService parceiroService;

    @GetMapping("/{id}")
    public ResponseEntity<Parceiro> encontrarPorId(@PathVariable Long id) {

        Parceiro obj = this.parceiroService.encontrarPorId(id);

        return ResponseEntity.ok().body(obj);
    }

    @GetMapping("/lista")
    public ResponseEntity<List<Parceiro>> listarParceirosCadastrados() {

        List<Parceiro> obj = this.parceiroService.listarParceirosCadastrados();
        
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody Parceiro obj) {

        this.parceiroService.criar(obj);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }
    
    
    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap) {
        modelMap.addAttribute("file", file);
        return "fileUploadView";
    }

    
    // @PostMapping
    // public ResponseEntity<Void> criar(@RequestParam("contrato") MultipartFile contrato, @RequestParam("logo") MultipartFile logo, @Valid @RequestBody Parceiro obj) {
    //     this.parceiroService.criar(obj, contrato, logo);

    //     URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();

    //     return ResponseEntity.created(uri).build();
    // }




    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarPorId(@Valid @RequestBody Parceiro obj, @PathVariable Long id) throws Exception {

        obj.setId(id);
        obj = this.parceiroService.atualizarPorId(obj);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Long id) throws Exception {

        this.parceiroService.deletarPorId(id);

        return ResponseEntity.noContent().build();
    }
}
