package test.coreer.config.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import test.coreer.service.CurrentTenantProvider;

/**
 * Created by aieremenko on 12/2/16.
 */
@Component
public class DimanexCurrentTenantResolver implements CurrentTenantIdentifierResolver {

    @Autowired
    CurrentTenantProvider currentTenantProvider;

    @Override
    public String resolveCurrentTenantIdentifier() {
        return currentTenantProvider.getCurrentTenantId().toString();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
