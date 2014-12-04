<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>
<%@ taglib prefix="cssConf" uri="sapproductconfig.tld" %>

<label id="${cstic.key}.label" for="${cstic.key}.checkBox" class="${cssConf:labelStyleClasses(cstic)}">${cstic.langdepname}</label>

<div class="${cssConf:valueStyleClass(cstic)}">
<form:checkbox class="${cssConf:valueStyleClass(cstic)} product-config-csticValueSelect product-config-csticValueSelect-single" id="${cstic.key}.checkBox" 
     path="${pathPrefix}domainvalues[0].selected" value="${cstic.domainvalues[0].selected}" />
</div>
<config:csticErrorMessages bindResult="${requestScope['org.springframework.validation.BindingResult.config']}" path="${pathPrefix}value"/>

<form:input type="hidden" id="${cstic.key}.valueName"
	path="${pathPrefix}domainvalues[0].name" />
