<%@ page session="false" contentType="text/html;charset=utf-8"%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://myfaces.apache.org/trinidad" prefix="tr"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>


  <%@include file="include/head.inc"%>
  <f:view>
    <f:loadBundle basename="de.l3s.liwa.assessment.bundle.messages"
		  var="msg" />
    <body>
      <tr:document>
	<t:panelLayout id="page" layout="classic" styleClass="pageLayout"
		       headerClass="pageHeader" navigationClass="pageNavigation"
		       bodyClass="pageBody" footerClass="pageFooter">

	  <f:facet name="navigation">
	    <f:subview id="navigation">
              <p style="text-align:center;"> 
                <h:graphicImage id="header_logo" url="images/Logo_smal.jpg" />
              </p>
              <br />              
	      <b><h:outputText value="User: #{sessionHandler.userName}" /></b>
	      <h:form>
		<h:commandLink action="go_help" value="Help" />
	      </h:form>
	      <h:form>
		<h:commandLink action="go_privacy" value="Privacy" />
	      </h:form>
	      <hr />
	      <b><h:outputText value="Assessment tasks" /></b>
	      <h:form>
		<h:commandLink action="#{sessionHandler.nextHost}"
			       value="Next host" />
	      </h:form>
	      <h:form>
		<h:outputText value="Revision" />
	      </h:form>
	      <hr />
	      <h:form>
		<h:commandLink action="go_logout" value="Logout" />
	      </h:form>
	    </f:subview>
	  </f:facet>

	  <f:facet name="body">
	    <f:subview id="body">
	      
	      <h:outputText
		 value=" Revision Phase: " /> 
	      <h:form>
		<h:commandLink action="#{sessionHandler.userHosts }" value ="Labelled hosts (by me)"/>
	      </h:form>
	      <h:form>
		<h:commandLink action="#{sessionHandler.conflictHosts }" value ="Conflicting hosts"/>
	      </h:form>
	      <tr:form>
		<hr />
		
		<h:dataTable id="mydatatable2" binding="#{sessionHandler.conflictHostTable}" value="#{sessionHandler.conflictHostList}" var="host" 
                             border="1" cellpadding="5" cellspacing="3">
                  <h:column>
                    <f:facet name="header">
                      <h:outputText value="Host Address" />
                    </f:facet>
                    <h:commandLink value="#{host.address}" action="#{sessionHandler.setRevisionHost2}" />
                  </h:column>

                  <h:column>
                    <f:facet name="header">
                      <h:outputText value="My Labels" />
                    </f:facet>
                    <h:outputText value="#{host.labelsAsStringFromUser}" />
                  </h:column>

                  <h:column>
                    <f:facet name="header">
                      <h:outputText value="My Comments" />
                    </f:facet>
                    <h:outputText value="#{host.commentsAsStringFromUser}" />
                  </h:column>

                  <h:column>
                    <f:facet name="header">
                      <h:outputText value="Spam Labels" />
                    </f:facet>
                    <h:outputText value="#{host.spamNumber}" />
                  </h:column>

                  <h:column>
                    <f:facet name="header">
                      <h:outputText value="Normal Labels" />
                    </f:facet>
                    <h:outputText value="#{host.normalNumber}" />
                  </h:column>
                  
		</h:dataTable>	

              </tr:form>
	    </f:subview>
	  </f:facet>

	</t:panelLayout>

      </tr:document>
    </body>
  </f:view>
</html>
