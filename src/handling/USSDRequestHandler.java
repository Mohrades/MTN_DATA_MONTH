package handling;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dao.DAO;
import env.WebAppProperties;

@SuppressWarnings("unused")
@Controller
@RequestMapping(value={"/*"})
public class USSDRequestHandler {

	@Autowired
	private DAO dao;
	
	@Autowired
	private WebAppProperties webAppProperties;

	// ----------------------- récupérer le corps du POST
	/*@RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded; charset=UTF-8")*/
	/*public ModelAndView handleSubmitSmRequest(@RequestParam(value="source", required=true) String source, @RequestParam(value="callbackUrl", required=true) String callbackUrl, @RequestParam(value="destination", required=true) String destination, @RequestParam(value="messageText", required=true) String messageText, HttpServletResponse response) throws Exception {*/
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return handleUSSDRequest(request, response);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return handleUSSDRequest(request, response);
	}

	public ModelAndView handleUSSDRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> headers = new USSDRequest().GetHeaders(request);
		Map<String, String> parameters = new USSDRequest().GetParameters(request, false);

		// on crée le modèle de la vue à afficher
		Map<String, Object> modele = new HashMap<String, Object>();

		try {
			if(parameters.isEmpty()) {
				modele.put("next", false);
				modele.put("message", "SHORT CODE USSD live...");
			}
			else {
				new InputHandler().handle(webAppProperties, parameters, modele, request, dao);
			}

		} catch(NullPointerException e) {
			modele.put("next", false);
			modele.put("message", "Desole, veuillez reessayer plus tard...");

		} catch(Throwable e) {
			modele.put("next", false);
			modele.put("message", "Desole, veuillez reessayer plus tard...");
		}

		// on retourne le ModelAndView
		return new ModelAndView(new CallbackDataAndView(), modele);
	}
}
