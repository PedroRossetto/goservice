package com.soulcode.goserviceapp.controller;

import com.soulcode.goserviceapp.domain.Agendamento;
import com.soulcode.goserviceapp.domain.Prestador;
import com.soulcode.goserviceapp.domain.Servico;
import com.soulcode.goserviceapp.repository.PrestadorRepository;
import com.soulcode.goserviceapp.service.AgendamentoService;
import com.soulcode.goserviceapp.service.PrestadorService;
import com.soulcode.goserviceapp.service.ServicoService;
import com.soulcode.goserviceapp.service.exceptions.ServicoNaoEncontradoException;
import com.soulcode.goserviceapp.service.exceptions.StatusAgendamentoImutavelException;
import com.soulcode.goserviceapp.service.exceptions.UsuarioNaoAutenticadoException;
import com.soulcode.goserviceapp.service.exceptions.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(value = "/prestador")
public class PrestadorController {

    @Autowired
    private PrestadorService prestadorService;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping(value = "/dados")
    public ModelAndView dados(Authentication authentication) {
        ModelAndView mv = new ModelAndView("dadosPrestador");
        try {
            Prestador prestador = prestadorService.findAuthenticated(authentication);
            mv.addObject("prestador", prestador);
            List<Servico> especialidades = servicoService.findByPrestadorEmail(authentication.getName());
            mv.addObject("especialidades", especialidades);
            List<Servico> servicos = servicoService.findAll();
            mv.addObject("serviços", servicos);
        }
        catch (UsuarioNaoEncontradoException | UsuarioNaoAutenticadoException ex) {
            mv.addObject("errorMessage", ex.getMessage());
        }
        catch (Exception ex) {
            mv.addObject("errorMessage", "Erro ao carregar dados do prestador");
        }
        return mv;
    }

    @PostMapping(value = "/dados")
    public String editarDados(Prestador prestador, RedirectAttributes attributes) {
        try {
            prestadorService.update(prestador);
            attributes.addFlashAttribute("siccessMessage", "Dados alterados");
        }
        catch (UsuarioNaoEncontradoException ex) {
            attributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        catch (Exception ex) {
            attributes.addFlashAttribute("errorMessage", "Erro ao alterar dados cadastrais.");
        }
        return "redirect:/prestador/dados";
    }

    @PostMapping(value = "/dados/especialidade/remover")
    public String removerEspecialidade(@RequestParam(name = "servicoId") Long id, RedirectAttributes attributes, Authentication authentication) {
        try {
            prestadorService.removeServicoPrestador(authentication, id);
            attributes.addFlashAttribute("successMessage", "Especialidade removida.");
        }
        catch (ServicoNaoEncontradoException | UsuarioNaoAutenticadoException ex) {
            attributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        catch (Exception ex) {
            attributes.addFlashAttribute("errorMessage", "Erro ao remover especialidade");
        }
        return "redirect:/prestador/dados";
    }

    @PostMapping(value = "/dados/especialidade/adicionar")
    public String adicionarEspecialidade(@RequestParam(name = "servicoId") Long id, Authentication authentication, RedirectAttributes attributes) {
        try {
            prestadorService.addServicoPrestador(authentication, id);
            attributes.addFlashAttribute("successMessage", "Especialidade adicionada.");
        }
        catch (ServicoNaoEncontradoException | UsuarioNaoAutenticadoException ex) {
            attributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        catch (Exception ex) {
            attributes.addFlashAttribute("errorMessage", "Erro ao adicionar nova especialidade");
        }
        return "redirect:/prestador/dados";
    }

    @GetMapping("/agenda")
    public ModelAndView agenda(Authentication authentication) {
        ModelAndView mv = new ModelAndView("agendaPrestador");
       try {
           List<Agendamento> agendamentos = agendamentoService.findByPrestador(authentication);
           mv.addObject("agendamentos", agendamentos);
       }
       catch (UsuarioNaoAutenticadoException | UsuarioNaoEncontradoException ex) {
           mv.addObject("errorMessage", ex.getMessage());
       }
       catch (Exception ex) {
           mv.addObject("errorMessage", "Erro ao carregar dados de agendamentos.");
       }
        return mv;
    }

    @PostMapping(value = "/agenda/cancelar")
    public String cancelarAgendamento(@RequestParam(name = "agendamentoId") Long agendamentoId, Authentication authentication,
                                      RedirectAttributes attributes) {
        try {
            agendamentoService.cancelAgendaPrestador(authentication, agendamentoId);
            attributes.addFlashAttribute("successMessage", "Agendamento cancelado.");
        }
        catch (UsuarioNaoAutenticadoException | UsuarioNaoEncontradoException | ServicoNaoEncontradoException |
               StatusAgendamentoImutavelException ex) {
            attributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        catch (Exception ex) {
            attributes.addFlashAttribute("errorMessage", "Erro ao cancelar agendamento.");
        }
        return "redirect:/prestador/agenda";
    }

    @PostMapping(value = "/agenda/confirmar")
    public String confirmarAgendamento(@RequestParam(name = "agendamentoId") Long agendamentoId, Authentication authentication,
                                      RedirectAttributes attributes) {
        try {
            agendamentoService.confirmAgenda(authentication, agendamentoId);
            attributes.addFlashAttribute("successMessage", "Agendamento confirmado.");
        }
        catch (UsuarioNaoAutenticadoException | UsuarioNaoEncontradoException | ServicoNaoEncontradoException |
               StatusAgendamentoImutavelException ex) {
            attributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        catch (Exception ex) {
            attributes.addFlashAttribute("errorMessage", "Erro ao confirmar agendamento.");
        }
        return "redirect:/prestador/agenda";
    }
}
