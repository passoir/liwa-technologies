<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:facet name="navigation">
	<f:view>
		<h:form>
			<h:commandLink id="n2" value="Privacy" action="go_privacy"></h:commandLink>
			<h:commandLink id="n3" value="Help" action="go_help"></h:commandLink>

		</h:form>
	</f:view>
</f:facet>
