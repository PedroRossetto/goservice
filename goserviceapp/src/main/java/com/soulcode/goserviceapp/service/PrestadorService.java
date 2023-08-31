package com.soulcode.goserviceapp.service;

import com.soulcode.goserviceapp.domain.Prestador;
import com.soulcode.goserviceapp.domain.Servico;
import com.soulcode.goserviceapp.repository.PrestadorRepository;
import com.soulcode.goserviceapp.service.exceptions.UsuarioNaoAutenticadoException;
import com.soulcode.goserviceapp.service.exceptions.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrestadorService {
    @Autowired
    private PrestadorRepository prestadorRepository;

    @Autowired
    private ServicoService servicoService;



    @Cacheable(cacheNames = "redisCache")
    public List<Prestador> findAll(){
        System.err.println("BUSCANDO NO BANCO DE DADOS...");
        return prestadorRepository.findAll();
    }

    public Prestador findById(Long id){
        Optional<Prestador> prestador = prestadorRepository.findById(id);
        if (prestador.isPresent()){
            return prestador.get();
        }else {
            throw new RuntimeException("Prestador não encontrado!");
        }
    }

    public Prestador update(Prestador prestador){
        Prestador updatePrestador = this.findById(prestador.getId());
        updatePrestador.setNome(prestador.getNome());
        updatePrestador.setEmail(prestador.getEmail());
        updatePrestador.setDescricao(prestador.getDescricao());
        updatePrestador.setTaxaPorHora(prestador.getTaxaPorHora());
        return prestadorRepository.save(updatePrestador);
    }

    public Prestador findAuthenticated(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()){
            Optional<Prestador> prestador = prestadorRepository.findByEmail(authentication.getName());
            if (prestador.isPresent()){
                return prestador.get();
            }
            else{
                throw new UsuarioNaoEncontradoException();
            }
        }
        else{
            throw new UsuarioNaoAutenticadoException();
        }
    }

    public void addServicoPrestador(Authentication authentication, Long id){
        Prestador prestador = findAuthenticated(authentication);
        Servico servico = servicoService.findById(id);
        prestador.addEspecialidade(servico);
        prestadorRepository.save(prestador);
    }

    public void removeServicoPrestador(Authentication authentication, Long id){
        Prestador prestador = findAuthenticated(authentication);
        Servico servico = servicoService.findById(id);
        prestador.removeEspecialidade(servico);
        prestadorRepository.save(prestador);
    }

    public List<Prestador> findByServicoId(Long id){
        return prestadorRepository.findByServicoId(id);
    }
}
