//$Id$
package com.srs.core;

import com.srs.model.IPaymentService;
import com.srs.model.PaymentDetail;

public class PaymentService implements IPaymentService {

    @Override
    public boolean processPayment(PaymentDetail paymentDetail) {
        return true;
    }
}
