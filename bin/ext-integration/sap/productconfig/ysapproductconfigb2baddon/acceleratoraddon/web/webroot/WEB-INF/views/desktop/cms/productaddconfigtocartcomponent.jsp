<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div <c:if test="${showOnlyForLongConfigurations}">style="display:none"</c:if> class="product-config-addtocart<c:if test="${showOnlyForLongConfigurations}"> product-config-addtocart-showonly</c:if>">
	<c:set var="buttonType">button</c:set>
	<c:if
		test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
		<c:set var="buttonType">submit</c:set>
	</c:if>
	
	<spring:theme code="text.addToCart" var="addToCartText" />
	<button type="${buttonType}"
		class="add_to_cart_button<c:if test="${fn:contains(buttonType, 'button')}"> out-of-stock</c:if>">
		<spring:theme code="text.addToCart" var="addToCartText" />
		<spring:theme code="basket.add.to.basket" />
	</button>
</div>
