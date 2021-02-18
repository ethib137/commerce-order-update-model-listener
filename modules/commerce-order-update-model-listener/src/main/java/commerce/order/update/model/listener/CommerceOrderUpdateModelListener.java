package commerce.order.update.model.listener;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.expando.kernel.service.ExpandoValueServiceUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author evanthibodeau
 */
@Component(
	immediate = true,
	property = {
		// TODO enter required service properties
	},
	service = ModelListener.class
)
public class CommerceOrderUpdateModelListener
	extends BaseModelListener<CommerceOrder> {

	// TODO enter required service methods
	@Override
	public void onAfterUpdate(CommerceOrder commerceOrder)
		throws ModelListenerException {

		System.out.println("############# UPDATE COMMERCE ORDER #############");
		System.out.println("Status: " + commerceOrder.getOrderStatus());
		System.out.println("Advanced Status: " + commerceOrder.getAdvanceStatus());

		if (commerceOrder.getOrderStatus() == WorkflowConstants.STATUS_PENDING) {
			long companyId = _portal.getDefaultCompanyId();
			long classNameId = _portal.getClassNameId(CommerceOrderItem.class);

			String className = _portal.getClassName(classNameId);

			List<CommerceOrderItem> commerceOrderItems =
				commerceOrder.getCommerceOrderItems();

			for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
				String code = "";

				int quantity = commerceOrderItem.getQuantity();

				Date date = new Date();

				long time = date.getTime();

				for (int i = 0; i < quantity; i++) {
					time = time + 11;

					if (i != 0) {
						code = code + ", ";
					}

					code = code + String.valueOf(time);
				}

				try {
					ExpandoValueServiceUtil.addValue(
						companyId, className, "CUSTOM_FIELDS", "Gift Card Code",
						commerceOrderItem.getCommerceOrderItemId(), code
					);
				} catch (PortalException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Reference
		private Portal _portal;

}