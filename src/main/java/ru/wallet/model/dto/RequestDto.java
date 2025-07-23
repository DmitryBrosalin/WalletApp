package ru.wallet.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    @NotBlank
    @Size(min = 36, max = 36, message = "Wallet UUID length must be actually 36 characters")
    private String walletId;
    @NotBlank(message = "Operation type must not be blank")
    private String operationType;
    @Positive(message = "Amount must be positive")
    private long amount;
}
