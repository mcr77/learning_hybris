<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
<%@ taglib prefix="cssConf" uri="sapproductconfig.tld" %>

<label id="${cstic.key}.label" for="${cstic.key}.checkBoxList" class="${cssConf:labelStyleClasses(cstic)}">${cstic.langdepname}</label>


<div id="${cstic.key}.checkBoxList"  class="${cssConf:valueStyleClass(cstic)}">
<c:forEach var="value" items="${cstic.domainvalues}" varStatus="status">
	<c:choose>
		<c:when test="${status.index == 0}">
			<c:set value="product-config-csticValueSelect-first" var="cssValueClass"/>
			<c:set value="product-config-csticValueLabel-first" var="cssLabelClass"/>
		</c:when>
		<c:otherwise>
			<c:set value="" var="cssValueClass"/>
			<c:set value="" var="cssLabelClass"/>
		</c:otherwise>
	</c:choose>
	<form:checkbox id="${value.key}.checkBox" class="product-config-csticValueSelect ${cssValueClass}" disabled="${value.readonly}"
  	  path="${pathPrefix}domainvalues[${status.index}].selected" value="${value.selected}"/>
  	<label id="${value.key}.label" class="product-config-csticValueLabel ${cssLabelClass}" for="${value.key}.checkBox">${value.langdepname}</label>
  	<form:input type="hidden" id="${value.key}.valueName"
	path="${pathPrefix}domainvalues[${status.index}].name" />
	<br/>
</c:forEach>
</div>
<config:csticErrorMessages bindResult="${requestScope['org.springframework.validation.BindingResult.config']}" path="${pathPrefix}value"/>
