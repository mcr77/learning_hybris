<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>

<%@ taglib prefix="cssConf" uri="sapproductconfig.tld"%>

<label id="${cstic.key}.label" for="${cstic.key}.ddlb"
	class="${cssConf:labelStyleClasses(cstic)}">${cstic.langdepname}</label>

<div class="${cssConf:valueStyleClass(cstic)}">
<form:select id="${cstic.key}.ddlb"
	class="${cssConf:valueStyleClass(cstic)}" path="${pathPrefix}value">
	<c:if test="${empty cstic.value}">
		<form:option value="NULL_VALUE">
			<spring:message code="sapproductconfig.ddlb.select.text"
				text="Please select" />
		</form:option>
	</c:if>
	<c:forEach var="value" items="${cstic.domainvalues}" varStatus="status">
		<form:option id="${value.key}.option" value="${value.name}"
			label="${value.langdepname}" />
	</c:forEach>
</form:select>
</div>
<config:csticErrorMessages
	bindResult="${requestScope['org.springframework.validation.BindingResult.config']}"
	path="${pathPrefix}value" />