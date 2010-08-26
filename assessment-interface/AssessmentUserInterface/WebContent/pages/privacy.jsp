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
	      <b><h:outputText value="User: #{sessionHandler.userName}" /> </b>
	      <h:form>

		<h:commandLink action="go_help" value="Help" />
	      </h:form>
	      <h:form>

		<h:outputText value="Privacy" />
	      </h:form>
	      <h:form>
		<h:commandLink action="go_logout" value="Logout" />
	      </h:form>
	      <hr>
		<b><h:outputText value="Assessment tasks" /> </b>
		<h:form>
		  <h:commandLink action="go_assessment" value="Next host" />
		</h:form>
		<hr>

	    </f:subview>
	  </f:facet>
	  <f:facet name="body">
	    <f:subview id="body">
	      <h:outputText value="Privacy" />
	    </f:subview>
	  </f:facet>
	</t:panelLayout>

      </tr:document>
    </body>
  </f:view>
</html>
