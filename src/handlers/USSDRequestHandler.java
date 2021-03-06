package handlers;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dao.DAO;
import dao.queries.JdbcMSISDNRedirectionDao;
import dao.queries.JdbcResourceBundleMessageSourceDao;
import dao.queries.JdbcUSSDServiceDao;
import domain.models.MSISDNRedirection;
import environment.Development;
import environment.Production;
import environment.USSDResponseView;
import product.ProductProperties;

@SuppressWarnings("unused")
@Controller
@RequestMapping(value={"/*"})
public class USSDRequestHandler {

	@Autowired
	private MessageSource i18n;

	@Autowired
	private JdbcResourceBundleMessageSourceDao databaseMessageSource;

	@Autowired
	private DAO dao;

	@Autowired
	private ProductProperties productProperties;

	// ----------------------- r�cup�rer le corps du POST
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
		Map<String, String> headers = new HttpUSSDRequest().GetHeaders(request);
		Map<String, String> parameters = new HttpUSSDRequest().GetParameters(request, false);

		// on cr�e le mod�le de la vue � afficher
		Map<String, Object> modele = new HashMap<String, Object>();

		try {
			if(parameters.containsKey("Environment") && parameters.get("Environment").equals("Production")) {
				parameters.remove("Environment"); // clear this http parameter
				reloadDatabaseMessages(headers); // update applications messages from database
				(new Production()).execute(i18n, productProperties, parameters, modele, dao, request, response);
			}
			else {
				MSISDNRedirection redirection = new JdbcMSISDNRedirectionDao(dao).getOneMSISDNRedirection(productProperties.getSc(), parameters.get("msisdn"), 0);

				if((redirection == null) || (redirection.getRedirection_url() == null)) {
					reloadDatabaseMessages(headers); // update applications messages from database
					(new Production()).execute(i18n, productProperties, parameters, modele, dao, request, response);
				}
				else {
					(new Development()).execute(redirection.getRedirection_url(), headers, parameters, modele, i18n);
				}
			}

		} catch(NullPointerException e) {
			modele.put("next", false);
			modele.put("message", i18n.getMessage("error", null, "Desole, veuillez reessayer plus tard...", null));

		} catch(Throwable e) {
			modele.put("next", false);
			modele.put("message", i18n.getMessage("error", null, "Desole, veuillez reessayer plus tard...", Locale.UK));
		}

		// on retourne le ModelAndView
		return new ModelAndView(new USSDResponseView(), modele);
	}

	// update applications messages from database
	public void reloadDatabaseMessages(Map<String, String> headers) {
		if(headers.containsKey("CacheRemoveAll") && (headers.get("CacheRemoveAll") != null) && ((String)headers.get("CacheRemoveAll")).equals("messageCache")) {
			databaseMessageSource.clearCache();
		}
	}

}
