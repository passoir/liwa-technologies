<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://myfaces.apache.org/trinidad" prefix="tr"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

  <%@include file="include/head.inc"%>
  <f:view>

    <f:loadBundle basename="de.l3s.liwa.assessment.bundle.messages"
                  var="msg" />
    <body>
      <%@ include file="formhandler.jsp" %>

      <div class="myHidden">
        <h:form id="myInputForm">
          <h:inputHidden id="myTextField" required="true"
                         value="#{sessionHandler.allLabels}" />
          <h:commandButton value="Press here" action="#{sessionHandler.actionNext}"
                           id="myCommandButton" style="visibility:hidden;"/>
        </h:form>

        <h:form id="myInputFormBack">
          <h:inputHidden id="myTextFieldBack" required="true"
                         value="#{sessionHandler.allLabels}" />
          <h:commandButton value="Press here Back" action="#{sessionHandler.actionBack}"
                           id="myCommandButtonBack" style="visibility:hidden;" />
        </h:form>

        <h:form id="langForm">
          <h:inputHidden id="usersLanguage" value="#{sessionHandler.usersLanguage}" />
        </h:form>

        <h:form id="saveSuccessForm">
          <h:inputHidden id="saveSuccessField" value="#{sessionHandler.saveSuccess}" />
        </h:form>

        <h:form id="currentHost">
          <h:inputHidden id="hostUrl" value="#{sessionHandler.currentHostAddress } " />
        </h:form>

        <a4j:form id="nowViewingForm2" ajaxSubmit="true">
          <%-- !! Dont forget to check and change the generated reRender ID-s if necessary! To check see the rendered html page with FF-'s firebug and set here the appropriate IDs. --%>

          <a4j:jsFunction  name="generatedRerenderFunction"
                           reRender="navigation:j_id_jsp_202909396_44:pagestable,navigation:j_id_jsp_202909396_55:inlinkstable,navigation:j_id_jsp_202909396_61:outlinkstable, navigation:viewHostF:viewHostL" >
	    <a4j:actionparam name="param1" assignTo="#{sessionHandler.viewedHostName}"  />
	  </a4j:jsFunction>
        </a4j:form>

      </div>

      <tr:document>
        <t:panelLayout id="page" layout="classic" styleClass="page.css"
                       headerClass="pageHeader" navigationClass="pageNavigation"
                       bodyClass="pageBody" footerClass="pageFooter">

          <f:facet name="navigation">
            <f:subview id="navigation">

              <h:form>
                <h:outputText value="  Now assessing: " />
                <h:commandLink value="#{sessionHandler.currentHostAddress }" />
                <br />
                <h:outputText value="  Live page: " />
                <h:commandLink id="outlink" value="#{sessionHandler.currentHostAddress }" onclick="loadLivePage(event);" />
              </h:form>
              <hr />

              <div class="smallText">
                <h:dataTable id="labelsdatatable"
                             value="#{sessionHandler.labelSetGroups}" var="labelsetgroup"
                             border="1" cellpadding="2" cellspacing="0">
                  <h:column>
                    <f:facet name="header">
                      <h:outputText value="Labels" />
                    </f:facet>

                    <h:dataTable value="#{labelsetgroup.labelSets}" var="labelset"
                                 border="1" cellpadding="2" cellspacing="0">
                      <h:column>
                        <%-- <f:facet name="header">
                             <h:outputText value="Question" />
                        </f:facet>
                        --%>
                        <h:outputText value="#{labelset.name}" title="#{labelset.tooltip}" />
                      </h:column>
                      <h:column >
                        <h:form id="labelsForm" title="#{labelset.tooltip}">
                          <h:selectOneMenu title="#{labelset.tooltip}" onchange="setMenusDisabled();">
                            <f:selectItems value="#{labelset.labels}"/>
                          </h:selectOneMenu>
                        </h:form>
                      </h:column>
                    </h:dataTable>
                  </h:column>
                </h:dataTable>
              </div>

              <h:form id="buttonsForm">

                <h:outputLabel for="Comment" value="Comment:" />
                <br />
                <h:inputText id="comment" required="false" size="30"
                             value="#{sessionHandler.hostComment}" />
                <br />

                <h:commandButton value="Next"
                                 onclick="return collectAndSaveLabels();" />
                <h:commandButton value="Back"
                                 onclick="return collectAndSaveLabelsBack();" />
		<h:commandButton value="Help" action="go_help" />

	      </h:form>

              <%-- javascript that disables/enables menus --%>
              <script>
                computeRules();
                setMenusDisabled();
              </script>

              <br />
              <hr />
              <h:outputText value="  Now viewing: " />
              <a4j:form id="viewHostF" ajaxSubmit="true">
                <h:commandLink  id="viewHostL" value="#{sessionHandler.viewedHostName } " >
                </h:commandLink>
              </a4j:form>
              <br />

	      <t:panelTabbedPane id="tabbedPane" width="100%" bgcolor="#cddcf6" align="center"
				 serverSideTabSwitch="false" selectedIndex="0">
                <t:panelTab label="Pages">
                  <tr:form>
                    <h:dataTable id="pagestable" binding="#{sessionHandler.samplePagesTable}"
                                 value="#{sessionHandler.viewedHost.samplePages}" var="page"
                                 border="0" cellpadding="2" cellspacing="2">
                      <h:column>
                        <f:facet name="header" >
                          <h:outputText value="Sample pages" />
                        </f:facet>
                        <h:commandLink value="#{page.dashedUrl}"  title="#{page.realUrl}" onclick="viewPage(event); return false;" >
                        </h:commandLink>
                      </h:column>
                    </h:dataTable>
                  </tr:form>
		</t:panelTab>

		<%-- <t:panelTab label="Attributes">
		     <tr:form>
                       <tr:table value="#{sessionHandler.currentHost.attributes}"
                                 var="attribute" width="72" >
                         <tr:column sortProperty="type" sortable="true"
				    headerText="Type" >
			   <h:outputText value="#{attribute.type.name}"/>
		         </tr:column>
		         <tr:column sortProperty="uiString" sortable="true"
				    headerText="Value" >
			   <h:outputText value="#{attribute.uiString}" />
                         </tr:column>
                       </tr:table>
    		     </tr:form>
                </t:panelTab>
                --%>

		<t:panelTab label="Comments">
		  <tr:form>
                    <tr:table value="#{sessionHandler.viewedHost.comments}" var="comment" width="100%" >
                      <tr:column headerText="Comment" >
                        <h:outputText value="#{comment}" />
                      </tr:column>
                    </tr:table>
		  </tr:form>
		</t:panelTab>

		<t:panelTab label="In">
                  <tr:form>
                    <h:dataTable id="inlinkstable" binding="#{sessionHandler.inLinksTable}"
                                 value="#{sessionHandler.viewedHost.inHosts}" var="hosturl"
                                 border="0" cellpadding="2" cellspacing="2">
                      <h:column>
                        <f:facet name="header" >
                          <h:outputText value="Hosts Pointing to this Host " />
                        </f:facet>
                        <h:commandLink value="#{hosturl}"
                                       onclick="changeVHost(event); return false;" >
                        </h:commandLink>
                      </h:column>
                    </h:dataTable>
                  </tr:form>
		</t:panelTab>

		<t:panelTab label="Out">
                  <tr:form>
                    <h:dataTable id="outlinkstable" binding="#{sessionHandler.outLinksTable}"
                                 value="#{sessionHandler.viewedHost.outHosts}" var="hosturl"
                                 border="0" cellpadding="2" cellspacing="2">
                      <h:column>
                        <f:facet name="header" >
                          <h:outputText value="Hosts Pointed by this Host " />
                        </f:facet>
                        <h:commandLink value="#{hosturl}"
                                       onclick="changeVHost(event); return false;" >
                        </h:commandLink>
                      </h:column>
                    </h:dataTable>
                  </tr:form>
		</t:panelTab>

              </t:panelTabbedPane>

              <hr />

              <a4j:form id="nextHostsForm" ajaxSubmit="true">

                <h:outputLabel for="host" value="Host URL to assess:" />
                <br />
                <h:inputText id="hostName" required="true" size="30" immediate="true"
                             value="#{sessionHandler.hostToAssessStr}"/>
                <br />

                <a4j:commandButton value="Add to my list" id="addUrlToListButton"
                                   onclick="return addHostUrlToUsersList();" immediate="true"
                                   reRender="navigation:nextHostsForm" >

                </a4j:commandButton>
              </a4j:form>

              <hr />
              <h:form>
                <b><h:outputText value="Assessment tasks" /> </b>
              </h:form>
              <hr />
              <h:form>
                <h:commandLink action="go_help" value="Help" />
              </h:form>
              <h:form>
                <h:commandLink action="go_privacy" value="Privacy" />
              </h:form>
              <h:form>
                <h:commandLink action="go_logout" value="Logout" />
              </h:form>

              <hr />
              <p style="text-align:center;">
                <h:graphicImage id="header_logo" url="images/Logo_smal.jpg" />
              </p>

            </f:subview>

          </f:facet>


          <f:facet name="body">
            <f:subview id="body"> 
              <iframe scrolling='auto' id='rf' name='rf' onload="catchevent(event);"
	              src="<h:outputText value="#{sessionHandler.currentHostUrl }"/>"
	              frameborder='1' style='width: 800px; height: 1200px; padding: 0px' align="center">
              </iframe>
              <%-- test --%>
              <%-- <iframe scrolling='auto' id='rf' name='rf' onload="catchevent(event);"
		      src="http://info.ilab.sztaki.hu/~aszabo/p.html" >
	        frameborder='1' style='width: 800px; height: 1200px; padding: 0px' align="center">
              </iframe> --%>
            </f:subview>
          </f:facet>

        </t:panelLayout>

      </tr:document>

    </body>
  </f:view>

</html>
