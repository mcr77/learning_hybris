/**
 * 
 */
package de.hybris.platform.sap.sappostransactionaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.sap.sapcarcommercefacades.order.CarOrderFacade;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;




/**
 * POS Transaction History COntroller
 */
@Controller
@RequestMapping("/my-account")
public class PosTransactionPageController extends AbstractPosTransactionPageController
{

	@Resource(name = "orderFacade")
	protected OrderFacade orderFacade;

	@Resource(name = "carOrderFacade")
	protected CarOrderFacade carOrderFacade;


	@Resource(name = "accountBreadcrumbBuilder")
	protected ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	protected static final String POS_TRANSACTION_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
	protected static final String POS_TRANSACTIONS_HISTORY_CMS_PAGE = "postransactions";
	protected static final String POS_TRANSACTION_DETAIL_CMS_PAGE = "postransaction";
	protected static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";


	private static final Logger LOG = Logger.getLogger(PosTransactionPageController.class);


	@RequestMapping(value = "/postransactions", method = RequestMethod.GET)
	@RequireHardLogIn
	public String posTransactions(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<CarOrderHistoryData> searchPageData = carOrderFacade.getPagedOrderHistoryForCustomer(pageableData);

		populateModel(model, searchPageData, showMode);
		storeCmsPageInModel(model, getContentPageForLabelOrId(POS_TRANSACTIONS_HISTORY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(POS_TRANSACTIONS_HISTORY_CMS_PAGE));

		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.posTransactionHistory"));
		model.addAttribute("metaRobots", "noindex,nofollow");

		return getViewForPage(model);
	}


	@RequestMapping(value = "/postransaction/" + POS_TRANSACTION_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String posTransaction(@SuppressWarnings("unused") @PathVariable("orderCode") final String transactionCode,
			@RequestParam(value = "storeId", required = true) final String storeId,
			@RequestParam(value = "transactionDate", required = true) final String transactionDate,
			@RequestParam(value = "transactionIndex", required = true) final Integer transactionIndex, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{

			final CarOrderHistoryData orderDetails = carOrderFacade.getOrderDetails(
					PosTransactionPageControllerUtil.formatDate(transactionDate), storeId, transactionIndex);

			model.addAttribute("orderData", orderDetails);

			final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);

			final String posTransactionsLinkName = getMessageSource().getMessage("text.account.posTransactionHistory", null,
					"In-Store Purchases", getI18nService().getCurrentLocale());

			final String posTransactionLinkName = getMessageSource().getMessage(
					"text.account.posTransactionHistory.posTransactionBreadcrumb", new Object[]
					{ orderDetails.getPurchaseOrderNumber() }, "In-Store Purchase {0}", getI18nService().getCurrentLocale());

			breadcrumbs.add(new Breadcrumb("/my-account/postransactions", posTransactionsLinkName, null));
			breadcrumbs.add(new Breadcrumb("#", posTransactionLinkName, null));

			model.addAttribute("breadcrumbs", breadcrumbs);
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load a POS Transaction that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(POS_TRANSACTION_DETAIL_CMS_PAGE));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(POS_TRANSACTION_DETAIL_CMS_PAGE));
		return getViewForPage(model);
	}
}
