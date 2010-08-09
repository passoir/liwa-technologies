<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Login page</title>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

</head>
<body>
<f:view>

	<f:loadBundle basename="de.l3s.liwa.assessment.bundle.messages"
		var="msg" />
	<h:outputText value="#{msg.login }" />
	<h:form id="form" style="margin: 100px;">
		<h:panelGrid columns="3" styleClass="borderTable"
			headerClass="panelHeading">
			<h:outputLabel for="name" value="#{msg.name}" />

			<h:inputText id="name" required="true"
				value="#{sessionHandler.loginName}" />
			<h:message for="name" />
			<h:outputLabel for="passwd" value="#{msg.password}" />
			<h:inputSecret id="passwd" required="true"
				value="#{sessionHandler.loginPassword}" />
			<h:message for="passwd" />
			<h:commandButton type="submit" action="#{sessionHandler.login}"
				value="#{msg.login}" />


		</h:panelGrid>

	</h:form>


</f:view>

</body>

</html>
