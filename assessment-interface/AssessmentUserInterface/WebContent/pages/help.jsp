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
		<h:outputText value="Help" />
	      </h:form>
	      <h:form>
		<h:commandLink action="go_privacy" value="Privacy" />
	      </h:form>
	      <h:form>
		<h:commandLink action="go_logout" value="Logout" />
	      </h:form>
	      <hr />
	      <b><h:outputText value="Assessment tasks" /> </b>
	      <h:form>
		<h:commandLink action="go_assessment" value="Next host" />
	      </h:form>
	      <hr />
	    </f:subview>
	  </f:facet>
	  <f:facet name="body">
	    <f:subview id="body">

              <p>As an overall guideline, please follow the steps below. You may return to any of the steps later to change your original decision. Note that some of the first decisions shade the later ones, i.e. all properties of a host that is excluded (adult, several sites hosted under one domain, etc.) turn grey and cannot be assessed anymore.</p>
              <p><strong>Take your time and look at different aspects</strong>
                : examine the Web sites carefully to check different aspects before taking a decision, particularly in sites that are hard to classify. Look at the in- and out-links, both within and outside of the data set. Use Google to find information about the present state of the site. Look at the present version - although the crawl has been done in February, things may have evolved since then.</p>
              <p>
                <b>There are known limitations in the iframe mechanism of the assessment interface</b>:<br />
                <ul>
                  <li>Some pages force the browser to display them on top of the interface. For certain hosts we see no way of using the interface for this reason. Please send whatever information you can gather about these hosts manually by email. So far it seems this is not a widespread problem.</li>
                  <li>A wide monitor is sometimes very useful :(</li></ul>
              </p>
              <p>
                <b>The interface warns you whenever you are viewing a site other than assessed</b>. Each site should be assessed based on its own pages and not its external content. Note that even www.site.eu and site.eu are different and you get the warning. Typically one of these variants is a redirect to the canonical name and you should only be assessing the non-redirecting version. Hosts with redirects only fall in the "too few text" language category.<br />
              </p>
              <h1>1. Check for reasons to exclude the site</h1>
              <p>First check some obvious reasons why the host may not be included in the sample at all. You will not have to assess the host (host is EXCLUDEd if</p>
              <ul><li><p>The host contains adult content 	(porn) - please mark the corresponding flag;</p>
                </li>
                <li><p>Mixed: multiple unrelated sites in 	the same host, several sites of different type under the same host 	name - please mark mixed;</p>
                </li>
                <li><p>The language is not your selected one or mixed over the site.  The language is autodetected but there may be errors. In this case please also mark the correct language. Remember assessment is on the site level, hence a Web site in multiple languages is mixed if it has structure www.website.eu/en/, www.website.eu/de/ etc.; but en.website.eu/ is (most likely) in English, de.website.eu is in German etc. since they are different hosts. Check carefully these options when a site offers language selection. Also note that some sites personalize their language by the location of the browser. In this case the language of the archived version in the interface counts even if you view the live version in another language.</p>
                </li>
                <li><p>Too few text (language label): there are less than 10 pages on the site that contain text, or most of the pages have just a couple of words - in general the whole text over the site is too short. Hosts with only redirects fall in this category although they should have been excluded from the sample.</p>
                </li>

                <li><p>If there is another serious reason 	why the site should not be labeled, mark &quot;Other problem&quot;. 	In this case labels are not stored but a comment explaining the 	reason is compulsory.</p>
                </li>
              </ul>
              <p>In case you face problems in assessing either of the labels below, mark &quot;Unsure&quot; any time.  Problems may be caused both by the labels we provide being inappropriate (we may have not thought of some issues or simply could not cover all aspects by the limited set of labels etc) or by some lack of domain knowledge, background information (you have no knowledge of the topic).</p>
              <p>Even if you mark a host &quot;Unsure&quot;, give labels up to your best knowledge. In case you mark &quot;Unsure&quot; or even if you can fully complete the labeling of the site but you feel uncomfortable about some of your decisions, please add a short comment describing your problem. This helps in improving both the interface and the label set itself and the reliability of the labels by revising them. You are not allowed say &quot;Unsure&quot; with empty comment.</p>

              <h1>2. Check Genre</h1>
              <p>Next check genre.  The labels are not exclusive: a host may for example be educational and database at the same time.</p>
              <h2>2a. Check if spam</h2>
              <p>First check if the site is spam.  This is the most important and most tricky one.  If the site is spam, the remaining labels do not have to be filled in since they are unreliable.</p>
              <p>We use the guidelines from <a href="http://barcelona.research.yahoo.net/webspam/datasets/uk2007/guidelines/">http://barcelona.research.yahoo.net/webspam/datasets/uk2007/guidelines/</a>
                . We give a short description. If in doubt, consult the full guidelines with examples.  Note that we do not use &quot;borderline&quot; categories. If you have doubt, please try to verify and as a final resort mark &quot;Unsure&quot; as in the overall guidelines.</p>

              <p><strong>General definition of Web spam</strong>
                :&laquo;<em>any deliberate action that is meant to trigger an unjustifiably favorable [ranking], considering the page's true value</em>
                &raquo; (Gy&ouml;ngyi and Garc&iacute;a Molina 2005). Look for <strong>aspects of the host that are mostly to attract and/or redirect traffic</strong>
                .</p>

              <p>Sites that do Web Spam:</p>
              <ul><li><p>Include aspects designed to 	attract/redirect traffic.</p>
                </li>
                <li><p>Almost always have commercial 	intent.</p>
                </li>
                <li><p>Rarely offer relevant content for 	users browsing them.</p>
                </li>
              </ul>

              <p>Typical Web Spam Aspects:</p>
              <ul><li><p>Include many unrelated keywords 	and links.</p>
                </li>
                <li><p>Use many keywords and punctuation 	marks such as dashes in the URL.</p>
                </li>
                <li><p>Redirect the user to another 	(usually unrelated) page.</p>
                </li>
                <li><p>Create many copies with 	substantially duplicate content.</p>

                </li>
                <li><p>Hide text by writing in the same 	color as the background of the page</p>
                </li>
              </ul>
              <p><strong>Pages that are only advertising</strong>
                , with very little content are spam, including automatically generated pages designed to sell advertising; sites that offer catalogs of products that are actually redirecting to other merchants, without providing extra value.</p>
              <p><strong>Pages that do not use Web spam tricks</strong>
                should not be labeled spam regardless of their quality. Normal pages can be high-quality or low-quality resources - other aspects of quality are addressed by other labels.</p>

              <h2>2b. Check genre</h2>
              <p>Guideline for other genres. If in doubt, please mark &quot;Unsure&quot;. Mark at least one genre (including spam and adult) but possibly more. You will not be allowed to continue if no genre is selected.</p>
              <ul><li><p>Editorial or news content: posts 	disclosing, announcing, disseminating news. Factual texts reporting 	on a state of affairs, like newswires (including sport) and police 	reports. Posts discussing, analyzing, advocating about a specific 	social/environmental/technological/economic issue, including 	propaganda adverts, political pamphlets.</p>
                </li>
                <li><p>Commercial content: product 	reviews, product shopping, on-line store, product catalogue, service 	catalogue, product related how-tos, FAQs, tutorials.</p>
                </li>
                <li><p>Educational and research content: tutorials, guidebooks, how-to guides, instructional material, educational material. Research papers, books. Catalogues, glossaries. Conferences, institutions, project pages. Health also belongs here.</p>

                </li>
                <li><p>Discussion spaces: includes 	dedicated forums, chat spaces, etc. Standard comment forms do not 	count.</p>
                </li>

                <li><p>Personal/Leisure: arts, music, home, family, kids, games, horoscopes etc. A personal blog for example belongs both here and to  &quot;discussion&quot;.</p></li>
                <li><p>Media: video, audio, ... In general a site where the main content is not text but media. For example a site about music is probably leisure and not media.</p>
                </li>
                <li><p>Database: a &quot;deep web&quot; 	site whose content can be retrieved only by querying a database. Sites offering forms fall in this category.</p>
                </li>
                <li><p>Adult: porn (will be discarded 	from sample)</p>

                </li>
              </ul>
              <p>Also mark unknown for hosts with little or no running text, like forms for queries, logins, download pages, flash animation, samples of source code, etc; one important subcategory here is index, i.e., portals, sitemaps, other lists of links (mostly containing incomplete or isolated sentences).</p>
              <h1>3. Flag any serious readability issues</h1>
              <p>Flag any serious readability issues you find regarding readability of the page for the two categories below. Decide at the host level, by viewing sufficient number of sample pages. A site that contains posts, forums etc related to its main content should be assessed based on its main content.</p>
              <ul><li><p>Serious perceptual issues: 	contrasting color, layout, etc. that makes the text hard to read</p>
                </li>
                <li><p>Serious linguistic correctness 	issues -- the text is poorly written: incorrect style, abundant 	grammar and spelling errors.</p>

                </li>
              </ul>
              <h1>4. Trustworthiness</h1>
              <p>This measure applies only to hosts of the following type:</p>
              <ul><li><p>News</p>
                </li>
                <li><p>Commercial</p>
                </li>

                <li><p>Educational</p>
                </li>
                <li><p>Media</p>
                </li>
                <li><p>Database</p>
                </li>
              </ul>
              <p>We assess sites based on viewing a sample of pages. We drop sites that contain a mix. A site that contains posts, forums etc related to its main content should be assessed based on its main content; these sites are not considered a mix.</p>

              <ul><li><p>3: I trust this fully. This is a 	famous authoritative source (a famous newspaper, company, 	organization)</p>
                </li>
                <li><p>2: I trust this marginally. Looks 	like an authoritative source but its ownership is unclear.</p>
                </li>
                <li><p>1: I do not trust this.  	There are aspects of the site that make me distrust this source.</p>
                </li>
              </ul>
              <h1>5. Neutrality.</h1>

              <p>We adapt the definition from Wikipedia (<a href="http://en.wikipedia.org/wiki/NPOV">NPOV</a>
                ): &quot;The neutral point of view is a means of dealing with conflicting perspectives on a topic as evidenced by reliable sources. It requires that all majority- and significant-minority views be presented fairly, in a disinterested tone, and in rough proportion to their prevalence within the source material.The neutral point of view neither sympathizes with nor disparages its subject, nor does it endorse or oppose specific viewpoints. It is not a lack of viewpoint, but is rather a specific, <em>editorially neutral</em>
                , point of view. An article should clearly describe, represent, and characterize all the disputes within a topic, but should not endorse any particular point of view. It should explain who believes what, and why, and which points of view are most common. It may contain critical evaluations of particular viewpoints based on reliable sources, but even text explaining sourced criticisms of a particular view must avoid taking sides.&quot;</p>
              <p>We assess neutrality only for hosts of the following type:</p>
              <ul><li><p>News</p>

                </li>
                <li><p>Educational</p>
                </li>
                <li><p>Media</p>
                </li>
                <li><p>Database</p>
                </li>
                <li><p>Commercial, with the remark below.</p>
                </li>

              </ul>
              <p>For commercial site this measure indicates no bias towards an undeclared target, e.g. review sites with a clear bias towards or against certain brand or product qualify as biased. A company home page advertising its own product qualify as neutral unless biased opinion is included towards competitors.</p>
              <p>We assess sites based on viewing a sample of pages. We drop sites that contain a mix. A site that contains posts, forums etc related to its main content should be assessed based on its main content; these sites are not considered a mix.</p>
              <p>Labels are on a scale from facts towards strong bias:</p>
              <ul><li><p>Facts: I think these are mostly 	facts</p>
                </li>
                <li><p>Fact/Opinion: I think these are 	opinions and facts; facts are included in the site or referenced 	from external sources.</p>

                </li>
                <li><p>Opinion: I think this is mostly an 	opinion that may or may not be supported by facts, but little or no 	facts are included or referenced.</p>
                </li>
              </ul>
              <p>Plus:</p>
              <ul><li><p>Biased flag: This source seems 	very biased to me:  promotes a particular religion, ideology, 	philosophy or political  standpoint.</p>
                </li>
              </ul>

              <p><strong>Examples:</strong> <br />
                http://www.foxnews.com/ (or any conservative media: Fact/Opinion)<br />
                http://www.nytimes.com/ (or any liberal media: Fact/Opinion)<br />
                http://www.goveg.com/ (or any activist group: Fact/Opinion + Bias)<br />
                http://www.vatican.va/phome_en.htm (or any religious group including facts such as activities, etc.: Fact/Opinion + Bias)<br />
                http://www.galactic-server.net/linkmap.html (or any fringe theories: Opinion + Bias)
              </p>
            </f:subview>
          </f:facet>
        </t:panelLayout>


      </tr:document>
    </body>
  </f:view>
</html>
