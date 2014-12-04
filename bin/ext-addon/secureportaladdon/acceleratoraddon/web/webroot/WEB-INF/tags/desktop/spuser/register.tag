<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<div class="item_container_holder span-15">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2>
			<spring:theme code="register.new.customer"/>
		</h2>
	</div>

	<div class="item_container">
		<p class="required">
			<spring:theme code="form.required"/>
		</p>
		<form:form method="post" commandName="registrationForm" action="${action}">
			<div class="form_field-elements left">
				<div class="newCustomerRegister left span-7">
					<formUtil:formSelectBox idKey="register.title" labelKey="register.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titles}"/>
					<formUtil:formInputBox idKey="secureportal.firstAndLastName" labelKey="secureportal.firstAndLastName" path="name" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="register.email" labelKey="register.email" path="email" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="secureportal.position" labelKey="secureportal.position" path="position" inputCSS="text" mandatory="true"/>
					<div class="left span-4 phone">
							<formUtil:formInputBox idKey="storeDetails.table.telephone" labelKey="storeDetails.table.telephone" path="telephone" inputCSS="text" mandatory="true"/>
						</div>
					<div class="left span-2 extension">
							<formUtil:formInputBox idKey="secureportal.extension" labelKey="secureportal.extension" path="telephoneExtension" inputCSS="text" mandatory="false"/>
					</div>
				</div>
				<div class="newCustomerRegister right span-7">
					<formUtil:formInputBox idKey="secureportal.companyName" labelKey="secureportal.companyName" path="companyName" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="address.line1" labelKey="address.line1" path="companyAddressStreet" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="address.line2" labelKey="address.line2" path="companyAddressStreetLine2" inputCSS="text" mandatory="false"/>
					<formUtil:formInputBox idKey="address.townCity" labelKey="address.townCity" path="companyAddressCity" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="address.postcode" labelKey="address.postcode" path="companyAddressPostalCode" inputCSS="text" mandatory="true"/>
					<formUtil:formSelectBox idKey="address.country_del" labelKey="address.country" path="companyAddressCountryIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCountry" items="${countries}" itemValue="isocode"/>
				</div>
				<div class="msg">
					<formUtil:formTextArea idKey="secureportal.message" labelKey="secureportal.message" path="message" areaCSS="textarea" mandatory="false"/>
				</div>
			</div>
			<%--for captchaaddon--%>
			<c:if test="${enableCaptcha}">
				<div id="recaptcha_widget" style="display:block" data-recaptcha-public-key="${requestScope.recaptchaPublicKey}">
					<c:if test="${requestScope.recaptchaChallangeAnswered == false}">
						<div class="form_field_error">
					</c:if>
	
					<div id="recaptcha_image" class="left"></div>
					<div class="left">
						<a href="javascript:Recaptcha.reload()" class="cicon reload"></a>
	
						<div class="recaptcha_only_if_image"><a href="javascript:Recaptcha.switch_type('audio')" class="cicon audio"></a></div>
						<div class="recaptcha_only_if_audio"><a href="javascript:Recaptcha.switch_type('image')" class="cicon image"></a></div>
					</div>
					
					<div class="recaptcha_only_if_incorrect_sol" style="color:red">Incorrect please try again</div>
	
					<div class="form_field-label">
						<span class="recaptcha_only_if_image">Enter the words above:</span>
						<span class="recaptcha_only_if_audio">Enter the numbers you hear:</span>
					</div>
					<div class="form_field-input clearfix">
						<input type="text" id="recaptcha_response_field" name="recaptcha_response_field" class="left"/>
						<a href="javascript:Recaptcha.showhelp()" class="cicon help left"></a>
					</div>
	
					<c:if test="${requestScope.recaptchaChallangeAnswered == false}">
					</div>
					</c:if>
			</c:if>
	</div>
	<div class="form-field-button">
		<ycommerce:testId code="register_Register_button">
			<button type="submit" class="form">
				<spring:theme code='${actionNameKey}'/>
			</button>
		</ycommerce:testId>
	</div>

	</form:form>
</div>

