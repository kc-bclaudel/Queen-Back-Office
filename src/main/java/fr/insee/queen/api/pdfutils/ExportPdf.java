package fr.insee.queen.api.pdfutils;

import fr.insee.queen.api.pdfutils.PDFDepositProofService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class ExportPdf extends HttpServlet {

    static Logger logger = LoggerFactory.getLogger(ExportPdf.class);

    private PDFDepositProofService depositProofService;

    private static String configPath = "/config/properties-local-prod.xml";
    private static String urlXpath = "/properties/property[@name='server-exist-orbeon']/@value";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        String pathDossier = servletContext.getInitParameter("oxf.resources.priority.0.oxf.resources.filesystem.sandbox-directory");
        String urlApi = "";

        try {
            urlApi = XMLUtil.execXpathOnFile(pathDossier.concat(configPath), urlXpath);
        } catch (Exception e) {
            logger.error("Souci lors de la récupération de l'url.");
            logger.error("Path du fichier utilisé : ".concat(configPath));
            logger.error("L'erreur est la suivante :", e);
        }
        logger.info("L'url de l'api exist est la suivante : ".concat(urlApi));
        depositProofService = new PDFDepositProofService(urlApi);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //HttpSession httpSession = ((HttpServletRequest) request).getSession();
        String idec = "AAAAA";//((String) httpSession.getAttribute("idec-pdf")).toUpperCase();
        String idUe = "BBBBB";//request.getParameter("survey-unit");
        String survey = "CCCCCCCC";//request.getParameter("survey");
        String model = "DDDDDDDDD";//request.getParameter("model");
        
        logger.info("////////////////");
        logger.info("idec {}", idec);
        logger.info("idUe {}", idUe);
        logger.info("survey {}", survey);
        logger.info("model {}", model);

        String filename = String.format("%s_%s.pdf", survey, idec);
//        response.setContentType("application/pdf");
//        response.setHeader("Content-disposition", "attachment; filename=\""+filename+"\"");

        try(OutputStream out = response.getOutputStream()){
        	logger.info("In try",model);
        	logger.info("deposit proof serv", depositProofService);
        	
        	//logger.info("L'url de l'api exist est la suivante : ".concat(urlApi));
            depositProofService = new PDFDepositProofService("http:/");
            File pdfFile = depositProofService.generatePdf(idUe,survey,model,idec);
            logger.info("after pdfFile",model);
            out.write(Files.readAllBytes(pdfFile.toPath()));
            pdfFile.delete();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
