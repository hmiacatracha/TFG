package es.udc.citytrash.controller.cuenta;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.udc.citytrash.business.service.cuenta.UserService;
import es.udc.citytrash.business.util.excepciones.ExpiredTokenException;
import es.udc.citytrash.business.util.excepciones.InstanceNotFoundException;
import es.udc.citytrash.business.util.excepciones.TokenInvalidException;
import es.udc.citytrash.controller.util.WebUtils;
import es.udc.citytrash.controller.util.anotaciones.UsuarioActual;
import es.udc.citytrash.controller.util.dtos.CambiarPasswordFormDto;
import es.udc.citytrash.controller.util.dtos.IdiomaFormDto;
import es.udc.citytrash.controller.util.dtos.TrabajadoDto;

@Controller
@RequestMapping("cuenta")
public class CuentaController {

	@Autowired
	UserService cuentaServicio;

	final Logger logger = LoggerFactory.getLogger(CuentaController.class);

	/*
	 * Retrieve User Information in Spring Security
	 * http://www.baeldung.com/get-user-in-spring-security
	 * https://docs.spring.io/spring-security/site/docs/current/reference/html/
	 * mvc.html @AuthenticationPrincipal
	 * https://docs.spring.io/spring-security/site/docs/current/reference/html/
	 * mvc.html
	 * 
	 */

	/* ACTIVAR CUENTA */
	@PreAuthorize("isAnonymous()")
	@RequestMapping(value = { WebUtils.REQUEST_MAPPING_CUENTA_ACTIVAR,
			WebUtils.URL_CUENTA_RECUPERAR }, method = RequestMethod.GET)
	public String activarCuenta(@RequestParam(value = "id", required = true) long id,
			@RequestParam(value = "token", required = true) String token, Model model,
			RedirectAttributes redirectAttributes) {
		CambiarPasswordFormDto activarCuentaForm = new CambiarPasswordFormDto();
		model.addAttribute("activarCuentaForm", activarCuentaForm);
		model.addAttribute("token", token);

		try {
			cuentaServicio.loguearsePorIdToken(id, token);
		} catch (TokenInvalidException e) {
			redirectAttributes.addFlashAttribute("msg", "TokenInvalidoException");
			return "redirect:" + WebUtils.URL_LOGIN;
		} catch (ExpiredTokenException e) {
			redirectAttributes.addFlashAttribute("msg", "ExpiredTokenException");
			return "redirect:" + WebUtils.URL_LOGIN;
		}
		logger.info("TOKEN activar cuenta DESPUES2=> " + token);
		logger.info("TOKEN activar cuenta DESPUES2 url=> " + WebUtils.URL_CUENTA_CAMBIAR_CONTRASENA);
		return "redirect:" + WebUtils.URL_CUENTA_ACTUALIZAR_CONTRASENA;
	}

	@PreAuthorize("isAnonymous()")
	@RequestMapping(value = WebUtils.REQUEST_MAPPING_CUENTA_RESET_PASSWORD, method = RequestMethod.POST)
	public String recuperarCuenta(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
		String email = request.getParameter("email");
		logger.info("PASO1 URL:" + WebUtils.URL_CUENTA_RESET_PASSWORD);
		try {
			cuentaServicio.recuperarCuenta(email, WebUtils.getURLWithContextPath(request));
			redirectAttributes.addFlashAttribute("titulo", "title_recuperar_cuenta");
			redirectAttributes.addFlashAttribute("mensaje", "mensaje_recuperar_cuenta(" + email + ")");
			redirectAttributes.addFlashAttribute("tipoAlerta", "success");

		} catch (InstanceNotFoundException e) {
			model.addAttribute("err", e);
			return WebUtils.VISTA_RECUPERAR_CUENTA;
		}
		redirectAttributes.addFlashAttribute("success", "ok");
		redirectAttributes.addFlashAttribute("email", email);
		return "redirect:" + WebUtils.URL_CUENTA_RESET_PASSWORD;
	}

