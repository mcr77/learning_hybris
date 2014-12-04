<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<%@ taglib prefix="cssConf" uri="sapproductconfig.tld" %>

<label id="${cstic.key}.label" for="${cstic.key}.text" class="${cssConf:labelStyleClasses(cstic)}">${cstic.langdepname}</label> 
<div id="${cstic.key}.text" class="${cssConf:valueStyleClass(cstic)}">
	<i><spring:message code="sapproductconfig.cstic.type.notimplemented" text="Not implemented" /></i>
</div>
