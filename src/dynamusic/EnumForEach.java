package dynamusic;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import javax.servlet.ServletException;
import java.io.IOException;

public class EnumForEach extends DynamoServlet {
    @Override
    public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) throws ServletException, IOException {
        String repositoryName = req.getParameter("repositoryName");
        String itemDescriptorName = req.getParameter("itemDescriptorName");
        String propertyName = req.getParameter("propertyName");
        Repository repository = (Repository) resolveName(repositoryName);

        try {
            String[] enumValues = EnumeratedProperties.getEnumeratedProperties(repository,
                                                                                itemDescriptorName,
                                                                                propertyName);
            if(enumValues != null){
                for(int i = 0; i < enumValues.length; i++){
                    req.setParameter("element", enumValues[i]);
                    req.serviceParameter("output", req, res);
                }
            }else{
                req.serviceParameter("error", req, res);
            }
        }catch (RepositoryException e){
            if(isLoggingError()){
                logError(e);
            }
            req.serviceParameter("error", req, res);
        }
    }

    public EnumForEach() {
    }
}