	/* RECUPERAR CUENTA */
	@PreAuthorize("isAnonymous()")
	@RequestMapping(value = { WebUtils.REQUEST_MAPPING_CUENTA_RESET_PASSWORD }, method = RequestMethod.GET)
	public String recuperarCuenta() {
		logger.info("PASO1 URL:" + WebUtils.URL_CUENTA_RESET_PASSWORD);
		return WebUtils.VISTA_RECUPERAR_CUENTA;
	}

	/* CAMBIAR CONTRASEÑA */
	@ModelAttribute("reiniciarPasswordForm")
	public CambiarPasswordFormDto cambiarPasswordForm() {
		return new CambiarPasswordFormDto();
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = WebUtils.REQUEST_MAPPING_CUENTA_ACTUALIZAR_CONTRASENA, method = RequestMethod.GET)
	public String cambiarPassword(Model model) {
		return WebUtils.VISTA_REINICIAR_CONTRASENA;
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = { WebUtils.REQUEST_MAPPING_CUENTA_ACTUALIZAR_CONTRASENA }, method = RequestMethod.POST)
	public String cambiarPassword(@UsuarioActual CustomUserDetails usuario, Model model,
			@ModelAttribute("reiniciarPasswordForm") @Valid CambiarPasswordFormDto form, BindingResult result,
			RedirectAttributes redir) {
		logger.info("POST ACTIVAR CUENTA1");

		if (result.hasErrors()) {
			logger.info("PAGINA vista => " + WebUtils.VISTA_REINICIAR_CONTRASENA);
			return WebUtils.VISTA_REINICIAR_CONTRASENA;
		}
		try {
			cuentaServicio.cambiarPassword(usuario.getPerfil().getEmail(), form.getPassword());
			redir.addFlashAttribute("login", "hola");
			logger.info("PAGINA REDIRECT => " + WebUtils.URL_HOME);
			return "redirect:" + WebUtils.URL_HOME;

		} catch (InstanceNotFoundException e) {
			logger.info("POST ACTIVAR CUENTA3");
			model = InstanceNotFoundException(model, e);
			logger.info("PAGINA VISTA => " + WebUtils.VISTA_REINICIAR_CONTRASENA);
			return WebUtils.VISTA_REINICIAR_CONTRASENA;
		}
	}

	/* CAMBIO DE IDIOMA */
	@ModelAttribute("idiomaForm")
	public IdiomaFormDto idiomaForm() {
		return new IdiomaFormDto();
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = { WebUtils.REQUEST_MAPPING_CUENTA_CAMBIO_IDIOMA }, method = RequestMethod.GET)
	public String cambiarIdioma(@UsuarioActual TrabajadoDto perfil, Model model) {
		return WebUtils.VISTA_CAMBIAR_IDIOMA;
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = { WebUtils.REQUEST_MAPPING_CUENTA_CAMBIO_IDIOMA }, method = RequestMethod.POST)
	public String cambiarIdioma(@UsuarioActual CustomUserDetails usuario,
			@ModelAttribute("idiomaForm") @Valid IdiomaFormDto idiomaForm, BindingResult result,
			HttpServletRequest request, Errors errors, Model model, HttpServletResponse response) {

		if (result.hasErrors()) {
			model.addAttribute("error", true);
			return WebUtils.VISTA_CAMBIAR_IDIOMA;
		}

		try {
			cuentaServicio.cambiarIdioma(usuario.getUsername(), idiomaForm.getIdioma());

		} catch (InstanceNotFoundException e) {
			model = InstanceNotFoundException(model, e);
			return WebUtils.VISTA_CAMBIAR_IDIOMA;
		}
		usuario.getPerfil().setIdioma(idiomaForm.getIdioma());
		RequestContextUtils.getLocaleResolver(request).setLocale(request, response,
				new Locale(idiomaForm.getIdioma().toString().toLowerCase()));
		model.addAttribute("success", true);
		logger.info("CAMBIO DE IDIOMA SUCCESS");
		return WebUtils.VISTA_CAMBIAR_IDIOMA;
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(InstanceNotFoundException.class)
	public Model InstanceNotFoundException(Model model, InstanceNotFoundException ex) {
		logger.info("ExceptionHandler InstanceNotFoundException");
		model.addAttribute("error", ex);
		model.addAttribute("type", "InstanceNotFoundException");
		model.addAttribute("key", ex.getKey());
		return model;
	}
}