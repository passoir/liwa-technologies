<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>login page</title>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

</head>
<body>
<center><f:view>
	<t:dataTable id="data" styleClass="standardTable"
		headerClass="standardTable_Header"
		rowClasses="standardTable_Row1,standardTable_Row2"
		columnClasses="standardTable_Column,standardTable_ColumnCentered,standardTable_Column"
		var="country" preserveDataModel="true">
		<h:column>
			<h:panelGrid columns="3">
				<t:popup id="a" styleClass="popup" closePopupOnExitingElement="true"
					closePopupOnExitingPopup="true" displayAtDistanceX="10"
					displayAtDistanceY="10">

					<f:facet name="popup">
						<h:panelGroup>
							<h:panelGrid columns="1">

							</h:panelGrid>
						</h:panelGroup>
					</f:facet>
				</t:popup>

				<t:popup id="b" styleClass="popup" closePopupOnExitingElement="true"
					closePopupOnExitingPopup="true" displayAtDistanceX="10"
					displayAtDistanceY="10">
					<f:facet name="popup">
						<h:panelGroup>
							<h:panelGrid columns="1">
								<h:outputText value="Sample Popup Text" />
							</h:panelGrid>
						</h:panelGroup>
					</f:facet>
				</t:popup>

				<t:popup id="c" styleClass="popup" closePopupOnExitingElement="true"
					closePopupOnExitingPopup="true" displayAtDistanceX="10"
					displayAtDistanceY="10">
					<h:outputText id="oc" value="Country ID" />
					<f:facet name="popup">
						<h:panelGroup>
							<h:panelGrid columns="1">

							</h:panelGrid>
						</h:panelGroup>
					</f:facet>
				</t:popup>
			</h:panelGrid>
		</h:column>
	</t:dataTable>



</f:view></center>
</body>
</html>