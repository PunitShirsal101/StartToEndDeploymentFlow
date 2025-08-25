package org.spunit.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spunit.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class EmailNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(EmailNotifier.class);

    @Override
    public void onCustomerCreated(Customer customer) {
        // Simulate sending an email by logging. Demonstrates polymorphism via Notifier interface.
        log.info("[Notifier] Customer created: id={}, name={}, email={}", customer.getId(), customer.getName(), customer.getEmail());
    }

    @Override
    public void onCustomerUpdated(Customer customer) {
        log.info("[Notifier] Customer updated: id={}, name={}, email={}", customer.getId(), customer.getName(), customer.getEmail());
    }
}
