package com.palmer.wfhbillingapi.service;

import com.palmer.wfhbillingapi.config.SupabaseProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseAdminServiceImpl implements SupabaseAdminService {

    private final SupabaseProperties supabaseProperties;
    private final RestClient restClient = RestClient.create();

    public SupabaseAdminServiceImpl(SupabaseProperties supabaseProperties) {
        this.supabaseProperties = supabaseProperties;
    }

    @Override
    public void updateUserTenantMetadata(String supabaseUserId, UUID tenantId) {
        Map<String, Map<String, String>> body = Map.of("app_metadata",
                Map.of("tenant_id", tenantId.toString(),
                        "app_role", "ADMIN"));

        restClient.put()
                .uri(supabaseProperties.url() + "/auth/v1/admin/users/" + supabaseUserId)
                .header("Authorization", "Bearer " + supabaseProperties.serviceRoleKey())
                .header("apiKey", supabaseProperties.serviceRoleKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
