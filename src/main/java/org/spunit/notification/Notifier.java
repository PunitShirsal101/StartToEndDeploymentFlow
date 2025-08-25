package org.spunit.notification;

import org.spunit.entity.Customer;

public interface Notifier {
    void onCustomerCreated(Customer customer);
    void onCustomerUpdated(Customer customer);
}
