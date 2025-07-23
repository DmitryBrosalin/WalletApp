package ru.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.wallet.model.dto.RequestDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletAppIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateNewWallet() throws Exception {
        mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").exists());
    }

    @Test
    public void testDoOperationDeposit() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andReturn();
        String uuid = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("uuid").asText();
        RequestDto request = new RequestDto();
        request.setWalletId(uuid);
        request.setOperationType("DEPOSIT");
        request.setAmount(100);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid))
                .andExpect(jsonPath("$.balance").value(100));
    }

    @Test
    public void testDoOperationWithdraw() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andReturn();
        String uuid = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("uuid").asText();
        RequestDto request = new RequestDto();
        request.setWalletId(uuid);
        request.setOperationType("DEPOSIT");
        request.setAmount(100);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        RequestDto request2 = new RequestDto();
        request2.setWalletId(uuid);
        request2.setOperationType("WITHDRAW");
        request2.setAmount(50);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid))
                .andExpect(jsonPath("$.balance").value(50));
    }

    @Test
    public void testDoOperationWithdrawInsufficientFunds() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andReturn();
        String uuid = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("uuid").asText();
        RequestDto request = new RequestDto();
        request.setWalletId(uuid);
        request.setOperationType("DEPOSIT");
        request.setAmount(100);

        mockMvc.perform(post("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        RequestDto request2 = new RequestDto();
        request2.setWalletId(uuid);
        request2.setOperationType("WITHDRAW");
        request2.setAmount(150);

        String message = "Insufficient funds for operation: WITHDRAW, amount = 150";
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    public void testDoOperationWalletNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andReturn();
        String uuid = UUID.randomUUID().toString();
        RequestDto request = new RequestDto();
        request.setWalletId(uuid);
        request.setOperationType("DEPOSIT");
        request.setAmount(100);

        String message = "Wallet with UUID = " + uuid + " was not found.";
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    public void testDoOperationIncorrectUuid() throws Exception {
        mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andReturn();
        String uuid = "000";
        RequestDto request = new RequestDto();
        request.setWalletId(uuid);
        request.setOperationType("DEPOSIT");
        request.setAmount(100);

        String message = "Wallet UUID length must be actually 36 characters";
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    public void testDoOperationNegativeAmount() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andReturn();
        String uuid = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("uuid").asText();
        RequestDto request = new RequestDto();
        request.setWalletId(uuid);
        request.setOperationType("DEPOSIT");
        request.setAmount(-100);

        String message = "Amount must be positive";
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    public void testDoOperationIncorrectOperationType() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andReturn();
        String uuid = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("uuid").asText();
        RequestDto request = new RequestDto();
        String operationType = "unknown";
        request.setWalletId(uuid);
        request.setOperationType(operationType);
        request.setAmount(100);

        String message = "Incorrect operation type: " + operationType;
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    public void testDoOperationInvalidRequest() throws Exception {
        RequestDto invalidRequest = new RequestDto();

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetWallet() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/wallet/create-new-wallet"))
                .andReturn();
        String uuid = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("uuid").asText();

        mockMvc.perform(get("/api/v1/wallets/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid));
    }

    @Test
    public void testGetWalletNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/non-existing-uuid"))
                .andExpect(status().isNotFound());
    }
}

